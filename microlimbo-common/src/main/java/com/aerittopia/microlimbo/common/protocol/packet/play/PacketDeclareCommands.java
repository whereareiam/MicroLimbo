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

package com.aerittopia.microlimbo.common.protocol.packet.play;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;

import java.util.Collections;
import java.util.List;

/**
 * Packet for 1.13+
 */
public class PacketDeclareCommands implements PacketOut {
	private final List<String> commands = Collections.emptyList();

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeVarInt(commands.size() * 2 + 1); // +1 because declaring root node

		// Declare root node

		message.writeByte(0);
		message.writeVarInt(commands.size());

		for (int i = 1; i <= commands.size() * 2; i++) {
			message.writeVarInt(i++);
		}

		// Declare other commands

		int i = 1;
		for (String cmd : commands) {
			message.writeByte(1 | 0x04);
			message.writeVarInt(1);
			message.writeVarInt(i + 1);
			message.writeString(cmd);
			i++;

			message.writeByte(2 | 0x04 | 0x10);
			message.writeVarInt(1);
			message.writeVarInt(i);
			message.writeString("arg");
			message.writeString("brigadier:string");
			message.writeVarInt(0);
			message.writeString("minecraft:ask_server");
			i++;
		}

		message.writeVarInt(0);
	}

}
