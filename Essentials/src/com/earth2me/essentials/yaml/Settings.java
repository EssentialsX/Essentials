package com.earth2me.essentials.yaml;

import me.snowleo.yaml.YamlClass;
import me.snowleo.yaml.YamlComment;

@YamlClass
public class Settings extends SettingsYaml
{
	@YamlComment(comment = "Hello")
	protected General test = new General();
	boolean test2 = true;

	public Settings() {
	}
	
}
