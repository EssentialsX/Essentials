package com.earth2me.essentials.update;

import com.earth2me.essentials.update.chat.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class EssentialsHelp implements Listener
{
	private transient Player chatUser;
	private final transient Server server;
	private final transient Plugin plugin;
	private transient IrcBot ircBot;
	private final transient Map<String, Command> commands = new HashMap<String, Command>();

	public EssentialsHelp(final Plugin plugin)
	{
		super();
		this.plugin = plugin;
		this.server = plugin.getServer();
		commands.put("!help", new HelpCommand());
		commands.put("!list", new ListCommand());
		commands.put("!startup", new StartupCommand(plugin));
		commands.put("!errors", new ErrorsCommand(plugin));
		commands.put("!config", new ConfigCommand(plugin));
	}

	public void registerEvents()
	{
		final PluginManager pluginManager = server.getPluginManager();
		pluginManager.registerEvents(this, plugin);
	}

	public void onCommand(final CommandSender sender)
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
		closeConnection();
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
			return handleAnswer(messageCleaned, player);
		}
		else
		{
			if (ircBot.isKicked())
			{
				closeConnection();
				return false;
			}
			final String lowMessage = messageCleaned.toLowerCase(Locale.ENGLISH);
			if (lowMessage.startsWith("!quit"))
			{
				closeConnection();
				player.sendMessage("Connection closed.");
				return true;
			}
			if (!ircBot.isConnected() || ircBot.getChannels().length == 0)
			{
				return false;
			}
			if (handleCommands(lowMessage, player))
			{
				return true;
			}
			ircBot.sendMessage(messageCleaned);
			chatUser.sendMessage("§6" + ircBot.getNick() + ": §7" + messageCleaned);
			return true;
		}
	}

	private void closeConnection()
	{
		chatUser = null;
		if (ircBot != null)
		{
			ircBot.quit();
			ircBot = null;
		}
	}

	private boolean handleAnswer(final String message, final Player player)
	{
		if (message.equalsIgnoreCase("yes"))
		{
			player.sendMessage("Connecting...");
			connectToIRC(player);
			return true;
		}
		if (message.equalsIgnoreCase("no") || message.equalsIgnoreCase("!quit"))
		{
			chatUser = null;
			return true;
		}
		return false;
	}

	private boolean handleCommands(final String lowMessage, final Player player)
	{
		final String[] parts = lowMessage.split(" ");
		if (commands.containsKey(parts[0]))
		{
			commands.get(parts[0]).run(ircBot, player);
			return true;
		}
		return false;
	}

	private void connectToIRC(final Player player)
	{
		ircBot = new IrcBot(player, "Ess_" + player.getName(), UsernameUtil.createUsername(player));
	}

	@EventHandler
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		if (event.getPlayer() == chatUser)
		{
			final boolean success = sendChatMessage(event.getPlayer(), event.getMessage());
			event.setCancelled(success);
		}
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		closeConnection();
	}
}
