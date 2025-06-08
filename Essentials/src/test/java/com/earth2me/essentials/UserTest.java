package com.earth2me.essentials;

import net.ess3.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class UserTest {
    private PlayerMock base1;
    private Essentials ess;
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        base1 = server.addPlayer("testPlayer1");
        ess.getUser(base1);
    }

    @AfterEach
    public void tearEach() {
        MockBukkit.unmock();
    }

    private void should(final String what) {
        System.out.println("UserTest should " + what);
    }

    @Test
    public void testUpdate() {
        final Player base1alt = server.getPlayer(base1.getName());
        assertEquals(base1alt, ess.getUser(base1alt).getBase());
    }

    @Test
    public void testHome() {
        final User user = ess.getUser(base1);
        final Location loc = base1.getLocation();
        loc.setWorld(server.getWorlds().get(0));
        user.setHome("home", loc);
        final Player base2 = server.getPlayer(base1.getName());
        final User user2 = ess.getUser(base2);

        final Location home = user2.getHome(loc);
        assertNotNull(home);
        assertEquals(loc.getWorld().getName(), home.getWorld().getName());
        assertEquals(loc.getX(), home.getX());
        assertEquals(loc.getY(), home.getY());
        assertEquals(loc.getZ(), home.getZ());
        assertEquals(loc.getYaw(), home.getYaw());
        assertEquals(loc.getPitch(), home.getPitch());
    }

    @Test
    public void testMoney() {
        should("properly set, take, give, and get money");
        final User user = ess.getUser(base1);
        BigDecimal i = new BigDecimal("100.5");
        try {
            user.setMoney(i);
            user.takeMoney(new BigDecimal(50));
            i = i.subtract(BigDecimal.valueOf(50));
            user.giveMoney(new BigDecimal(25));
            i = i.add(BigDecimal.valueOf(25));
        } catch (final MaxMoneyException ex) {
            fail();
        }

        assertEquals(user.getMoney(), i);
    }
}
