package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.utils.NumberUtil;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.earth2me.essentials.I18n.capitalCase;
import static com.earth2me.essentials.I18n.tl;

public class Kits implements IConf {
    private final IEssentials ess;
    private final EssentialsConfiguration rootConfig;
    private final Map<String, EssentialsConfiguration> kitToConfigMap = new HashMap<>();
    private final Map<String, CommentedConfigurationNode> kitToItemsMap = new HashMap<>();

    public Kits(final IEssentials essentials) {
        this.ess = essentials;
        this.rootConfig = new EssentialsConfiguration(new File(essentials.getDataFolder(), "kits.yml"), "/kits.yml");

        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        rootConfig.load();
        parseKits();
    }

    private void parseKit(final String kitName, final CommentedConfigurationNode kitSection, final EssentialsConfiguration parentConfig) {
        if (kitSection.isMap()) {
            final String effectiveKitName = kitName.toLowerCase(Locale.ENGLISH);
            kitToConfigMap.put(effectiveKitName, parentConfig);
            kitToItemsMap.put(effectiveKitName, kitSection);
        }
    }

    private void parseKits() {
        kitToConfigMap.clear();
        kitToItemsMap.clear();

        // Kits from kits.yml file
        final CommentedConfigurationNode fileKits = rootConfig.getSection("kits");
        if (fileKits != null) {
            for (final Map.Entry<String, CommentedConfigurationNode> kitEntry : ConfigurateUtil.getMap(fileKits).entrySet()) {
                parseKit(kitEntry.getKey(), kitEntry.getValue(), rootConfig);
            }
        }

        // Kits from kits subdirectory
        final File kitsFolder = new File(this.ess.getDataFolder(), "kits");
        if (!kitsFolder.exists() || !kitsFolder.isDirectory()) {
            return;
        }

        final File[] kitsFiles = kitsFolder.listFiles();

        //noinspection ConstantConditions - will not be null, conditions checked above.
        for (final File kitFile : kitsFiles) {
            if (kitFile.getName().endsWith(".yml")) {
                final EssentialsConfiguration kitConfig = new EssentialsConfiguration(kitFile);
                kitConfig.load();
                final CommentedConfigurationNode kits = kitConfig.getSection("kits");
                if (kits != null) {
                    for (final Map.Entry<String, CommentedConfigurationNode> kitEntry : ConfigurateUtil.getMap(kits).entrySet()) {
                        parseKit(kitEntry.getKey(), kitEntry.getValue(), kitConfig);
                    }
                }
            }
        }
    }

    /**
     * Should be used for EssentialsUpgrade conversions <b>only</b>.
     */
    public EssentialsConfiguration getRootConfig() {
        return rootConfig;
    }

    public Set<String> getKitKeys() {
        return kitToItemsMap.keySet();
    }

    public Map<String, Object> getKit(String name) {
        if (name != null) {
            name = name.replace('.', '_').replace('/', '_');
            if (kitToItemsMap.containsKey(name)) {
                return ConfigurateUtil.getRawMap(kitToItemsMap.get(name));
            }
        }
        return null;
    }

    // Tries to find an existing kit name that matches the given name, ignoring case. Returns null if no match.
    public String matchKit(final String name) {
        if (name != null) {
            for (final String kitName : kitToItemsMap.keySet()) {
                if (kitName.equalsIgnoreCase(name)) {
                    return kitName;
                }
            }
        }
        return null;
    }

    public void addKit(String name, final List<String> lines, final long delay) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        // Will overwrite but w/e
        rootConfig.setProperty("kits." + name + ".delay", delay);
        rootConfig.setProperty("kits." + name + ".items", lines);
        parseKits();
        rootConfig.save();
    }

    public void removeKit(String name) {
        name = name.replace('.', '_').replace('/', '_').toLowerCase(Locale.ENGLISH);
        if (!kitToConfigMap.containsKey(name) || !kitToItemsMap.containsKey(name)) {
            return;
        }

        final EssentialsConfiguration config = kitToConfigMap.get(name);
        config.removeProperty("kits." + name);

        config.blockingSave();
        parseKits();
    }

    public String listKits(final net.ess3.api.IEssentials ess, final User user) throws Exception {
        try {
            final StringBuilder list = new StringBuilder();
            for (final String kitItem : kitToItemsMap.keySet()) {
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
