package com.earth2me.essentials.provider;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.ess3.provider.TaskSchedulerProvider;
import net.ess3.provider.providers.BukkitTaskSchedulerProvider;
import net.ess3.provider.providers.FoliaTaskSchedulerProvider;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskSchedulerProviderTest {
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testBukkitProviderRunsTasksOnTheMainThread() {
        final PluginMock plugin = MockBukkit.createMockPlugin();
        final PlayerMock player = server.addPlayer();
        final TaskSchedulerProvider scheduler = new BukkitTaskSchedulerProvider(plugin);
        final AtomicInteger runs = new AtomicInteger();

        scheduler.runGlobal(runs::incrementAndGet, 0);
        scheduler.runEntity(player, runs::incrementAndGet, 0);
        scheduler.runLocation(player.getLocation(), runs::incrementAndGet, 0);
        assertEquals(0, runs.get());
        server.getScheduler().performOneTick();
        assertEquals(3, runs.get());

        scheduler.runGlobal(runs::incrementAndGet, 2);
        server.getScheduler().performOneTick();
        assertEquals(3, runs.get());
        server.getScheduler().performOneTick();
        assertEquals(4, runs.get());
    }

    @Test
    public void testBukkitProviderCancelsTasks() {
        final PluginMock plugin = MockBukkit.createMockPlugin();
        final TaskSchedulerProvider scheduler = new BukkitTaskSchedulerProvider(plugin);
        final AtomicInteger runs = new AtomicInteger();

        scheduler.runGlobal(runs::incrementAndGet, 1).cancel();
        final TaskSchedulerProvider.Task timer = scheduler.runGlobalTimer(runs::incrementAndGet, 1, 1);
        server.getScheduler().performTicks(3);
        assertEquals(3, runs.get());
        timer.cancel();
        server.getScheduler().performTicks(3);
        assertEquals(3, runs.get());

        scheduler.runGlobalTimer(runs::incrementAndGet, 1, 1);
        scheduler.cancelAll();
        server.getScheduler().performTicks(3);
        assertEquals(3, runs.get());
    }

    @Test
    public void testExecuteRunsInlineOnTheMainThread() {
        final PluginMock plugin = MockBukkit.createMockPlugin();
        final PlayerMock player = server.addPlayer();
        final TaskSchedulerProvider scheduler = new BukkitTaskSchedulerProvider(plugin);
        final AtomicInteger runs = new AtomicInteger();

        scheduler.executeGlobal(runs::incrementAndGet);
        scheduler.executeEntity(player, runs::incrementAndGet);
        scheduler.executeLocation(player.getLocation(), runs::incrementAndGet);
        assertEquals(3, runs.get());
    }

    @Test
    public void testFoliaProviderUsesTheSchedulerOfTheThingItTouches() {
        final Fixture f = new Fixture();
        final Runnable task = () -> {
        };
        final Location location = new Location(null, 0, 0, 0);

        f.provider.runGlobal(task, 0);
        verify(f.global).run(eq(f.plugin), any());
        f.provider.runGlobal(task, 5);
        verify(f.global).runDelayed(eq(f.plugin), any(), eq(5L));
        f.provider.runGlobalTimer(task, 0, 20);
        verify(f.global).runAtFixedRate(eq(f.plugin), any(), eq(1L), eq(20L));

        f.provider.runEntity(f.entity, task, 0);
        verify(f.entityScheduler).run(eq(f.plugin), any(), isNull());
        f.provider.runEntity(f.entity, task, 5);
        verify(f.entityScheduler).runDelayed(eq(f.plugin), any(), isNull(), eq(5L));
        f.provider.runEntityTimer(f.entity, task, null, 0, 20);
        verify(f.entityScheduler).runAtFixedRate(eq(f.plugin), any(), isNull(), eq(1L), eq(20L));

        f.provider.runLocation(location, task, 0);
        verify(f.region).run(eq(f.plugin), eq(location), any());
        f.provider.runLocation(location, task, 5);
        verify(f.region).runDelayed(eq(f.plugin), eq(location), any(), eq(5L));

        f.provider.runAsync(task, 0);
        verify(f.async).runNow(eq(f.plugin), any());
        f.provider.runAsync(task, 5);
        verify(f.async).runDelayed(eq(f.plugin), any(), eq(250L), eq(TimeUnit.MILLISECONDS));
        f.provider.runAsyncTimer(task, 20, 40);
        verify(f.async).runAtFixedRate(eq(f.plugin), any(), eq(1000L), eq(2000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testFoliaProviderRunsExecuteInlineOnlyForTheOwningThread() {
        final Fixture f = new Fixture();
        final AtomicInteger runs = new AtomicInteger();

        when(f.server.isOwnedByCurrentRegion(f.entity)).thenReturn(true);
        f.provider.executeEntity(f.entity, runs::incrementAndGet);
        assertEquals(1, runs.get());
        verify(f.entityScheduler, never()).run(any(), any(), any());

        when(f.server.isOwnedByCurrentRegion(f.entity)).thenReturn(false);
        f.provider.executeEntity(f.entity, runs::incrementAndGet);
        assertEquals(1, runs.get());
        verify(f.entityScheduler).run(eq(f.plugin), any(), isNull());

        when(f.server.isGlobalTickThread()).thenReturn(false);
        f.provider.executeGlobal(runs::incrementAndGet);
        assertEquals(1, runs.get());
        verify(f.global).run(eq(f.plugin), any());
    }

    @Test
    public void testFoliaProviderReportsAnEntityRemovedBeforeSchedulingAsRetired() {
        final Fixture f = new Fixture();
        final AtomicInteger retired = new AtomicInteger();

        when(f.entityScheduler.run(any(), any(), any())).thenReturn(null);
        f.provider.runEntity(f.entity, () -> {
        }, retired::incrementAndGet, 0).cancel();
        assertEquals(1, retired.get());
    }

    @Test
    public void testFoliaProviderCancelsGlobalAndAsyncTasksOnly() {
        final Fixture f = new Fixture();

        f.provider.cancelAll();
        verify(f.global).cancelTasks(f.plugin);
        verify(f.async).cancelTasks(f.plugin);
        verify(f.region, never()).run(any(Plugin.class), any(Location.class), any());
    }

    private static final class Fixture {
        private final Plugin plugin = mock(Plugin.class);
        private final Server server = mock(Server.class);
        private final GlobalRegionScheduler global = mock(GlobalRegionScheduler.class);
        private final RegionScheduler region = mock(RegionScheduler.class);
        private final AsyncScheduler async = mock(AsyncScheduler.class);
        private final EntityScheduler entityScheduler = mock(EntityScheduler.class);
        private final Entity entity = mock(Entity.class);
        private final FoliaTaskSchedulerProvider provider;

        private Fixture() {
            when(plugin.getServer()).thenReturn(server);
            when(server.getGlobalRegionScheduler()).thenReturn(global);
            when(server.getRegionScheduler()).thenReturn(region);
            when(server.getAsyncScheduler()).thenReturn(async);
            when(entity.getScheduler()).thenReturn(entityScheduler);
            final ScheduledTask scheduled = mock(ScheduledTask.class);
            when(global.run(any(), any())).thenReturn(scheduled);
            when(global.runDelayed(any(), any(), anyLong())).thenReturn(scheduled);
            when(global.runAtFixedRate(any(), any(), anyLong(), anyLong())).thenReturn(scheduled);
            when(entityScheduler.run(any(), any(), any())).thenReturn(scheduled);
            when(entityScheduler.runDelayed(any(), any(), any(), anyLong())).thenReturn(scheduled);
            when(entityScheduler.runAtFixedRate(any(), any(), any(), anyLong(), anyLong())).thenReturn(scheduled);
            when(region.run(any(Plugin.class), any(Location.class), any())).thenReturn(scheduled);
            when(region.runDelayed(any(Plugin.class), any(Location.class), any(), anyLong())).thenReturn(scheduled);
            when(async.runNow(any(), any())).thenReturn(scheduled);
            when(async.runDelayed(any(), any(), anyLong(), any())).thenReturn(scheduled);
            when(async.runAtFixedRate(any(), any(), anyLong(), anyLong(), any())).thenReturn(scheduled);
            provider = new FoliaTaskSchedulerProvider(plugin);
        }
    }
}
