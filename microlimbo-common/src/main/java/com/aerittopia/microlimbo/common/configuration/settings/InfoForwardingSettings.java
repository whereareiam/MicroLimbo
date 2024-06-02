package com.aerittopia.microlimbo.common.configuration.settings;

import com.aerittopia.microlimbo.api.model.ForwardingType;
import lombok.Getter;

import java.util.List;

@Getter
public class InfoForwardingSettings {
	private ForwardingType type = ForwardingType.NONE;
	private String secret = "YOUR_SECRET_HERE";
	private List<String> tokens = List.of("BUNGEE_GUARD_TOKEN");

	public boolean isLegacy() {
		return this.type == ForwardingType.LEGACY;
	}

	public boolean isBungeeGuard() {
		return this.type == ForwardingType.BUNGEE_GUARD;
	}

	public boolean isNone() {
		return this.type == ForwardingType.NONE;
	}

	public boolean isModern() {
		return this.type == ForwardingType.MODERN;
	}

	public boolean hasToken(String token) {
		return tokens != null && token != null && tokens.contains(token);
	}

	public byte[] getSecret() {
		return this.secret.getBytes();
	}
}
