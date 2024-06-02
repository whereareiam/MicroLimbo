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
import com.aerittopia.microlimbo.common.protocol.PacketSnapshot;
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketEncoder extends MessageToByteEncoder<Packet> {
	private final LoggingHelper loggingHelper;

	private PacketRegistry registry;
	private LimboPlayer limboPlayer;

	@Inject
	public PacketEncoder(LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
		Version version = limboPlayer.getClientVersion();
		registry = limboPlayer.getState().clientBound.getRegistry(version);

		if (registry == null) return;

		ByteMessage message = new ByteMessage(out);
		int packetId;

		if (packet instanceof PacketSnapshot) {
			packetId = registry.getPacketId(((PacketSnapshot) packet).getWrappedPacket().getClass());
		} else {
			packetId = registry.getPacketId(packet.getClass());
		}

		if (packetId == -1) {
			loggingHelper.warning("Undefined packet class: %s[0x%s] (%d bytes)", packet.getClass().getName(), Integer.toHexString(packetId), message.readableBytes());
			return;
		}

		message.writeVarInt(packetId);

		try {
			packet.encode(message, version);

			loggingHelper.debug("Sending %s[0x%s] packet (%d bytes)", packet.toString(), Integer.toHexString(packetId), message.readableBytes());
		} catch (Exception e) {
			loggingHelper.severe("Cannot encode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
		}
	}
}
