package com.aerittopia.microlimbo.common;

import com.aerittopia.microlimbo.api.event.EventManager;
import com.google.inject.AbstractModule;

public class CommonLimboConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(EventManager.class).to(com.aerittopia.microlimbo.common.EventManager.class);
	}
}
