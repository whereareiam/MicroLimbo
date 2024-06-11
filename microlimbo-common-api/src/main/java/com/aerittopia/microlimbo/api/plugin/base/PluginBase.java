package com.aerittopia.microlimbo.api.plugin.base;

import java.nio.file.Path;

public abstract class PluginBase implements Plugin {
	@Override
	public String getName() {
		return getConfig().getName();
	}

	@Override
	public Path getDataFolder() {
		return getServer().getPluginManager().getPluginFolder().toPath().resolve(getName());
	}
}
