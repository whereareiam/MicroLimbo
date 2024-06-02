package com.aerittopia.microlimbo.api.model.component.bossbar;

import lombok.Getter;

@Getter
public enum BossBarDivision {
	SOLID(0),
	DASHES_6(1),
	DASHES_10(2),
	DASHES_12(3),
	DASHES_20(4);

	private final int index;

	BossBarDivision(int index) {
		this.index = index;
	}
}
