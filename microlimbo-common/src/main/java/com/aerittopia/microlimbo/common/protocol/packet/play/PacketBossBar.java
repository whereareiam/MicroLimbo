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

import com.aerittopia.microlimbo.api.model.component.bossbar.BossBar;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.Setter;

import java.util.UUID;

/**
 * Packet for 1.9+
 */
@Setter
public class PacketBossBar implements PacketOut {

	private UUID uuid;
	private BossBar bossBar;
	private int flags;

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeUniqueId(uuid);
		message.writeVarInt(0); // Create bossbar
		message.writeNBTMessage(bossBar.getMessage(), version);
		message.writeFloat(bossBar.getHealth());
		message.writeVarInt(bossBar.getColor().getIndex());
		message.writeVarInt(bossBar.getDivision().getIndex());
		message.writeByte(flags);
	}
}
