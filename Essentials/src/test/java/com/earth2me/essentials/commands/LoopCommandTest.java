package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import net.ess3.provider.TaskSchedulerProvider;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
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

public class LoopCommandTest {
    private ServerMock server;
    private Essentials ess;
    private User user;
    private Player player;
    private IEssentials commandEss;
    private TaskSchedulerProvider scheduler;
    private LoopCommand command;
    private CommandSource sender;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        sender = new CommandSource(ess, server.getConsoleSender());

        player = mock(Player.class);
        user = mock(User.class);
        when(user.getBase()).thenReturn(player);
        commandEss = mock(IEssentials.class);
        scheduler = mock(TaskSchedulerProvider.class);
        when(commandEss.getTaskScheduler()).thenReturn(scheduler);
        when(commandEss.getOnlineUsers()).thenReturn(Collections.singletonList(user));
        command = new LoopCommand();
        command.setEssentials(commandEss);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testChangesThePlayerInlineWhenTheCurrentThreadOwnsThem() throws Exception {
        when(scheduler.isOwnedByCurrentThread(player)).thenReturn(true);

        command.loop(server, sender);

        assertEquals(Collections.singletonList(user), command.updated);
        verify(scheduler, never()).runEntity(any(), any());
    }

    @Test
    public void testChangesThePlayerOnTheirOwnThreadWhenAnotherThreadOwnsThem() throws Exception {
        when(scheduler.isOwnedByCurrentThread(player)).thenReturn(false);

        command.loop(server, sender);

        assertEquals(new ArrayList<User>(), command.updated);
        final ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runEntity(eq(player), task.capture());
        task.getValue().run();
        assertEquals(Collections.singletonList(user), command.updated);
    }

    private static final class LoopCommand extends EssentialsLoopCommand {
        private final List<User> updated = new ArrayList<>();

        private LoopCommand() {
            super("test");
        }

        private void loop(final Server server, final CommandSource sender) throws TranslatableException, NotEnoughArgumentsException {
            loopOnlinePlayers(server, sender, true, true, "*", new String[0]);
        }

        @Override
        protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) {
            updated.add(user);
        }

        @Override
        protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        }
    }
}
