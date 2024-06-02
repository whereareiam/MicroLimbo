/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aerittopia.microlimbo.common.connection.client;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.common.LimboServer;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.configuration.settings.TrafficSettings;
import com.aerittopia.microlimbo.common.connection.PacketDecoder;
import com.aerittopia.microlimbo.common.connection.PacketEncoder;
import com.aerittopia.microlimbo.common.connection.pipeline.ChannelTrafficHandler;
import com.aerittopia.microlimbo.common.connection.pipeline.VarIntFrameDecoder;
import com.aerittopia.microlimbo.common.connection.pipeline.VarIntLengthEncoder;
import com.aerittopia.microlimbo.common.connection.player.LimboPlayer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

@Singleton
public class ClientChannelInitializer extends ChannelInitializer<Channel> {
	private final Injector injector;
	private final LimboServer server;
	private final EventManager eventManager;
	private final SettingsConfig settingsConfig;
	private final LoggingHelper loggingHelper;

	@Inject
	public ClientChannelInitializer(Injector injector, LimboServer server, EventManager eventManager, SettingsConfig settingsConfig, LoggingHelper loggingHelper) {
		this.injector = injector;
		this.server = server;
		this.eventManager = eventManager;
		this.settingsConfig = settingsConfig;
		this.loggingHelper = loggingHelper;
	}

	@Override
	protected void initChannel(Channel channel) {
		ChannelPipeline pipeline = channel.pipeline();

		PacketDecoder decoder = injector.getInstance(PacketDecoder.class);
		PacketEncoder encoder = injector.getInstance(PacketEncoder.class);
		LimboPlayer limboPlayer = new LimboPlayer(
				new ClientConnection(
						server,
						eventManager,
						loggingHelper,
						decoder,
						encoder,
						channel
				)
		);

		limboPlayer.getClientConnection().setLimboPlayer(limboPlayer);
		decoder.setLimboPlayer(limboPlayer);
		encoder.setLimboPlayer(limboPlayer);

		pipeline.addLast("timeout", new ReadTimeoutHandler(settingsConfig.getConnection().getReadTimeout(), TimeUnit.MILLISECONDS));
		pipeline.addLast("frame_decoder", injector.getInstance(VarIntFrameDecoder.class));
		pipeline.addLast("frame_encoder", new VarIntLengthEncoder());

		TrafficSettings traffic = settingsConfig.getTraffic();
		if (traffic.isUseAdditionalHandler()) {
			pipeline.addLast("traffic_limit", new ChannelTrafficHandler(
					loggingHelper,
					traffic.getMaxPacketSize(),
					traffic.getInterval(),
					traffic.getMaxPacketRate()
			));
		}

		pipeline.addLast("decoder", decoder);
		pipeline.addLast("encoder", encoder);
		pipeline.addLast("handler", limboPlayer.getClientConnection());
	}
}