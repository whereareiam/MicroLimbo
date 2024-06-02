package com.aerittopia.microlimbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.LoggerFactory;

@Singleton
public class StandaloneLoggingHelper implements LoggingHelper {
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("Limbo");
	private final SettingsConfig settingsConfig;

	@Inject
	public StandaloneLoggingHelper(SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;

		LOGGER.setLevel(getLevel(settingsConfig.getDebugLevel()));
	}

	@Override
	public void info(String message, Object... objects) {
		if (settingsConfig.getDebugLevel() >= 2)
			LOGGER.info(String.format(message, objects));
	}

	@Override
	public void warning(String message, Object... objects) {
		if (settingsConfig.getDebugLevel() >= 1)
			LOGGER.warn(String.format(message, objects));
	}

	@Override
	public void severe(String message, Object... objects) {
		if (settingsConfig.getDebugLevel() >= 0)
			LOGGER.error(String.format(message, objects));
	}

	@Override
	public void debug(String message, Object... objects) {
		if (settingsConfig.getDebugLevel() >= 3)
			LOGGER.debug(String.format(message, objects));
	}

	private Level getLevel(int level) {
		return switch (level) {
			case 0 -> Level.ERROR;
			case 1 -> Level.WARN;
			case 2 -> Level.INFO;
			case 3 -> Level.DEBUG;
			default -> throw new IllegalStateException("Undefined log level: " + level);
		};
	}
}
