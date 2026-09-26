package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.utils.RegistryUtil;
import com.google.common.collect.ImmutableMap;
import net.ess3.provider.SchedulingProvider;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class NyanCommand extends EssentialsTreeNode {
    private static final Sound NOTE_HARP = RegistryUtil.valueOf(Sound.class, "BLOCK_NOTE_BLOCK_HARP", "BLOCK_NOTE_HARP", "NOTE_PIANO");
    private static final String NYAN_TUNE = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";

    private transient SchedulingProvider.EssentialsTask currentTune = null;

    public NyanCommand() {
        super(new String[]{"nyan", "nya"}, true);
    }

    @Override
    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (currentTune != null) {
            currentTune.cancel();
        }

        final TuneTicker ticker = new TuneTicker(ess, NYAN_TUNE, NOTE_HARP, ess::getOnlinePlayers);
        currentTune = ess.scheduleGlobalRepeatingTask(() -> {
            if (!ticker.tick()) {
                currentTune.cancel();
            }
        }, 20, 2);
    }

    private static class TuneTicker {
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

        private final com.earth2me.essentials.IEssentials ess;
        private final String[] tune;
        private final Sound sound;
        private final Supplier<Collection<Player>> players;
        private int i = 0;

        TuneTicker(final com.earth2me.essentials.IEssentials ess, final String tuneStr, final Sound sound, final Supplier<Collection<Player>> players) {
            this.ess = ess;
            this.tune = tuneStr.split(",");
            this.sound = sound;
            this.players = players;
        }

        /**
         * Plays the next note to every online player and returns whether there are more notes left.
         */
        boolean tick() {
            final String note = tune[i];
            i++;
            final boolean more = i < tune.length;

            if (note != null && !note.isEmpty()) {
                final Float pitch = noteMap.get(note);
                for (final Player onlinePlayer : players.get()) {
                    ess.scheduleEntityDelayedTask(onlinePlayer, () -> onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1, pitch));
                }
            }

            return more;
        }
    }
}
