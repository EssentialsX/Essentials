package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.earth2me.essentials.I18n.tlLiteral;

public class XMPPManager extends Handler implements MessageListener, ChatManagerListener, IConf {
    private static final Logger logger = EssentialsXMPP.getWrappedLogger();
    private static final SimpleFormatter formatter = new SimpleFormatter();
    private final transient EssentialsConfiguration config;
    private final transient Map<String, Chat> chats = Collections.synchronizedMap(new HashMap<>());
    private final transient Set<LogRecord> logrecords = Collections.synchronizedSet(new HashSet<>());
    private final transient IEssentialsXMPP parent;
    private transient XMPPConnection connection;
    private transient ChatManager chatManager;
    private transient List<String> logUsers;
    private transient Level logLevel;
    private transient boolean ignoreLagMessages = true;
    private transient Thread loggerThread;
    private transient boolean threadrunning = true;

    XMPPManager(final IEssentialsXMPP parent) {
        super();
        this.parent = parent;
        config = new EssentialsConfiguration(new File(parent.getDataFolder(), "config.yml"), "/config.yml", EssentialsXMPP.class);
        reloadConfig();
    }

    boolean sendMessage(final String address, final String message) {
        if (address != null && !address.isEmpty()) {
            try {
                startChat(address);
                final Chat chat;
                synchronized (chats) {
                    chat = chats.get(address);
                }
                if (chat != null) {
                    if (!connection.isConnected()) {
                        disconnect();
                        connect();
                    }
                    chat.sendMessage(FormatUtil.stripFormat(message));
                    return true;
                }
            } catch (final XMPPException ex) {
                disableChat(address);
            }
        }
        return false;
    }

    @Override
    public void processMessage(final Chat chat, final Message msg) {
        // Normally we should log the error message
        // But we would create a loop if the connection to a log-user fails.
        if (msg.getType() != Message.Type.error && msg.getBody().length() > 0) {
            final String message = msg.getBody();
            switch (message.charAt(0)) {
                case '@':
                    sendPrivateMessage(chat, message);
                    break;
                case '/':
                    sendCommand(chat, message);
                    break;
                default:
                    final IUser sender = parent.getUserByAddress(StringUtils.parseBareAddress(chat.getParticipant()));
                    parent.broadcastMessage(sender, "=" + sender.getBase().getDisplayName() + ": " + message, StringUtils.parseBareAddress(chat.getParticipant()));
                    break;
            }
        }
    }

    private boolean connect() {
        final String server = config.getString("xmpp.server", null);
        if (server == null || server.equals("example.com")) {
            logger.log(Level.WARNING, tlLiteral("xmppNotConfigured"));
            return false;
        }
        final int port = config.getInt("xmpp.port", 5222);
        final String serviceName = config.getString("xmpp.servicename", server);
        final String xmppuser = config.getString("xmpp.user", null);
        final String password = config.getString("xmpp.password", null);
        final boolean requireTLS = config.getBoolean("xmpp.require-server-tls", false);
        final ConnectionConfiguration connConf = new ConnectionConfiguration(server, port, serviceName);
        final String stringBuilder = "Connecting to xmpp server " + server + ":" + port + " as user " + xmppuser + ".";
        logger.log(Level.INFO, stringBuilder);
        connConf.setSASLAuthenticationEnabled(config.getBoolean("xmpp.sasl-enabled", false));
        connConf.setSendPresence(true);
        connConf.setReconnectionAllowed(true);
        connConf.setDebuggerEnabled(config.getBoolean("debug", false));
        if (requireTLS) {
            // "enabled" (TLS optional) is the default
            connConf.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        }
        connection = new XMPPConnection(connConf);
        try {
            connection.connect();

            connection.login(xmppuser, password, "Essentials-XMPP");
            connection.sendPacket(new Presence(Presence.Type.available, "No one online.", 2, Presence.Mode.available));

            connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
            chatManager = connection.getChatManager();
            chatManager.addChatListener(this);
            return true;
        } catch (final XMPPException ex) {
            logger.log(Level.WARNING, "Failed to connect to server: " + server, ex);
            logger.log(Level.WARNING, "Connected: " + connection.isConnected());
            logger.log(Level.WARNING, "Secure: " + connection.isSecureConnection());
            logger.log(Level.WARNING, "Using TLS: " + connection.isUsingTLS());
            logger.log(Level.WARNING, "Authenticated: " + connection.getSASLAuthentication().isAuthenticated());
            return false;
        }
    }

    final void disconnect() {
        if (loggerThread != null) {
            loggerThread.interrupt();
        }
        if (chatManager != null) {
            chatManager.removeChatListener(this);
            chatManager = null;
        }
        if (connection != null) {
            connection.disconnect(new Presence(Presence.Type.unavailable));
        }
    }

    final void updatePresence() {
        if (connection == null) {
            parent.getEss().getLogger().warning(AdventureUtil.miniToLegacy(tlLiteral("xmppNotConfigured")));
            return;
        }

        final int usercount;
        final StringBuilder stringBuilder = new StringBuilder();

        usercount = parent.getEss().getOnlinePlayers().size();

        if (usercount == 0) {
            final String presenceMsg = "No one online.";
            connection.sendPacket(new Presence(Presence.Type.available, presenceMsg, 2, Presence.Mode.dnd));
        }
        if (usercount == 1) {
            final String presenceMsg = "1 player online.";
            connection.sendPacket(new Presence(Presence.Type.available, presenceMsg, 2, Presence.Mode.available));
        }
        if (usercount > 1) {
            stringBuilder.append(usercount).append(" players online.");
            connection.sendPacket(new Presence(Presence.Type.available, stringBuilder.toString(), 2, Presence.Mode.available));
        }
    }

    @Override
    public void chatCreated(final Chat chat, final boolean createdLocally) {
        if (!createdLocally) {
            chat.addMessageListener(this);
            final Chat old = chats.put(StringUtils.parseBareAddress(chat.getParticipant()), chat);
            if (old != null) {
                old.removeMessageListener(this);
            }
        }
    }

    @Override
    public final void reloadConfig() {
        logger.removeHandler(this);
        config.load();
        synchronized (chats) {
            disconnect();
            chats.clear();
            if (!connect()) {
                return;
            }
            startLoggerThread();
        }
        if (config.getBoolean("log-enabled", false)) {
            logger.addHandler(this);
            logUsers = config.getList("log-users", String.class);
            final String level = config.getString("log-level", "info");
            try {
                logLevel = Level.parse(level.toUpperCase(Locale.ENGLISH));
            } catch (final IllegalArgumentException e) {
                logLevel = Level.INFO;
            }
            ignoreLagMessages = config.getBoolean("ignore-lag-messages", true);
        }
    }

    @Override
    public void publish(final LogRecord logRecord) {
        try {
            if (ignoreLagMessages && logRecord.getMessage().equals("Can't keep up! Did the system time change, or is the server overloaded?")) {
                return;
            }
            if (logRecord.getLevel().intValue() >= logLevel.intValue()) {
                synchronized (logrecords) {
                    logrecords.add(logRecord);
                }
            }
        } catch (final Exception ignored) {
            // Ignore all exceptions
            // Otherwise we create a loop.
        }
    }

    @Override
    public void flush() {
        // Ignore this
    }

    @Override
    public void close() throws SecurityException {
        // Ignore this
    }

    private void startLoggerThread() {
        loggerThread = new Thread(() -> {
            final Set<LogRecord> copy = new HashSet<>();
            final Set<String> failedUsers = new HashSet<>();
            while (threadrunning) {
                synchronized (logrecords) {
                    if (!logrecords.isEmpty()) {
                        copy.addAll(logrecords);
                        logrecords.clear();
                    }
                }
                if (!copy.isEmpty()) {
                    for (final String user : logUsers) {
                        try {
                            XMPPManager.this.startChat(user);
                            for (final LogRecord logRecord : copy) {
                                final String message = formatter.format(logRecord);
                                if (!XMPPManager.this.sendMessage(user, FormatUtil.stripLogColorFormat(message))) {
                                    failedUsers.add(user);
                                    break;
                                }

                            }
                        } catch (final XMPPException ex) {
                            failedUsers.add(user);
                            logger.removeHandler(XMPPManager.this);
                            logger.log(Level.SEVERE, "Failed to deliver log message! Disabling logging to XMPP.", ex);
                        }
                    }
                    logUsers.removeAll(failedUsers);
                    if (logUsers.isEmpty()) {
                        logger.removeHandler(XMPPManager.this);
                        threadrunning = false;
                    }
                    copy.clear();
                }
                try {
                    Thread.sleep(2000);
                } catch (final InterruptedException ex) {
                    threadrunning = false;
                }
            }
            logger.removeHandler(XMPPManager.this);
        });
        loggerThread.start();
    }

    private void startChat(final String address) throws XMPPException {
        if (chatManager == null) {
            return;
        }
        synchronized (chats) {
            if (!chats.containsKey(address)) {
                final Chat chat = chatManager.createChat(address, this);
                if (chat == null) {
                    throw new XMPPException("Could not start Chat with " + address);
                }
                chats.put(address, chat);
            }
        }
    }

    private void sendPrivateMessage(final Chat chat, final String message) {
        final String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            final List<Player> matches = parent.getServer().matchPlayer(parts[0].substring(1));

            if (matches.isEmpty()) {
                try {
                    chat.sendMessage("User " + parts[0] + " not found");
                } catch (final XMPPException ex) {
                    logger.log(Level.WARNING, "Failed to send xmpp message.", ex);
                }
            } else {
                final String from = "[" + parent.getUserByAddress(StringUtils.parseBareAddress(chat.getParticipant())) + ">";
                for (final Player p : matches) {
                    p.sendMessage(from + p.getDisplayName() + "]  " + message);
                }
            }
        }
    }

    private void sendCommand(final Chat chat, final String message) {
        if (config.getList("op-users", String.class).contains(StringUtils.parseBareAddress(chat.getParticipant()))) {
            parent.getServer().getScheduler().runTask(parent, () -> {
                try {
                    parent.getServer().dispatchCommand(Console.getInstance().getCommandSender(), message.substring(1));
                } catch (final Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            });
        }
    }

    private void disableChat(final String address) {
        final Chat chat = chats.get(address);
        if (chat != null) {
            chat.removeMessageListener(this);
            chats.remove(address);
        }
    }

    public boolean isConfigValid() {
        final String server = config.getString("xmpp.server", null);
        return server != null && !server.equals("example.com");
    }
}
