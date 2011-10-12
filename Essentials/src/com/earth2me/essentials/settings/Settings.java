package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.ListType;
import com.earth2me.essentials.storage.MapType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends StorageObject
{
	public Settings()
	{
		super();
		locations.put("Test", new Location());
		m_o_t_d.add("Welcome to the server!");
		m_o_t_d.add("Have a nice day!\nwoooooo");
	}
	private boolean test;
	private Boolean test2;
	@Comment(
	{
		"Hello!",
		"World"
	})
	private String yay = "null";
	private String lol = "lol: 1";
	private General general = new General();
	@MapType(Location.class)
	private Map<String, Location> locations = new HashMap<String, Location>();
	@ListType
	private List<String> m_o_t_d = new ArrayList<String>();
}
