package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
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
	public void onPlayerJoin(PlayerEvent event)
	{
		try
		{
			Essentials.loadClasses();
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
			Essentials.loadClasses();
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
			Essentials.loadClasses();
			EssentialsChatWorker.onPlayerChat(server, event);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}
}
