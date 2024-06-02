package com.aerittopia.microlimbo.common.protocol.packet.configuration;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.connection.PacketHandler;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketIn;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
public class PacketClientInformation implements PacketIn {
	private final PacketHandler packetHandler;
	private Locale locale;

	@Inject
	public PacketClientInformation(PacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}

	@Override
	public void decode(ByteMessage message, Version version) {
		locale = Locale.of(message.readString());
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
