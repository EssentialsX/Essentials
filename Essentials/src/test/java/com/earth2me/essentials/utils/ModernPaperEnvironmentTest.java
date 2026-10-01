package com.earth2me.essentials.utils;

import io.papermc.lib.environments.PaperEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ModernPaperEnvironmentTest {
    private static final String YEAR_VERSION = "26.3-140-main@a1b2c3d (2026-09-30T10:00:00Z) (MC: 26.3)";

    @Test
    public void testPaperLibFallsBackToSynchronousTeleportOnYearVersions() {
        // Documents why ModernPaperEnvironment exists: PaperLib itself picks the synchronous handlers here.
        withVersion(YEAR_VERSION, PaperEnvironment::new, env -> {
            final Entity entity = mock(Entity.class);
            final Location location = new Location(mock(World.class), 0, 64, 0);
            env.teleport(entity, location, TeleportCause.PLUGIN);
            verify(entity).teleport(location, TeleportCause.PLUGIN);
            verify(entity, never()).teleportAsync(any(Location.class), any(TeleportCause.class));
        });
    }

    @Test
    public void testTeleportsAsynchronouslyOnYearVersions() {
        withVersion(YEAR_VERSION, ModernPaperEnvironment::new, env -> {
            final Entity entity = mock(Entity.class);
            final Location location = new Location(mock(World.class), 0, 64, 0);
            env.teleport(entity, location, TeleportCause.PLUGIN);
            verify(entity).teleportAsync(location, TeleportCause.PLUGIN);
            verify(entity, never()).teleport(any(Location.class), any(TeleportCause.class));
        });
    }

    @Test
    public void testLoadsChunksAsynchronouslyOnYearVersions() {
        withVersion(YEAR_VERSION, ModernPaperEnvironment::new, env -> {
            final World world = mock(World.class);
            env.getChunkAtAsync(world, 1, 2, true);
            verify(world).getChunkAtAsync(1, 2, true, false);
        });
    }

    private static <E extends PaperEnvironment> void withVersion(final String version, final Supplier<E> create, final Consumer<E> test) {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getVersion).thenReturn(version);
            test.accept(create.get());
        }
    }
}
