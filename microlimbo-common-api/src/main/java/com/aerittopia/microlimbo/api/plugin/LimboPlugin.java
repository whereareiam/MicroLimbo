package com.aerittopia.microlimbo.api.plugin;

import com.aerittopia.microlimbo.api.LimboServer;

public interface LimboPlugin {
	PluginConfiguration getConfig();
	
	LimboServer getServer();

	void onEnable();

	void onDisable();
}
