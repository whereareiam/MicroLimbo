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

package com.aerittopia.microlimbo.common;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.connection.Player;
import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.api.plugin.PluginManager;
import com.aerittopia.microlimbo.common.command.management.CommandRegistrar;
import com.aerittopia.microlimbo.common.command.management.CommonCommandManager;
import com.aerittopia.microlimbo.common.configuration.customization.CustomizationConfig;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.configuration.settings.netty.NettySettings;
import com.aerittopia.microlimbo.common.connection.PacketSnapshots;
import com.aerittopia.microlimbo.common.connection.client.ClientChannelInitializer;
import com.aerittopia.microlimbo.common.registry.DimensionRegistry;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Singleton
public final class LimboServer implements com.aerittopia.microlimbo.api.LimboServer {
	private final Injector injector;
	private final SettingsConfig settingsConfig;
	private final LoggingHelper loggingHelper;

	private final List<Player> players = new ArrayList<>();
	private ScheduledFuture<?> keepAliveTask;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	@Inject
	public LimboServer(Injector injector, SettingsConfig settingsConfig, LoggingHelper loggingHelper) {
		this.injector = injector;
		this.settingsConfig = settingsConfig;
		this.loggingHelper = loggingHelper;
	}

	public void start() {
		loggingHelper.info("Starting server...");

		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

		// Component initialization
		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(CommonCommandManager.class).start();
		injector.getInstance(CommonPluginManager.class).loadPlugins();

		injector.getInstance(DimensionRegistry.class).load(
				injector.getInstance(CustomizationConfig.class).getDimension()
		);
		injector.getInstance(PacketSnapshots.class).initPackets();

		startBootstrap();

		keepAliveTask = workerGroup.scheduleAtFixedRate(() -> players.forEach(p -> p.getClientConnection().sendKeepAlive()), 0L, 5L, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "MicroLimbo shutdown thread"));

		loggingHelper.info("Server started on %s", settingsConfig.getConnection().getAddress());

		System.gc();
	}

	private void startBootstrap() {
		Class<? extends ServerChannel> channelClass;
		NettySettings nettySettings = settingsConfig.getNetty();

		if (nettySettings.isUseEpoll() && Epoll.isAvailable()) {
			bossGroup = new EpollEventLoopGroup(nettySettings.getThreads().getBossThreads());
			workerGroup = new EpollEventLoopGroup(nettySettings.getThreads().getWorkerThreads());
			channelClass = EpollServerSocketChannel.class;
			loggingHelper.debug("Using Epoll transport type");
		} else {
			bossGroup = new NioEventLoopGroup(nettySettings.getThreads().getBossThreads());
			workerGroup = new NioEventLoopGroup(nettySettings.getThreads().getWorkerThreads());
			channelClass = NioServerSocketChannel.class;
			loggingHelper.debug("Using Java NIO transport type");
		}

		injector.getInstance(ServerBootstrap.class)
				.group(bossGroup, workerGroup)
				.channel(channelClass)
				.childHandler(injector.getInstance(ClientChannelInitializer.class))
				.childOption(ChannelOption.TCP_NODELAY, true)
				.localAddress(settingsConfig.getConnection().getAddress())
				.bind();
	}

	private void stop() {
		loggingHelper.info("Stopping server...");

		if (keepAliveTask != null)
			keepAliveTask.cancel(true);

		if (bossGroup != null)
			bossGroup.shutdownGracefully();

		if (workerGroup != null)
			workerGroup.shutdownGracefully();

		loggingHelper.info("Server stopped, Goodbye!");
	}

	public void addPlayer(Player player) {
		players.add(player);

		// TODO Event
	}

	public void removePlayer(Player player) {
		players.remove(player);

		// TODO Event
	}

	@Override
	public PluginManager getPluginManager() {
		return injector.getInstance(PluginManager.class);
	}

	@Override
	public EventManager getEventManager() {
		return injector.getInstance(EventManager.class);
	}

	@Override
	public String getVersion() {
		try {
			return this.getClass().getPackage().getImplementationVersion();
		} catch (Exception e) {
			return "Version not available";
		}
	}

	@Override
	public List<Player> getPlayers() {
		return List.copyOf(players);
	}

	@Override
	public Player getPlayer(UUID uuid) {
		return players.stream()
				.filter(player -> player.getUniqueId().equals(uuid))
				.findFirst()
				.orElse(null);
	}

	@Override
	public Player getPlayer(String name) {
		return players.stream()
				.filter(player -> player.getUsername().equals(name))
				.findFirst()
				.orElse(null);
	}

	@Override
	public int getMaxPlayers() {
		return settingsConfig.getConnection().getMaxPlayers();
	}
}
