package com.aerittopia.microlimbo.common.protocol.packet.login;

import com.aerittopia.microlimbo.common.connection.PacketHandler;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.packet.PacketIn;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import com.google.inject.Inject;

public class PacketLoginAcknowledged implements PacketIn, PacketOut {
	private final PacketHandler packetHandler;

	@Inject
	public PacketLoginAcknowledged(PacketHandler packetHandler) {
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
