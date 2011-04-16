package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsChatPlayerListener extends PlayerListener
{
	private final Server server;

	public EssentialsChatPlayerListener(Server server)
	{
		this.server = server;
	}

	@Override
	@SuppressWarnings("CallToThreadDumpStack")
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		try
		{
			EssentialsChatWorker.onPlayerJoin(server, event);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("CallToThreadDumpStack")
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		try
		{
			EssentialsChatWorker.onPlayerRespawn(server, event);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("CallToThreadDumpStack")
	public void onPlayerChat(PlayerChatEvent event)
	{
		try
		{
			EssentialsChatWorker.onPlayerChat(server, event);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}
}
