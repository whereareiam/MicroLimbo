package com.aerittopia.microlimbo;

import com.aerittopia.microlimbo.api.CommandManager;
import com.aerittopia.microlimbo.api.LimboServer;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.common.command.management.CommonCommandManager;
import com.google.inject.AbstractModule;

public class StandaloneLimboConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(LoggingHelper.class).to(StandaloneLoggingHelper.class);
		bind(CommandManager.class).to(CommonCommandManager.class);

		bind(LimboServer.class).to(com.aerittopia.microlimbo.common.LimboServer.class);
	}
}
