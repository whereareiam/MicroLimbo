/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aerittopia.microlimbo.common.connection.client;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.api.event.player.PlayerDisconnectEvent;
import com.aerittopia.microlimbo.common.LimboServer;
import com.aerittopia.microlimbo.common.connection.PacketDecoder;
import com.aerittopia.microlimbo.common.connection.PacketEncoder;
import com.aerittopia.microlimbo.common.connection.player.LimboPlayer;
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import com.aerittopia.microlimbo.common.protocol.packet.play.PacketKeepAlive;
import com.aerittopia.microlimbo.common.registry.State;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class ClientConnection extends ChannelInboundHandlerAdapter implements com.aerittopia.microlimbo.api.connection.ClientConnection {
	private final LimboServer server;
	private final EventManager eventManager;
	private final LoggingHelper loggingHelper;

	private final PacketDecoder packetDecoder;
	private final PacketEncoder packetEncoder;

	private final Channel channel;
	private SocketAddress remoteAddress;

	private LimboPlayer limboPlayer;

	public ClientConnection(LimboServer server, EventManager eventManager, LoggingHelper loggingHelper, PacketDecoder packetDecoder, PacketEncoder packetEncoder, Channel channel) {
		this.server = server;
		this.eventManager = eventManager;
		this.loggingHelper = loggingHelper;
		this.packetDecoder = packetDecoder;
		this.packetEncoder = packetEncoder;
		this.channel = channel;
		this.remoteAddress = channel.remoteAddress();
	}

	@Override
	public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
		State state = limboPlayer.getState();

		if (state.equals(State.PLAY) || state.equals(State.CONFIGURATION)) {
			server.getPlayers().forEach(player -> {
				if (player.getClientConnection().equals(this)) {
					player.disconnect();
					server.removePlayer(player);

					PlayerDisconnectEvent playerDisconnectEvent = new PlayerDisconnectEvent(limboPlayer);
					eventManager.fireEvent(playerDisconnectEvent);
				}
			});
		}

		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (channel.isActive()) {
			loggingHelper.severe("Unhandled exception: %s", cause);
		}
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object object) {
		if (object instanceof Packet packet)
			packet.handle(this);
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return channel.remoteAddress();
	}

	public void setRemoteAddress(String host) {
		this.remoteAddress = new InetSocketAddress(host, ((InetSocketAddress) this.remoteAddress).getPort());
	}

	@Override
	public void writePacket(Object packet) {
		if (channel.isActive()) {
			channel.write(packet, channel.voidPromise());
		}
	}

	@Override
	public void sendPacket(Object packet) {
		if (channel.isActive()) {
			channel.writeAndFlush(packet, channel.voidPromise());
		}
	}

	@Override
	public void sendPacket(Object packet, boolean close) {
		if (channel.isActive() && close) {
			channel.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE);
		} else {
			sendPacket(packet);
		}
	}

	@Override
	public void sendKeepAlive() {
		if (limboPlayer.getState().equals(State.PLAY)) {
			PacketKeepAlive keepAlive = new PacketKeepAlive(
					ThreadLocalRandom.current().nextLong()
			);

			sendPacket(keepAlive);
		}
	}
}
