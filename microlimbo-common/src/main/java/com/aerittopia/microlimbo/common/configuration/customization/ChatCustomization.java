package com.aerittopia.microlimbo.common.configuration.customization;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatCustomization {
	private boolean sendWelcomeMessage = false;
	private List<String> welcomeMessage = List.of(
			"",
			" <dark_purple><bold>Server information<reset>",
			"  <white>Welcome to <aqua>MicroLimbo",
			"  <white>Straight from the future!",
			""
	);
}
