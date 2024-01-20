package net.essentialsx.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.ess3.nms.refl.providers.AchievementListenerProvider;
import net.ess3.nms.refl.providers.AdvancementListenerProvider;
import net.ess3.provider.providers.PaperAdvancementListenerProvider;
import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.services.discord.DiscordService;
import net.essentialsx.api.v2.services.discord.InteractionController;
import net.essentialsx.api.v2.services.discord.InteractionException;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import net.essentialsx.api.v2.services.discord.InteractionRole;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.api.v2.services.discord.Unsafe;
import net.essentialsx.discord.interactions.InteractionControllerImpl;
import net.essentialsx.discord.interactions.InteractionMemberImpl;
import net.essentialsx.discord.interactions.InteractionRoleImpl;
import net.essentialsx.discord.interactions.commands.ExecuteCommand;
import net.essentialsx.discord.interactions.commands.ListCommand;
import net.essentialsx.discord.interactions.commands.MessageCommand;
import net.essentialsx.discord.listeners.BukkitListener;
import net.essentialsx.discord.listeners.DiscordCommandDispatcher;
import net.essentialsx.discord.listeners.DiscordListener;
import net.essentialsx.discord.listeners.EssentialsChatListener;
import net.essentialsx.discord.listeners.BukkitChatListener;
import net.essentialsx.discord.util.ConsoleInjector;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import net.essentialsx.discord.util.WrappedWebhookClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public class JDADiscordService implements DiscordService, IEssentialsModule {
    private final static Logger logger = EssentialsDiscord.getWrappedLogger();
    private final EssentialsDiscord plugin;
    private final Unsafe unsafe = this::getJda;

    private JDA jda;
    private Guild guild;
    private TextChannel primaryChannel;
    private WrappedWebhookClient consoleWebhook;
    private String lastConsoleId;
    private final Map<String, MessageType> registeredTypes = new HashMap<>();
    private final Map<MessageType, String> typeToChannelId = new HashMap<>();
    private final Map<String, WrappedWebhookClient> channelIdToWebhook = new HashMap<>();
    private ConsoleInjector injector;
    private DiscordCommandDispatcher commandDispatcher;
    private InteractionControllerImpl interactionController;
    private Listener chatListener;
    private boolean invalidStartup = false;

    public JDADiscordService(EssentialsDiscord plugin) {
        this.plugin = plugin;
        for (final MessageType type : MessageType.DefaultTypes.values()) {
            registerMessageType(plugin, type);
        }
    }

    public TextChannel getChannel(String key, boolean primaryFallback) {
        if (NumberUtil.isLong(key)) {
            return getDefinedChannel(key, primaryFallback);
        }
        return getDefinedChannel(getSettings().getMessageChannel(key), primaryFallback);
    }

    public TextChannel getDefinedChannel(String key, boolean primaryFallback) {
        final long resolvedId = getSettings().getChannelId(key);

        if (isDebug()) {
            logger.log(Level.INFO, "Channel definition " + key + " resolved as " + resolvedId);
        }
        TextChannel channel = guild.getTextChannelById(resolvedId);
        if (channel == null && primaryFallback) {
            if (isDebug()) {
                logger.log(Level.WARNING, "Resolved channel id " + resolvedId + " was not found! Falling back to primary channel.");
            }
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

        final boolean isSilentMessage = message.startsWith("@silent");
        
        if (isSilentMessage) message = message.replace("@silent", "");

        final String strippedContent = FormatUtil.stripFormat(message);

        final String webhookChannelId = typeToChannelId.get(event.getType());

        if (webhookChannelId != null) {
            final WrappedWebhookClient client = channelIdToWebhook.get(webhookChannelId);
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
                .setAllowedMentions(groupMentions ? null : DiscordUtil.NO_GROUP_MENTIONS)
                .setSuppressedNotifications(isSilentMessage)
                .queue();
    }

    public void startup() throws LoginException, InterruptedException {
        shutdown();

        invalidStartup = true;
        logger.log(Level.INFO, tl("discordLoggingIn"));
        if (plugin.getSettings().getBotToken().replace("INSERT-TOKEN-HERE", "").trim().isEmpty()) {
            throw new IllegalArgumentException(tl("discordErrorNoToken"));
        }

        jda = JDABuilder.createDefault(plugin.getSettings().getBotToken())
                .addEventListeners(new DiscordListener(this))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.EMOJI)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setContextEnabled(false)
                .build()
                .awaitReady();
        invalidStartup = false;
        updatePresence();
        logger.log(Level.INFO, tl("discordLoggingInDone", jda.getSelfUser().getAsTag()));

        if (jda.getGuilds().isEmpty()) {
            invalidStartup = true;
            throw new IllegalArgumentException(tl("discordErrorNoGuildSize"));
        }

        guild = jda.getGuildById(plugin.getSettings().getGuildId());
        if (guild == null) {
            invalidStartup = true;
            throw new IllegalArgumentException(tl("discordErrorNoGuild"));
        }

        interactionController = new InteractionControllerImpl(this);
        // Each will throw an exception if disabled
        try {
            interactionController.registerCommand(new ExecuteCommand(this));
        } catch (InteractionException ignored) {
        }
        try {
            interactionController.registerCommand(new MessageCommand(this));
        } catch (InteractionException ignored) {
        }
        try {
            interactionController.registerCommand(new ListCommand(this));
        } catch (InteractionException ignored) {
        }

        // Load emotes into cache, JDA will handle updates from here on out.
        guild.retrieveEmojis().queue();

        updatePrimaryChannel();

        updateConsoleRelay();

        updateTypesRelay();

        // We will see you in the future :balloon:
        // DiscordUtil.cleanWebhooks(guild, DiscordUtil.CONSOLE_RELAY_NAME);
        // DiscordUtil.cleanWebhooks(guild, DiscordUtil.ADVANCED_RELAY_NAME);

        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), plugin);

        updateListener();

        try {
            if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_12_0_R01)) {
                try {
                    Class.forName("io.papermc.paper.advancement.AdvancementDisplay");
                    Bukkit.getPluginManager().registerEvents(new PaperAdvancementListenerProvider(), plugin);
                } catch (ClassNotFoundException e) {
                    Bukkit.getPluginManager().registerEvents(new AdvancementListenerProvider(), plugin);
                }
            } else {
                Bukkit.getPluginManager().registerEvents(new AchievementListenerProvider(), plugin);
            }
        } catch (final Throwable e) {
            logger.log(Level.WARNING, "Error while loading the achievement/advancement listener. You will not receive achievement/advancement notifications on Discord.", e);
        }

        getPlugin().getEss().scheduleSyncDelayedTask(() -> DiscordUtil.dispatchDiscordMessage(JDADiscordService.this, MessageType.DefaultTypes.SERVER_START, getSettings().getStartMessage(), true, null, null, null));

        Bukkit.getServicesManager().register(DiscordService.class, this, plugin, ServicePriority.Normal);
    }

    @Override
    public boolean isRegistered(String key) {
        return registeredTypes.containsKey(key);
    }

    @Override
    public void registerMessageType(Plugin plugin, MessageType type) {
        if (!type.getKey().matches("^[a-z][a-z0-9-]*$")) {
            throw new IllegalArgumentException("MessageType key must match \"^[a-z][a-z0-9-]*$\"");
        }

        if (registeredTypes.containsKey(type.getKey())) {
            throw new IllegalArgumentException("A MessageType with that key is already registered!");
        }

        registeredTypes.put(type.getKey(), type);
    }

    @Override
    public void sendMessage(MessageType type, String message, boolean allowGroupMentions) {
        if (!registeredTypes.containsKey(type.getKey()) && !NumberUtil.isLong(type.getKey())) {
            logger.warning("Sending message to channel \"" + type.getKey() + "\" which is an unregistered type! If you are a plugin author, you should be registering your MessageType before using them.");
        }
        final DiscordMessageEvent event = new DiscordMessageEvent(type, FormatUtil.stripFormat(message), allowGroupMentions);
        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));
        }
    }

    @Override
    public void sendChatMessage(final Player player, final String chatMessage) {
        sendChatMessage(ChatType.UNKNOWN, player, chatMessage);
    }

    @Override
    public void sendChatMessage(ChatType chatType, Player player, String chatMessage) {
        final User user = getPlugin().getEss().getUser(player);

        final String formattedMessage = MessageUtil.formatMessage(getSettings().getMcToDiscordFormat(player, chatType),
                MessageUtil.sanitizeDiscordMarkdown(player.getName()),
                MessageUtil.sanitizeDiscordMarkdown(player.getDisplayName()),
                user.isAuthorized("essentials.discord.markdown") ? chatMessage : MessageUtil.sanitizeDiscordMarkdown(chatMessage),
                MessageUtil.sanitizeDiscordMarkdown(getPlugin().getEss().getSettings().getWorldAlias(player.getWorld().getName())),
                MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripEssentialsFormat(getPlugin().getEss().getPermissionsHandler().getPrefix(player))),
                MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripEssentialsFormat(getPlugin().getEss().getPermissionsHandler().getSuffix(player))));

        final String avatarUrl = DiscordUtil.getAvatarUrl(this, player);

        final String formattedName = MessageUtil.formatMessage(getSettings().getMcToDiscordNameFormat(player),
                player.getName(),
                player.getDisplayName(),
                getPlugin().getEss().getSettings().getWorldAlias(player.getWorld().getName()),
                FormatUtil.stripEssentialsFormat(getPlugin().getEss().getPermissionsHandler().getPrefix(player)),
                FormatUtil.stripEssentialsFormat(getPlugin().getEss().getPermissionsHandler().getSuffix(player)),
                guild.getMember(jda.getSelfUser()).getEffectiveName());

        DiscordUtil.dispatchDiscordMessage(this, chatTypeToMessageType(chatType), formattedMessage, user.isAuthorized("essentials.discord.ping"), avatarUrl, formattedName, player.getUniqueId());
    }

    private MessageType chatTypeToMessageType(ChatType chatType) {
        switch (chatType) {
            case SHOUT:
                return MessageType.DefaultTypes.SHOUT;
            case QUESTION:
                return MessageType.DefaultTypes.QUESTION;
            case LOCAL:
                return MessageType.DefaultTypes.LOCAL;
            default:
                return MessageType.DefaultTypes.CHAT;
        }
    }

    @Override
    public InteractionController getInteractionController() {
        return interactionController;
    }

    public void updatePrimaryChannel() {
        TextChannel channel = guild.getTextChannelById(plugin.getSettings().getPrimaryChannelId());
        if (channel == null) {
            if (!(guild.getDefaultChannel() instanceof TextChannel)) {
                throw new RuntimeException(tl("discordErrorNoPerms"));
            }
            channel = (TextChannel) guild.getDefaultChannel();
            logger.warning(tl("discordErrorNoPrimary", channel.getName()));
        }

        if (!channel.canTalk()) {
            throw new RuntimeException(tl("discordErrorNoPrimaryPerms", channel.getName()));
        }
        primaryChannel = channel;
    }

    public String parseMessageEmotes(String message) {
        for (final RichCustomEmoji emote : guild.getEmojiCache()) {
            message = message.replaceAll(":" + Pattern.quote(emote.getName()) + ":", emote.getAsMention());
        }
        return message;
    }

    public void updateListener() {
        if (chatListener != null) {
            HandlerList.unregisterAll(chatListener);
            chatListener = null;
        }

        chatListener = getSettings().isUseEssentialsEvents() && plugin.isEssentialsChat()
            ? new EssentialsChatListener(this)
            : new BukkitChatListener(this);

        Bukkit.getPluginManager().registerEvents(chatListener, plugin);
    }

    public void updatePresence() {
        jda.getPresence().setPresence(plugin.getSettings().getStatus(), plugin.getSettings().getStatusActivity());
    }

    public void updateTypesRelay() {
        if (!getSettings().isShowAvatar() && !getSettings().isShowName() && !getSettings().isShowDisplayName()) {
            for (WrappedWebhookClient webhook : channelIdToWebhook.values()) {
                webhook.close();
            }
            typeToChannelId.clear();
            channelIdToWebhook.clear();
            return;
        }

        for (MessageType type : MessageType.DefaultTypes.values()) {
            if (!type.isPlayer()) {
                continue;
            }

            final TextChannel channel = getChannel(type.getKey(), true);
            if (channel.getId().equals(typeToChannelId.get(type))) {
                continue;
            }

            final Webhook webhook = DiscordUtil.getOrCreateWebhook(channel, DiscordUtil.ADVANCED_RELAY_NAME).join();
            if (webhook == null) {
                final WrappedWebhookClient current = channelIdToWebhook.get(channel.getId());
                if (current != null) {
                    current.close();
                }
                channelIdToWebhook.remove(channel.getId()).close();
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

                if (channel.getId().equals(lastConsoleId)) {
                    return;
                }

                final Webhook webhook = DiscordUtil.getOrCreateWebhook(channel, DiscordUtil.CONSOLE_RELAY_NAME).join();
                if (webhook == null) {
                    logger.info(tl("discordErrorLoggerNoPerms"));
                    return;
                }
                webhookId = webhook.getIdLong();
                webhookToken = webhook.getToken();
                lastConsoleId = channel.getId();
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
        if (injector == null || injector.isRemoved()) {
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
            if (!invalidStartup) {
                sendMessage(MessageType.DefaultTypes.SERVER_STOP, getSettings().getStopMessage(), true);
                DiscordUtil.dispatchDiscordMessage(JDADiscordService.this, MessageType.DefaultTypes.SERVER_STOP, getSettings().getStopMessage(), true, null, null, null);
            }

            shutdownConsoleRelay(true);

            for (WrappedWebhookClient webhook : channelIdToWebhook.values()) {
                webhook.close();
            }

            // Unregister leftover jda listeners
            for (Object obj : jda.getRegisteredListeners()) {
                jda.removeEventListener(obj);
            }

            // Unregister Bukkit Events
            HandlerList.unregisterAll(plugin);

            // Creates a future which will be completed when JDA fully shutdowns
            final CompletableFuture<Void> future = new CompletableFuture<>();
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onShutdown(@NotNull ShutdownEvent event) {
                    future.complete(null);
                }
            });

            // Tell JDA to wrap it up
            jda.shutdown();
            try {
                // Wait for JDA to wrap it up
                future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.log(Level.WARNING, "JDA took longer than expected to shutdown, this may have caused some problems.", e);
            } finally {
                jda = null;
            }
        }
    }

    @Override
    public CompletableFuture<InteractionMember> getMemberById(final String id) {
        final CompletableFuture<InteractionMember> future = new CompletableFuture<>();
        getGuild().retrieveMemberById(id).queue(member -> {
            if (member != null) {
                future.complete(new InteractionMemberImpl(member));
                return;
            }
            future.complete(null);
        }, fail -> future.complete(null));
        return future;
    }

    @Override
    public InteractionRole getRole(String id) {
        final Role role = getGuild().getRoleById(id);
        return role == null ? null : new InteractionRoleImpl(role);
    }

    @Override
    public CompletableFuture<Void> modifyMemberRoles(InteractionMember member, Collection<InteractionRole> addRoles, Collection<InteractionRole> removeRoles) {
        if ((addRoles == null || addRoles.isEmpty()) && (removeRoles == null || removeRoles.isEmpty())) {
            return CompletableFuture.completedFuture(null);
        }

        final List<Role> add = new ArrayList<>();
        final List<Role> remove = new ArrayList<>();
        if (addRoles != null) {
            for (final InteractionRole role : addRoles) {
                add.add(((InteractionRoleImpl) role).getJdaObject());
            }
        }
        if (removeRoles != null) {
            for (final InteractionRole role : removeRoles) {
                remove.add(((InteractionRoleImpl) role).getJdaObject());
            }
        }

        final CompletableFuture<Void> future = new CompletableFuture<>();
        guild.modifyMemberRoles(((InteractionMemberImpl) member).getJdaObject(), add, remove).queue(future::complete);
        return future;
    }

    @Override
    public String getInviteUrl() {
        return getSettings().getDiscordUrl();
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public Unsafe getUnsafe() {
        return unsafe;
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

    public WrappedWebhookClient getConsoleWebhook() {
        return consoleWebhook;
    }

    public boolean isInvalidStartup() {
        return invalidStartup;
    }

    public boolean isDebug() {
        return plugin.getEss().getSettings().isDebug();
    }
}
