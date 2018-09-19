package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.commands.IEssentialsCommand;
import net.ess3.api.Economy;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.*;


public class EconomyTest {
    private static transient Essentials ess;
    private static FakeServer server;
    private static final String payMustBePositiveMessage = I18n.tl("payMustBePositive");

    private static final String NPC = "npc1";
    private static final String PLAYERNAME = "testPlayer1";
    private static final String PLAYERNAME2 = "testPlayer2";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpEnvironment() throws IOException, InvalidDescriptionException {
        server = new FakeServer();
        server.createWorld("testWorld", Environment.NORMAL);
        ess = new Essentials(server);
        ess.setupForTesting(server);
    }

    @Before
    public void setUpDummyPlayers() throws Exception {
        server.addPlayer(new OfflinePlayer(PLAYERNAME, ess.getServer()));
        server.addPlayer(new OfflinePlayer(PLAYERNAME2, ess.getServer()));
    }

    @Test
    public void resettingBalanceOfUnknownPlayer_shouldThrowException() throws UserDoesNotExistException, NoLoanPermittedException {
        expectedException.expect(UserDoesNotExistException.class);

        Economy.resetBalance("UnknownPlayer");
    }

    @Test
    public void negativeAccountBalance_shouldThrowException() throws UserDoesNotExistException, NoLoanPermittedException {
        expectedException.expect(NoLoanPermittedException.class);

        assertTrue("Player exists", Economy.playerExists(PLAYERNAME));
        Economy.resetBalance(PLAYERNAME);

        assertEquals("Player should have zero balance after reset", ZERO, Economy.getMoneyExact(PLAYERNAME));
        Economy.substract(PLAYERNAME, valueOf(5));
    }

    @Test
    public void testFormat() {
        assertEquals("$1,000", Economy.format(valueOf(1000.0)));
        assertEquals("$10", Economy.format(valueOf(10.0)));
        assertEquals("$10.10", Economy.format(valueOf(10.10)));
        assertEquals("$10.10", Economy.format(valueOf(10.1000001)));
        assertEquals("$10.10", Economy.format(valueOf(10.1099999)));
    }

    @Test
    public void testPlayerEconomy() throws UserDoesNotExistException, NoLoanPermittedException {
        assertTrue("Player exists after creation", Economy.playerExists(PLAYERNAME));

        Economy.resetBalance(PLAYERNAME);
        assertEquals("Player has no money after creation", valueOf(0), Economy.getMoneyExact(PLAYERNAME));

        Economy.add(PLAYERNAME, valueOf(10));
        assertEquals("Player should have had money after addition", valueOf(10), Economy.getMoneyExact(PLAYERNAME));

        Economy.substract(PLAYERNAME, valueOf(5));
        assertEquals("Player should have had his money subtracted", valueOf(5), Economy.getMoneyExact(PLAYERNAME));

        Economy.multiply(PLAYERNAME, valueOf(2));
        assertEquals("Player should have had his money multiplied", valueOf(10), Economy.getMoneyExact(PLAYERNAME));

        Economy.divide(PLAYERNAME, valueOf(2));
        assertEquals("Player should have had his money divided", valueOf(5), Economy.getMoneyExact(PLAYERNAME));

        Economy.setMoney(PLAYERNAME, valueOf(10));
        assertEquals("Player should have had his money set", valueOf(10), Economy.getMoneyExact(PLAYERNAME));
        assertTrue("Player should have less than 11$", Economy.hasLess(PLAYERNAME, valueOf(20)));
        assertTrue("Player should have less than 9$", Economy.hasMore(PLAYERNAME, valueOf(9)));
        assertTrue("Player should have enough money", Economy.hasEnough(PLAYERNAME, valueOf(5)));
    }

    @Test
    public void testNPC() throws UserDoesNotExistException {
        assertFalse("There should be no NPC before creating it", Economy.playerExists(NPC));
        assertTrue("NPC creation should be possible", Economy.createNPC(NPC));
        assertTrue("NPC exists after creation", Economy.playerExists(NPC));
        assertNotNull("NPC can be accessed after creation", ess.getOfflineUser(NPC));

        Economy.removeNPC(NPC);
        assertFalse("NPC does not exist after deleting", Economy.playerExists(NPC));
    }

    @Test
    public void whenPayingNegativeAmount_shouldThrowException() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage(payMustBePositiveMessage);

        User user = ess.getUser(PLAYERNAME);
        runCommand("pay", user, PLAYERNAME2 + " -123");
    }

    private void runCommand(String command, User user, String args) throws Exception {
        runCommand(command, user, args.split("\\s+"));
    }

    private void runCommand(String command, User user, String[] args) throws Exception {
        IEssentialsCommand cmd;

        cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
        cmd.setEssentials(ess);
        cmd.run(server, user, command, null, args);
    }

    private void runConsoleCommand(String command, String args) throws Exception {
        runConsoleCommand(command, args.split("\\s+"));
    }

    private void runConsoleCommand(String command, String[] args) throws Exception {
        IEssentialsCommand cmd;
        CommandSender sender = server.getConsoleSender();

        cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
        cmd.setEssentials(ess);
        cmd.run(server, new CommandSource(sender), command, null, args);
    }
}
