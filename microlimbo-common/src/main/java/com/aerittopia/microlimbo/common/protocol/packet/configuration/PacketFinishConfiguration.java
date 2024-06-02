package com.aerittopia.microlimbo.common.protocol.packet.configuration;

import com.aerittopia.microlimbo.common.connection.PacketHandler;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.packet.PacketIn;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PacketFinishConfiguration implements PacketIn, PacketOut {
	private final PacketHandler packetHandler;

	@Inject
	public PacketFinishConfiguration(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
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
