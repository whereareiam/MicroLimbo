package com.aerittopia.microlimbo.api.model.component.bossbar;

import lombok.Getter;

@Getter
public enum BossBarColor {
	PINK(0),
	BLUE(1),
	RED(2),
	GREEN(3),
	YELLOW(4),
	PURPLE(5),
	WHITE(6);

	private final int index;

	BossBarColor(int index) {
		this.index = index;
	}
}
