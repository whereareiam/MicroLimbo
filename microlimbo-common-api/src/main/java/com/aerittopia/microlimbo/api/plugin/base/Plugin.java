package com.aerittopia.microlimbo.api.plugin.base;

import com.aerittopia.microlimbo.api.LimboServer;
import com.aerittopia.microlimbo.api.plugin.PluginConfiguration;

import java.nio.file.Path;

public interface Plugin {
	PluginConfiguration getConfig();

	String getName();

	LimboServer getServer();

	Path getDataFolder();

	void onEnable();

	void onDisable();
}
