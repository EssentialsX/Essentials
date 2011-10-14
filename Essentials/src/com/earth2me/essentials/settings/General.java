package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.MapType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class General extends StorageObject
{
	public General()
	{
		super();
		locations.put("Test", new Location());
		locations.put("Test5", new Location());
		locations.put("Test4", new Location());
		locations.put("Test3", new Location());
		locations.put("Test2", new Location());
	}
	private boolean debug = false;
	private boolean signsDisabled = false;
	private int test = 1;
	private String test2 = "\tline1\nline2\nline3";
	@Comment("Backup runs a command while saving is disabled")
	private Backup backup = new Backup();
	@Comment(
	{
		"Set the locale here, if you want to change the language of Essentials.",
		"If this is not set, Essentials will use the language of your computer.",
		"Available locales: da, de, en, fr, nl"
	})
	private String locale;
	@MapType(Location.class)
	private LinkedHashMap<String, Location> locations = new LinkedHashMap<String, Location>();
}
