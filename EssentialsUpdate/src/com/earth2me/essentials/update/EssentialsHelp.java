package com.earth2me.essentials.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jibble.pircbot.User;


public class EssentialsHelp extends PlayerListener
{
	private transient Player chatUser;
	private final transient Server server;
	private final transient Plugin plugin;
	private final static Charset UTF8 = Charset.forName("utf-8");
	private transient IrcBot ircBot;

	public EssentialsHelp(final Plugin plugin)
	{
		super();
		this.plugin = plugin;
		this.server = plugin.getServer();
	}

	public void registerEvents()
	{
		final PluginManager pluginManager = server.getPluginManager();
		pluginManager.registerEvent(Type.PLAYER_QUIT, this, Priority.Low, plugin);
		pluginManager.registerEvent(Type.PLAYER_CHAT, this, Priority.Low, plugin);
	}

	public void onCommand(CommandSender sender)
	{
		if (sender instanceof Player && sender.hasPermission("essentials.helpchat"))
		{
			if (chatUser == null)
			{
				chatUser = (Player)sender;
				ircBot = null;
				sender.sendMessage("You will be connected to the Essentials Help Chat.");
				sender.sendMessage("All your chat messages will be forwarded to the channel. You can't chat with other players on your server while in help chat, but you can use commands.");
				sender.sendMessage("Please be patient, if noone is available, check back later.");
				sender.sendMessage("Type !help to get a list of all commands.");
				sender.sendMessage("Type !quit to leave the channel.");
				sender.sendMessage("Do you want to join the channel now? (yes/no)");
			}
			if (!chatUser.equals(sender))
			{
				sender.sendMessage("The player " + chatUser.getDisplayName() + " is already using the essentialshelp.");
			}
		}
		else
		{
			sender.sendMessage("Please run the command as op from in game.");
		}
	}

	public void onDisable()
	{
		if ( ircBot != null)
		{
			ircBot.quit();
			ircBot = null;
		}
	}

	private boolean sendChatMessage(final Player player, final String message)
	{
		final String messageCleaned = message.trim();
		if (messageCleaned.isEmpty())
		{
			return false;
		}
		if (ircBot == null)
		{
			if (messageCleaned.equalsIgnoreCase("yes"))
			{
				player.sendMessage("Connecting...");
				connectToIRC(player);
				return true;
			}
			if (messageCleaned.equalsIgnoreCase("no") || message.equalsIgnoreCase("!quit"))
			{
				chatUser = null;
				return true;
			}
			return false;
		}
		else
		{
			if (ircBot.isKicked()) {
				chatUser = null;
				ircBot.quit();
				ircBot = null;
				return false;
			}
			final String lowMessage = messageCleaned.toLowerCase();
			if (lowMessage.startsWith("!quit"))
			{
				chatUser = null;
				if (ircBot != null) {
					ircBot.quit();
					ircBot = null;
				}
				player.sendMessage("Connection closed.");
				return true;
			}
			if (!ircBot.isConnected() || ircBot.getChannels().length == 0)
			{
				return false;
			}
			if (lowMessage.startsWith("!list"))
			{
				final User[] members = ircBot.getUsers();
				final StringBuilder sb = new StringBuilder();
				for (User user : members)
				{
					if (sb.length() > 0)
					{
						sb.append("§f, ");
					}
					if (user.isOp() || user.hasVoice())
					{
						sb.append("§6");
					}
					else
					{
						sb.append("§7");
					}
					sb.append(user.getPrefix()).append(user.getNick());
				}
				player.sendMessage(sb.toString());
				return true;
			}
			if (lowMessage.startsWith("!help"))
			{
				player.sendMessage("Commands: (Note: Files send to the chat will be public viewable.)");
				player.sendMessage("!errors - Send the last server errors to the chat.");
				player.sendMessage("!startup - Send the last startup messages to the chat.");
				player.sendMessage("!config - Sends your Essentials config to the chat.");
				player.sendMessage("!list - List all players in chat.");
				player.sendMessage("!quit - Leave chat.");
				return true;
			}
			if (lowMessage.startsWith("!errors"))
			{
				sendErrors();
				return true;
			}
			if (lowMessage.startsWith("!startup"))
			{
				sendStartup();
				return true;
			}
			if (lowMessage.startsWith("!config"))
			{
				sendConfig();
				return true;
			}
			ircBot.sendMessage(messageCleaned);
			chatUser.sendMessage("§6" + ircBot.getNick() + ": §7" + messageCleaned);
			return true;
		}
	}

	private String buildIrcName()
	{
		final StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(chatUser.getName());

		final Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(server.getVersion());
		if (versionMatch.matches())
		{
			nameBuilder.append(" CB");
			nameBuilder.append(versionMatch.group(4));
		}

		final Plugin essentials = server.getPluginManager().getPlugin("Essentials");
		if (essentials != null)
		{
			nameBuilder.append(" ESS");
			nameBuilder.append(essentials.getDescription().getVersion());
		}

		final Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
		if (groupManager != null)
		{
			nameBuilder.append(" GM");
			if (!groupManager.isEnabled())
			{
				nameBuilder.append('!');
			}
		}

		final Plugin pex = server.getPluginManager().getPlugin("PermissionsEx");
		if (pex != null)
		{
			nameBuilder.append(" PEX");
			if (!pex.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(pex.getDescription().getVersion());
		}

		final Plugin pb = server.getPluginManager().getPlugin("PermissionsBukkit");
		if (pb != null)
		{
			nameBuilder.append(" PB");
			if (!pb.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(pb.getDescription().getVersion());
		}

		final Plugin bp = server.getPluginManager().getPlugin("bPermissions");
		if (bp != null)
		{
			nameBuilder.append(" BP");
			if (!bp.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(bp.getDescription().getVersion());
		}

		final Plugin perm = server.getPluginManager().getPlugin("Permissions");
		if (perm != null)
		{
			nameBuilder.append(" P");
			if (!perm.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(perm.getDescription().getVersion());
		}

		return nameBuilder.toString();
	}

	private void connectToIRC(final Player player)
	{
		ircBot = new IrcBot(player, "Ess_" + player.getName(), buildIrcName());
	}

	private void sendErrors()
	{
		BufferedReader page = null;
		try
		{
			File bukkitFolder = plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
			if (bukkitFolder == null || !bukkitFolder.exists())
			{
				chatUser.sendMessage("Bukkit folder not found.");
				return;
			}
			File logFile = new File(bukkitFolder, "server.log");
			if (!logFile.exists())
			{
				chatUser.sendMessage("Server log not found.");
				return;
			}
			FileInputStream fis = new FileInputStream(logFile);
			if (logFile.length() > 1000000)
			{
				fis.skip(logFile.length() - 1000000);
			}
			page = new BufferedReader(new InputStreamReader(fis));
			final StringBuilder input = new StringBuilder();
			String line;
			Pattern pattern = Pattern.compile("^[0-9 :-]+\\[INFO\\].*");
			while ((line = page.readLine()) != null)
			{
				if (!pattern.matcher(line).matches())
				{
					input.append(line).append("\n");
				}
			}
			if (input.length() > 10000)
			{
				input.delete(0, input.length() - 10000);
			}
			final PastieUpload pastie = new PastieUpload();
			final String url = pastie.send(input.toString());
			String message = "Errors: " + url;
			chatUser.sendMessage("§6" + ircBot.getNick() + ": §7" + message);
			ircBot.sendMessage(message);
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, null, ex);
			chatUser.sendMessage(ex.getMessage());
		}
		finally
		{
			try
			{
				if (page != null)
				{
					page.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(EssentialsHelp.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void sendStartup()
	{
		BufferedReader page = null;
		try
		{
			File bukkitFolder = plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
			if (bukkitFolder == null || !bukkitFolder.exists())
			{
				chatUser.sendMessage("Bukkit folder not found.");
				return;
			}
			File logFile = new File(bukkitFolder, "server.log");
			if (!logFile.exists())
			{
				chatUser.sendMessage("Server log not found.");
				return;
			}
			FileInputStream fis = new FileInputStream(logFile);
			if (logFile.length() > 1000000)
			{
				fis.skip(logFile.length() - 1000000);
			}
			page = new BufferedReader(new InputStreamReader(fis));
			final StringBuilder input = new StringBuilder();
			String line;
			Pattern patternStart = Pattern.compile("^[0-9 :-]+\\[INFO\\] Starting minecraft server version.*");
			Pattern patternEnd = Pattern.compile("^[0-9 :-]+\\[INFO\\] Done \\([0-9.,]+s\\)! For help, type \"help\".*");
			boolean log = false;
			while ((line = page.readLine()) != null)
			{
				if (patternStart.matcher(line).matches())
				{
					if (input.length() > 0)
					{
						input.delete(0, input.length());
					}
					log = true;
				}
				if (log)
				{
					input.append(line).append("\n");
				}
				if (patternEnd.matcher(line).matches())
				{
					log = false;
				}
			}
			if (input.length() > 10000)
			{
				input.delete(0, input.length() - 10000);
			}
			final PastieUpload pastie = new PastieUpload();
			final String url = pastie.send(input.toString());
			String message = "Startup: " + url;
			chatUser.sendMessage("§6" + ircBot.getNick() + ": §7" + message);
			ircBot.sendMessage(message);
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, null, ex);
			chatUser.sendMessage(ex.getMessage());
		}
		finally
		{
			try
			{
				if (page != null)
				{
					page.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(EssentialsHelp.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void sendConfig()
	{
		BufferedReader page = null;
		try
		{
			File configFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials");
			if (!configFolder.exists())
			{
				chatUser.sendMessage("Essentials plugin folder not found.");
				return;
			}
			File configFile = new File(configFolder, "config.yml");
			if (!configFile.exists())
			{
				chatUser.sendMessage("Essentials config file not found.");
				return;
			}
			page = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), UTF8));
			final StringBuilder input = new StringBuilder();
			String line;
			while ((line = page.readLine()) != null)
			{
				input.append(line).append("\n");
			}
			final PastieUpload pastie = new PastieUpload();
			final String url = pastie.send(input.toString());
			String message = "Essentials config.yml: " + url;
			chatUser.sendMessage("§6" + ircBot.getNick() + ": §7" + message);
			ircBot.sendMessage(message);

		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, null, ex);
			chatUser.sendMessage(ex.getMessage());
		}
		finally
		{
			try
			{
				if (page != null)
				{
					page.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(EssentialsHelp.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.getPlayer() == chatUser)
		{
			boolean success = sendChatMessage(event.getPlayer(), event.getMessage());
			event.setCancelled(success);
			return;
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		chatUser = null;
		if (ircBot != null) {
			ircBot.quit();
			ircBot = null;
		}
		return;
	}
}
