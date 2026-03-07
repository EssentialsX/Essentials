package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.utils.RegistryUtil;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class NyanCommand extends EssentialsTreeNode {
    private static final Sound NOTE_HARP = RegistryUtil.valueOf(Sound.class, "BLOCK_NOTE_BLOCK_HARP", "BLOCK_NOTE_HARP", "NOTE_PIANO");
    private static final String NYAN_TUNE = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";

    private transient TuneRunnable currentTune = null;

    public NyanCommand() {
        super(new String[]{"nyan", "nya"}, true);
    }

    @Override
    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (currentTune != null) {
            currentTune.cancel();
        }

        currentTune = new TuneRunnable(NYAN_TUNE, NOTE_HARP, ess::getOnlinePlayers);
        currentTune.runTaskTimer(ess, 20, 2);
    }

    private static class TuneRunnable extends BukkitRunnable {
        private static final Map<String, Float> noteMap = ImmutableMap.<String, Float>builder()
                .put("1F#", 0.5f)
                .put("1G", 0.53f)
                .put("1G#", 0.56f)
                .put("1A", 0.6f)
                .put("1A#", 0.63f)
                .put("1B", 0.67f)
                .put("1C", 0.7f)
                .put("1C#", 0.76f)
                .put("1D", 0.8f)
                .put("1D#", 0.84f)
                .put("1E", 0.9f)
                .put("1F", 0.94f)
                .put("2F#", 1.0f)
                .put("2G", 1.06f)
                .put("2G#", 1.12f)
                .put("2A", 1.18f)
                .put("2A#", 1.26f)
                .put("2B", 1.34f)
                .put("2C", 1.42f)
                .put("2C#", 1.5f)
                .put("2D", 1.6f)
                .put("2D#", 1.68f)
                .put("2E", 1.78f)
                .put("2F", 1.88f)
                .build();

        private final String[] tune;
        private final Sound sound;
        private final Supplier<Collection<Player>> players;
        private int i = 0;

        TuneRunnable(final String tuneStr, final Sound sound, final Supplier<Collection<Player>> players) {
            this.tune = tuneStr.split(",");
            this.sound = sound;
            this.players = players;
        }

        @Override
        public void run() {
            final String note = tune[i];
            i++;
            if (i >= tune.length) {
                cancel();
            }
            if (note == null || note.isEmpty()) {
                return;
            }

            for (final Player onlinePlayer : players.get()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1, noteMap.get(note));
            }
        }
    }
}
