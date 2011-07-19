package com.earth2me.essentials.chat;

import org.bukkit.event.player.PlayerChatEvent;


public interface IEssentialsChatListener
{
	boolean shouldHandleThisChat(PlayerChatEvent event);

	String modifyMessage(String message);
}
