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

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.connection.PacketHandler;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketIn;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PacketStatusRequest implements PacketIn {
	private final PacketHandler packetHandler;

	@Inject
	public PacketStatusRequest(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}

	@Override
	public void decode(ByteMessage message, Version version) {
		// Ignored
	}

	@Override
	public void handle(ClientConnection connection) {
		packetHandler.handlePacket(this, connection);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
