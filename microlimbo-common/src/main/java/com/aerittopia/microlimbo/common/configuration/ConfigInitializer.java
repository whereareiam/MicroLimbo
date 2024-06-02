package com.aerittopia.microlimbo.common.configuration;

import com.aerittopia.microlimbo.common.configuration.customization.CustomizationConfig;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.google.inject.AbstractModule;

import java.nio.file.Path;

public class ConfigInitializer extends AbstractModule {
	private final Path dataPath;

	public ConfigInitializer(Path dataPath) {
		this.dataPath = dataPath;
	}

	@Override
	protected void configure() {
		ConfigService configService = new ConfigService(dataPath);

		bind(SettingsConfig.class).toInstance(configService.registerConfig(SettingsConfig.class, "", "settings.json"));
		bind(CustomizationConfig.class).toInstance(configService.registerConfig(CustomizationConfig.class, "", "customization.json"));
	}
}
