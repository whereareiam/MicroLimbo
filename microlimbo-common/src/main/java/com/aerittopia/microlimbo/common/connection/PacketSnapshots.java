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

import com.aerittopia.microlimbo.api.model.component.Title;
import com.aerittopia.microlimbo.api.model.dimension.DimensionInfo;
import com.aerittopia.microlimbo.common.configuration.customization.*;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.protocol.PacketSnapshot;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketFinishConfiguration;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketRegistryData;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketLoginSuccess;
import com.aerittopia.microlimbo.common.protocol.packet.play.*;
import com.aerittopia.microlimbo.common.registry.ConstantsRegistry;
import com.aerittopia.microlimbo.common.registry.DimensionRegistry;
import com.aerittopia.microlimbo.common.util.NBTMessageUtil;
import com.aerittopia.microlimbo.common.util.UniqueIdUtil;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public final class PacketSnapshots {
	public static PacketSnapshot PACKET_LOGIN_SUCCESS;
	public static PacketSnapshot PACKET_JOIN_GAME;
	public static PacketSnapshot PACKET_SPAWN_POSITION;
	public static PacketSnapshot PACKET_PLUGIN_MESSAGE;
	public static PacketSnapshot PACKET_PLAYER_ABILITIES;
	public static PacketSnapshot PACKET_PLAYER_INFO;
	public static PacketSnapshot PACKET_DECLARE_COMMANDS;
	public static PacketSnapshot PACKET_JOIN_MESSAGE;
	public static PacketSnapshot PACKET_BOSS_BAR;
	public static PacketSnapshot PACKET_HEADER_AND_FOOTER;
	public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK_LEGACY;

	// For 1.19 we need to spawn player outside the world to avoid stuck in terrain loading
	public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK;
	public static PacketSnapshot PACKET_TITLE_TITLE;
	public static PacketSnapshot PACKET_TITLE_SUBTITLE;
	public static PacketSnapshot PACKET_TITLE_TIMES;
	public static PacketSnapshot PACKET_TITLE_LEGACY_TITLE;
	public static PacketSnapshot PACKET_TITLE_LEGACY_SUBTITLE;
	public static PacketSnapshot PACKET_TITLE_LEGACY_TIMES;
	public static PacketSnapshot PACKET_REGISTRY_DATA;
	public static List<PacketSnapshot> PACKETS_REGISTRY_DATA;
	public static PacketSnapshot PACKET_FINISH_CONFIGURATION;
	public static List<PacketSnapshot> PACKETS_EMPTY_CHUNKS;
	public static PacketSnapshot PACKET_START_WAITING_CHUNKS;

	private final Injector injector;
	private final SettingsConfig settingsConfig;
	private final CustomizationConfig customizationConfig;
	private final DimensionRegistry dimensionRegistry;

	@Inject
	public PacketSnapshots(Injector injector, SettingsConfig settingsConfig, CustomizationConfig customizationConfig, DimensionRegistry dimensionRegistry) {
		this.injector = injector;
		this.settingsConfig = settingsConfig;
		this.customizationConfig = customizationConfig;
		this.dimensionRegistry = dimensionRegistry;
	}

	public void initPackets() {
		final String username = customizationConfig.getBranding().getServerName();
		final UUID uniqueId = UniqueIdUtil.getOfflineModeUuid(username);

		initLogingSuccessPacket(username, uniqueId);
		initJoinGamePacket();
		initPlayerAbilitiesPacket();
		initPlayerPositionAndLookPackets();
		initSpawnPositionPacket();
		initDeclareCommandsPacket();
		initPlayerInfoPacket(username, uniqueId);

		// Customization
		initPlayerListHeaderPacket();
		initPluginMessagePacket();
		initChatMessagePacket();
		initBossBarPacket();
		initTitlePackets();

		initRegistryDataPacket();
		initFinishConfigurationPacket();
		initGameEventPacket();
		initEmptyChunksPackets();
	}

	private void initEmptyChunksPackets() {
		int chunkXOffset = 0; // Default x position is 0
		int chunkZOffset = 0; // Default z position is 0
		int chunkEdgeSize = 1; // TODO Make configurable?

		List<PacketSnapshot> emptyChunks = new ArrayList<>();
		// Make multiple chunks for edges
		for (int chunkX = chunkXOffset - chunkEdgeSize; chunkX <= chunkXOffset + chunkEdgeSize; ++chunkX) {
			for (int chunkZ = chunkZOffset - chunkEdgeSize; chunkZ <= chunkZOffset + chunkEdgeSize; ++chunkZ) {
				PacketEmptyChunk packetEmptyChunk = new PacketEmptyChunk();
				packetEmptyChunk.setX(chunkX);
				packetEmptyChunk.setZ(chunkZ);

				emptyChunks.add(PacketSnapshot.of(packetEmptyChunk));
			}
		}
		PACKETS_EMPTY_CHUNKS = emptyChunks;
	}

	private void initGameEventPacket() {
		PACKET_START_WAITING_CHUNKS = PacketSnapshot.of(new PacketGameEvent((byte) 13, 0));
	}

	private void initFinishConfigurationPacket() {
		PACKET_FINISH_CONFIGURATION = PacketSnapshot.of(injector.getInstance(PacketFinishConfiguration.class));
	}

	private void initRegistryDataPacket() {
		PACKET_REGISTRY_DATA = PacketSnapshot.of(new PacketRegistryData(dimensionRegistry, null));

		DimensionInfo dimensionInfo1_20_5 = dimensionRegistry.getDimension_Info_1_20_5();
		List<PacketSnapshot> packetRegistries = new ArrayList<>();
		CompoundBinaryTag dimensionTag = dimensionInfo1_20_5.getData();
		for (String registryType : dimensionTag.keySet()) {
			CompoundBinaryTag compoundRegistryType = dimensionTag.getCompound(registryType);

			PacketRegistryData registryData = new PacketRegistryData(dimensionRegistry, null);

			ListBinaryTag values = compoundRegistryType.getList("value");
			registryData.setMetadataWriter((message, version) -> {
				message.writeString(registryType);

				message.writeVarInt(values.size());
				for (BinaryTag entry : values) {
					CompoundBinaryTag entryTag = (CompoundBinaryTag) entry;

					String name = entryTag.getString("name");
					CompoundBinaryTag element = entryTag.getCompound("element");

					message.writeString(name);
					message.writeBoolean(true);
					message.writeNamelessCompoundTag(element);
				}
			});

			packetRegistries.add(PacketSnapshot.of(registryData));
		}

		PACKETS_REGISTRY_DATA = packetRegistries;
	}

	private void initTitlePackets() {
		TitleCustomization titleConfig = customizationConfig.getTitle();
		if (titleConfig.isEnabled()) {
			Title title = titleConfig.getTitle();

			PacketTitleSetTitle packetTitle = new PacketTitleSetTitle();
			PacketTitleSetSubTitle packetSubtitle = new PacketTitleSetSubTitle();
			PacketTitleTimes packetTimes = new PacketTitleTimes();

			PacketTitleLegacy legacyTitle = new PacketTitleLegacy();
			PacketTitleLegacy legacySubtitle = new PacketTitleLegacy();
			PacketTitleLegacy legacyTimes = new PacketTitleLegacy();

			packetTitle.setTitle(title.getTitle());
			packetSubtitle.setSubtitle(title.getSubtitle());
			packetTimes.setFadeIn(title.getFadeIn());
			packetTimes.setStay(title.getStay());
			packetTimes.setFadeOut(title.getFadeOut());

			legacyTitle.setTitle(title);
			legacyTitle.setAction(PacketTitleLegacy.Action.SET_TITLE);

			legacySubtitle.setTitle(title);
			legacySubtitle.setAction(PacketTitleLegacy.Action.SET_SUBTITLE);

			legacyTimes.setTitle(title);
			legacyTimes.setAction(PacketTitleLegacy.Action.SET_TIMES_AND_DISPLAY);

			PACKET_TITLE_TITLE = PacketSnapshot.of(packetTitle);
			PACKET_TITLE_SUBTITLE = PacketSnapshot.of(packetSubtitle);
			PACKET_TITLE_TIMES = PacketSnapshot.of(packetTimes);

			PACKET_TITLE_LEGACY_TITLE = PacketSnapshot.of(legacyTitle);
			PACKET_TITLE_LEGACY_SUBTITLE = PacketSnapshot.of(legacySubtitle);
			PACKET_TITLE_LEGACY_TIMES = PacketSnapshot.of(legacyTimes);
		}
	}

	private void initBossBarPacket() {
		BossBarCustomization bossBarCustomization = customizationConfig.getBossBar();
		if (bossBarCustomization.isEnabled()) {
			PacketBossBar bossBar = new PacketBossBar();
			bossBar.setBossBar(bossBarCustomization.getBossBar());
			bossBar.setUuid(UUID.randomUUID());
			PACKET_BOSS_BAR = PacketSnapshot.of(bossBar);
		}
	}

	private void initChatMessagePacket() {
		ChatCustomization chat = customizationConfig.getChat();
		if (chat.isSendWelcomeMessage()) {
			PacketChatMessage joinMessage = new PacketChatMessage();
			joinMessage.setMessage(NBTMessageUtil.create(String.join("\n", chat.getWelcomeMessage())));
			joinMessage.setPosition(PacketChatMessage.PositionLegacy.SYSTEM_MESSAGE);
			joinMessage.setSender(UUID.randomUUID());
			PACKET_JOIN_MESSAGE = PacketSnapshot.of(joinMessage);
		}
	}

	private void initPluginMessagePacket() {
		BrandingCustomization branding = customizationConfig.getBranding();
		if (branding.isUseCustomBranding()) {
			PacketPluginMessage pluginMessage = new PacketPluginMessage();
			pluginMessage.setChannel(ConstantsRegistry.BRAND_CHANNEL);
			pluginMessage.setMessage(branding.getServerName());
			PACKET_PLUGIN_MESSAGE = PacketSnapshot.of(pluginMessage);
		}
	}

	private void initPlayerListHeaderPacket() {
		TabCustomization tab = customizationConfig.getTab();
		if (tab.isUseCustomTab()) {
			PacketPlayerListHeader header = new PacketPlayerListHeader();
			header.setHeader(NBTMessageUtil.create(String.join("\n", tab.getHeader())));
			header.setFooter(NBTMessageUtil.create(String.join("\n", tab.getFooter())));

			PACKET_HEADER_AND_FOOTER = PacketSnapshot.of(header);
		}
	}

	private void initPlayerInfoPacket(String username, UUID uniqueId) {
		PACKET_PLAYER_INFO = PacketSnapshot.of(new PacketPlayerInfo(
				customizationConfig.getGamemode().getId(),
				username,
				uniqueId
		));
	}

	private void initDeclareCommandsPacket() {
		PACKET_DECLARE_COMMANDS = PacketSnapshot.of(new PacketDeclareCommands());
	}

	private void initSpawnPositionPacket() {
		PACKET_SPAWN_POSITION = PacketSnapshot.of(new PacketSpawnPosition(0, 64, 0));
	}

	private void initPlayerPositionAndLookPackets() {
		int teleportId = ThreadLocalRandom.current().nextInt();

		PACKET_PLAYER_POS_AND_LOOK_LEGACY = PacketSnapshot.of(new PacketPlayerPositionAndLook(0, 64, 0, 0, 0, teleportId));
		PACKET_PLAYER_POS_AND_LOOK = PacketSnapshot.of(new PacketPlayerPositionAndLook(0, 400, 0, 0, 0, teleportId));
	}

	private void initPlayerAbilitiesPacket() {
		PACKET_PLAYER_ABILITIES = PacketSnapshot.of(new PacketPlayerAbilities());
	}

	private void initJoinGamePacket() {
		String worldName = "minecraft:" + customizationConfig.getDimension().name().toLowerCase();

		PacketJoinGame joinGame = new PacketJoinGame(
				0,
				customizationConfig.getGamemode().getId(),
				settingsConfig.getConnection().getMaxPlayers(),
				0,
				true,
				true,
				false,
				false,
				false,
				false,
				dimensionRegistry,
				worldName,
				new String[]{worldName}
		);

		PACKET_JOIN_GAME = PacketSnapshot.of(joinGame);
	}

	private void initLogingSuccessPacket(String username, UUID uniqueId) {
		PacketLoginSuccess loginSuccess = new PacketLoginSuccess(
				uniqueId,
				username
		);

		PACKET_LOGIN_SUCCESS = PacketSnapshot.of(loginSuccess);
	}
}
