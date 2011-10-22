package com.earth2me.essentials.update;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class UpdateProcess extends PlayerListener
{
	private transient Player currentPlayer;
	private final transient Plugin plugin;
	private final transient UpdateCheck updateCheck;
	
	public UpdateProcess(final Plugin plugin, final UpdateCheck updateCheck)
	{
		this.plugin = plugin;
		this.updateCheck = updateCheck;
	}
	
	public void registerEvents()
	{
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		pluginManager.registerEvent(Type.PLAYER_QUIT, this, Priority.Low, plugin);
		pluginManager.registerEvent(Type.PLAYER_CHAT, this, Priority.Lowest, plugin);
	}
	
	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (event.getPlayer() == currentPlayer)
		{
			reactOnMessage(event.getMessage());
			event.setCancelled(true);
			return;
		}
	}
	
	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		if (player.hasPermission("essentials.update") && !updateCheck.isEssentialsInstalled())
		{
			player.sendMessage("Hello " + player.getDisplayName());
			player.sendMessage("Please type /essentialsupdate into the chat to start the installation of Essentials.");
		}
		if (player.hasPermission("essentials.update"))
		{
			final UpdateCheck.CheckResult result = updateCheck.getResult();
			switch (result)
			{
			case NEW_ESS:
				player.sendMessage("The new version " + updateCheck.getNewVersion().toString() + " for Essentials is available. Please type /essentialsupdate to update.");
				break;
			case NEW_BUKKIT:
				player.sendMessage("Your bukkit version is not the recommended build for Essentials, please update to version " + updateCheck.getNewBukkitVersion() + ".");
				break;
			case NEW_ESS_BUKKIT:
				player.sendMessage("There is a new version " + updateCheck.getNewVersion().toString() + " of Essentials for Bukkit " + updateCheck.getNewBukkitVersion());
				break;
			default:
			}
		}
	}
	
	void doAutomaticUpdate()
	{
		final UpdatesDownloader downloader = new UpdatesDownloader();
		final VersionInfo info = updateCheck.getNewVersionInfo();
		final List<String> changelog = info.getChangelog();
		Bukkit.getLogger().info("Essentials changelog " + updateCheck.getNewVersion().toString());
		for (String line : changelog)
		{
			Bukkit.getLogger().info(" - "+line);
		}
		downloader.start(plugin.getServer().getUpdateFolderFile(), info);
	}
	
	void doManualUpdate()
	{
		
	}
	
	void onCommand(CommandSender sender)
	{
		if (sender instanceof Player && sender.hasPermission("essentials.install"))
		{
			if (currentPlayer == null)
			{
				currentPlayer = (Player)sender;
				if (updateCheck.isEssentialsInstalled())
				{
					doManualUpdate();
				}
				else
				{
					sender.sendMessage("Thank you for choosing Essentials.");
					sender.sendMessage("The following installation wizard will guide you through the installation of Essentials.");
					sender.sendMessage("Your answers will be saved for a later update.");
					sender.sendMessage("Please answer the messages with yes or no, if not otherwise stated.");
					sender.sendMessage("Write bye/exit/quit if you want to exit the wizard at anytime.");
					
				}
			}
			if (!currentPlayer.equals(sender))
			{
				sender.sendMessage("The player " + currentPlayer.getDisplayName() + " is already using the wizard.");
			}
		}
		else
		{
			sender.sendMessage("Please run the command as op from in game.");
		}
	}
	
	private void reactOnMessage(String message)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
