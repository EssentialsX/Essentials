package com.earth2me.essentials.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public interface IEssentialsChatListener
{
	boolean shouldHandleThisChat(AsyncPlayerChatEvent event);

	String modifyMessage(AsyncPlayerChatEvent event, Player target, String message);
}
