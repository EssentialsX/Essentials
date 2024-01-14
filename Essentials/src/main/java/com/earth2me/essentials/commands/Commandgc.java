package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandgc extends EssentialsCommand {
    public Commandgc() {
        super("gc");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final double tps = 20d; //TODO
        final ChatColor color;
        if (tps >= 18.0) {
            color = ChatColor.GREEN;
        } else if (tps >= 15.0) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        sender.sendMessage(tl("uptime", DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime())));
        sender.sendMessage(tl("tps", "" + color + NumberUtil.formatDouble(tps)));
        sender.sendMessage(tl("gcmax", Runtime.getRuntime().maxMemory() / 1024 / 1024));
        sender.sendMessage(tl("gctotal", Runtime.getRuntime().totalMemory() / 1024 / 1024));
        sender.sendMessage(tl("gcfree", Runtime.getRuntime().freeMemory() / 1024 / 1024));

        final List<World> worlds = server.getWorlds();
        for (final World w : worlds) {
            String worldType = "World";
            switch (w.getEnvironment()) {
                case NETHER:
                    worldType = "Nether";
                    break;
                case THE_END:
                    worldType = "The End";
                    break;
            }

            int tileEntities = 0;

            try {
                for (final Chunk chunk : w.getLoadedChunks()) {
                    tileEntities += chunk.getTileEntities().length;
                }
            } catch (final java.lang.ClassCastException ex) {
                ess.getLogger().log(Level.SEVERE, "Corrupted chunk data on world " + w, ex);
            }

            sender.sendMessage(tl("gcWorld", worldType, w.getName(), w.getLoadedChunks().length, w.getEntities().size(), tileEntities));
        }
    }
}
