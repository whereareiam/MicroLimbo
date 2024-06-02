package com.aerittopia.microlimbo.common.configuration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigService {
	private final Path dataPath;
	private final Map<Class<?>, String[]> registeredConfigs = new HashMap<>();

	public ConfigService(Path dataPath) {
		this.dataPath = dataPath;
	}

	public <T> T registerConfig(Class<T> configClass, String path, String fileName) {
		if (registeredConfigs.containsKey(configClass) || path == null || fileName == null) return null;

		ConfigLoader<T> configLoader = new ConfigLoader<>(dataPath);
		configLoader.load(configClass, path, fileName);
		T object = configLoader.getConfig();

		registeredConfigs.put(configClass, new String[]{path, fileName});

		return object;
	}
}