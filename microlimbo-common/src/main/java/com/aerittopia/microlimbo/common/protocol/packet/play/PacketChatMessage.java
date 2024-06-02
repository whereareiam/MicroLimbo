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

import com.aerittopia.microlimbo.api.model.NBTMessage;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.Setter;

import java.util.UUID;

@Setter
public class PacketChatMessage implements PacketOut {

	private NBTMessage message;
	private PositionLegacy position;
	private UUID sender;

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeNBTMessage(this.message, version);
		if (version.moreOrEqual(Version.V1_19_1)) {
			message.writeBoolean(position.index == PositionLegacy.ACTION_BAR.index);
		} else if (version.moreOrEqual(Version.V1_19)) {
			message.writeVarInt(position.index);
		} else if (version.moreOrEqual(Version.V1_8)) {
			message.writeByte(position.index);
		}

		if (version.moreOrEqual(Version.V1_16) && version.less(Version.V1_19))
			message.writeUniqueId(sender);
	}

	public enum PositionLegacy {

		CHAT(0),
		SYSTEM_MESSAGE(1),
		ACTION_BAR(2);

		private final int index;

		PositionLegacy(int index) {
			this.index = index;
		}

	}

}
