package com.earth2me.essentials;

import junit.framework.TestCase;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import com.earth2me.essentials.yaml.Settings;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.yaml.snakeyaml.constructor.Constructor;


public class YamlTest extends TestCase
{
	public YamlTest()
	{
	}

	public void testYaml()
	{
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setExplicitRoot(Tag.MAP);
		final Yaml yaml = new Yaml(options);
		//Settings settings = (Settings)yaml.load("");
		Settings set1 = new Settings();
		final String dump = yaml.dump(set1);
		final Yaml yaml2 = new Yaml(new Constructor(Settings.class));
		final Settings set = (Settings)yaml2.load(dump);
		if (set != null)
		{
			//assert set.getGeneral().getLocation() == null;
			//assert set.equals(new Settings());
			System.out.println(dump);
		}
	}
}
