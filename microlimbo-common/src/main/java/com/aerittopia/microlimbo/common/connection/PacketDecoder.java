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

package com.aerittopia.microlimbo.common.connection;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.connection.player.LimboPlayer;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.PacketRegistry;
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import com.aerittopia.microlimbo.common.registry.State;
import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {
	private final LoggingHelper loggingHelper;

	private PacketRegistry registry;
	private LimboPlayer limboPlayer;
	private State temporalState = null;

	@Inject
	public PacketDecoder(LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		Version version = limboPlayer.getClientVersion();

		if (temporalState == State.LOGIN) {
			registry = temporalState.serverBound.getRegistry(version);
			temporalState = null;
		} else registry = limboPlayer.getState().serverBound.getRegistry(version);

		if (!ctx.channel().isActive() || registry == null) return;

		ByteMessage message = new ByteMessage(buf);
		int packetId = message.readVarInt();
		Packet packet = registry.getPacket(packetId);

		if (packet != null) {
			loggingHelper.debug("Received packet %s[0x%s] (%d bytes)", packet.toString(), Integer.toHexString(packetId), message.readableBytes());
			try {
				packet.decode(message, version);
			} catch (Exception e) {
				loggingHelper.warning("Cannot decode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
			}

			ctx.fireChannelRead(packet);
		} else {
			loggingHelper.debug("Undefined incoming packet: 0x" + Integer.toHexString(packetId) + " [" + limboPlayer.getState() + "]");
		}
	}
}
