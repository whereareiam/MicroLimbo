package com.aerittopia.microlimbo.api.connection;

import com.aerittopia.microlimbo.api.registry.Version;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("unused")
public interface Player {
	String getUsername();

	UUID getUniqueId();

	Version getClientVersion();

	ClientConnection getClientConnection();

	Locale getLocale();

	void disconnect();

	void disconnect(Component reason);
}
