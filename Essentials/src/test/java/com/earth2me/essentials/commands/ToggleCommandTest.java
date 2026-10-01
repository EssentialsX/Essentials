package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.provider.TaskSchedulerProvider;
import org.bukkit.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToggleCommandTest {
    private ServerMock server;
    private CommandSource sender;
    private PlayerMock player;
    private User user;
    private TaskSchedulerProvider scheduler;
    private ToggleCommand command;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        Essentials.TESTING = true;
        sender = new CommandSource(MockBukkit.load(Essentials.class), server.getConsoleSender());

        player = server.addPlayer("target");
        user = mock(User.class);
        when(user.getBase()).thenReturn(player);
        final IEssentials ess = mock(IEssentials.class);
        scheduler = mock(TaskSchedulerProvider.class);
        when(ess.getTaskScheduler()).thenReturn(scheduler);
        when(ess.getUser(player)).thenReturn(user);
        command = new ToggleCommand();
        command.setEssentials(ess);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testTogglesAnotherPlayerInlineWhenTheCurrentThreadOwnsThem() throws Exception {
        when(scheduler.isOwnedByCurrentThread(player)).thenReturn(true);

        command.toggleOtherPlayers(server, sender, new String[] {"target", "on"});

        assertEquals(Collections.singletonList(Boolean.TRUE), command.toggled);
        verify(scheduler, never()).runEntity(any(), any());
    }

    @Test
    public void testTogglesAnotherPlayerOnTheirOwnThreadWhenAnotherThreadOwnsThem() throws Exception {
        when(scheduler.isOwnedByCurrentThread(player)).thenReturn(false);

        command.toggleOtherPlayers(server, sender, new String[] {"target", "off"});

        assertEquals(new ArrayList<Boolean>(), command.toggled);
        final ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runEntity(eq(player), task.capture());
        task.getValue().run();
        assertEquals(Collections.singletonList(Boolean.FALSE), command.toggled);
    }

    private static final class ToggleCommand extends EssentialsToggleCommand {
        private final List<Boolean> toggled = new ArrayList<>();

        private ToggleCommand() {
            super("test", "essentials.test.others");
        }

        @Override
        protected void togglePlayer(final CommandSource sender, final User user, final Boolean enabled) {
            toggled.add(enabled);
        }

        @Override
        protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        }
    }
}
