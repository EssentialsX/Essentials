package com.earth2me.essentials.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;


public interface IEssentialsChatListener
{
	boolean shouldHandleThisChat(PlayerChatEvent event);

	String modifyMessage(PlayerChatEvent event, Player target, String message);
}
