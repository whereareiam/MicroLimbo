package com.aerittopia.microlimbo.common.configuration.settings;

import lombok.Getter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Getter
public class ConnectionSettings {
	private String host = "localhost";
	private int port = 25565;
	private int readTimeout = 30000;
	private int maxPlayers = 100;

	public SocketAddress getAddress() {
		if (host == null || host.isEmpty()) {
			return new InetSocketAddress(port);
		}

		return new InetSocketAddress(host, port);
	}
}
