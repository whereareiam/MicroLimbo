package com.aerittopia.microlimbo.api.model;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.nbt.BinaryTag;

@Setter
@Getter
public class NBTMessage {
	private String json;
	private BinaryTag tag;

	public NBTMessage(String json, BinaryTag tag) {
		this.json = json;
		this.tag = tag;
	}
}
