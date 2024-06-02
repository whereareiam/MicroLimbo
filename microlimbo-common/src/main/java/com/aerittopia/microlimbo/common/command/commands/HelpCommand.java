package com.aerittopia.microlimbo.common.command.commands;

import com.aerittopia.microlimbo.api.CommandManager;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.command.Command;
import com.google.inject.Inject;

import java.util.Map;

public class HelpCommand implements Command {
	private final CommandManager commandManager;
	private final LoggingHelper loggingHelper;

	@Inject
	public HelpCommand(CommandManager commandManager, LoggingHelper loggingHelper) {
		this.commandManager = commandManager;
		this.loggingHelper = loggingHelper;
	}

	@Override
	public void execute() {
		Map<String, Command> commands = commandManager.getCommands();

		StringBuilder message = new StringBuilder("\n\tAvailable commands:");

		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			message.append("\n\t ").append(entry.getKey()).append(" - ").append(entry.getValue().description());
		}

		loggingHelper.info(message.toString());
	}

	@Override
	public String description() {
		return "Show this message";
	}
}
