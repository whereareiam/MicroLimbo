package com.aerittopia.microlimbo.api.plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface PluginManager {
	Optional<LimboPlugin> getPlugin(String name);

	List<LimboPlugin> getPlugins();

	File getPluginFolder();
}
