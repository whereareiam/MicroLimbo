package com.aerittopia.microlimbo.api.plugin;

public abstract class LimboPlugin {
	private final PluginConfiguration config;

	protected LimboPlugin(PluginConfiguration config) {
		this.config = config;
	}

	public abstract void onEnable();

	public abstract void onDisable();

	public PluginConfiguration getConfig() {
		return config;
	}
}
