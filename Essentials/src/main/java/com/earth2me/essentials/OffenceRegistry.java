package com.earth2me.essentials;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class OffenceRegistry {

    private static final long NINETY_DAYS_MS = 90L * 24L * 60L * 60L * 1000L;
    private final List<Offence> offences = new CopyOnWriteArrayList<>();
    private final File dataFile;
    private final IEssentials ess;

    public OffenceRegistry(final IEssentials ess) {
        this.ess = ess;
        this.dataFile = new File(ess.getDataFolder(), "offences.yml");
        try {
            load();
        } catch (final Exception e) {
            ess.getLogger().log(Level.WARNING, "Failed to load offences.yml, starting with empty registry.", e);
        }
    }

    public void addOffence(final Offence offence) {
        offences.add(offence);
        try {
            save();
        } catch (final Exception e) {
            ess.getLogger().log(Level.WARNING, "Failed to save offences.yml.", e);
        }
    }

    public List<Offence> getOffences(final String playerName) {
        final long cutoff = System.currentTimeMillis() - NINETY_DAYS_MS;
        return offences.stream()
                .filter(o -> o.getTargetName().equalsIgnoreCase(playerName))
                .filter(o -> o.getTimestamp() >= cutoff)
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public void purgeOld() {
        final long cutoff = System.currentTimeMillis() - NINETY_DAYS_MS;
        final boolean removed = offences.removeIf(o -> o.getTimestamp() < cutoff);
        if (removed) {
            ess.getLogger().log(Level.INFO, "Purged old offences from registry.");
            try {
                save();
            } catch (final Exception e) {
                ess.getLogger().log(Level.WARNING, "Failed to save offences.yml after purge.", e);
            }
        }
    }

    private void load() {
        try {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            if (!dataFile.exists()) {
                dataFile.createNewFile();
                ess.getLogger().log(Level.INFO, "Created new offences.yml");
                return;
            }
        } catch (final IOException e) {
            ess.getLogger().log(Level.SEVERE, "Failed to create offences.yml", e);
            return;
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        final List<?> list = config.getList("offences");

        if (list == null || list.isEmpty()) {
            ess.getLogger().log(Level.INFO, "offences.yml is empty, no offences loaded.");
            return;
        }

        int loaded = 0;
        int failed = 0;

        for (final Object obj : list) {
            if (!(obj instanceof Map)) {
                failed++;
                continue;
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) obj;
            try {
                final String typeStr = (String) map.get("type");
                if (typeStr == null) {
                    ess.getLogger().log(Level.WARNING, "Skipping offence entry with missing type.");
                    failed++;
                    continue;
                }

                final Offence.Type type = Offence.Type.valueOf(typeStr);
                final String targetName = (String) map.getOrDefault("targetName", "");
                final String staffName = (String) map.getOrDefault("staffName", "");
                final String reason = (String) map.getOrDefault("reason", "");
                final Object tsObj = map.get("timestamp");
                final long timestamp = tsObj instanceof Number ? ((Number) tsObj).longValue() : 0L;
                final String extra = map.containsKey("extra") ? (String) map.get("extra") : null;

                if (timestamp < System.currentTimeMillis() - NINETY_DAYS_MS) {
                    continue;
                }

                offences.add(new Offence(type, targetName, staffName, reason, timestamp, extra));
                loaded++;
            } catch (final IllegalArgumentException e) {
                ess.getLogger().log(Level.WARNING, "Unknown offence type in offences.yml, skipping entry: " + map.get("type"));
                failed++;
            } catch (final Exception e) {
                ess.getLogger().log(Level.WARNING, "Failed to load an offence entry from offences.yml: " + obj, e);
                failed++;
            }
        }

        ess.getLogger().log(Level.INFO, "Loaded " + loaded + " offences from offences.yml" + (failed > 0 ? " (" + failed + " entries skipped)" : "") + ".");
    }

    public void save() {
        final YamlConfiguration config = new YamlConfiguration();
        final List<Map<String, Object>> list = new ArrayList<>();

        for (final Offence o : offences) {
            if (o.getTargetName() == null || o.getType() == null) {
                continue;
            }
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", o.getType().name());
            map.put("targetName", o.getTargetName());
            map.put("staffName", o.getStaffName() != null ? o.getStaffName() : "");
            map.put("reason", o.getReason() != null ? o.getReason() : "");
            map.put("timestamp", o.getTimestamp());
            if (o.getExtra() != null && !o.getExtra().isEmpty()) {
                map.put("extra", o.getExtra());
            }
            list.add(map);
        }

        config.set("offences", list);

        try {
            config.save(dataFile);
        } catch (final IOException e) {
            ess.getLogger().log(Level.SEVERE, "Failed to save offences.yml", e);
        }
    }
}




