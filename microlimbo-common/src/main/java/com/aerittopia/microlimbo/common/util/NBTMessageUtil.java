package com.aerittopia.microlimbo.common.util;

import com.aerittopia.microlimbo.api.model.NBTMessage;
import com.google.gson.*;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NBTMessageUtil {
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
	private static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();

	public static NBTMessage create(Component reason) {
		String message = SERIALIZER.serialize(reason);
		BinaryTag compoundBinaryTag = fromJson(JsonParser.parseString(message));

		return new NBTMessage(message, compoundBinaryTag);
	}

	public static NBTMessage create(String message) {
		message = SERIALIZER.serialize(MINI_MESSAGE.deserialize(message));
		BinaryTag compoundBinaryTag = fromJson(JsonParser.parseString(message));

		return new NBTMessage(message, compoundBinaryTag);
	}

	public static BinaryTag fromJson(JsonElement json) {
		if (json.isJsonPrimitive()) {
			return handleJsonPrimitive(json.getAsJsonPrimitive());
		} else if (json.isJsonObject()) {
			return handleJsonObject(json.getAsJsonObject());
		} else if (json.isJsonArray()) {
			return handleJsonArray(json.getAsJsonArray());
		} else if (json.isJsonNull()) {
			return EndBinaryTag.endBinaryTag();
		}

		throw new IllegalArgumentException("Unknown JSON element: " + json);
	}

	private static BinaryTag handleJsonPrimitive(JsonPrimitive jsonPrimitive) {
		if (jsonPrimitive.isNumber()) {
			return handleJsonNumber(jsonPrimitive.getAsNumber());
		} else if (jsonPrimitive.isString()) {
			return StringBinaryTag.stringBinaryTag(jsonPrimitive.getAsString());
		} else if (jsonPrimitive.isBoolean()) {
			return ByteBinaryTag.byteBinaryTag(jsonPrimitive.getAsBoolean() ? (byte) 1 : (byte) 0);
		} else {
			throw new IllegalArgumentException("Unknown JSON primitive: " + jsonPrimitive);
		}
	}

	private static BinaryTag handleJsonNumber(Number number) {
		if (number instanceof Byte) {
			return ByteBinaryTag.byteBinaryTag((Byte) number);
		} else if (number instanceof Short) {
			return ShortBinaryTag.shortBinaryTag((Short) number);
		} else if (number instanceof Integer) {
			return IntBinaryTag.intBinaryTag((Integer) number);
		} else if (number instanceof Long) {
			return LongBinaryTag.longBinaryTag((Long) number);
		} else if (number instanceof Float) {
			return FloatBinaryTag.floatBinaryTag((Float) number);
		} else if (number instanceof Double) {
			return DoubleBinaryTag.doubleBinaryTag((Double) number);
		}

		throw new IllegalArgumentException("Unknown JSON number: " + number);
	}

	private static BinaryTag handleJsonObject(JsonObject jsonObject) {
		CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
		for (Map.Entry<String, JsonElement> property : jsonObject.entrySet()) {
			builder.put(property.getKey(), fromJson(property.getValue()));
		}

		return builder.build();
	}

	private static BinaryTag handleJsonArray(JsonArray jsonArray) {
		if (jsonArray.isEmpty()) {
			return ListBinaryTag.listBinaryTag(EndBinaryTag.endBinaryTag().type(), Collections.emptyList());
		}

		BinaryTagType<? extends BinaryTag> listType = fromJson(jsonArray.get(0)).type();
		if (listType.equals(ByteBinaryTag.ZERO.type())) {
			return ByteArrayBinaryTag.byteArrayBinaryTag(getByteArray(jsonArray));
		} else if (listType.equals(IntBinaryTag.intBinaryTag(0).type())) {
			return IntArrayBinaryTag.intArrayBinaryTag(getIntArray(jsonArray));
		} else if (listType.equals(LongBinaryTag.longBinaryTag(0).type())) {
			return LongArrayBinaryTag.longArrayBinaryTag(getLongArray(jsonArray));
		} else {
			return getListBinaryTag(jsonArray, listType);
		}
	}

	private static byte[] getByteArray(JsonArray jsonArray) {
		byte[] bytes = new byte[jsonArray.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (Byte) jsonArray.get(i).getAsNumber();
		}

		return bytes;
	}

	private static int[] getIntArray(JsonArray jsonArray) {
		int[] ints = new int[jsonArray.size()];
		for (int i = 0; i < ints.length; i++) {
			ints[i] = (Integer) jsonArray.get(i).getAsNumber();
		}

		return ints;
	}

	private static long[] getLongArray(JsonArray jsonArray) {
		long[] longs = new long[jsonArray.size()];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = (Long) jsonArray.get(i).getAsNumber();
		}

		return longs;
	}

	private static BinaryTag getListBinaryTag(JsonArray jsonArray, BinaryTagType<? extends BinaryTag> listType) {
		List<BinaryTag> tagItems = new ArrayList<>(jsonArray.size());

		for (JsonElement jsonEl : jsonArray) {
			BinaryTag subTag = fromJson(jsonEl);
			if (subTag.type() != listType) {
				throw new IllegalArgumentException("Cannot convert mixed JsonArray to Tag");
			}

			tagItems.add(subTag);
		}

		return ListBinaryTag.listBinaryTag(listType, tagItems);
	}
}