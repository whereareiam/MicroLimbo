package com.aerittopia.microlimbo.common.configuration.customization;

import com.aerittopia.microlimbo.api.model.Gamemode;
import com.aerittopia.microlimbo.api.model.dimension.Dimension;
import com.google.inject.Singleton;
import lombok.Getter;

@Getter
@Singleton
public class CustomizationConfig {
	private Gamemode gamemode = Gamemode.SPECTATOR;
	private Dimension dimension = Dimension.THE_END;
	private BrandingCustomization branding = new BrandingCustomization();
	private MotdCustomization motd = new MotdCustomization();
	private TabCustomization tab = new TabCustomization();
	private ChatCustomization chat = new ChatCustomization();
	private BossBarCustomization bossBar = new BossBarCustomization();
	private TitleCustomization title = new TitleCustomization();
}
