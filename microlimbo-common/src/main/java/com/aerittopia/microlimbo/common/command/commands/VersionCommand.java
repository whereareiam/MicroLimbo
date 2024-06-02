package com.aerittopia.microlimbo.common.command.commands;

import com.aerittopia.microlimbo.api.LimboServer;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.command.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class VersionCommand implements Command {
	private final LimboServer server;
	private final LoggingHelper loggingHelper;

	@Inject
	public VersionCommand(LimboServer server, LoggingHelper loggingHelper) {
		this.server = server;
		this.loggingHelper = loggingHelper;
	}

	@Override
	public void execute() {
		loggingHelper.info("Version: %s", server.getVersion());
	}

	@Override
	public String description() {
		return "Display limbo version";
	}
}
