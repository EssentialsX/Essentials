package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.earth2me.essentials.I18n.capitalCase;
import static com.earth2me.essentials.I18n.tl;

public class Kits implements IConf {
    private final IEssentials ess;
    private final EssentialsConfiguration config;
    private CommentedConfigurationNode kits;

    public Kits(final IEssentials essentials) {
        this.ess = essentials;
        this.config = new EssentialsConfiguration(new File(essentials.getDataFolder(), "kits.yml"), "/kits.yml");

        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        kits = _getKits();
    }

    private CommentedConfigurationNode _getKits() {
        final CommentedConfigurationNode section = config.getSection("kits");
        final CommentedConfigurationNode newSection = CommentedConfigurationNode.root();

        // Read kits.yml in main Essentials data folder
        if (section != null) {
            for (final String kitItem : ConfigurateUtil.getKeys(section)) {
                final CommentedConfigurationNode kitSection = section.node(kitItem);
                if (kitSection.isMap()) {
                    try {
                        newSection.node(kitItem.toLowerCase(Locale.ENGLISH)).set(kitSection);
                    } catch (SerializationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Read kits folder
        File kitsFolder = new File(this.ess.getDataFolder(), "kits");
        if(!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        } else {
            File[] kitsFiles = kitsFolder.listFiles();
            int numFiles = kitsFiles.length;

            for(int i = 0; i < numFiles; i++) {
                File f = kitsFiles[i];
                if(f.getName().endsWith(".yml")) {
                    EssentialsConfiguration essConfig = new EssentialsConfiguration(new File(ess.getDataFolder(), "kits" + File.separator + f.getName()), "/kits.yml");
                    essConfig.load();
                    if(essConfig.hasProperty("kits")){
                        final CommentedConfigurationNode kits = essConfig.getSection("kits");

                        if(!kits.isNull()) {
                            for (final String kitItem : ConfigurateUtil.getKeys(kits)) {
                                final CommentedConfigurationNode kitSection = kits.node(kitItem);
                                if (kitSection.isMap()) {
                                    try {
                                        newSection.node(kitItem.toLowerCase(Locale.ENGLISH)).set(kitSection);
                                    } catch (SerializationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        ess.getLogger().info("File in kits folder not loaded because \"kits\" section could not be found. Please see proper formatting for kit files. File: " + f.getName());
                    }
                }
            }
        }
        return newSection;
    }

    public EssentialsConfiguration getConfig() {
        return config;
    }

    public CommentedConfigurationNode getKits() {
        return kits;
    }

    public Map<String, Object> getKit(String name) {
        name = name.replace('.', '_').replace('/', '_');
        if (getKits() != null) {
            final CommentedConfigurationNode kits = getKits();
            // Other parts of the codebase/3rd party plugins expect us to lowercase kit names here.
            // This isn't strictly needed for the future of Essentials, but for compatibility it's here.
            final CommentedConfigurationNode kitSection = kits.node(name.toLowerCase());
            if (!kitSection.virtual() && kitSection.isMap()) {
                return ConfigurateUtil.getRawMap(kitSection);
            }
        }

        return null;
    }

    // Tries to find an existing kit name that matches the given name, ignoring case. Returns null if no match.
    public String matchKit(final String name) {
        final CommentedConfigurationNode section = config.getSection("kits");
        if (section != null) {
            for (final String kitName : ConfigurateUtil.getKeys(section)) {
                if (kitName.equalsIgnoreCase(name)) {
                    return kitName;
                }
            }
        }
        return null;
    }

    public void addKit(final String name, final List<String> lines, final long delay) {
        // Will overwrite but w/e
        config.setProperty("kits." + name + ".delay", delay);
        config.setProperty("kits." + name + ".items", lines);
        kits = _getKits();
        config.save();
    }

    public void removeKit(final String name) {
        config.removeProperty("kits." + name);
        kits = _getKits();
        config.save();
    }

    public String listKits(final net.ess3.api.IEssentials ess, final User user) throws Exception {
        try {
            final CommentedConfigurationNode kits = config.getSection("kits");
            final StringBuilder list = new StringBuilder();
            for (final String kitItem : ConfigurateUtil.getKeys(kits)) {
                if (user == null) {
                    list.append(" ").append(capitalCase(kitItem));
                } else if (user.isAuthorized("essentials.kits." + kitItem.toLowerCase(Locale.ENGLISH))) {
                    String cost = "";
                    String name = capitalCase(kitItem);
                    final BigDecimal costPrice = new Trade("kit-" + kitItem.toLowerCase(Locale.ENGLISH), ess).getCommandCost(user);
                    if (costPrice.signum() > 0) {
                        cost = tl("kitCost", NumberUtil.displayCurrency(costPrice, ess));
                    }

                    final Kit kit = new Kit(kitItem, ess);
                    final double nextUse = kit.getNextUse(user);
                    if (nextUse == -1 && ess.getSettings().isSkippingUsedOneTimeKitsFromKitList()) {
                        continue;
                    } else if (nextUse != 0) {
                        name = tl("kitDelay", name);
                    }

                    list.append(" ").append(name).append(cost);
                }
            }
            return list.toString().trim();
        } catch (final Exception ex) {
            throw new Exception(tl("kitError"), ex);
        }

    }
}
