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
import com.aerittopia.microlimbo.common.registry.DimensionRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketJoinGame implements PacketOut {
	private int entityId;
	private int gameMode = 2;
	private int previousGameMode = -1;
	private int maxPlayers;
	private int viewDistance = 2;
	private long hashedSeed;
	private boolean isHardcore = false;
	private boolean reducedDebugInfo;
	private boolean enableRespawnScreen;
	private boolean isDebug;
	private boolean isFlat;
	private boolean limitedCrafting;
	private boolean secureProfile;
	private DimensionRegistry dimensionRegistry;
	private String worldName;
	private String[] worldNames;

	public PacketJoinGame(int entityId, int gameMode, int maxPlayers, int hashedSeed, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean isDebug, boolean isFlat, boolean limitedCrafting, boolean secureProfile, DimensionRegistry dimensionRegistry, String worldName, String[] worldNames) {
		this.entityId = entityId;
		this.gameMode = gameMode;
		this.maxPlayers = maxPlayers;
		this.hashedSeed = hashedSeed;
		this.reducedDebugInfo = reducedDebugInfo;
		this.enableRespawnScreen = enableRespawnScreen;
		this.isDebug = isDebug;
		this.isFlat = isFlat;
		this.limitedCrafting = limitedCrafting;
		this.secureProfile = secureProfile;
		this.dimensionRegistry = dimensionRegistry;
		this.worldName = worldName;
		this.worldNames = worldNames;
	}

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeInt(entityId);

		if (version.fromTo(Version.V1_7_2, Version.V1_7_6)) {
			message.writeByte(gameMode == 3 ? 1 : gameMode);
			message.writeByte(dimensionRegistry.getDefaultDimension_Info_1_16().getId());
			message.writeByte(0); // Difficulty
			message.writeByte(maxPlayers);
			message.writeString("flat"); // Level type
		}

		if (version.fromTo(Version.V1_8, Version.V1_9)) {
			message.writeByte(gameMode);
			message.writeByte(dimensionRegistry.getDefaultDimension_Info_1_16().getId());
			message.writeByte(0); // Difficulty
			message.writeByte(maxPlayers);
			message.writeString("flat"); // Level type
			message.writeBoolean(reducedDebugInfo);
		}

		if (version.fromTo(Version.V1_9_1, Version.V1_13_2)) {
			message.writeByte(gameMode);
			message.writeInt(dimensionRegistry.getDefaultDimension_Info_1_16().getId());
			message.writeByte(0); // Difficulty
			message.writeByte(maxPlayers);
			message.writeString("flat"); // Level type
			message.writeBoolean(reducedDebugInfo);
		}

		if (version.fromTo(Version.V1_14, Version.V1_14_4)) {
			message.writeByte(gameMode);
			message.writeInt(dimensionRegistry.getDefaultDimension_Info_1_16().getId());
			message.writeByte(maxPlayers);
			message.writeString("flat"); // Level type
			message.writeVarInt(viewDistance);
			message.writeBoolean(reducedDebugInfo);
		}

		if (version.fromTo(Version.V1_15, Version.V1_15_2)) {
			message.writeByte(gameMode);
			message.writeInt(dimensionRegistry.getDefaultDimension_Info_1_16().getId());
			message.writeLong(hashedSeed);
			message.writeByte(maxPlayers);
			message.writeString("flat"); // Level type
			message.writeVarInt(viewDistance);
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
		}

		if (version.fromTo(Version.V1_16, Version.V1_16_1)) {
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeStringsArray(worldNames);
			message.writeCompoundTag(dimensionRegistry.getOldCodec());
			message.writeString(dimensionRegistry.getDefaultDimension_Info_1_16().getDimension().name().toLowerCase());
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeByte(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
		}

		if (version.fromTo(Version.V1_16_2, Version.V1_17_1)) {
			message.writeBoolean(isHardcore);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeStringsArray(worldNames);
			message.writeCompoundTag(dimensionRegistry.getCodec_1_16());
			message.writeCompoundTag(dimensionRegistry.getDefaultDimension_Info_1_16().getData());
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
		}

		if (version.fromTo(Version.V1_18, Version.V1_18_2)) {
			message.writeBoolean(isHardcore);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeStringsArray(worldNames);
			if (version.moreOrEqual(Version.V1_18_2)) {
				message.writeCompoundTag(dimensionRegistry.getCodec_1_18_2());
				message.writeCompoundTag(dimensionRegistry.getDefaultDimension_Info_1_18_2().getData());
			} else {
				message.writeCompoundTag(dimensionRegistry.getCodec_1_16());
				message.writeCompoundTag(dimensionRegistry.getDefaultDimension_Info_1_16().getData());
			}
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeVarInt(viewDistance); // Simulation Distance
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
		}

		if (version.fromTo(Version.V1_19, Version.V1_19_4)) {
			message.writeBoolean(isHardcore);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeStringsArray(worldNames);
			if (version.moreOrEqual(Version.V1_19_1)) {
				if (version.moreOrEqual(Version.V1_19_4)) {
					message.writeCompoundTag(dimensionRegistry.getCodec_1_19_4());
				} else {
					message.writeCompoundTag(dimensionRegistry.getCodec_1_19_1());
				}
			} else {
				message.writeCompoundTag(dimensionRegistry.getCodec_1_19());
			}
			message.writeString(worldName); // World type
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeVarInt(viewDistance); // Simulation Distance
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
			message.writeBoolean(false);
		}

		if (version.equals(Version.V1_20)) {
			message.writeBoolean(isHardcore);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeStringsArray(worldNames);
			message.writeCompoundTag(dimensionRegistry.getCodec_1_20());
			message.writeString(worldName); // World type
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeVarInt(viewDistance); // Simulation Distance
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
			message.writeBoolean(false);
			message.writeVarInt(0);
		}

		if (version.fromTo(Version.V1_20_2, Version.V1_20_3)) {
			message.writeBoolean(isHardcore);
			message.writeStringsArray(worldNames);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeVarInt(viewDistance); // Simulation Distance
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(limitedCrafting);
			message.writeString(worldName);
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
			message.writeBoolean(false);
			message.writeVarInt(0);
		}

		if (version.moreOrEqual(Version.V1_20_5)) {
			message.writeBoolean(isHardcore);
			message.writeStringsArray(worldNames);
			message.writeVarInt(maxPlayers);
			message.writeVarInt(viewDistance);
			message.writeVarInt(viewDistance); // Simulation Distance
			message.writeBoolean(reducedDebugInfo);
			message.writeBoolean(enableRespawnScreen);
			message.writeBoolean(limitedCrafting);
			message.writeVarInt(dimensionRegistry.getDimension_Info_1_20_5().getId());
			message.writeString(worldName);
			message.writeLong(hashedSeed);
			message.writeByte(gameMode);
			message.writeByte(previousGameMode);
			message.writeBoolean(isDebug);
			message.writeBoolean(isFlat);
			message.writeBoolean(false);
			message.writeVarInt(0);
			message.writeBoolean(secureProfile);
		}
	}
}
