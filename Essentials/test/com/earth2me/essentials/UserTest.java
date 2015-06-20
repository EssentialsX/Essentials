package com.earth2me.essentials;

import junit.framework.TestCase;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.plugin.InvalidDescriptionException;

import java.io.IOException;
import java.math.BigDecimal;


public class UserTest extends TestCase {
    private final OfflinePlayer base1;
    private final Essentials ess;
    private final FakeServer server;

    public UserTest(String testName) {
        super(testName);
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

    private void should(String what) {
        System.out.println(getName() + " should " + what);
    }

    public void testUpdate() {
        OfflinePlayer base1alt = server.createPlayer(base1.getName());
        assertEquals(base1alt, ess.getUser(base1alt).getBase());
    }

    public void testHome() {
        User user = ess.getUser(base1);
        Location loc = base1.getLocation();
        user.setHome("home", loc);
        OfflinePlayer base2 = server.createPlayer(base1.getName());
        User user2 = ess.getUser(base2);

        Location home = user2.getHome(loc);
        assertNotNull(home);
        assertEquals(loc.getWorld().getName(), home.getWorld().getName());
        assertEquals(loc.getX(), home.getX());
        assertEquals(loc.getY(), home.getY());
        assertEquals(loc.getZ(), home.getZ());
        assertEquals(loc.getYaw(), home.getYaw());
        assertEquals(loc.getPitch(), home.getPitch());
    }

    public void testMoney() {
        should("properly set, take, give, and get money");
        User user = ess.getUser(base1);
        BigDecimal i = new BigDecimal("100.5");
        try {
            user.setMoney(i);
            user.takeMoney(new BigDecimal(50));
            i = i.subtract(BigDecimal.valueOf(50));
            user.giveMoney(new BigDecimal(25));
            i = i.add(BigDecimal.valueOf(25));
        } catch (MaxMoneyException ex) {
            fail();
        }

        assertEquals(user.getMoney(), i);
    }
}
