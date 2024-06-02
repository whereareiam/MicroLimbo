package com.aerittopia.microlimbo.common.configuration.settings.netty;

import lombok.Getter;

@Getter
public class NettyThreadsSettings {
	private int bossThreads = 1;
	private int workerThreads = 4;
}
