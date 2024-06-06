package com.aerittopia.microlimbo.api.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PluginConfiguration {
	private String name;
	private String version;
	private List<String> authors;

	private String main;
}
