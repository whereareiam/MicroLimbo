package com.aerittopia.microlimbo.common.configuration.customization;

import com.aerittopia.microlimbo.api.model.component.bossbar.BossBar;
import com.aerittopia.microlimbo.api.model.component.bossbar.BossBarColor;
import com.aerittopia.microlimbo.api.model.component.bossbar.BossBarDivision;
import com.aerittopia.microlimbo.common.util.NBTMessageUtil;
import lombok.Getter;

@Getter
public class BossBarCustomization {
	private boolean enabled = false;
	private String content = "<dark_purple>MicroLimbo";

	private float progress = 1.0f;
	private BossBarColor color = BossBarColor.PURPLE;
	private BossBarDivision division = BossBarDivision.DASHES_20;

	public BossBar getBossBar() {
		return new BossBar(NBTMessageUtil.create(content), progress, color, division);
	}
}
