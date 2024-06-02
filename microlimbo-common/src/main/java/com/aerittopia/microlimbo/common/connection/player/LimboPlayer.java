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

package com.aerittopia.microlimbo.common.connection.player;

import com.aerittopia.microlimbo.api.connection.Player;
import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.connection.client.ClientConnection;
import com.aerittopia.microlimbo.common.protocol.packet.login.PacketDisconnect;
import com.aerittopia.microlimbo.common.protocol.packet.play.*;
import com.aerittopia.microlimbo.common.registry.State;
import com.aerittopia.microlimbo.common.util.NBTMessageUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
@ToString
public class LimboPlayer implements Player {
	private final ClientConnection connection;

	private UUID uniqueId;
	private String username;
	private Version clientVersion = Version.getMin();
	private State state = State.HANDSHAKE;
	private Locale locale = Locale.ENGLISH;

	private int loginId = -1;

	public LimboPlayer(ClientConnection connection) {
		this.connection = connection;
	}

	@Override
	public ClientConnection getClientConnection() {
		return connection;
	}

	@Override
	public void disconnect() {
		disconnect(null);
	}

	@Override
	public void disconnect(Component component) {
		if (component == null) component = Component.translatable("multiplayer.disconnect.generic");

		if (connection.getChannel().isActive() && state == State.LOGIN) {
			getClientConnection().sendPacket(new PacketDisconnect(component), true);
		}
	}

	@Override
	public void sendMessage(Component component) {
		PacketChatMessage chatMessage = new PacketChatMessage();
		chatMessage.setMessage(NBTMessageUtil.create(component));
		chatMessage.setPosition(PacketChatMessage.PositionLegacy.SYSTEM_MESSAGE);
		chatMessage.setSender(UUID.randomUUID());

		getClientConnection().sendPacket(chatMessage);
	}

	@Override
	public void sendTitle(Title title) {
		PacketTitleSetTitle packetTitle = new PacketTitleSetTitle();
		PacketTitleSetSubTitle packetSubtitle = new PacketTitleSetSubTitle();
		PacketTitleTimes packetTimes = new PacketTitleTimes();

		packetTitle.setTitle(NBTMessageUtil.create(title.title()));
		packetSubtitle.setSubtitle(NBTMessageUtil.create(title.subtitle()));

		Title.Times times = title.times();
		if (times.fadeIn() != null && times.stay() != null && times.fadeOut() != null) {
			packetTimes.setFadeIn(title.times().fadeIn().getNano());
			packetTimes.setStay(title.times().stay().getNano());
			packetTimes.setFadeOut(title.times().fadeOut().getNano());
		}

		getClientConnection().sendPacket(packetTitle);
		getClientConnection().sendPacket(packetSubtitle);
		getClientConnection().sendPacket(packetTimes);
	}

	@Override
	public void sendPluginMessage(String channel, String message) {
		PacketPluginMessage pluginMessage = new PacketPluginMessage();
		pluginMessage.setChannel(channel);
		pluginMessage.setMessage(message);

		getClientConnection().sendPacket(pluginMessage);
	}
}
