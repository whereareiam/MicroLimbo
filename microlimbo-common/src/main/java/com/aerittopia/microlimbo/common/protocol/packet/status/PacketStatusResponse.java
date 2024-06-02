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

package com.aerittopia.microlimbo.common.protocol.packet.status;

import com.aerittopia.microlimbo.api.LimboServer;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.configuration.customization.CustomizationConfig;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PacketStatusResponse implements PacketOut {
	private static final String TEMPLATE = "{ \"version\": { \"name\": \"%s\", \"protocol\": %d }, \"players\": { \"max\": %d, \"online\": %d, \"sample\": [] }, \"description\": %s }";
	private final LimboServer server;
	private final SettingsConfig settingsConfig;
	private final CustomizationConfig customizationConfig;

	@Inject
	public PacketStatusResponse(LimboServer server, SettingsConfig settingsConfig, CustomizationConfig customizationConfig) {
		this.server = server;
		this.settingsConfig = settingsConfig;
		this.customizationConfig = customizationConfig;
	}

	@Override
	public void encode(ByteMessage message, Version version) {
		int protocol;
		int staticProtocol = customizationConfig.getMotd().getProtocol();

		if (staticProtocol > 0) {
			protocol = staticProtocol;
		} else {
			protocol = settingsConfig.getForwarding().isNone()
					? version.getProtocolNumber()
					: Version.getMax().getProtocolNumber();
		}

		String ver = customizationConfig.getMotd().getVersion();
		String desc = customizationConfig.getMotd().getMotd();
		int online = server.getPlayers().size();

		message.writeString(getResponseJson(ver, protocol,
				settingsConfig.getConnection().getMaxPlayers(),
				online, desc));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	private String getResponseJson(String version, int protocol, int maxPlayers, int online, String description) {
		return String.format(TEMPLATE, version, protocol, maxPlayers, online, description);
	}
}
