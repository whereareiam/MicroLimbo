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

package com.aerittopia.microlimbo.common.protocol.packet.login;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketLoginSuccess implements PacketOut {
	private UUID uniqueId;
	private String username;

	@Override
	public void encode(ByteMessage message, Version version) {
		if (version.moreOrEqual(Version.V1_16)) {
			message.writeUniqueId(uniqueId);
		} else if (version.moreOrEqual(Version.V1_7_6)) {
			message.writeString(uniqueId.toString());
		} else {
			message.writeString(uniqueId.toString().replace("-", ""));
		}
		message.writeString(username);
		if (version.moreOrEqual(Version.V1_19)) {
			message.writeVarInt(0);
		}
		if (version.moreOrEqual(Version.V1_20_5)) {
			message.writeBoolean(true);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
