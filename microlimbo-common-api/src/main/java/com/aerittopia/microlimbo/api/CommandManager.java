package com.aerittopia.microlimbo.api;

import com.aerittopia.microlimbo.api.command.Command;

import java.util.Map;

public interface CommandManager {
	void register(Command cmd, String... aliases);

	Map<String, Command> getCommands();

	Command getCommand(String name);

	void dispatchCommand(String command);
}
