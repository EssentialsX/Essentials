package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.provider.TileEntityProvider;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Commandgc extends EssentialsCommand {
    public Commandgc() {
        super("gc");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final double tps = ess.getTimer().getAverageTPS();
        final ChatColor color;
        if (tps >= 18.0) {
            color = ChatColor.GREEN;
        } else if (tps >= 15.0) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        sender.sendTl("uptime", DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        if (!VersionUtil.isFoliaServer()) sender.sendTl("tps", "" + color + NumberUtil.formatDouble(tps));
        sender.sendTl("gcmax", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        sender.sendTl("gctotal", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        sender.sendTl("gcfree", Runtime.getRuntime().freeMemory() / 1024 / 1024);

        final List<World> worlds = server.getWorlds();
        final TileEntityProvider tileEntityProvider = ess.provider(TileEntityProvider.class);
        for (final World w : worlds) {
            getChunkStatsAsync(tileEntityProvider, w, result -> {
                sender.sendTl("gcWorld",
                        getWorldType(w),
                        w.getName(),
                        w.getLoadedChunks().length,
                        result.getEntities(),
                        result.getTileEntities()
                );
            });
        }
    }

    private String getWorldType(World world) {
        switch (world.getEnvironment()) {
            case NETHER:
                return "Nether";
            case THE_END:
                return "The End";
            default:
                return "World";
        }
    }

    private void getChunkStatsAsync(TileEntityProvider tileEntityProvider, World w, Consumer<Result> callback) {

        Chunk[] chunks = w.getLoadedChunks();

        if (chunks.length == 0) {
            callback.accept(new Result(0, 0));
            return;
        }

        if (!VersionUtil.isFoliaServer()) {
            int tileEntities = 0;

            try {
                for (final Chunk chunk : chunks) {
                    tileEntities += tileEntityProvider.getTileEntities(chunk).length;
                }
            } catch (final java.lang.ClassCastException ex) {
                ess.getLogger().log(Level.SEVERE, "Corrupted chunk data on world " + w, ex);
            }

            callback.accept(new Result(
                    tileEntities,
                    w.getEntities().size()
            ));
            return;
        }

        AtomicInteger remaining = new AtomicInteger(chunks.length);
        AtomicInteger totalTileEntities = new AtomicInteger(0);
        AtomicInteger totalEntities = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        for (Chunk chunk : chunks) {
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();

            ess.getSchedulerAdapter().runRegionTask(w, chunkX, chunkZ, () -> {
                try {
                    totalTileEntities.addAndGet(
                            tileEntityProvider.getTileEntities(chunk).length
                    );
                    totalEntities.addAndGet(
                            chunk.getEntities().length
                    );
                } catch (Exception ex) {
                    errors.incrementAndGet();
                    ess.getLogger().log(Level.SEVERE, "Corrupted chunk data", ex);
                } finally {
                    if (remaining.decrementAndGet() == 0) {
                        ess.getSchedulerAdapter().runTask(() ->
                                callback.accept(new Result(
                                        totalTileEntities.get(),
                                        totalEntities.get()
                                ))
                        );
                    }
                }
            });
        }
    }

    private static class Result {
        private final int tileEntities;
        private final int entities;

        public Result(int tileEntities, int entities) {
            this.tileEntities = tileEntities;
            this.entities = entities;
        }

        public int getTileEntities() {
            return tileEntities;
        }

        public int getEntities() {
            return entities;
        }
    }
}
