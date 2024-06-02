package com.aerittopia.microlimbo.common.command.management;

import com.aerittopia.microlimbo.api.CommandManager;
import com.aerittopia.microlimbo.common.command.commands.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class CommandRegistrar {
	private final Injector injector;
	private final CommandManager commandManager;

	@Inject
	public CommandRegistrar(Injector injector, CommandManager commandManager) {
		this.injector = injector;
		this.commandManager = commandManager;
	}

	public void registerCommands() {
		commandManager.register(injector.getInstance(HelpCommand.class), "help");
		commandManager.register(injector.getInstance(ConnectionsCommand.class), "conn", "connection", "connections", "players");
		commandManager.register(injector.getInstance(MemoryCommand.class), "mem", "memory");
		commandManager.register(injector.getInstance(StopCommand.class), "stop");
		commandManager.register(injector.getInstance(VersionCommand.class), "version", "ver");
	}
}
