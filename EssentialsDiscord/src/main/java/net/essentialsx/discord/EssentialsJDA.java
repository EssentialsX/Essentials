package net.essentialsx.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.earth2me.essentials.utils.FormatUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.discord.interactions.InteractionController;
import net.essentialsx.discord.interactions.commands.ExecuteCommand;
import net.essentialsx.discord.interactions.commands.MessageCommand;
import net.essentialsx.discord.listeners.BukkitListener;
import net.essentialsx.discord.listeners.DiscordListener;
import net.essentialsx.discord.util.ConsoleInjector;
import net.essentialsx.discord.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class EssentialsJDA {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final EssentialsDiscord plugin;

    private JDA jda;
    private Guild guild;
    private TextChannel primaryChannel;
    private WebhookClient consoleWebhook;
    private ConsoleInjector injector;
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
        return new WebhookMessageBuilder()
                .setAvatarUrl(jda.getSelfUser().getAvatarUrl())
                .setAllowedMentions(AllowedMentions.none())
                .setUsername(getSettings().getConsoleWebhookName())
                .setContent(message)
                .build();

    }

    public void sendMessage(DiscordMessageEvent.MessageType messageType, String message, boolean groupMentions) {
        final TextChannel channel = getChannel(messageType.getKey(), true);
        if (!channel.canTalk()) {
            logger.warning("Cannot send message in channel: #" + channel.getName() + "Please ensure the bot has \"Send Messages\" permission in that channel!");
            return;
        }
        channel.sendMessage(FormatUtil.stripFormat(message))
                .allowedMentions(groupMentions ? null : DiscordUtil.NO_GROUP_MENTIONS)
                .queue();
    }

    public void startup() throws LoginException, InterruptedException {
        shutdown();

        logger.log(Level.INFO, "Attempting to login to discord...");
        if (plugin.getSettings().getBotToken().replace("INSERT-TOKEN-HERE", "").trim().isEmpty()) {
            throw new IllegalArgumentException("No token provided!");
        }

        jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(new DiscordListener(this))
                //.addEventListeners(new DiscordCommandDispatcher(this))
                .setContextEnabled(false)
                .setRawEventsEnabled(true)
                .build()
                .awaitReady();
        updatePresence();
        logger.log(Level.INFO, "Successfully logged in as " + jda.getSelfUser().getAsTag());

        guild = jda.getGuildById(plugin.getSettings().getGuildId());
        if (guild == null) {
            throw new IllegalArgumentException("No guild configured!");
        }

        interactionController = new InteractionController(this);
        interactionController.registerCommand(new ExecuteCommand(this));
        interactionController.registerCommand(new MessageCommand(this));

        updatePrimaryChannel();

        updateConsoleRelay();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), plugin);
    }

    public void updatePrimaryChannel() {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getPrimaryChannelId());
        if (channel == null) {
            channel = guild.getDefaultChannel();
            if (channel == null || !channel.canTalk()) {
                throw new RuntimeException("Bot cannot see or talk in any channel!");
            }
        }
        primaryChannel = channel;
    }

    public void updatePresence() {
        jda.getPresence().setPresence(plugin.getSettings().getStatus(), plugin.getSettings().getStatusActivity());
    }

    public void updateConsoleRelay() {
        final String consoleDef = getSettings().getConsoleChannelDef();
        final Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(consoleDef);
        final long webhookId;
        final String webhookToken;
        if (matcher.matches()) {
            webhookId = Long.parseUnsignedLong(matcher.group(1));
            webhookToken = matcher.group(2);
        } else {
            final TextChannel channel = getChannel(consoleDef, false);
            if (channel != null) {
                final Webhook webhook = DiscordUtil.getAndCleanWebhook(channel);
                if (webhook == null) {
                    logger.info("Discord console logger has been disabled due to insufficient permissions! Please make sure your bot has the \"Manage Webhooks\" permission and run /essentials reload");
                    return;
                }
                webhookId = webhook.getIdLong();
                webhookToken = webhook.getToken();
            } else if (!getSettings().getConsoleChannelDef().equals("none") && !getSettings().getConsoleChannelDef().startsWith("0")) {
                logger.info("Discord console logger has been disabled due to an invalid channel definition! If you meant to disable it, ");
                return;
            } else {
                // It's either not configured at all or knowingly disabled.
                return;
            }
        }

        consoleWebhook = DiscordUtil.getWebhookClient(webhookId, webhookToken, jda.getHttpClient());
        if (injector == null) {
            injector = new ConsoleInjector(this);
            injector.start();
        }
    }

    public void shutdown() {
        if (interactionController != null) {
            interactionController.shutdown();
        }

        if (jda != null) {
            jda.removeEventListener(jda.getRegisteredListeners());
            HandlerList.unregisterAll(plugin);
            jda.shutdown();
        }

        if (injector != null) {
            injector.remove();
            injector.stop();
        }

        if (consoleWebhook != null && !consoleWebhook.isShutdown()) {
            consoleWebhook.close();
        }
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
