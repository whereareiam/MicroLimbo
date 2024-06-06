package com.aerittopia.microlimbo.common;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.plugin.LimboPlugin;
import com.aerittopia.microlimbo.api.plugin.PluginConfiguration;
import com.aerittopia.microlimbo.api.plugin.PluginManager;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CommonPluginManager implements PluginManager {
	private static final Path PLUGINS_DIR = Paths.get("plugins");
	@Getter
	private final Injector injector;
	private final LoggingHelper loggingHelper;
	private final Gson gson = new Gson();
	@Getter
	private final List<LimboPlugin> plugins = new ArrayList<>();

	@Inject
	public CommonPluginManager(Injector injector, LoggingHelper loggingHelper) {
		this.injector = injector;
		this.loggingHelper = loggingHelper;

		if (!Files.exists(PLUGINS_DIR)) {
			try {
				Files.createDirectories(PLUGINS_DIR);
			} catch (IOException e) {
				loggingHelper.severe("Failed to create plugins directory", e);
			}
		}
	}

	public void loadPlugins() {
		try (Stream<Path> paths = Files.list(PLUGINS_DIR)) {
			paths.forEach(this::loadPlugin);
		} catch (IOException e) {
			loggingHelper.severe("Failed to load plugins", e);
		}
	}

	private void loadPlugin(Path pluginPath) {
		Path pluginConfigPath = pluginPath.resolve("plugin.json");
		if (!Files.exists(pluginConfigPath)) {
			pluginConfigPath = pluginPath.resolve("plugin.microlimbo.json");
		}

		if (Files.exists(pluginConfigPath)) {
			try (FileReader reader = new FileReader(pluginConfigPath.toFile())) {
				PluginConfiguration config = gson.fromJson(reader, PluginConfiguration.class);
				Class<? extends LimboPlugin> mainClass = (Class<? extends LimboPlugin>) Class.forName(config.getMain());

				LimboPlugin plugin = injector.getInstance(mainClass);
				plugins.add(plugin);
			} catch (Exception e) {
				loggingHelper.severe("Failed to load plugin", e);
			}
		}
	}

	@Override
	public Optional<LimboPlugin> getPlugin(String name) {
		return plugins.stream().filter(p -> p.getConfig().getName().equals(name)).findFirst();
	}

	@Override
	public File getPluginFolder() {
		return new File(PLUGINS_DIR.toFile().getAbsolutePath());
	}
}