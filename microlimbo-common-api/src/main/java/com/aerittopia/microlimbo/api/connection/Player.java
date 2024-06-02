package com.aerittopia.microlimbo.api.connection;

import com.aerittopia.microlimbo.api.registry.Version;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

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

	void sendMessage(Component component);

	void sendTitle(Title title);

	void sendPluginMessage(String channel, String message);
}
