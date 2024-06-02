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
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketKeepAlive implements Packet {
	private long id;

	@Override
	public void encode(ByteMessage message, Version version) {
		if (version.moreOrEqual(Version.V1_12_2)) {
			message.writeLong(id);
		} else if (version.moreOrEqual(Version.V1_8)) {
			message.writeVarInt((int) id);
		} else {
			message.writeInt((int) id);
		}
	}

	@Override
	public void decode(ByteMessage message, Version version) {
		if (version.moreOrEqual(Version.V1_12_2)) {
			this.id = message.readLong();
		} else if (version.moreOrEqual(Version.V1_8)) {
			this.id = message.readVarInt();
		} else {
			this.id = message.readInt();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
