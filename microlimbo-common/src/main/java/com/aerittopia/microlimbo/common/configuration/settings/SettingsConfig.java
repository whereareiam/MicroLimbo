package com.aerittopia.microlimbo.common.configuration.settings;

import com.aerittopia.microlimbo.common.configuration.settings.netty.NettySettings;
import com.google.inject.Singleton;
import lombok.Getter;

@Getter
@Singleton
public class SettingsConfig {
	private int debugLevel = 2;
	private ConnectionSettings connection = new ConnectionSettings();
	private InfoForwardingSettings forwarding = new InfoForwardingSettings();
	private NettySettings netty = new NettySettings();
	private TrafficSettings traffic = new TrafficSettings();
}
