package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Home implements StorageObject
{
	@Comment("When players die, should they respawn at their homes, instead of the spawnpoint?")
	private boolean respawnAtHome = false;
	@Comment(
	{
		"When a player interacts with a bed, should their home be set to that location?",
		"If you enable this and remove default player access to the /sethome command, ",
		"you can make beds the only way for players to set their home location."
	})
	private boolean bedSetsHome = false;
	@Comment("If no home is set, should the player be send to spawn, when /home is used.")
	private boolean spawnIfNoHome = false;
	@Comment("Allows people to set their bed at daytime")
	private boolean updateBedAtDaytime = true;
}
