package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class MessagingTest {

    private PlayerMock base1;
    private Essentials ess;
    private ServerMock server;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            fail("IOException");
        }
        base1 = server.addPlayer("testPlayer1");
        ess.getUser(base1);
    }

    @AfterEach
    void tearDown() {
        ess.tearDownForTesting();
        MockBukkit.unmock();
    }

    private void runCommand(final String command, final User user, final String args) throws Exception {
        runCommand(command, user, args.split("\\s+"));
    }

    private void runCommand(final String command, final User user, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, user, command, null, args);
        } catch (final NoChargeException ignored) {
        }

    }

    private void runConsoleCommand(final String command, final String args) throws Exception {
        runConsoleCommand(command, args.split("\\s+"));
    }

    private void runConsoleCommand(final String command, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        final CommandSender sender = server.getConsoleSender();

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, new CommandSource(ess, sender), command, null, args);
        } catch (final NoChargeException ignored) {
        }
    }

    @Test
    public void testNullLastMessageReplyRecipient() {
        assertThrows(Exception.class, () -> {
            final User user1 = ess.getUser(base1);
            final Console console = Console.getInstance();
            if (ess.getSettings().isLastMessageReplyRecipient()) {
                assertNull(console.getReplyRecipient()); // console never messaged or received messages from anyone.

                if (ess.getSettings().isLastMessageReplyRecipient()) {
                    runCommand("r", user1, "This is me sending you a message using /r without you replying!");
                }

                // Not really much of a strict test, but just "testing" console output.
                user1.setAfk(true);

                // Console replies using "/r Hey, son!"
                //
                // This throws Exception because the console hasnt messaged anyone.
                runConsoleCommand("r", "Hey, son!");
            } else {
                throw new Exception(); // Needed to prevent build failures.
            }
        });
    }

    @Test
    public void testNonNullLastMessageReplyRecipient() throws Exception {
        final User user1 = ess.getUser(base1);
        final Console console = Console.getInstance();

        if (ess.getSettings().isLastMessageReplyRecipient()) {
            assertNull(console.getReplyRecipient()); // console never messaged or received messages from anyone.

            // user1 messages console saying "Hey, master!"
            runCommand("msg", user1, console.getName() + " Hey, master!");

            // console should now have its reply-recipient as user1, since the console doesn't have a previous recipient.
            assertEquals(user1, console.getReplyRecipient());

            if (ess.getSettings().isLastMessageReplyRecipient()) {
                runCommand("r", user1, "This is me sending you a message using /r without you replying!");
            }

            // Not really much of a strict test, but just "testing" console output. 
            user1.setAfk(true);

            runConsoleCommand("r", "Hey, son!");
        }
    }
}
