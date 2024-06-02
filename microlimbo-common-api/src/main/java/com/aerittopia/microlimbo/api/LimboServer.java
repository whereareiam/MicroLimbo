package com.aerittopia.microlimbo.api;

import com.aerittopia.microlimbo.api.connection.Player;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface LimboServer {
	String getVersion();

	List<Player> getPlayers();

	Player getPlayer(String name);

	Player getPlayer(UUID uniqueId);
}
