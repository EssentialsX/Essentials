package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Afk implements StorageObject
{
	@Comment(
	{
		"After this timeout in seconds, the user will be set as afk.",
		"Set to -1 for no timeout."
	})
	private long autoAFK = 300;
	@Comment(
	{
		"Auto-AFK Kick",
		"After this timeout in seconds, the user will be kicked from the server.",
		"Set to -1 for no timeout."
	})
	private long autoAFKKick = -1;
	@Comment(
	{
		"Set this to true, if you want to freeze the player, if he is afk.",
		"Other players or monsters can't push him out of afk mode then.",
		"This will also enable temporary god mode for the afk player.",
		"The player has to use the command /afk to leave the afk mode.",
		"You have to add a message to your welcome message or help page,",
		"since the player will not get a message, if he tries to move."
	})
	private boolean freezeAFKPlayers = false;
	
	private boolean disableItemPickupWhileAfk = true;
}
