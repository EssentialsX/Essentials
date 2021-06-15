package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import net.ess3.api.Economy;
import net.ess3.api.MaxMoneyException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class EconomyTest {
    private static final String NPCNAME = "npc1";
    private static final String PLAYERNAME = "testPlayer1";
    private static final String PLAYERNAME2 = "testPlayer2";
    private final transient Essentials ess;
    private final FakeServer server;

    public EconomyTest() {
        this.server = FakeServer.getServer();
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            Assert.fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            Assert.fail("IOException");
        }
        server.addPlayer(new OfflinePlayer(PLAYERNAME, ess.getServer()));
        server.addPlayer(new OfflinePlayer(PLAYERNAME2, ess.getServer()));
    }

    // only one big test, since we use static instances
    @Test
    public void testEconomy() {
        // test NPC
        Assert.assertFalse("NPC does not exists", Economy.playerExists(NPCNAME));
        Assert.assertTrue("Create NPC", Economy.createNPC(NPCNAME));
        Assert.assertTrue("NPC exists", Economy.playerExists(NPCNAME));
        Assert.assertNotNull("NPC can be accessed", ess.getOfflineUser(NPCNAME));
        try {
            Economy.removeNPC(NPCNAME);
        } catch (final UserDoesNotExistException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertFalse("NPC can be removed", Economy.playerExists(NPCNAME));

        //test Math
        try {

            Assert.assertTrue("Player exists", Economy.playerExists(PLAYERNAME));
            Economy.resetBalance(PLAYERNAME);
            Assert.assertEquals("Player has no money", 0.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.add(PLAYERNAME, 10.0);
            Assert.assertEquals("Add money", 10.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.subtract(PLAYERNAME, 5.0);
            Assert.assertEquals("Subtract money", 5.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.multiply(PLAYERNAME, 2.0);
            Assert.assertEquals("Multiply money", 10.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.divide(PLAYERNAME, 2.0);
            Assert.assertEquals("Divide money", 5.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.setMoney(PLAYERNAME, 10.0);
            Assert.assertEquals("Set money", 10.0, Economy.getMoney(PLAYERNAME), 0);
        } catch (final NoLoanPermittedException | UserDoesNotExistException | MaxMoneyException ex) {
            Assert.fail(ex.getMessage());
        }

        //test Format
        Assert.assertEquals("Format $1,000", "$1,000", Economy.format(1000.0));
        Assert.assertEquals("Format $10", "$10", Economy.format(10.0));
        Assert.assertEquals("Format $10.10", "$10.10", Economy.format(10.10));
        Assert.assertEquals("Format $10.10", "$10.10", Economy.format(10.1000001));
        Assert.assertEquals("Format $10.10", "$10.10", Economy.format(10.1099999));

        //test Exceptions
        try {
            Assert.assertTrue("Player exists", Economy.playerExists(PLAYERNAME));
            Economy.resetBalance(PLAYERNAME);
            Assert.assertEquals("Reset balance", 0.0, Economy.getMoney(PLAYERNAME), 0);
            Economy.subtract(PLAYERNAME, 5.0);
            Assert.fail("Did not throw exception");
        } catch (final NoLoanPermittedException | MaxMoneyException ignored) {
        } catch (final UserDoesNotExistException ex) {
            Assert.fail(ex.getMessage());
        }

        try {
            Economy.resetBalance("UnknownPlayer");
            Assert.fail("Did not throw exception");
        } catch (final NoLoanPermittedException | MaxMoneyException ex) {
            Assert.fail(ex.getMessage());
        } catch (final UserDoesNotExistException ignored) {
        }
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
            cmd.run(server, new CommandSource(sender), command, null, args);
        } catch (final NoChargeException ignored) {
        }
    }

    @Test
    public void testNegativePayCommand() {
        final User user1 = ess.getUser(PLAYERNAME);
        try {
            runCommand("pay", user1, PLAYERNAME2 + " -123");
        } catch (final Exception e) {
            Assert.assertEquals(I18n.tl("payMustBePositive"), e.getMessage());
        }
    }
}
