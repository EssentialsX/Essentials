package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;

import junit.framework.TestCase;
import net.ess3.api.Economy;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;

import java.io.IOException;


public class EconomyTest extends TestCase {
    private final transient Essentials ess;
    private static final String NPCNAME = "npc1";
    private static final String PLAYERNAME = "testPlayer1";
    private static final String PLAYERNAME2 = "testPlayer2";
    private final FakeServer server;

    public EconomyTest(final String testName) {
        super(testName);
        this.server = new FakeServer();
        server.createWorld("testWorld", Environment.NORMAL);
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (IOException ex) {
            fail("IOException");
        }
        server.addPlayer(new OfflinePlayer(PLAYERNAME, ess.getServer()));
        server.addPlayer(new OfflinePlayer(PLAYERNAME2, ess.getServer()));
    }

    // only one big test, since we use static instances
    @Test
    public void testEconomy() {
        // test NPC
        assertFalse("NPC does not exists", Economy.playerExists(NPCNAME));
        assertTrue("Create NPC", Economy.createNPC(NPCNAME));
        assertTrue("NPC exists", Economy.playerExists(NPCNAME));
        assertNotNull("NPC can be accessed", ess.getOfflineUser(NPCNAME));
        try {
            Economy.removeNPC(NPCNAME);
        } catch (UserDoesNotExistException ex) {
            fail(ex.getMessage());
        }
        assertFalse("NPC can be removed", Economy.playerExists(NPCNAME));

        //test Math
        try {

            assertTrue("Player exists", Economy.playerExists(PLAYERNAME));
            Economy.resetBalance(PLAYERNAME);
            assertEquals("Player has no money", 0.0, Economy.getMoney(PLAYERNAME));
            Economy.add(PLAYERNAME, 10.0);
            assertEquals("Add money", 10.0, Economy.getMoney(PLAYERNAME));
            Economy.subtract(PLAYERNAME, 5.0);
            assertEquals("Subtract money", 5.0, Economy.getMoney(PLAYERNAME));
            Economy.multiply(PLAYERNAME, 2.0);
            assertEquals("Multiply money", 10.0, Economy.getMoney(PLAYERNAME));
            Economy.divide(PLAYERNAME, 2.0);
            assertEquals("Divide money", 5.0, Economy.getMoney(PLAYERNAME));
            Economy.setMoney(PLAYERNAME, 10.0);
            assertEquals("Set money", 10.0, Economy.getMoney(PLAYERNAME));
        } catch (NoLoanPermittedException ex) {
            fail(ex.getMessage());
        } catch (UserDoesNotExistException ex) {
            fail(ex.getMessage());
        }

        //test Format
        assertEquals("Format $1,000", "$1,000", Economy.format(1000.0));
        assertEquals("Format $10", "$10", Economy.format(10.0));
        assertEquals("Format $10.10", "$10.10", Economy.format(10.10));
        assertEquals("Format $10.10", "$10.10", Economy.format(10.1000001));
        assertEquals("Format $10.10", "$10.10", Economy.format(10.1099999));


        //test Exceptions
        try {
            assertTrue("Player exists", Economy.playerExists(PLAYERNAME));
            Economy.resetBalance(PLAYERNAME);
            assertEquals("Reset balance", 0.0, Economy.getMoney(PLAYERNAME));
            Economy.subtract(PLAYERNAME, 5.0);
            fail("Did not throw exception");
        } catch (NoLoanPermittedException ex) {
        } catch (UserDoesNotExistException ex) {
            fail(ex.getMessage());
        }

        try {
            Economy.resetBalance("UnknownPlayer");
            fail("Did not throw exception");
        } catch (NoLoanPermittedException ex) {
            fail(ex.getMessage());
        } catch (UserDoesNotExistException ex) {
        }
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

    public void testNegativePayCommand() throws Exception {
        User user1 = ess.getUser(PLAYERNAME);
        try {
            runCommand("pay", user1, PLAYERNAME2 + " -123");
        } catch (Exception e) {
            assertEquals(I18n.tl("payMustBePositive"), e.getMessage());
        }
    }
}
