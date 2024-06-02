package com.aerittopia.microlimbo.api.model;

import lombok.Getter;

@Getter
public enum Gamemode {
	SURVIVAL(0),
	CREATIVE(1),
	ADVENTURE(2),
	SPECTATOR(3);

	private final int id;

	Gamemode(int id) {
		this.id = id;
	}
}
