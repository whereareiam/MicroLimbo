package com.aerittopia.microlimbo.common.command.commands;

import com.aerittopia.microlimbo.common.LimboServer;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.command.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConnectionsCommand implements Command {
	private final LimboServer server;
	private final LoggingHelper loggingHelper;

	@Inject
	public ConnectionsCommand(LimboServer server, LoggingHelper loggingHelper) {
		this.server = server;
		this.loggingHelper = loggingHelper;
	}

	@Override
	public void execute() {
		loggingHelper.info("Connections: %d", server.getPlayers().size());
	}

	@Override
	public String description() {
		return "Display connections count";
	}
}
