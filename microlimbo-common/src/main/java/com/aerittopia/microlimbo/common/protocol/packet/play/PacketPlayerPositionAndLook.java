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
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayerPositionAndLook implements PacketOut {

	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private int teleportId;

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeDouble(x);
		message.writeDouble(y + (version.less(Version.V1_8) ? 1.62F : 0));
		message.writeDouble(z);
		message.writeFloat(yaw);
		message.writeFloat(pitch);

		if (version.moreOrEqual(Version.V1_8)) {
			message.writeByte(0x08);
		} else {
			message.writeBoolean(true);
		}

		if (version.moreOrEqual(Version.V1_9)) {
			message.writeVarInt(teleportId);
		}

		if (version.fromTo(Version.V1_17, Version.V1_19_3)) {
			message.writeBoolean(false); // Dismount vehicle
		}
	}

}
