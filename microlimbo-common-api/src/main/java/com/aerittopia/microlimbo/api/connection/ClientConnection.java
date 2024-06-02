package com.aerittopia.microlimbo.api.connection;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public interface ClientConnection {
	Channel getChannel();

	SocketAddress getRemoteAddress();

	void writePacket(Object packet);

	void sendPacket(Object packet);

	void sendPacket(Object packet, boolean close);

	void sendKeepAlive();
}
