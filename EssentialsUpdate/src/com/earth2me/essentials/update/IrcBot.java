package com.earth2me.essentials.update;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;


public class IrcBot extends PircBot
{
	private static final String channel = "#essentials";
	private static final int port = 6667;
	private static final String server = "irc.esper.net";
	private transient boolean reconnect = true;
	private transient Player player;
	private transient boolean kicked = false;

	public IrcBot(Player player, final String nickName, final String versionString)
	{
		this.player = player;
		setName(nickName);
		setLogin("esshelp");
		setVersion(versionString);
		connect();
		joinChannel(channel);
	}

	private void connect()
	{
		try
		{
			connect(server, port);
			return;
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		catch (IrcException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}

	}

	public void quit()
	{
		reconnect = false;
		disconnect();
	}

	@Override
	protected void onConnect()
	{
		reconnect = true;
	}

	@Override
	protected void onDisconnect()
	{
		super.onDisconnect();
		if (reconnect)
		{
			connect();
		}
	}

	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
	{
		if (recipientNick.equals(getNick()))
		{
			player.sendMessage("You have been kicked from the channel: " + reason);
			quit();
			kicked = true;
		}
	}

	public boolean isKicked()
	{
		return kicked;
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		player.sendMessage(formatChatMessage(sender, message, false));
	}

	@Override
	protected void onAction(String sender, String login, String hostname, String target, String action)
	{
		player.sendMessage(formatChatMessage(sender, action, true));
	}

	@Override
	protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice)
	{
		player.sendMessage(formatChatMessage(sourceNick, notice, false));
	}

	@Override
	protected void onTopic(String channel, String topic, String setBy, long date, boolean changed)
	{
		player.sendMessage(formatChatMessage(channel, topic, false));
	}

	public String formatChatMessage(String nick, String message, boolean action)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("§6");
		if (action)
		{
			sb.append('*');
		}
		sb.append(nick);
		if (!action)
		{
			sb.append(':');
		}
		sb.append(" §7");
		sb.append(replaceColors(message));
		return sb.toString();
	}

	private String replaceColors(String message)
	{
		String m = Colors.removeFormatting(message);
		m = m.replaceAll("\u000310(,(0?[0-9]|1[0-5]))?", "§b");
		m = m.replaceAll("\u000311(,(0?[0-9]|1[0-5]))?", "§f");
		m = m.replaceAll("\u000312(,(0?[0-9]|1[0-5]))?", "§9");
		m = m.replaceAll("\u000313(,(0?[0-9]|1[0-5]))?", "§d");
		m = m.replaceAll("\u000314(,(0?[0-9]|1[0-5]))?", "§8");
		m = m.replaceAll("\u000315(,(0?[0-9]|1[0-5]))?", "§7");
		m = m.replaceAll("\u00030?1(,(0?[0-9]|1[0-5]))?", "§0");
		m = m.replaceAll("\u00030?2(,(0?[0-9]|1[0-5]))?", "§1");
		m = m.replaceAll("\u00030?3(,(0?[0-9]|1[0-5]))?", "§2");
		m = m.replaceAll("\u00030?4(,(0?[0-9]|1[0-5]))?", "§c");
		m = m.replaceAll("\u00030?5(,(0?[0-9]|1[0-5]))?", "§4");
		m = m.replaceAll("\u00030?6(,(0?[0-9]|1[0-5]))?", "§5");
		m = m.replaceAll("\u00030?7(,(0?[0-9]|1[0-5]))?", "§6");
		m = m.replaceAll("\u00030?8(,(0?[0-9]|1[0-5]))?", "§e");
		m = m.replaceAll("\u00030?9(,(0?[0-9]|1[0-5]))?", "§a");
		m = m.replaceAll("\u00030?0(,(0?[0-9]|1[0-5]))?", "§f");
		m = Colors.removeColors(m);
		return m;
	}

	public void sendMessage(String message)
	{
		sendMessage(channel, message);
	}

	public User[] getUsers()
	{
		return getUsers(channel);
	}
}
