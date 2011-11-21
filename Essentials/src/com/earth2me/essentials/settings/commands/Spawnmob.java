package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Spawnmob extends StorageObject
{
	@Comment("The maximum amount of monsters, a player can spawn with a call of /spawnmob.")
	private int limit = 10;
}
