package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Settings implements StorageObject
{
	@Comment(
	{
		"##########################################################",
		"+------------------------------------------------------+ #",
		"|                 General Settings                     | #",
		"+------------------------------------------------------+ #",
		"##########################################################"
	})
	private General general = new General();
	@Comment(
	{
		"##########################################################",
		"+------------------------------------------------------+ #",
		"|                  Chat Settings                       | #",
		"+------------------------------------------------------+ #",
		"##########################################################"
	})
	private Chat chat = new Chat();
	@Comment(
	{
		"##########################################################",
		"+------------------------------------------------------+ #",
		"|                 Economy Settings                     | #",
		"+------------------------------------------------------+ #",
		"##########################################################"
	})
	private Economy economy = new Economy();
	@Comment(
	{
		"##########################################################",
		"+------------------------------------------------------+ #",
		"|                 Commands Settings                    | #",
		"+------------------------------------------------------+ #",
		"##########################################################"
	})
	private Commands commands = new Commands();
	@Comment(
	{
		"##########################################################",
		"+------------------------------------------------------+ #",
		"|                  Worlds Settings                     | #",
		"+------------------------------------------------------+ #",
		"##########################################################"
	})
	@MapValueType(WorldOptions.class)
	private Map<String, WorldOptions> worlds = new HashMap<String, WorldOptions>();

	public WorldOptions getWorldOptions(final String name)
	{
		if (worlds == null)
		{
			worlds = new HashMap<String, WorldOptions>();
		}
		final WorldOptions options = worlds.get(name);
		return (options == null) ? new WorldOptions() : options;
	}
}
