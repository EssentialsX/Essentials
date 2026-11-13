package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
import net.ess3.provider.AbstractChatEvent;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class AntiSpamTests {

    private Essentials ess;
    private FileConfiguration config;
    private Server server;
    private ConsoleCommandSender console;
    private Player player;
    private AntiSpam antiSpam;

    @BeforeEach
    void setUp() {
        ess = mock(Essentials.class);
        config = mock(FileConfiguration.class);
        server = mock(Server.class);
        console = mock(ConsoleCommandSender.class);
        player = mock(Player.class);

        when(ess.getConfig()).thenReturn(config);
        when(ess.getServer()).thenReturn(server);
        when(server.getConsoleSender()).thenReturn(console);

        // Default configuration
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(false);
        when(config.getBoolean("chat.duplicate-prevention")).thenReturn(true);
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(false);
        when(config.getInt("chat.chat-cooldown-time")).thenReturn(5);

        // Player configuration
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn("TestPlayer");
        when(player.hasPermission("essentials.chat.bypass.cooldown")).thenReturn(false);

        antiSpam = new AntiSpam(ess);
    }

    private AbstractChatEvent createEvent(String message) {
        final AbstractChatEvent event = mock(AbstractChatEvent.class);

        when(event.getPlayer()).thenReturn(player);
        when(event.getMessage()).thenReturn(message);
        when(event.isCancelled()).thenReturn(false);

        return event;
    }


    private void assertMessageCancelled(String message, boolean cancelled) {
        final AbstractChatEvent event = createEvent(message);

        antiSpam.newMessage(event);

        if (cancelled) {
            verify(event).setCancelled(true);
        } else {
            verify(event, never()).setCancelled(true);
        }
    }

    // Duplicate prevention

    @Test
    void firstMessageShouldBeAllowed() {
        assertMessageCancelled("Hello everyone", false);
    }

    @Test
    void identicalMessageShouldBeCancelled() {
        assertMessageCancelled("Hello everyone", false);
        assertMessageCancelled("Hello everyone", true);
    }

    @Test
    void duplicateMessageShouldIgnoreCapitalization() {
        assertMessageCancelled("Hello Everyone", false);
        assertMessageCancelled("HELLO EVERYONE", true);
    }

    @Test
    void messageStartingWithPreviousMessageShouldBeCancelled() {
        assertMessageCancelled("Hello everyone", false);
        assertMessageCancelled("Hello", true);
    }

    @Test
    void messageEndingWithPreviousMessageShouldBeCancelled() {
        assertMessageCancelled("Hello everyone", false);
        assertMessageCancelled("everyone", true);
    }

    @Test
    void differentMessageShouldBeAllowed() {
        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Goodbye", false);
    }

    @Test
    void duplicatePreventionDisabledShouldAllowDuplicates() {
        when(config.getBoolean("chat.duplicate-prevention")).thenReturn(false);

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Hello", false);
    }

    // Chat cooldown

    @Test
    void firstMessageShouldNotTriggerCooldown() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(true);

        assertMessageCancelled("Hello", false);
    }

    @Test
    void secondMessageDuringCooldownShouldBeCancelled() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(true);

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Goodbye", true);
    }

    @Test
    void cooldownBypassPermissionShouldAllowMessages() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(true);
        when(player.hasPermission("essentials.chat.bypass.cooldown")).thenReturn(true);

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Goodbye", false);
    }

    @Test
    void disabledCooldownShouldAllowMessages() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(false);

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Goodbye", false);
    }

    @Test
    void cooldownRejectedMessageShouldNotReplaceLastAcceptedMessage() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(true);

        // First message is accepted.
        assertMessageCancelled("First message", false);

        // Second message is rejected by cooldown.
        assertMessageCancelled("Second message", true);

        // Bypass cooldown to isolate duplicate detection.
        when(player.hasPermission("essentials.chat.bypass.cooldown")).thenReturn(true);

        // The original accepted message should still be recognized as a duplicate.
        assertMessageCancelled("First message", true);
    }

    // Caps filter

    @Test
    void excessiveCapitalizationShouldBeConvertedToLowercase() {
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(true);

        final AbstractChatEvent event = createEvent("HELLO");

        antiSpam.newMessage(event);

        verify(event).setMessage("hello");
    }

    @Test
    void normalCapitalizationShouldRemainUnchanged() {
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(true);

        final AbstractChatEvent event = createEvent("Hello everyone");

        antiSpam.newMessage(event);

        verify(event).setMessage("Hello everyone");
    }

    @Test
    void disabledCapsFilterShouldLeaveMessageUnchanged() {
        final AbstractChatEvent event = createEvent("HELLO");

        antiSpam.newMessage(event);

        verify(event).setMessage("HELLO");
    }

    @Test
    void capsFilterShouldSendWarning() {
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(true);

        antiSpam.newMessage(createEvent("HELLO"));

        verify(player).sendMessage(contains("capital letters"));
    }

    @Test
    void capsPunishmentShouldReplacePlayerPlaceholder() {
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(true);
        when(config.getString("caps-punish")).thenReturn("mute {player} 10m");

        antiSpam.newMessage(createEvent("HELLO"));

        verify(server).dispatchCommand(console, "mute TestPlayer 10m");
    }

    @Test
    void emptyCapsPunishmentShouldNotDispatchCommand() {
        when(config.getBoolean("chat.chat-caps-filter")).thenReturn(true);
        when(config.getString("caps-punish")).thenReturn(" ");

        antiSpam.newMessage(createEvent("HELLO"));

        verifyNoInteractions(server);
    }

    // Duplicate punishment

    @Test
    void duplicatePunishmentShouldReplacePlayerPlaceholder() {
        when(config.getString("duplication-punish")).thenReturn("mute {player} 10m");

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Hello", true);

        verify(server).dispatchCommand(console, "mute TestPlayer 10m");
    }

    @Test
    void emptyDuplicatePunishmentShouldNotDispatchCommand() {
        when(config.getString("duplication-punish")).thenReturn("");

        assertMessageCancelled("Hello", false);
        assertMessageCancelled("Hello", true);

        verifyNoInteractions(server);
    }

    // Event handling

    @Test
    void alreadyCancelledEventShouldRemainCancelled() {
        final AbstractChatEvent event = createEvent("Hello");
        when(event.isCancelled()).thenReturn(true);

        antiSpam.newMessage(event);

        verify(event, never()).setCancelled(false);
        verify(event, never()).setCancelled(true);
    }

    // Player cleanup

    @Test
    void unloadPlayerShouldClearDuplicateHistory() {
        assertMessageCancelled("Hello", false);

        antiSpam.unloadPlayer(player);

        assertMessageCancelled("Hello", false);
    }

    @Test
    void unloadPlayerShouldClearCooldownHistory() {
        when(config.getBoolean("chat.chat-cooldown-enable")).thenReturn(true);

        assertMessageCancelled("Hello", false);

        antiSpam.unloadPlayer(player);

        // The same message should be accepted after cleanup.
        assertMessageCancelled("Hello", false);
    }
}
