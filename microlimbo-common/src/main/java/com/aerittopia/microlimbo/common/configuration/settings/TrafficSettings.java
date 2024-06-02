package com.aerittopia.microlimbo.common.configuration.settings;

import lombok.Getter;

@Getter
public class TrafficSettings {
	private boolean useAdditionalHandler = true;
	private int maxPacketSize = 8192;
	private double interval = 7.0;
	private double maxPacketRate = 500.0;
}
