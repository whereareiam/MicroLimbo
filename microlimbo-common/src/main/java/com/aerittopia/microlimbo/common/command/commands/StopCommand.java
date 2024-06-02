package com.aerittopia.microlimbo.common.command.commands;

import com.aerittopia.microlimbo.api.command.Command;

public class StopCommand implements Command {

	@Override
	public void execute() {
		System.exit(0);
	}

	@Override
	public String description() {
		return "Stop the server";
	}
}
