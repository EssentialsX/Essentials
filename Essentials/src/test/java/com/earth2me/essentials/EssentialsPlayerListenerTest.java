package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.provider.KnownCommandsProvider;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EssentialsPlayerListenerTest {
    private IEssentials ess;
    private ISettings settings;
    private User user;
    private Player player;
    private Pattern cooldownPattern;
    private EssentialsPlayerListener listener;

    @BeforeEach
    public void setUp() {
        player = MockBukkit.mock().addPlayer();
        ess = mock(IEssentials.class);
        settings = mock(ISettings.class);
        user = mock(User.class);
        final Server commandServer = mock(Server.class);
        final PluginCommand pluginCommand = mock(PluginCommand.class);
        final KnownCommandsProvider knownCommandsProvider = mock(KnownCommandsProvider.class);
        cooldownPattern = Pattern.compile("feed");

        when(ess.getServer()).thenReturn(commandServer);
        when(ess.getSettings()).thenReturn(settings);
        when(ess.getUser(player)).thenReturn(user);
        when(ess.provider(KnownCommandsProvider.class)).thenReturn(knownCommandsProvider);
        when(knownCommandsProvider.getKnownCommands()).thenReturn(Collections.singletonMap("efeed", pluginCommand));
        when(commandServer.getPluginCommand("efeed")).thenReturn(pluginCommand);
        when(pluginCommand.getName()).thenReturn("feed");
        when(settings.getSocialSpyCommands()).thenReturn(Collections.emptySet());
        when(settings.getMuteCommands()).thenReturn(Collections.emptySet());
        when(settings.isCommandCooldownsEnabled()).thenReturn(true);
        when(settings.getCommandCooldownEntry("feed")).thenReturn(new AbstractMap.SimpleImmutableEntry<>(cooldownPattern, 60_000L));
        when(user.getCommandCooldowns()).thenReturn(Collections.emptyMap());
        listener = new EssentialsPlayerListener(ess);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testUnregisteredCaseVariantDoesNotStartCooldown() {
        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/EFEED"));

        verify(settings, never()).getCommandCooldownEntry(anyString());
        verify(user, never()).addCommandCooldown(any(Pattern.class), any(Date.class), anyBoolean());
    }

    @Test
    public void testRegisteredAliasStartsCanonicalCommandCooldown() {
        listener.onPlayerCommandPreprocess(new PlayerCommandPreprocessEvent(player, "/efeed"));

        verify(settings).getCommandCooldownEntry("feed");
        verify(user).addCommandCooldown(eq(cooldownPattern), any(Date.class), eq(false));
    }
}
