package com.earth2me.essentials;

import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static com.earth2me.essentials.I18n.capitalCase;
import static com.earth2me.essentials.I18n.tl;

public class Kits implements IConf, IKitSettings {

    private final Logger logger;
    private final IEssentials ess;
    private final EssentialsConf config;
    private ConfigurationSection kits;

    public Kits(final IEssentials essentials) {
        logger = essentials.getLogger();
        ess = essentials;
        config = new EssentialsConf(new File(essentials.getDataFolder(), "kits.yml"));
        config.setTemplateName("/kits.yml");
        config.load();
    }

    public void attemptConversion() {
        if (!config.isFirstRun()) {
            return;
        }

        logger.info("Attempting to convert old kits in config.yml to new kits.yml");

        ConfigurationSection section = ess.getSettings().getKitSection();
        if (section == null) {
            logger.info("No kits found to migrate.");
            return;
        }

        int count = 0;

        Map<String, Object> legacyKits = ess.getSettings().getKitSection().getValues(true);

        for (Map.Entry<String, Object> entry : legacyKits.entrySet()) {
            logger.info("Converting " + entry.getKey());
            config.set("kits." + entry.getKey(), entry.getValue());
        }

        logger.info("Done converting kits.");
        config.save();
    }

    @Override
    public void reloadConfig() {
        kits = _getKits();
    }

    private ConfigurationSection _getKits() {
        if (config.isConfigurationSection("kits")) {
            final ConfigurationSection section = config.getConfigurationSection("kits");
            final ConfigurationSection newSection = new MemoryConfiguration();
            for (String kitItem : section.getKeys(false)) {
                if (section.isConfigurationSection(kitItem)) {
                    newSection.set(kitItem.toLowerCase(Locale.ENGLISH), section.getConfigurationSection(kitItem));
                }
            }
            return newSection;
        }
        return null;
    }

    public ConfigurationSection getKits() {
        return kits;
    }

    public Map<String, Object> getKit(String name) {
        name = name.replace('.', '_').replace('/', '_');
        if (getKits() != null) {
            final ConfigurationSection kits = getKits();
            if (kits.isConfigurationSection(name)) {
                return kits.getConfigurationSection(name).getValues(true);
            }
        }
        return null;
    }

    public void addKit(String name, List<String> lines, long delay) {
        // Will overwrite but w/e
        config.set("kits." + name + ".delay", delay);
        config.set("kits." + name + ".items", lines);
        kits = _getKits();
        config.save();
    }

    @Override
    public boolean isSkippingUsedOneTimeKitsFromKitList() {
        return config.getBoolean("skip-used-one-time-kits-from-kit-list", false);
    }

    @Override
    public boolean isPastebinCreateKit() {
        return config.getBoolean("pastebin-createkit", true);
    }

    public String listKits(final net.ess3.api.IEssentials ess, final User user) throws Exception {
        try {
            final ConfigurationSection kits = config.getConfigurationSection("kits");
            final StringBuilder list = new StringBuilder();
            for (String kitItem : kits.getKeys(false)) {
                if (user == null) {
                    list.append(" ").append(capitalCase(kitItem));
                } else if (user.isAuthorized("essentials.kits." + kitItem.toLowerCase(Locale.ENGLISH))) {
                    String cost = "";
                    String name = capitalCase(kitItem);
                    BigDecimal costPrice = new Trade("kit-" + kitItem.toLowerCase(Locale.ENGLISH), ess).getCommandCost(user);
                    if (costPrice.signum() > 0) {
                        cost = tl("kitCost", NumberUtil.displayCurrency(costPrice, ess));
                    }

                    Kit kit = new Kit(kitItem, ess);
                    double nextUse = kit.getNextUse(user);
                    if (nextUse == -1 && ess.getKits().isSkippingUsedOneTimeKitsFromKitList()) {
                        continue;
                    } else if (nextUse != 0) {
                        name = tl("kitDelay", name);
                    }

                    list.append(" ").append(name).append(cost);
                }
            }
            return list.toString().trim();
        } catch (Exception ex) {
            throw new Exception(tl("kitError"), ex);
        }

    }
}
