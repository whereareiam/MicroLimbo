package com.aerittopia.microlimbo.api.plugin;

import com.aerittopia.microlimbo.api.plugin.base.Plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface PluginManager {
	Optional<Plugin> getPlugin(String name);

	List<Plugin> getPlugins();

	File getPluginFolder();
}
