package com.earth2me.essentials.items;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;
import net.ess3.api.IItemDb;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class CustomItemResolver implements IItemDb.ItemResolver, IConf {
    private final EssentialsConf config;
    private final Essentials ess;
    private final HashMap<String, String> map = new HashMap<>();

    public CustomItemResolver(final Essentials ess) {
        config = new EssentialsConf(new File(ess.getDataFolder(), "custom_items.yml"));
        this.ess = ess;
        config.setTemplateName("/custom_items.yml");
    }

    @Override
    public ItemStack apply(final String item) {
        if (map.containsKey(item)) {
            try {
                return ess.getItemDb().get(map.get(item));
            } catch (final Exception ignored) {
            }
        }

        return null;
    }

    @Override
    public Collection<String> getNames() {
        return map.keySet();
    }

    @Override
    public void reloadConfig() {
        map.clear();
        config.load();

        final ConfigurationSection section = config.getConfigurationSection("aliases");
        if (section == null || section.getKeys(false).isEmpty()) {
            ess.getLogger().warning("No aliases found in custom_items.yml.");
            return;
        }

        for (final String alias : section.getKeys(false)) {
            if (!section.isString(alias)) continue;
            final String target = section.getString(alias);

            if (target != null && !section.contains(target) && existsInItemDb(target)) {
                map.put(alias, target);
            }
        }
    }

    public void setAlias(final String alias, final String target) {
        if (map.containsKey(alias) && map.get(alias).equalsIgnoreCase(target)) {
            return;
        }

        map.put(alias, target);
        save();
    }

    public void removeAlias(final String alias) {
        if (map.remove(alias) != null) {
            save();
        }
    }

    private void save() {
        config.setProperty("aliases", map);
        config.save();
    }

    private boolean existsInItemDb(final String item) {
        try {
            ess.getItemDb().get(item);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
