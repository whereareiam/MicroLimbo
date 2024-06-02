package com.aerittopia.microlimbo.common.configuration.customization;

import lombok.Getter;

import java.util.List;

@Getter
public class TabCustomization {
	private boolean useCustomTab = false;
	private List<String> header = List.of(
			" ",
			"<dark_purple><bold>MicroLimbo<bold:false>",
			"    <white><italic>Straight from the future!    ",
			"  "
	);
	private List<String> footer = List.of(
			" ",
			"<white>Powered by <dark_purple>MicroLimbo",
			"<white>Visit <dark_purple>www.example.com",
			"  "
	);
}
