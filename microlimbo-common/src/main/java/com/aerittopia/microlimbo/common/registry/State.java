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

package com.aerittopia.microlimbo.common.registry;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.InjectorProvider;
import com.aerittopia.microlimbo.common.protocol.Mapping;
import com.aerittopia.microlimbo.common.protocol.ProtocolMappings;
import com.aerittopia.microlimbo.common.protocol.packet.PacketHandshake;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketClientInformation;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketFinishConfiguration;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketRegistryData;
import com.aerittopia.microlimbo.common.protocol.packet.login.*;
import com.aerittopia.microlimbo.common.protocol.packet.play.*;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusPing;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusRequest;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.aerittopia.microlimbo.api.registry.Version.*;

@Getter
public enum State {
	HANDSHAKE(0) {
		{
			serverBound.register(() -> InjectorProvider.getInjector().getInstance(PacketHandshake.class),
					Mapping.map(0x00, Version.getMin(), Version.getMax())
			);
		}
	},
	STATUS(1) {
		{
			serverBound.register(() -> InjectorProvider.getInjector().getInstance(PacketStatusRequest.class),
					Mapping.map(0x00, Version.getMin(), Version.getMax())
			);
			serverBound.register(() -> InjectorProvider.getInjector().getInstance(PacketStatusPing.class),
					Mapping.map(0x01, Version.getMin(), Version.getMax())
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketStatusResponse.class),
					Mapping.map(0x00, Version.getMin(), Version.getMax())
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketStatusPing.class),
					Mapping.map(0x01, Version.getMin(), Version.getMax())
			);
		}
	},
	LOGIN(2) {
		{
			serverBound.register(() -> InjectorProvider.getInjector().getInstance(PacketLoginStart.class),
					Mapping.map(0x00, Version.getMin(), Version.getMax())
			);
			serverBound.register(() -> InjectorProvider.getInjector().getInstance(PacketLoginPluginResponse.class),
					Mapping.map(0x02, Version.getMin(), Version.getMax())
			);
			serverBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketLoginAcknowledged.class),
					Mapping.map(0x03, V1_20_2, V1_20_5)
			);
			clientBound.register(PacketDisconnect::new,
					Mapping.map(0x00, Version.getMin(), Version.getMax())
			);
			clientBound.register(PacketLoginSuccess::new,
					Mapping.map(0x02, Version.getMin(), Version.getMax())
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketLoginPluginRequest.class),
					Mapping.map(0x04, Version.getMin(), Version.getMax())
			);
		}
	},
	CONFIGURATION(3) {
		{
			serverBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketClientInformation.class),
					Mapping.map(0x00, V1_20_2, V1_20_5)
			);
			clientBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketPluginMessage.class),
					Mapping.map(0x00, V1_20_2, V1_20_3),
					Mapping.map(0x01, V1_20_5, V1_20_5)
			);
			clientBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketDisconnect.class),
					Mapping.map(0x01, V1_20_2, V1_20_3),
					Mapping.map(0x02, V1_20_5, V1_20_5)
			);
			clientBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketFinishConfiguration.class),
					Mapping.map(0x02, V1_20_2, V1_20_3),
					Mapping.map(0x03, V1_20_5, V1_20_5)
			);
			clientBound.register(
					PacketKeepAlive::new,
					Mapping.map(0x03, V1_20_2, V1_20_3),
					Mapping.map(0x04, V1_20_5, V1_20_5)
			);
			clientBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketRegistryData.class),
					Mapping.map(0x05, V1_20_2, V1_20_3),
					Mapping.map(0x07, V1_20_5, V1_20_5)
			);

			serverBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketPluginMessage.class),
					Mapping.map(0x01, V1_20_2, V1_20_3),
					Mapping.map(0x02, V1_20_2, V1_20_5)
			);
			serverBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketFinishConfiguration.class),
					Mapping.map(0x02, V1_20_2, V1_20_3),
					Mapping.map(0x03, V1_20_5, V1_20_5)
			);
			serverBound.register(
					PacketKeepAlive::new,
					Mapping.map(0x03, V1_20_2, V1_20_3),
					Mapping.map(0x04, V1_20_5, V1_20_5)
			);
		}
	},
	PLAY(4) {
		{
			serverBound.register(
					() -> InjectorProvider.getInjector().getInstance(PacketClientInformation.class),
					Mapping.map(0x08, V1_19_1, V1_19_1),
					Mapping.map(0x07, V1_19_3, V1_19_3),
					Mapping.map(0x08, V1_19_4, V1_20)
			);
			serverBound.register(PacketKeepAlive::new,
					Mapping.map(0x00, V1_7_2, V1_8),
					Mapping.map(0x0B, V1_9, V1_11_1),
					Mapping.map(0x0C, V1_12, V1_12),
					Mapping.map(0x0B, V1_12_1, V1_12_2),
					Mapping.map(0x0E, V1_13, V1_13_2),
					Mapping.map(0x0F, V1_14, V1_15_2),
					Mapping.map(0x10, V1_16, V1_16_4),
					Mapping.map(0x0F, V1_17, V1_18_2),
					Mapping.map(0x11, V1_19, V1_19),
					Mapping.map(0x12, V1_19_1, V1_19_1),
					Mapping.map(0x11, V1_19_3, V1_19_3),
					Mapping.map(0x12, V1_19_4, V1_20),
					Mapping.map(0x14, V1_20_2, V1_20_2),
					Mapping.map(0x15, V1_20_3, V1_20_3),
					Mapping.map(0x18, V1_20_5, V1_20_5)
			);

			clientBound.register(PacketDeclareCommands::new,
					Mapping.map(0x11, V1_13, V1_14_4),
					Mapping.map(0x12, V1_15, V1_15_2),
					Mapping.map(0x11, V1_16, V1_16_1),
					Mapping.map(0x10, V1_16_2, V1_16_4),
					Mapping.map(0x12, V1_17, V1_18_2),
					Mapping.map(0x0F, V1_19, V1_19_1),
					Mapping.map(0x0E, V1_19_3, V1_19_3),
					Mapping.map(0x10, V1_19_4, V1_20),
					Mapping.map(0x11, V1_20_2, V1_20_5)
			);
			clientBound.register(PacketJoinGame::new,
					Mapping.map(0x01, V1_7_2, V1_8),
					Mapping.map(0x23, V1_9, V1_12_2),
					Mapping.map(0x25, V1_13, V1_14_4),
					Mapping.map(0x26, V1_15, V1_15_2),
					Mapping.map(0x25, V1_16, V1_16_1),
					Mapping.map(0x24, V1_16_2, V1_16_4),
					Mapping.map(0x26, V1_17, V1_18_2),
					Mapping.map(0x23, V1_19, V1_19),
					Mapping.map(0x25, V1_19_1, V1_19_1),
					Mapping.map(0x24, V1_19_3, V1_19_3),
					Mapping.map(0x28, V1_19_4, V1_20),
					Mapping.map(0x29, V1_20_2, V1_20_3),
					Mapping.map(0x2B, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketPluginMessage.class),
					Mapping.map(0x19, V1_13, V1_13_2),
					Mapping.map(0x18, V1_14, V1_14_4),
					Mapping.map(0x19, V1_15, V1_15_2),
					Mapping.map(0x18, V1_16, V1_16_1),
					Mapping.map(0x17, V1_16_2, V1_16_4),
					Mapping.map(0x18, V1_17, V1_18_2),
					Mapping.map(0x15, V1_19, V1_19),
					Mapping.map(0x16, V1_19_1, V1_19_1),
					Mapping.map(0x15, V1_19_3, V1_19_3),
					Mapping.map(0x17, V1_19_4, V1_20),
					Mapping.map(0x18, V1_20_2, V1_20_3),
					Mapping.map(0x19, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketPlayerAbilities.class),
					Mapping.map(0x39, V1_7_2, V1_8),
					Mapping.map(0x2B, V1_9, V1_12),
					Mapping.map(0x2C, V1_12_1, V1_12_2),
					Mapping.map(0x2E, V1_13, V1_13_2),
					Mapping.map(0x31, V1_14, V1_14_4),
					Mapping.map(0x32, V1_15, V1_15_2),
					Mapping.map(0x31, V1_16, V1_16_1),
					Mapping.map(0x30, V1_16_2, V1_16_4),
					Mapping.map(0x32, V1_17, V1_18_2),
					Mapping.map(0x2F, V1_19, V1_19),
					Mapping.map(0x31, V1_19_1, V1_19_1),
					Mapping.map(0x30, V1_19_3, V1_19_3),
					Mapping.map(0x34, V1_19_4, V1_20),
					Mapping.map(0x36, V1_20_2, V1_20_3),
					Mapping.map(0x38, V1_20_5, V1_20_5)
			);
			clientBound.register(PacketPlayerPositionAndLook::new,
					Mapping.map(0x08, V1_7_2, V1_8),
					Mapping.map(0x2E, V1_9, V1_12),
					Mapping.map(0x2F, V1_12_1, V1_12_2),
					Mapping.map(0x32, V1_13, V1_13_2),
					Mapping.map(0x35, V1_14, V1_14_4),
					Mapping.map(0x36, V1_15, V1_15_2),
					Mapping.map(0x35, V1_16, V1_16_1),
					Mapping.map(0x34, V1_16_2, V1_16_4),
					Mapping.map(0x38, V1_17, V1_18_2),
					Mapping.map(0x36, V1_19, V1_19),
					Mapping.map(0x39, V1_19_1, V1_19_1),
					Mapping.map(0x38, V1_19_3, V1_19_3),
					Mapping.map(0x3C, V1_19_4, V1_20),
					Mapping.map(0x3E, V1_20_2, V1_20_3),
					Mapping.map(0x40, V1_20_5, V1_20_5)
			);
			clientBound.register(PacketKeepAlive::new,
					Mapping.map(0x00, V1_7_2, V1_8),
					Mapping.map(0x1F, V1_9, V1_12_2),
					Mapping.map(0x21, V1_13, V1_13_2),
					Mapping.map(0x20, V1_14, V1_14_4),
					Mapping.map(0x21, V1_15, V1_15_2),
					Mapping.map(0x20, V1_16, V1_16_1),
					Mapping.map(0x1F, V1_16_2, V1_16_4),
					Mapping.map(0x21, V1_17, V1_18_2),
					Mapping.map(0x1E, V1_19, V1_19),
					Mapping.map(0x20, V1_19_1, V1_19_1),
					Mapping.map(0x1F, V1_19_3, V1_19_3),
					Mapping.map(0x23, V1_19_4, V1_20),
					Mapping.map(0x24, V1_20_2, V1_20_3),
					Mapping.map(0x26, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketChatMessage.class),
					Mapping.map(0x02, V1_7_2, V1_8),
					Mapping.map(0x0F, V1_9, V1_12_2),
					Mapping.map(0x0E, V1_13, V1_14_4),
					Mapping.map(0x0F, V1_15, V1_15_2),
					Mapping.map(0x0E, V1_16, V1_16_4),
					Mapping.map(0x0F, V1_17, V1_18_2),
					Mapping.map(0x5F, V1_19, V1_19),
					Mapping.map(0x62, V1_19_1, V1_19_1),
					Mapping.map(0x60, V1_19_3, V1_19_3),
					Mapping.map(0x64, V1_19_4, V1_20),
					Mapping.map(0x67, V1_20_2, V1_20_2),
					Mapping.map(0x69, V1_20_3, V1_20_3),
					Mapping.map(0x6C, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketBossBar.class),
					Mapping.map(0x0C, V1_9, V1_14_4),
					Mapping.map(0x0D, V1_15, V1_15_2),
					Mapping.map(0x0C, V1_16, V1_16_4),
					Mapping.map(0x0D, V1_17, V1_18_2),
					Mapping.map(0x0A, V1_19, V1_19_3),
					Mapping.map(0x0B, V1_19_4, V1_20),
					Mapping.map(0x0A, V1_20_2, V1_20_5)
			);
			clientBound.register(PacketPlayerInfo::new,
					Mapping.map(0x38, V1_7_2, V1_8),
					Mapping.map(0x2D, V1_9, V1_12),
					Mapping.map(0x2E, V1_12_1, V1_12_2),
					Mapping.map(0x30, V1_13, V1_13_2),
					Mapping.map(0x33, V1_14, V1_14_4),
					Mapping.map(0x34, V1_15, V1_15_2),
					Mapping.map(0x33, V1_16, V1_16_1),
					Mapping.map(0x32, V1_16_2, V1_16_4),
					Mapping.map(0x36, V1_17, V1_18_2),
					Mapping.map(0x34, V1_19, V1_19),
					Mapping.map(0x37, V1_19_1, V1_19_1),
					Mapping.map(0x36, V1_19_3, V1_19_3),
					Mapping.map(0x3A, V1_19_4, V1_20),
					Mapping.map(0x3C, V1_20_2, V1_20_3),
					Mapping.map(0x3E, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketTitleLegacy.class),
					Mapping.map(0x45, V1_8, V1_11_1),
					Mapping.map(0x47, V1_12, V1_12),
					Mapping.map(0x48, V1_12_1, V1_12_2),
					Mapping.map(0x4B, V1_13, V1_13_2),
					Mapping.map(0x4F, V1_14, V1_14_4),
					Mapping.map(0x50, V1_15, V1_15_2),
					Mapping.map(0x4F, V1_16, V1_16_4)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketTitleSetTitle.class),
					Mapping.map(0x59, V1_17, V1_17_1),
					Mapping.map(0x5A, V1_18, V1_19),
					Mapping.map(0x5D, V1_19_1, V1_19_1),
					Mapping.map(0x5B, V1_19_3, V1_19_3),
					Mapping.map(0x5F, V1_19_4, V1_20),
					Mapping.map(0x61, V1_20_2, V1_20_2),
					Mapping.map(0x63, V1_20_3, V1_20_3),
					Mapping.map(0x65, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketTitleSetSubTitle.class),
					Mapping.map(0x57, V1_17, V1_17_1),
					Mapping.map(0x58, V1_18, V1_19),
					Mapping.map(0x5B, V1_19_1, V1_19_1),
					Mapping.map(0x59, V1_19_3, V1_19_3),
					Mapping.map(0x5D, V1_19_4, V1_20),
					Mapping.map(0x5F, V1_20_2, V1_20_2),
					Mapping.map(0x61, V1_20_3, V1_20_3),
					Mapping.map(0x63, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketTitleTimes.class),
					Mapping.map(0x5A, V1_17, V1_17_1),
					Mapping.map(0x5B, V1_18, V1_19),
					Mapping.map(0x5E, V1_19_1, V1_19_1),
					Mapping.map(0x5C, V1_19_3, V1_19_3),
					Mapping.map(0x60, V1_19_4, V1_20),
					Mapping.map(0x62, V1_20_2, V1_20_2),
					Mapping.map(0x64, V1_20_3, V1_20_3),
					Mapping.map(0x66, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketPlayerListHeader.class),
					Mapping.map(0x47, V1_8, V1_8),
					Mapping.map(0x48, V1_9, V1_9_2),
					Mapping.map(0x47, V1_9_4, V1_11_1),
					Mapping.map(0x49, V1_12, V1_12),
					Mapping.map(0x4A, V1_12_1, V1_12_2),
					Mapping.map(0x4E, V1_13, V1_13_2),
					Mapping.map(0x53, V1_14, V1_14_4),
					Mapping.map(0x54, V1_15, V1_15_2),
					Mapping.map(0x53, V1_16, V1_16_4),
					Mapping.map(0x5E, V1_17, V1_17_1),
					Mapping.map(0x5F, V1_18, V1_18_2),
					Mapping.map(0x60, V1_19, V1_19),
					Mapping.map(0x63, V1_19_1, V1_19_1),
					Mapping.map(0x61, V1_19_3, V1_19_3),
					Mapping.map(0x65, V1_19_4, V1_20),
					Mapping.map(0x68, V1_20_2, V1_20_2),
					Mapping.map(0x6A, V1_20_3, V1_20_3),
					Mapping.map(0x6D, V1_20_5, V1_20_5)
			);
			clientBound.register(PacketSpawnPosition::new,
					Mapping.map(0x4C, V1_19_3, V1_19_3),
					Mapping.map(0x50, V1_19_4, V1_20),
					Mapping.map(0x52, V1_20_2, V1_20_2),
					Mapping.map(0x54, V1_20_3, V1_20_3),
					Mapping.map(0x56, V1_20_5, V1_20_5)
			);
			clientBound.register(PacketGameEvent::new,
					Mapping.map(0x20, V1_20_3, V1_20_3),
					Mapping.map(0x22, V1_20_5, V1_20_5)
			);
			clientBound.register(() -> InjectorProvider.getInjector().getInstance(PacketEmptyChunk.class),
					Mapping.map(0x25, V1_20_3, V1_20_3),
					Mapping.map(0x27, V1_20_5, V1_20_5)
			);
		}
	};

	private static final Map<Integer, State> STATE_BY_ID = new HashMap<>();

	static {
		for (State registry : values()) {
			STATE_BY_ID.put(registry.stateId, registry);
		}
	}

	public final ProtocolMappings serverBound = new ProtocolMappings();
	public final ProtocolMappings clientBound = new ProtocolMappings();
	private final int stateId;

	State(int stateId) {
		this.stateId = stateId;
	}

	public static State getById(int stateId) {
		return STATE_BY_ID.get(stateId);
	}
}