package com.aerittopia.microlimbo.common.configuration.customization;

import com.aerittopia.microlimbo.api.model.component.Title;
import com.aerittopia.microlimbo.common.util.NBTMessageUtil;
import lombok.Getter;

@Getter
public class TitleCustomization {
	private boolean enabled = false;
	private String title = "<dark_purple><bold>MicroLimbo<reset>";
	private String subtitle = "<dark_gray>Straight from the future!";

	private int fadeIn = 20;
	private int stay = 60;
	private int fadeOut = 20;

	public Title getTitle() {
		return new Title(
				NBTMessageUtil.create(title),
				NBTMessageUtil.create(subtitle),
				fadeIn,
				stay,
				fadeOut
		);
	}
}
