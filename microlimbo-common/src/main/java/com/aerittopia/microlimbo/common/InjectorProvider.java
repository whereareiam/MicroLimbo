package com.aerittopia.microlimbo.common;

import com.aerittopia.microlimbo.common.configuration.ConfigInitializer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Setter;

import java.nio.file.Paths;

public class InjectorProvider {
	@Setter
	private static Injector injector;

	public static Injector getInjector() {
		if (injector == null) {
			injector = Guice.createInjector(new CommonLimboConfiguration(), new ConfigInitializer(Paths.get("./")));
		}

		return injector;
	}
}
