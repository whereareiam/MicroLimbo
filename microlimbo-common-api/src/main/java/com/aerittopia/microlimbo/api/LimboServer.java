package com.aerittopia.microlimbo.api;

import com.aerittopia.microlimbo.api.connection.Player;
import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.api.plugin.PluginManager;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface LimboServer {
	PluginManager getPluginManager();

	EventManager getEventManager();

	String getVersion();

	List<Player> getPlayers();

	Player getPlayer(String name);

	Player getPlayer(UUID uniqueId);

	int getMaxPlayers();
}
