package com.aerittopia.microlimbo.common.configuration.settings.netty;

import lombok.Getter;

@Getter
public class NettySettings {
	private boolean useEpoll = true;
	private NettyThreadsSettings threads = new NettyThreadsSettings();
}
