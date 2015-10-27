package com.earth2me.essentials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;

import java.io.IOException;


public class MessagingTest {

    private final OfflinePlayer base1;
    private final Essentials ess;
    private final FakeServer server;

    public MessagingTest() {
        server = new FakeServer();
        server.createWorld("testWorld", Environment.NORMAL);
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (IOException ex) {
            fail("IOException");
        }
        base1 = server.createPlayer("testPlayer1");
        server.addPlayer(base1);
        ess.getUser(base1);
    }

    private void runCommand(String command, User user, String args) throws Exception {
        runCommand(command, user, args.split("\\s+"));
    }

    private void runCommand(String command, User user, String[] args) throws Exception {
        IEssentialsCommand cmd;

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, user, command, null, args);
        } catch (NoChargeException ex) {
        }

    }

    private void runConsoleCommand(String command, String args) throws Exception {
        runConsoleCommand(command, args.split("\\s+"));
    }

    private void runConsoleCommand(String command, String[] args) throws Exception {
        IEssentialsCommand cmd;

        CommandSender sender = server.getConsoleSender();

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, new CommandSource(sender), command, null, args);
        } catch (NoChargeException ex) {
        }
    }

    @Test(expected = Exception.class) // I really don't like this, but see note below about console reply
    public void testMessage() throws Exception {
        User user1 = ess.getUser(base1);
        Console console = Console.getInstance();

        if (ess.getSettings().isLastMessageReplyRecipient()) {
            assertNull(console.getReplyRecipient()); // console never messaged or received messages from anyone.

            // user1 messages console saying "Hey, master!"
            runCommand("msg", user1, console.getName() + " Hey, master!");

            // console should now have its reply-recipient as user1, since the console doesn't have a previous recipient.
            assertEquals(console.getReplyRecipient(), user1);

            if (ess.getSettings().isLastMessageReplyRecipient()) {
                runCommand("r", user1, "This is me sending you a message using /r without you replying!");
            }

            // Not really much of a strict test, but just "testing" console output. 
            user1._setAfk(true);

            // Console replies using "/r Hey, son!"
            //
            // This throws Exception because the base1 is an OfflinePlayer (isOnline() returns false).
            runConsoleCommand("r", "Hey, son!");
        } else {
            assertNull(console.getReplyRecipient()); // user2 never received messages from anyone.
        }
    }
}
