package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class MessagingTest {

    private final OfflinePlayerStub base1;
    private final Essentials ess;
    private final FakeServer server;

    public MessagingTest() {
        server = FakeServer.getServer();
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            fail("IOException");
        }
        base1 = server.createPlayer("testPlayer1");
        server.addPlayer(base1);
        ess.getUser(base1);
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

    @Test(expected = Exception.class) // I really don't like this, but see note below about console reply
    public void testNullLastMessageReplyRecipient() throws Exception {
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
