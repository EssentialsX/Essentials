package com.earth2me.essentials.update;

import f00f.net.irc.martyr.GenericAutoService;
import f00f.net.irc.martyr.IRCConnection;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.clientstate.Channel;
import f00f.net.irc.martyr.clientstate.Member;
import f00f.net.irc.martyr.commands.InviteCommand;
import f00f.net.irc.martyr.commands.KickCommand;
import f00f.net.irc.martyr.commands.MessageCommand;
import f00f.net.irc.martyr.commands.NoticeCommand;
import f00f.net.irc.martyr.commands.QuitCommand;
import f00f.net.irc.martyr.commands.TopicCommand;
import f00f.net.irc.martyr.errors.GenericJoinError;
import f00f.net.irc.martyr.services.AutoJoin;
import f00f.net.irc.martyr.services.AutoReconnect;
import f00f.net.irc.martyr.services.AutoRegister;
import f00f.net.irc.martyr.services.AutoResponder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
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


public class EssentialsHelp extends PlayerListener
{
	private transient Player chatUser;
	private transient IRCConnection connection;
	private transient AutoReconnect autoReconnect;
	private transient boolean shouldQuit = false;
	private final transient Server server;
	private final transient Plugin plugin;
	private final static Charset UTF8 = Charset.forName("utf-8");

	public EssentialsHelp(Plugin plugin)
	{
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
				connection = null;
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
		if (autoReconnect != null && connection != null)
		{
			autoReconnect.disable();
			shouldQuit = true;
			connection.disconnect();
		}
	}

	private void sendChatMessage(final Player player, final String message)
	{
		final String messageCleaned = message.trim();
		if (messageCleaned.isEmpty())
		{
			return;
		}
		if (connection == null)
		{
			if (messageCleaned.equalsIgnoreCase("yes"))
			{
				player.sendMessage("Connecting...");
				connectToIRC(player);
			}
			if (messageCleaned.equalsIgnoreCase("no") || message.equalsIgnoreCase("!quit"))
			{
				chatUser = null;
			}
		}
		else
		{
			final String lowMessage = messageCleaned.toLowerCase();
			if (lowMessage.startsWith("!quit"))
			{
				chatUser = null;
				autoReconnect.disable();
				shouldQuit = true;
				connection.sendCommand(new QuitCommand("Connection closed by user."));
				player.sendMessage("Connection closed.");
				return;
			}
			if (!connection.getClientState().getChannels().hasMoreElements())
			{
				player.sendMessage("Not connected yet!");
				return;
			}
			if (lowMessage.startsWith("!list"))
			{
				final Enumeration members = ((Channel)connection.getClientState().getChannels().nextElement()).getMembers();
				final StringBuilder sb = new StringBuilder();
				while (members.hasMoreElements())
				{
					if (sb.length() > 0)
					{
						sb.append("§f, ");
					}
					final Member member = (Member)members.nextElement();
					if (member.hasOps() || member.hasVoice())
					{
						sb.append("§6");
					}
					else
					{
						sb.append("§7");
					}
					sb.append(member.getNick());
				}
				player.sendMessage(sb.toString());
				return;
			}
			if (lowMessage.startsWith("!help"))
			{
				player.sendMessage("Commands: (Note: Files send to the chat will be public viewable.)");
				player.sendMessage("!errors - Send the last server errors to the chat.");
				player.sendMessage("!startup - Send the last startup messages to the chat.");
				player.sendMessage("!config - Sends your Essentials config to the chat.");
				player.sendMessage("!list - List all players in chat.");
				player.sendMessage("!quit - Leave chat.");
				return;
			}
			if (lowMessage.startsWith("!errors"))
			{
				sendErrors();
				return;
			}
			if (lowMessage.startsWith("!startup"))
			{
				sendStartup();
				return;
			}
			if (lowMessage.startsWith("!config"))
			{
				sendConfig();
				return;
			}
			final Channel channel = (Channel)connection.getClientState().getChannels().nextElement();
			connection.sendCommand(new MessageCommand(channel.getName(), messageCleaned));
			chatUser.sendMessage("§6" + connection.getClientState().getNick().getNick() + ": §7" + messageCleaned);
		}
	}

	private void connectToIRC(final Player player)
	{
		connection = new IRCConnection();
		// Required services
		new AutoResponder(connection);
		int versionNumber = 0;
		final StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(player.getName());

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

		new AutoRegister(connection, "Ess_" + player.getName(), "esshelp", nameBuilder.toString());

		autoReconnect = new AutoReconnect(connection);
		new KickAutoJoin(connection, "#essentials");

		new IRCListener(connection);
		autoReconnect.go("irc.esper.net", 6667);
	}

	private void handleIRCmessage(final String nick, final String message)
	{

		if (chatUser != null)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("§6");
			sb.append(nick);
			sb.append(": §7");
			final String coloredmessage = message.replace("\u000300", "§f").replace("\u000301", "§0").replace("\u000302", "§1").replace("\u000303", "§2").replace("\u000304", "§c").replace("\u000305", "§4").replace("\u000306", "§5").replace("\u000307", "§6").replace("\u000308", "§e").replace("\u000309", "§a").replace("\u00030", "§f").replace("\u000310", "§b").replace("\u000311", "§f").replace("\u000312", "§9").replace("\u000313", "§d").replace("\u000314", "§8").replace("\u000315", "§7").replace("\u00031", "§0").replace("\u00032", "§1").replace("\u00033", "§2").replace("\u00034", "§c").replace("\u00035", "§4").replace("\u00036", "§5").replace("\u00037", "§6").replace("\u00038", "§e").replace("\u00039", "§a").replace("\u0003", "§7");
			sb.append(coloredmessage);
			chatUser.sendMessage(sb.toString());
		}
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
				fis.skip(logFile.length()-1000000);
			}
			page = new BufferedReader(new InputStreamReader(fis));
			final StringBuilder input = new StringBuilder();
			String line;
			Pattern pattern = Pattern.compile("^[0-9 :-]+\\[INFO\\].*");
			while ((line = page.readLine()) != null)
			{
				if (!pattern.matcher(line).matches()) {
					input.append(line).append("\n");
				}
			}
			if (input.length()>10000) {
				input.delete(0, input.length()-10000);
			}
			final PastieUpload pastie = new PastieUpload();
			final String url = pastie.send(input.toString());
			final Channel channel = (Channel)connection.getClientState().getChannels().nextElement();
			String message = "Errors: " + url;
			chatUser.sendMessage("§6" + connection.getClientState().getNick().getNick() + ": §7" + message);
			connection.sendCommand(new MessageCommand(channel.getName(), message));
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
				fis.skip(logFile.length()-1000000);
			}
			page = new BufferedReader(new InputStreamReader(fis));
			final StringBuilder input = new StringBuilder();
			String line;
			Pattern patternStart = Pattern.compile("^[0-9 :-]+\\[INFO\\] Starting minecraft server version.*");
			Pattern patternEnd = Pattern.compile("^[0-9 :-]+\\[INFO\\] Done \\([0-9.,]+s\\)! For help, type \"help\".*");
			boolean log = false;
			while ((line = page.readLine()) != null)
			{
				if (patternStart.matcher(line).matches()) {
					if (input.length() > 0) {
						input.delete(0, input.length());
					}
					log = true;
				}
				if (log) {
					input.append(line).append("\n");
				}
				if (patternEnd.matcher(line).matches()) {
					log = false;
				}
			}
			if (input.length()>10000) {
				input.delete(0, input.length()-10000);
			}
			final PastieUpload pastie = new PastieUpload();
			final String url = pastie.send(input.toString());
			final Channel channel = (Channel)connection.getClientState().getChannels().nextElement();
			String message = "Startup: " + url;
			chatUser.sendMessage("§6" + connection.getClientState().getNick().getNick() + ": §7" + message);
			connection.sendCommand(new MessageCommand(channel.getName(), message));
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
			final Channel channel = (Channel)connection.getClientState().getChannels().nextElement();
			String message = "Essentials config.yml: " + url;
			chatUser.sendMessage("§6" + connection.getClientState().getNick().getNick() + ": §7" + message);
			connection.sendCommand(new MessageCommand(channel.getName(), message));

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
			sendChatMessage(event.getPlayer(), event.getMessage());
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		chatUser = null;
		if (autoReconnect != null)
		{
			autoReconnect.disable();
		}
		shouldQuit = true;
		if (connection != null)
		{
			connection.sendCommand(new QuitCommand("Connection closed by user."));
		}
		return;
	}


	class KickAutoJoin extends AutoJoin
	{
		private String channel;

		public KickAutoJoin(IRCConnection connection, String channel)
		{
			super(connection, channel);
			this.channel = channel;
		}

		@Override
		protected void updateCommand(InCommand command_o)
		{
			if (command_o instanceof KickCommand)
			{
				final KickCommand kickCommand = (KickCommand)command_o;

				if (kickCommand.kickedUs(getConnection().getClientState()))
				{
					if (Channel.areEqual(kickCommand.getChannel(), channel))
					{
						chatUser.sendMessage("You have been kicked from the channel: " + kickCommand.getComment());
						chatUser = null;
						autoReconnect.disable();
						shouldQuit = true;
						connection.sendCommand(new QuitCommand("Connection closed by user."));
					}
				}
			}
			else if (command_o instanceof GenericJoinError)
			{
				GenericJoinError joinErr = (GenericJoinError)command_o;

				if (Channel.areEqual(joinErr.getChannel(), channel))
				{
					scheduleJoin();
				}
			}
			else if (command_o instanceof InviteCommand)
			{
				InviteCommand invite = (InviteCommand)command_o;
				if (!getConnection().getClientState().isOnChannel(invite.getChannel()))
				{
					performJoin();
				}
			}
		}
	}


	class IRCListener extends GenericAutoService
	{
		public IRCListener(final IRCConnection connection)
		{
			super(connection);
			enable();
		}

		@Override
		protected void updateState(final State state)
		{
			if (state == State.UNCONNECTED && shouldQuit)
			{
				connection = null;
				shouldQuit = false;
			}
		}

		@Override
		protected void updateCommand(final InCommand command)
		{
			if (command instanceof MessageCommand)
			{
				final MessageCommand msg = (MessageCommand)command;
				EssentialsHelp.this.handleIRCmessage(msg.getSource().getNick(), msg.getMessage());
			}
			if (command instanceof TopicCommand)
			{
				final TopicCommand msg = (TopicCommand)command;
				EssentialsHelp.this.handleIRCmessage(msg.getChannel(), msg.getTopic());
			}
			if (command instanceof NoticeCommand)
			{
				final NoticeCommand msg = (NoticeCommand)command;
				EssentialsHelp.this.handleIRCmessage(msg.getFrom().getNick(), msg.getNotice());
			}
		}
	}
}
