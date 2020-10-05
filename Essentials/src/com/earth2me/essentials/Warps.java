package com.earth2me.essentials;

import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.InvalidNameException;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.Server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.tl;

public class Warps implements IConf, net.ess3.api.IWarps {
    private static final Logger logger = Logger.getLogger("Essentials");
    private final Map<StringIgnoreCase, EssentialsConf> warpPoints = new HashMap<>();
    private final File warpsFolder;
    private final Server server;

    public Warps(final Server server, final File dataFolder) {
        this.server = server;
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
    public Collection<String> getList() {
        final List<String> keys = new ArrayList<>();
        for (final StringIgnoreCase stringIgnoreCase : warpPoints.keySet()) {
            keys.add(stringIgnoreCase.getString());
        }
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        return keys;
    }

    @Override
    public Location getWarp(final String warp) throws WarpNotFoundException, InvalidWorldException {
        final EssentialsConf conf = warpPoints.get(new StringIgnoreCase(warp));
        if (conf == null) {
            throw new WarpNotFoundException();
        }
        return conf.getLocation(null, server);
    }

    @Override
    public void setWarp(final String name, final Location loc) throws Exception {
        setWarp(null, name, loc);
    }

    @Override
    public void setWarp(final IUser user, final String name, final Location loc) throws Exception {
        final String filename = StringUtil.sanitizeFileName(name);
        EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
        if (conf == null) {
            final File confFile = new File(warpsFolder, filename + ".yml");
            if (confFile.exists()) {
                throw new Exception(tl("similarWarpExist"));
            }
            conf = new EssentialsConf(confFile);
            warpPoints.put(new StringIgnoreCase(name), conf);
        }
        conf.setProperty(null, loc);
        conf.setProperty("name", name);
        if (user != null) conf.setProperty("lastowner", user.getBase().getUniqueId().toString());
        try {
            conf.saveWithError();
        } catch (final IOException ex) {
            throw new IOException(tl("invalidWarpName"));
        }
    }

    @Override
    public UUID getLastOwner(final String warp) throws WarpNotFoundException {
        final EssentialsConf conf = warpPoints.get(new StringIgnoreCase(warp));
        if (conf == null) {
            throw new WarpNotFoundException();
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(conf.getString("lastowner"));
        } catch (final Exception ignored) {
        }
        return uuid;
    }

    @Override
    public void removeWarp(final String name) throws Exception {
        final EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
        if (conf == null) {
            throw new Exception(tl("warpNotExist"));
        }
        if (!conf.getFile().delete()) {
            throw new Exception(tl("warpDeleteError"));
        }
        warpPoints.remove(new StringIgnoreCase(name));
    }

    @Override
    public final void reloadConfig() {
        warpPoints.clear();
        final File[] listOfFiles = warpsFolder.listFiles();
        if (listOfFiles.length >= 1) {
            for (final File listOfFile : listOfFiles) {
                final String filename = listOfFile.getName();
                if (listOfFile.isFile() && filename.endsWith(".yml")) {
                    try {
                        final EssentialsConf conf = new EssentialsConf(listOfFile);
                        conf.load();
                        final String name = conf.getString("name");
                        if (name != null) {
                            warpPoints.put(new StringIgnoreCase(name), conf);
                        }
                    } catch (final Exception ex) {
                        logger.log(Level.WARNING, tl("loadWarpError", filename), ex);
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
