package com.aerittopia.microlimbo.api.plugin;

import lombok.Getter;

@Getter
public abstract class LimboPlugin {
	private final PluginConfiguration config;

	protected LimboPlugin(PluginConfiguration config) {
		this.config = config;
	}

	public abstract void onEnable();

	public abstract void onDisable();
}
