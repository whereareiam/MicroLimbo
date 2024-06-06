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

package com.aerittopia.microlimbo.common.connection;

import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.api.event.handshake.HandshakeEvent;
import com.aerittopia.microlimbo.api.event.player.PlayerLocaleChangeEvent;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.LimboServer;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.connection.player.LimboPlayer;
import com.aerittopia.microlimbo.common.connection.player.PlayerLoginHandler;
import com.aerittopia.microlimbo.common.protocol.PacketSnapshot;
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import com.aerittopia.microlimbo.common.protocol.packet.PacketHandshake;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketClientInformation;
import com.aerittopia.microlimbo.common.protocol.packet.configuration.PacketFinishConfiguration;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketLoginAcknowledged;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketLoginPluginRequest;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketLoginPluginResponse;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketLoginStart;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusPing;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusRequest;
import com.aerittopia.microlimbo.common.protocol.packet.status.PacketStatusResponse;
import com.aerittopia.microlimbo.common.registry.ConstantsRegistry;
import com.aerittopia.microlimbo.common.registry.State;
import com.aerittopia.microlimbo.common.util.ForwardingUtil;
import com.aerittopia.microlimbo.common.util.UniqueIdUtil;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

@Singleton
public class PacketHandler {
	private final Injector injector;
	private final LimboServer server;
	private final EventManager eventManager;
	private final SettingsConfig settingsConfig;

	private final ForwardingUtil forwardingUtil;
	private final PlayerLoginHandler playerLoginHandler;

	private final LoggingHelper loggingHelper;

	private final Map<Class<? extends Packet>, BiConsumer<ClientConnection, Packet>> handlers = new HashMap<>();

	@Inject
	public PacketHandler(Injector injector, LimboServer server, EventManager eventManager, SettingsConfig settingsConfig, ForwardingUtil forwardingUtil, PlayerLoginHandler playerLoginHandler, LoggingHelper loggingHelper) {
		this.injector = injector;
		this.server = server;
		this.eventManager = eventManager;
		this.settingsConfig = settingsConfig;
		this.forwardingUtil = forwardingUtil;
		this.playerLoginHandler = playerLoginHandler;
		this.loggingHelper = loggingHelper;

		handlers.put(PacketHandshake.class, this::handleHandshake);
		handlers.put(PacketStatusRequest.class, this::handleStatusRequest);
		handlers.put(PacketStatusPing.class, this::handleStatusPing);
		handlers.put(PacketLoginStart.class, this::handleLoginStart);
		handlers.put(PacketLoginPluginResponse.class, this::handleLoginPluginResponse);
		handlers.put(PacketLoginAcknowledged.class, this::handleLoginAcknowledged);
		handlers.put(PacketFinishConfiguration.class, this::handleFinishConfiguration);
		handlers.put(PacketClientInformation.class, this::handleClientInformation);
	}

	public void handlePacket(Packet packet, ClientConnection clientConnection) {
		BiConsumer<ClientConnection, Packet> handler = handlers.get(packet.getClass());
		if (handler != null) {
			handler.accept(clientConnection, packet);
		}
	}

	private void handleClientInformation(ClientConnection connection, Packet packet) {
		LimboPlayer limboPlayer = connection.getLimboPlayer();
		PacketClientInformation packetClientInformation = (PacketClientInformation) packet;

		PlayerLocaleChangeEvent playerLocaleChangeEvent = new PlayerLocaleChangeEvent(limboPlayer);
		eventManager.fireEvent(playerLocaleChangeEvent);

		if (playerLocaleChangeEvent.isCancelled()) {
			return;
		}

		limboPlayer.setLocale(packetClientInformation.getLocale());
	}

	private void handleFinishConfiguration(ClientConnection connection, Packet p) {
		playerLoginHandler.spawnPlayer(connection.getLimboPlayer());
	}

	private void handleLoginAcknowledged(ClientConnection connection, Packet p) {
		LimboPlayer limboPlayer = connection.getLimboPlayer();
		limboPlayer.setState(State.CONFIGURATION);

		if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null)
			connection.writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);

		if (limboPlayer.getClientVersion().moreOrEqual(Version.V1_20_5)) {
			for (PacketSnapshot packetSnapshot : PacketSnapshots.PACKETS_REGISTRY_DATA) {
				connection.writePacket(packetSnapshot);
			}
		} else {
			connection.writePacket(PacketSnapshots.PACKET_REGISTRY_DATA);
		}

		connection.sendPacket(PacketSnapshots.PACKET_FINISH_CONFIGURATION);
	}

	private void handleLoginPluginResponse(ClientConnection connection, Packet p) {
		LimboPlayer limboPlayer = connection.getLimboPlayer();
		PacketLoginPluginResponse packet = (PacketLoginPluginResponse) p;

		if (settingsConfig.getForwarding().isModern()
				&& packet.getMessageId() == limboPlayer.getLoginId()) {

			if (!packet.isSuccessful() || packet.getData() == null) {
				limboPlayer.disconnect(Component.text("You need to connect with Velocity"));
				return;
			}

			if (!forwardingUtil.checkVelocityKeyIntegrity(packet.getData())) {
				limboPlayer.disconnect(Component.text("Can't verify forwarded limboPlayer info"));
				return;
			}

			// Order is important
			limboPlayer.getClientConnection().setRemoteAddress(packet.getData().readString());
			limboPlayer.setUniqueId(packet.getData().readUuid());
			limboPlayer.setUsername(packet.getData().readString());

			playerLoginHandler.fireLoginSuccess(limboPlayer);
		}
	}

	private void handleLoginStart(ClientConnection connection, Packet p) {
		LimboPlayer limboPlayer = connection.getLimboPlayer();
		PacketLoginStart packet = (PacketLoginStart) p;

		if (settingsConfig.getConnection().getMaxPlayers() > 0 &&
				server.getPlayers().size() >= settingsConfig.getConnection().getMaxPlayers()) {
			limboPlayer.disconnect(Component.translatable("multiplayer.disconnect.server_full"));
			return;
		}

		if (!limboPlayer.getClientVersion().isSupported()) {
			limboPlayer.disconnect(Component.translatable("multiplayer.status.incompatible"));
			return;
		}

		if (settingsConfig.getForwarding().isModern()) {
			int loginId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
			PacketLoginPluginRequest request = new PacketLoginPluginRequest();

			request.setMessageId(loginId);
			request.setChannel(ConstantsRegistry.VELOCITY_INFO_CHANNEL);
			request.setData(Unpooled.EMPTY_BUFFER);

			limboPlayer.setLoginId(loginId);

			connection.sendPacket(request);
			return;
		}

		if (!settingsConfig.getForwarding().isModern()) {
			limboPlayer.setUsername(packet.getUsername());
			limboPlayer.setUniqueId(UniqueIdUtil.getOfflineModeUuid(packet.getUsername()));
		}

		playerLoginHandler.fireLoginSuccess(limboPlayer);
	}

	private void handleStatusPing(ClientConnection connection, Packet p) {
		connection.sendPacket(p, true);
	}

	private void handleStatusRequest(ClientConnection connection, Packet p) {
		connection.sendPacket(injector.getInstance(PacketStatusResponse.class));
	}

	private void handleHandshake(ClientConnection connection, Packet p) {
		LimboPlayer limboPlayer = connection.getLimboPlayer();
		PacketHandshake packet = (PacketHandshake) p;

		limboPlayer.setClientVersion(packet.getVersion());
		limboPlayer.setState(packet.getNextState());

		loggingHelper.debug("Pinged from %s [%s]", connection.getRemoteAddress(),
				limboPlayer.getClientVersion().toString());

		if (settingsConfig.getForwarding().isLegacy()) {
			String[] split = packet.getHost().split("\00");

			if (split.length == 3 || split.length == 4) {
				connection.setRemoteAddress(split[1]);
				limboPlayer.setUniqueId(UniqueIdUtil.fromString(split[2]));
			} else {
				limboPlayer.disconnect(Component.text("You've enabled Player info forwarding. You need to connect with proxy"));
			}
		} else if (settingsConfig.getForwarding().isBungeeGuard()) {
			if (!forwardingUtil.checkBungeeGuardHandshake(limboPlayer, packet.getHost())) {
				limboPlayer.disconnect(Component.text("Invalid BungeeGuard token or packet format"));
			}
		}

		HandshakeEvent handshakeEvent = new HandshakeEvent(limboPlayer);
		eventManager.fireEvent(handshakeEvent);

		if (handshakeEvent.isCancelled()) {
			limboPlayer.disconnect(handshakeEvent.getCancelReason());
		}
	}
}
