package com.aerittopia.microlimbo.common.command.management;

import com.aerittopia.microlimbo.api.CommandManager;
import com.aerittopia.microlimbo.api.command.Command;
import com.google.inject.Singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Singleton
public final class CommonCommandManager extends Thread implements CommandManager {
	private static final Map<String, Command> commands = new HashMap<>();

	@Override
	public void run() {
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				String command = scanner.nextLine().trim();

				Command handler = getCommand(command);
				if (handler == null) return;

				handler.execute();
			}
		}
	}

	@Override
	public Map<String, Command> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	@Override
	public Command getCommand(String name) {
		return commands.get(name.toLowerCase());
	}

	@Override
	public void register(Command cmd, String... aliases) {
		for (String alias : aliases) {
			commands.put(alias.toLowerCase(), cmd);
		}
	}
}
