package net.essentialsx.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.earth2me.essentials.utils.FormatUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.hooks.EventListener;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.discord.interactions.InteractionController;
import net.essentialsx.discord.interactions.commands.ExecuteCommand;
import net.essentialsx.discord.interactions.commands.ListCommand;
import net.essentialsx.discord.interactions.commands.MessageCommand;
import net.essentialsx.discord.listeners.BukkitListener;
import net.essentialsx.discord.listeners.DiscordCommandDispatcher;
import net.essentialsx.discord.listeners.DiscordListener;
import net.essentialsx.discord.util.ConsoleInjector;
import net.essentialsx.discord.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsJDA {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final EssentialsDiscord plugin;

    private JDA jda;
    private Guild guild;
    private TextChannel primaryChannel;
    private WebhookClient consoleWebhook;
    private String lastConsoleId;
    private final Map<DiscordMessageEvent.MessageType, String> typeToChannelId = new HashMap<>();
    private final Map<String, WebhookClient> channelIdToWebhook = new HashMap<>();
    private ConsoleInjector injector;
    private DiscordCommandDispatcher commandDispatcher;
    private InteractionController interactionController;

    public EssentialsJDA(EssentialsDiscord plugin) {
        this.plugin = plugin;
    }

    public TextChannel getChannel(String key, boolean primaryFallback) {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getChannelId(key));
        if (channel == null && primaryFallback) {
            channel = primaryChannel;
        }
        return channel;
    }

    public WebhookMessage getWebhookMessage(String message) {
        return getWebhookMessage(message, jda.getSelfUser().getAvatarUrl(), getSettings().getConsoleWebhookName(), false);
    }

    public WebhookMessage getWebhookMessage(String message, String avatarUrl, String name, boolean groupMentions) {
        return new WebhookMessageBuilder()
                .setAvatarUrl(avatarUrl)
                .setAllowedMentions(groupMentions ? DiscordUtil.ALL_MENTIONS_WEBHOOK : DiscordUtil.NO_GROUP_MENTIONS_WEBHOOK)
                .setUsername(name)
                .setContent(message)
                .build();
    }

    public void sendMessage(DiscordMessageEvent event, String message, boolean groupMentions) {
        final TextChannel channel = getChannel(event.getType().getKey(), true);

        final String strippedContent = FormatUtil.stripFormat(message);

        final String webhookChannelId = typeToChannelId.get(event.getType());
        if (webhookChannelId != null) {
            final WebhookClient client = channelIdToWebhook.get(webhookChannelId);
            if (client != null) {
                final String avatarUrl = event.getAvatarUrl() != null ? event.getAvatarUrl() : jda.getSelfUser().getAvatarUrl();
                final String name = event.getName() != null ? event.getName() : guild.getSelfMember().getEffectiveName();
                client.send(getWebhookMessage(strippedContent, avatarUrl, name, groupMentions));
                return;
            }
        }

        if (!channel.canTalk()) {
            logger.warning(tl("discordNoSendPermission", channel.getName()));
            return;
        }
        channel.sendMessage(strippedContent)
                .allowedMentions(groupMentions ? null : DiscordUtil.NO_GROUP_MENTIONS)
                .queue();
    }

    public void startup() throws LoginException, InterruptedException {
        shutdown();

        logger.log(Level.INFO, tl("discordLoggingIn"));
        if (plugin.getSettings().getBotToken().replace("INSERT-TOKEN-HERE", "").trim().isEmpty()) {
            throw new IllegalArgumentException(tl("discordErrorNoToken"));
        }

        jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(new DiscordListener(this))
                .setContextEnabled(false)
                .setRawEventsEnabled(true)
                .build()
                .awaitReady();
        updatePresence();
        logger.log(Level.INFO, tl("discordLoggingInDone", jda.getSelfUser().getAsTag()));

        if (jda.getGuilds().isEmpty()) {
            throw new IllegalArgumentException(tl("discordErrorNoGuildSize"));
        }

        guild = jda.getGuildById(plugin.getSettings().getGuildId());
        if (guild == null) {
            throw new IllegalArgumentException(tl("discordErrorNoGuild"));
        }

        interactionController = new InteractionController(this);
        interactionController.registerCommand(new ExecuteCommand(this));
        interactionController.registerCommand(new MessageCommand(this));
        interactionController.registerCommand(new ListCommand(this));

        updatePrimaryChannel();

        updateConsoleRelay();

        updateTypesRelay();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), plugin);
    }

    public void updatePrimaryChannel() {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getPrimaryChannelId());
        if (channel == null) {
            channel = guild.getDefaultChannel();
            if (channel == null || !channel.canTalk()) {
                throw new RuntimeException(tl("discordErrorNoPerms"));
            }
        }
        primaryChannel = channel;
    }

    public void updatePresence() {
        jda.getPresence().setPresence(plugin.getSettings().getStatus(), plugin.getSettings().getStatusActivity());
    }

    public void updateTypesRelay() {
        if (!getSettings().isShowAvatar() && !getSettings().isShowName()) {
            for (WebhookClient webhook : channelIdToWebhook.values()) {
                webhook.close();
            }
            typeToChannelId.clear();
            channelIdToWebhook.clear();
            return;
        }

        for (DiscordMessageEvent.MessageType type : DiscordMessageEvent.MessageType.DefaultTypes.values()) {
            if (!type.isPlayer()) {
                continue;
            }

            final TextChannel channel = getChannel(type.getKey(), true);
            if (channel.getId().equals(typeToChannelId.get(type))) {
                continue;
            }

            final String webhookName = "EssX Advanced Relay";
            Webhook webhook = DiscordUtil.getAndCleanWebhooks(channel, webhookName).join();
            webhook = webhook == null ? DiscordUtil.createWebhook(channel, webhookName).join() : webhook;
            if (webhook == null) {
                final WebhookClient current = channelIdToWebhook.get(channel.getId());
                if (current != null) {
                    current.close();
                }
                channelIdToWebhook.remove(channel.getId());
                continue;
            }
            typeToChannelId.put(type, channel.getId());
            channelIdToWebhook.put(channel.getId(), DiscordUtil.getWebhookClient(webhook.getIdLong(), webhook.getToken(), jda.getHttpClient()));
        }
    }

    public void updateConsoleRelay() {
        final String consoleDef = getSettings().getConsoleChannelDef();
        final Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(consoleDef);
        final long webhookId;
        final String webhookToken;
        if (matcher.matches()) {
            webhookId = Long.parseUnsignedLong(matcher.group(1));
            webhookToken = matcher.group(2);
            if (commandDispatcher != null) {
                jda.removeEventListener(commandDispatcher);
                commandDispatcher = null;
            }
        } else {
            final TextChannel channel = getChannel(consoleDef, false);
            if (channel != null) {
                if (channel.getId().equals(lastConsoleId)) {
                    return;
                }

                final String webhookName = "EssX Console Relay";
                Webhook webhook = DiscordUtil.getAndCleanWebhooks(channel, webhookName).join();
                webhook = webhook == null ? DiscordUtil.createWebhook(channel, webhookName).join() : webhook;
                if (webhook == null) {
                    logger.info(tl("discordErrorLoggerNoPerms"));
                    return;
                }
                webhookId = webhook.getIdLong();
                webhookToken = webhook.getToken();
                lastConsoleId = channel.getId();
                if (getSettings().isConsoleCommandRelay()) {
                    if (commandDispatcher == null) {
                        commandDispatcher = new DiscordCommandDispatcher(this);
                        jda.addEventListener(commandDispatcher);
                    }
                    commandDispatcher.setChannelId(channel.getId());
                } else if (commandDispatcher != null) {
                    jda.removeEventListener(commandDispatcher);
                    commandDispatcher = null;
                }
            } else if (!getSettings().getConsoleChannelDef().equals("none") && !getSettings().getConsoleChannelDef().startsWith("0")) {
                logger.info(tl("discordErrorLoggerInvalidChannel"));
                shutdownConsoleRelay(true);
                return;
            } else {
                // It's either not configured at all or knowingly disabled.
                shutdownConsoleRelay(true);
                return;
            }
        }

        shutdownConsoleRelay(false);
        consoleWebhook = DiscordUtil.getWebhookClient(webhookId, webhookToken, jda.getHttpClient());
        if (injector == null) {
            injector = new ConsoleInjector(this);
            injector.start();
        }
    }

    private void shutdownConsoleRelay(final boolean closeInjector) {
        if (consoleWebhook != null && !consoleWebhook.isShutdown()) {
            consoleWebhook.close();
        }
        consoleWebhook = null;

        if (closeInjector) {
            if (injector != null) {
                injector.remove();
                injector = null;
            }

            if (commandDispatcher != null) {
                jda.removeEventListener(commandDispatcher);
                commandDispatcher = null;
            }
        }
    }

    public void shutdown() {
        if (interactionController != null) {
            interactionController.shutdown();
        }

        if (jda != null) {
            for (Object obj : jda.getRegisteredListeners()) {
                if (!(obj instanceof EventListener)) { // Yeah bro I wish I knew too :/
                    jda.removeEventListener(obj);
                }
            }
            HandlerList.unregisterAll(plugin);
            jda.shutdown();
        }

        shutdownConsoleRelay(true);
    }

    public JDA getJda() {
        return jda;
    }

    public Guild getGuild() {
        return guild;
    }

    public EssentialsDiscord getPlugin() {
        return plugin;
    }

    public DiscordSettings getSettings() {
        return plugin.getSettings();
    }

    public WebhookClient getConsoleWebhook() {
        return consoleWebhook;
    }

    public boolean isDebug() {
        return plugin.getEss().getSettings().isDebug();
    }
}
