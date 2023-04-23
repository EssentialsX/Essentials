package com.earth2me.essentials;

import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.ess3.api.InvalidNameException;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Warps implements IConf, net.ess3.api.IWarps {
    private final Map<UUID, EssentialsConfiguration> warpPoints = new HashMap<>();
    private final BiMap<String, UUID> nameUUIDConversion = HashBiMap.create();
    private final File warpsFolder;

    public Warps(final File dataFolder) {
        warpsFolder = new File(dataFolder, "warps");
        if (!warpsFolder.exists()) {
            warpsFolder.mkdirs();
        }
        reloadConfig();
    }

    @Override
    public boolean isEmpty() {
        return warpPoints.isEmpty();
    }

    @Override
    public boolean isWarp(String name) {
        return warpPoints.containsKey(nameUUIDConversion.get(name));
    }

    @Override
    public Collection<String> getList() {
        final List<String> keys = new ArrayList<>(nameUUIDConversion.keySet());
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        return keys;
    }

    @Override
    public Location getWarp(final String warp) throws WarpNotFoundException, InvalidWorldException {
        final EssentialsConfiguration conf = warpPoints.get(nameUUIDConversion.get(warp));
        if (conf == null) {
            throw new WarpNotFoundException();
        }

        final Location loc = conf.getLocation(null).location();
        if (loc == null) {
            throw new WarpNotFoundException();
        }
        return loc;
    }

    @Override
    public void setWarp(final String name, final Location loc) throws Exception {
        setWarp(null, name, loc);
    }

    @Override
    public void setWarp(final IUser user, final String name, final Location loc) throws Exception {
        final UUID uuid = UUID.randomUUID();
        nameUUIDConversion.put(name, uuid);
        EssentialsConfiguration conf = warpPoints.get(uuid);
        if (conf == null) {
            final File confFile = new File(warpsFolder, uuid + ".yml");
            conf = new EssentialsConfiguration(confFile);
            conf.load();
            warpPoints.put(uuid, conf);
        }
        conf.setProperty(null, loc);
        conf.setProperty("name", name);
        if (user != null) {
            conf.setProperty("lastowner", user.getBase().getUniqueId().toString());
        }
        conf.save();
    }

    @Override
    public UUID getLastOwner(final String warp) throws WarpNotFoundException {
        final EssentialsConfiguration conf = warpPoints.get(nameUUIDConversion.get(warp));
        if (conf == null) {
            throw new WarpNotFoundException();
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(conf.getString("lastowner", null));
        } catch (final Exception ignored) {
        }
        return uuid;
    }

    @Override
    public void removeWarp(final String name) throws Exception {
        final UUID uuid = nameUUIDConversion.get(name);
        final EssentialsConfiguration conf = warpPoints.get(uuid);
        if (conf == null) {
            throw new Exception(tl("warpNotExist"));
        }
        if (!conf.getFile().delete()) {
            throw new Exception(tl("warpDeleteError"));
        }
        warpPoints.remove(uuid);
    }

    @Override
    public final void reloadConfig() {
        warpPoints.clear();
        final File[] listOfFiles = warpsFolder.listFiles();
        if (listOfFiles.length >= 1) {
            for (File listOfFile : listOfFiles) {
                final String filename = listOfFile.getName();
                if (listOfFile.isFile() && filename.endsWith(".yml")) {
                    try {
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(filename.substring(0, filename.length()-5));
                        } catch (Exception e) {
                            Essentials.getWrappedLogger().log(Level.INFO, tl("loadDeprecatedWarp", filename));
                            uuid = UUID.randomUUID();
                            final File newFile = new File(warpsFolder, uuid + ".yml");
                            Files.copy(listOfFile.toPath(), newFile.toPath());
                            listOfFile.delete();
                            listOfFile = newFile;
                        }
                        final EssentialsConfiguration conf = new EssentialsConfiguration(listOfFile);
                        conf.load();
                        final String name = conf.getString("name", null);
                        nameUUIDConversion.put(name, uuid);
                        if (name != null && conf.hasProperty("world")) {
                            warpPoints.put(uuid, conf);
                        }
                    } catch (final Exception ex) {
                        Essentials.getWrappedLogger().log(Level.WARNING, tl("loadWarpError", filename), ex);
                    }
                }
            }
        }
    }

    /**
     * @deprecated This method relates to the abandoned 3.x storage refactor and is not implemented.
     */
    @Override
    @Deprecated
    public File getWarpFile(final String name) throws InvalidNameException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCount() {
        return getList().size();
    }

    /**
     * @deprecated This method relates to the abandoned 2.x storage refactor and is not implemented.
     */
    @Deprecated
    private static class StringIgnoreCase {
        private final String string;

        StringIgnoreCase(final String string) {
            this.string = string;
        }

        @Override
        public int hashCode() {
            return getString().toLowerCase(Locale.ENGLISH).hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof StringIgnoreCase) {
                return getString().equalsIgnoreCase(((StringIgnoreCase) o).getString());
            }
            return false;
        }

        public String getString() {
            return string;
        }
    }
}
