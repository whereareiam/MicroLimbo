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

import java.util.EnumSet;
import java.util.UUID;

/**
 * This packet was very simplified and using only for ADD_PLAYER action
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayerInfo implements PacketOut {
	private int gameMode;
	private String username;
	private UUID uniqueId;

	@Override
	public void encode(ByteMessage message, Version version) {
		if (version.less(Version.V1_8)) {
			message.writeString(username);
			message.writeBoolean(true); // Is online
			message.writeShort(0);
		} else {
			if (version.moreOrEqual(Version.V1_19_3)) {
				EnumSet<Action> actions = EnumSet.noneOf(Action.class);
				actions.add(Action.ADD_PLAYER);
				actions.add(Action.UPDATE_LISTED);
				actions.add(Action.UPDATE_GAMEMODE);
				message.writeEnumSet(actions, Action.class);

				message.writeVarInt(1); // Array length (1 element)
				message.writeUniqueId(uniqueId); // UUID
				message.writeString(username); //Username
				message.writeVarInt(0); //Properties (0 is empty)

				message.writeBoolean(true); //Update listed
				message.writeVarInt(gameMode); //Gamemode
				return;
			}

			message.writeVarInt(0); // Add player action
			message.writeVarInt(1);
			message.writeUniqueId(uniqueId);
			message.writeString(username);
			message.writeVarInt(0);
			message.writeVarInt(gameMode);
			message.writeVarInt(60);
			message.writeBoolean(false);

			if (version.moreOrEqual(Version.V1_19)) {
				message.writeBoolean(false);
			}
		}
	}

	public enum Action {
		ADD_PLAYER,
		INITIALIZE_CHAT,
		UPDATE_GAMEMODE,
		UPDATE_LISTED,
		UPDATE_LATENCY,
		UPDATE_DISPLAY_NAME
	}
}
