package com.aerittopia.microlimbo.common.configuration.customization;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.List;

@Getter
public class MotdCustomization {
	private List<String> motd = List.of(
			"<dark_purple><bold> MicroLimbo<reset>",
			"<white><italic> Straight from the future!"
	);
	private String version = "MicroLimbo";
	private int protocol = -1;

	public String getMotd() {
		final MiniMessage miniMessage = MiniMessage.miniMessage();
		final GsonComponentSerializer serializer = GsonComponentSerializer.gson();

		return serializer.serialize(miniMessage.deserialize(String.join("\n", motd)));
	}
}
