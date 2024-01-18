package com.earth2me.essentials;

import com.earth2me.essentials.commands.Commandhome;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HomeTest {

    @Mock
    Settings settings;
    @Mock
    Commandhome commandhome;
    @InjectMocks
    net.ess3.api.IEssentials essentials;

    @Before
    public void before() throws Exception {
        settings = mock(Settings.class);
        commandhome = mock(Commandhome.class);
        essentials = mock(net.ess3.api.IEssentials.class);

        when(settings.getWorldGroupHomeLimit("test-wg")).thenReturn(7);
        when(settings.getWorldHomeLimit("world_the_end")).thenReturn(6);
        when(settings.getWorldGroupHomeList("test-wg")).thenReturn(new HashSet<>(Arrays.asList("world", "world_nether")));
        when(settings.getHomeLimit(any(User.class))).thenCallRealMethod();
        when(settings.getHomeLimit("default")).thenReturn(3);
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);
        when(settings.getMultipleHomes()).thenReturn(new HashSet<>(Collections.singletonList("vip")));
        when(settings.getHomeLimit("vip")).thenReturn(5);
        when(settings.getHomesPerWorldGroup()).thenReturn(new HashSet<>(Collections.singletonList("test-wg")));
        when(settings.getHomesPerWorld()).thenReturn(new HashSet<>(Collections.singletonList("world_the_end")));
        when(settings.isUserInWorld(any(User.class), any(String.class))).thenCallRealMethod();
        when(settings.isUserInWorldGroup(any(User.class), any(String.class))).thenCallRealMethod();

        when(essentials.getSettings()).thenReturn(settings);
        commandhome.setEssentials(essentials);
        when(commandhome.isUserHomeInWorldOrWorldGroupWorld(any(String.class), any(String.class))).thenCallRealMethod();
    }

    @Test
    public void testHomeLimitWithoutMultiplePerm() {
        final User user = mock(User.class);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(false);

        assertEquals(1, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithoutWorldHomeLimit() {
        final User user = mock(User.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithoutWorldHomeLimitVip() {
        final User user = mock(User.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(false);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(true);

        assertEquals(5, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldHomeLimit() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world_the_end");

        assertEquals(6, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldHomeLimitDefault() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldGroupHomeLimit() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertEquals(7, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeLimitWithWorldGroupHomeLimitOutsideDefault() {
        final User user = mock(User.class);
        final World world = mock(World.class);

        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        when(user.isAuthorized("essentials.sethome.multiple")).thenReturn(true);
        when(user.isAuthorized("essentials.sethome.multiple.vip")).thenReturn(false);
        when(user.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world_another");

        assertEquals(3, settings.getHomeLimit(user));
    }

    @Test
    public void testHomeInWG() throws NoSuchFieldException {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);

        final Field field = EssentialsCommand.class.getDeclaredField("ess");
        field.setAccessible(true);
        FieldSetter.setField(commandhome, field, essentials);

        assertTrue(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_nether"));
    }

    @Test
    public void testHomeNotInWG() throws NoSuchFieldException {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(true);
        final Field field = EssentialsCommand.class.getDeclaredField("ess");
        field.setAccessible(true);
        FieldSetter.setField(commandhome, field, essentials);

        assertFalse(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_the_end"));
    }

    @Test
    public void testHomeInWorld() throws NoSuchFieldException {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(false);

        final Field field = EssentialsCommand.class.getDeclaredField("ess");
        field.setAccessible(true);
        FieldSetter.setField(commandhome, field, essentials);

        assertTrue(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world"));
    }

    @Test
    public void testHomeNotInWorld() throws NoSuchFieldException {
        when(settings.isHomeLimitPerWorldEnabled()).thenReturn(true);
        when(settings.isHomeLimitPerWorldGroupEnabled()).thenReturn(false);

        final Field field = EssentialsCommand.class.getDeclaredField("ess");
        field.setAccessible(true);
        FieldSetter.setField(commandhome, field, essentials);

        assertFalse(commandhome.isUserHomeInWorldOrWorldGroupWorld("world", "world_nether"));
    }
}
