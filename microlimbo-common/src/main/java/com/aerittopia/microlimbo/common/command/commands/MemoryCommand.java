package com.aerittopia.microlimbo.common.command.commands;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.command.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MemoryCommand implements Command {
	private final LoggingHelper loggingHelper;

	@Inject
	public MemoryCommand(LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

	@Override
	public void execute() {
		Runtime runtime = Runtime.getRuntime();
		long mb = 1024 * 1024;
		long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		long total = runtime.totalMemory() / mb;
		long free = runtime.freeMemory() / mb;
		long max = runtime.maxMemory() / mb;

		loggingHelper.info(
				"""

						\tMemory usage:\

						\t Used: %d MB\

						\t Total: %d MB\

						\t Free: %d MB\

						\t Max: %d MB""",
				used, total, free, max
		);
	}

	@Override
	public String description() {
		return "Display memory usage";
	}
}
