package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class GroupOptions extends StorageObject
{
	@Comment("Message format of chat messages")
	private String messageFormat;
	@Comment("Prefix for name")
	private String prefix;
	@Comment("Suffix for name")
	private String suffix;
	@Comment("Amount of homes a player can have")
	private Integer homes;
	@Comment("Cooldown between teleports")
	private Integer teleportCooldown;
	@Comment("Delay before teleport")
	private Integer teleportDelay;
	@Comment("Cooldown between heals")
	private Integer healCooldown;
}
