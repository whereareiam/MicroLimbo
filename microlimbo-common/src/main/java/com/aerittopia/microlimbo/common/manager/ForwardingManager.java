package com.aerittopia.microlimbo.common.manager;

import com.aerittopia.microlimbo.common.configuration.settings.InfoForwardingSettings;
import com.aerittopia.microlimbo.common.configuration.settings.SettingsConfig;
import com.aerittopia.microlimbo.common.connection.player.LimboPlayer;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.util.UniqueIdUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.UUID;

@Singleton
public class ForwardingManager {
	private final InfoForwardingSettings forwarding;

	@Inject
	public ForwardingManager(SettingsConfig settingsConfig) {
		this.forwarding = settingsConfig.getForwarding();
	}

	public boolean checkVelocityKeyIntegrity(ByteMessage message) {
		byte[] signature = new byte[32];
		message.readBytes(signature);

		byte[] data = new byte[message.readableBytes()];
		message.getBytes(message.readerIndex(), data);

		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(forwarding.getSecret(), "HmacSHA256"));
			byte[] mySignature = mac.doFinal(data);

			if (!MessageDigest.isEqual(signature, mySignature)) return false;
		} catch (InvalidKeyException | java.security.NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}

		int version = message.readVarInt();
		if (version != 1)
			throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');

		return true;
	}

	public boolean checkBungeeGuardHandshake(LimboPlayer limboPlayer, String handshake) {
		String[] split = handshake.split("\00");

		if (split.length != 4)
			return false;

		String socketAddressHostname = split[1];
		UUID uniqueId = UniqueIdUtil.fromString(split[2]);
		JsonArray array;

		try {
			array = JsonParser.parseString(split[3]).getAsJsonArray();
		} catch (JsonSyntaxException e) {
			return false;
		}

		String token = null;

		for (Object obj : array) {
			if (obj instanceof JsonObject object) {
				if (object.get("name").getAsString().equals("bungeeguard-token")) {
					token = object.get("value").getAsString();
					break;
				}
			}
		}

		if (!forwarding.hasToken(token))
			return false;

		limboPlayer.getClientConnection().setRemoteAddress(socketAddressHostname);
		limboPlayer.setUniqueId(uniqueId);

		return true;
	}
}
