package com.aerittopia.microlimbo.api;

public interface LoggingHelper {
	void info(String message, Object... objects);

	void warning(String message, Object... objects);

	void severe(String message, Object... objects);

	void debug(String message, Object... objects);
}
