package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;


public class XMPPManager extends Handler implements MessageListener, ChatManagerListener, IConf
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient EssentialsConf config;
	private transient XMPPConnection connection;
	private transient ChatManager chatManager;
	private final transient Map<String, Chat> chats = Collections.synchronizedMap(new HashMap<String, Chat>());
	private final transient JavaPlugin parent;
	private transient List<String> logUsers;
	private transient Level logLevel;
	private transient boolean ignoreLagMessages = true;
	
	public XMPPManager(final JavaPlugin parent)
	{
		super();
		this.parent = parent;
		config = new EssentialsConf(new File(parent.getDataFolder(), "config.yml"));
		config.setTemplateName("/config.yml", EssentialsXMPP.class);
		reloadConfig();
	}
	
	public void sendMessage(final String address, final String message)
	{
		if (address != null && !address.isEmpty())
		{
			try
			{
				startChat(address);
				final Chat chat = chats.get(address);
				if (chat != null)
				{
					chat.sendMessage(message.replaceAll("§[0-9a-f]", ""));
				}
			}
			catch (XMPPException ex)
			{
				disableChat(address);
			}
		}
	}
	
	@Override
	public void processMessage(final Chat chat, final Message msg)
	{
		// Normally we should log the error message
		// But we would create a loop if the connection to a log-user fails.
		if (msg.getType() != Message.Type.error && msg.getBody().length() > 0)
		{
			final String message = msg.getBody();
			switch (message.charAt(0))
			{
			case '@':
				sendPrivateMessage(chat, message);
				break;
			case '/':
				sendCommand(chat, message);
				break;
			default:
				parent.getServer().broadcastMessage("<X:" + EssentialsXMPP.getInstance().getUserByAddress(StringUtils.parseBareAddress(chat.getParticipant())) + "> " + message);
			}
		}
	}
	
	private void connect()
	{
		final String server = config.getString("xmpp.server");
		if (server == null || server.equals("example.com"))
		{
			LOGGER.log(Level.WARNING, "config broken for xmpp");
			return;
		}
		final int port = config.getInt("xmpp.port", 5222);
		final String serviceName = config.getString("xmpp.servicename", server);
		final String xmppuser = config.getString("xmpp.user");
		final String password = config.getString("xmpp.password");
		final ConnectionConfiguration connConf = new ConnectionConfiguration(server, port, serviceName);
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Connecting to xmpp server ").append(server).append(":").append(port);
		stringBuilder.append(" as user ").append(xmppuser).append(".");
		LOGGER.log(Level.INFO, stringBuilder.toString());
		connConf.setSASLAuthenticationEnabled(config.getBoolean("xmpp.sasl-enabled", false));
		connConf.setSendPresence(true);
		connConf.setReconnectionAllowed(true);
		connection = new XMPPConnection(connConf);
		try
		{
			connection.connect();
			connection.login(xmppuser, password);
			connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
			chatManager = connection.getChatManager();
			chatManager.addChatListener(this);
		}
		catch (XMPPException ex)
		{
			LOGGER.log(Level.WARNING, "Failed to connect to server: " + server, ex);
		}
	}
	
	public final void disconnect()
	{
		if (connection != null)
		{
			connection.disconnect(new Presence(Presence.Type.unavailable));
		}
	}
	
	@Override
	public void chatCreated(final Chat chat, final boolean createdLocally)
	{
		if (!createdLocally)
		{
			chat.addMessageListener(this);
			final Chat old = chats.put(StringUtils.parseBareAddress(chat.getParticipant()), chat);
			if (old != null)
			{
				old.removeMessageListener(this);
			}
		}
	}
	
	@Override
	public final void reloadConfig()
	{
		config.load();
		synchronized (chats)
		{
			disconnect();
			chats.clear();
			connect();
		}
		LOGGER.removeHandler(this);
		if (config.getBoolean("log-enabled", false))
		{
			LOGGER.addHandler(this);
			logUsers = config.getStringList("log-users", new ArrayList<String>());
			final String level = config.getString("log-level", "info");
			try
			{
				logLevel = Level.parse(level.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				logLevel = Level.INFO;
			}
			ignoreLagMessages = config.getBoolean("ignore-lag-messages", true);
		}
	}
	
	@Override
	public void publish(final LogRecord logRecord)
	{
		try
		{
			if (ignoreLagMessages && logRecord.getMessage().equals("Can't keep up! Did the system time change, or is the server overloaded?"))
			{
				return;
			}
			if (logRecord.getLevel().intValue() >= logLevel.intValue())
			{
				for (String user : logUsers)
				{
					startChat(user);
					final Chat chat = chats.get(user);
					if (chat != null)
					{
						chat.sendMessage(String.format("[" + logRecord.getLevel().getLocalizedName() + "] " + logRecord.getMessage(), logRecord.getParameters()));
					}
				}
			}
		}
		catch (Exception e)
		{
			// Ignore all exceptions
			// Otherwise we create a loop.
		}
	}
	
	@Override
	public void flush()
	{
		// Ignore this
	}
	
	@Override
	public void close() throws SecurityException
	{
		// Ignore this
	}
	
	private void startChat(final String address) throws XMPPException
	{
		if (chatManager == null)
		{
			return;
		}
		synchronized (chats)
		{
			if (!chats.containsKey(address))
			{
				final Chat chat = chatManager.createChat(address, this);
				if (chat == null)
				{
					throw new XMPPException("Could not start Chat with " + address);
				}
				chats.put(address, chat);
			}
		}
	}
	
	private void sendPrivateMessage(final Chat chat, final String message)
	{
		final String[] parts = message.split(" ", 2);
		if (parts.length == 2)
		{
			final List<Player> matches = parent.getServer().matchPlayer(parts[0].substring(1));
			
			if (matches.isEmpty())
			{
				try
				{
					chat.sendMessage("User " + parts[0] + " not found");
				}
				catch (XMPPException ex)
				{
					LOGGER.log(Level.WARNING, "Failed to send xmpp message.", ex);
				}
			}
			else
			{
				final String from = "[X:" + EssentialsXMPP.getInstance().getUserByAddress(StringUtils.parseBareAddress(chat.getParticipant())) + ">";
				for (Player p : matches)
				{
					p.sendMessage(from + p.getDisplayName() + "]  " + message);
				}
			}
		}
	}
	
	private void sendCommand(final Chat chat, final String message)
	{
		if (config.getStringList("op-users", new ArrayList<String>()).contains(StringUtils.parseBareAddress(chat.getParticipant())))
		{
			try
			{
				parent.getServer().dispatchCommand(Console.getCommandSender(parent.getServer()), message.substring(1));
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}
	
	private void disableChat(final String address)
	{
		final Chat chat = chats.get(address);
		if (chat != null)
		{
			chat.removeMessageListener(this);
			chats.remove(address);
		}
	}
}
