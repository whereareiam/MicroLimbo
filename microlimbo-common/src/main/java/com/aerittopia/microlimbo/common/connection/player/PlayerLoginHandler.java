package com.aerittopia.microlimbo.common.connection.player;

import com.aerittopia.microlimbo.api.event.EventManager;
import com.aerittopia.microlimbo.api.event.player.PlayerJoinEvent;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.LimboServer;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.connection.PacketSnapshots;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.PacketSnapshot;
import com.aerittopia.microlimbo.common.registry.State;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

@Singleton
public class PlayerLoginHandler {
	private final LimboServer server;
	private final SettingsConfig settingsConfig;
	private final EventManager eventManager;

	@Inject
	public PlayerLoginHandler(LimboServer server, SettingsConfig settingsConfig, EventManager eventManager) {
		this.server = server;
		this.settingsConfig = settingsConfig;
		this.eventManager = eventManager;
	}

	public void fireLoginSuccess(LimboPlayer limboPlayer) {
		if (settingsConfig.getForwarding().isModern() && limboPlayer.getLoginId() == -1) {
			limboPlayer.disconnect(Component.text("You need to connect with Velocity"));
			return;
		}

		server.addPlayer(limboPlayer);
		limboPlayer.getClientConnection().sendPacket(PacketSnapshots.PACKET_LOGIN_SUCCESS);

		// Preparing for configuration mode
		if (limboPlayer.getClientVersion().moreOrEqual(Version.V1_20_2)) {
			limboPlayer.getClientConnection().getPacketDecoder().setTemporalState(State.LOGIN);
			limboPlayer.setState(State.CONFIGURATION);
			return;
		}

		spawnPlayer(limboPlayer);
	}

	public void spawnPlayer(LimboPlayer limboPlayer) {
		ClientConnection connection = limboPlayer.getClientConnection();
		Version clientVersion = limboPlayer.getClientVersion();

		limboPlayer.setState(State.PLAY);

		Runnable sendPlayPackets = () -> {
			connection.writePacket(PacketSnapshots.PACKET_JOIN_GAME);
			connection.writePacket(PacketSnapshots.PACKET_PLAYER_ABILITIES);

			if (clientVersion.less(Version.V1_9)) {
				connection.writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK_LEGACY);
			} else {
				connection.writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK);
			}

			if (clientVersion.moreOrEqual(Version.V1_19_3))
				connection.writePacket(PacketSnapshots.PACKET_SPAWN_POSITION);

			if (clientVersion.equals(Version.V1_16_4))
				connection.writePacket(PacketSnapshots.PACKET_PLAYER_INFO);

			if (clientVersion.moreOrEqual(Version.V1_13)) {
				connection.writePacket(PacketSnapshots.PACKET_DECLARE_COMMANDS);

				if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null)
					connection.writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);
			}

			if (PacketSnapshots.PACKET_BOSS_BAR != null && clientVersion.moreOrEqual(Version.V1_9))
				connection.writePacket(PacketSnapshots.PACKET_BOSS_BAR);

			if (PacketSnapshots.PACKET_JOIN_MESSAGE != null)
				connection.writePacket(PacketSnapshots.PACKET_JOIN_MESSAGE);

			if (PacketSnapshots.PACKET_TITLE_TITLE != null && clientVersion.moreOrEqual(Version.V1_8))
				if (clientVersion.moreOrEqual(Version.V1_17)) {
					connection.writePacket(PacketSnapshots.PACKET_TITLE_TITLE);
					connection.writePacket(PacketSnapshots.PACKET_TITLE_SUBTITLE);
					connection.writePacket(PacketSnapshots.PACKET_TITLE_TIMES);
				} else {
					connection.writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TITLE);
					connection.writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_SUBTITLE);
					connection.writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TIMES);
				}

			if (PacketSnapshots.PACKET_HEADER_AND_FOOTER != null && clientVersion.moreOrEqual(Version.V1_8))
				connection.writePacket(PacketSnapshots.PACKET_HEADER_AND_FOOTER);

			if (clientVersion.moreOrEqual(Version.V1_20_3)) {
				connection.writePacket(PacketSnapshots.PACKET_START_WAITING_CHUNKS);

				for (PacketSnapshot chunk : PacketSnapshots.PACKETS_EMPTY_CHUNKS) {
					connection.writePacket(chunk);
				}
			}

			connection.sendKeepAlive();
		};

		PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(limboPlayer);
		eventManager.fireEvent(playerJoinEvent);

		if (playerJoinEvent.isCancelled()) {
			limboPlayer.disconnect(playerJoinEvent.getCancelReason());
			return;
		}

		if (clientVersion.lessOrEqual(Version.V1_7_6)) {
			connection.getChannel().eventLoop().schedule(sendPlayPackets, 100, TimeUnit.MILLISECONDS); // TODO
		} else {
			sendPlayPackets.run();
		}
	}
}
