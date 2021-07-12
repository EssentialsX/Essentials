package com.earth2me.essentials.items;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IConf;
import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import net.ess3.api.IItemDb;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemResolver implements IItemDb.ItemResolver, IConf {
    private final EssentialsConfiguration config;
    private final Essentials ess;
    private final HashMap<String, String> map = new HashMap<>();

    public CustomItemResolver(final Essentials ess) {
        config = new EssentialsConfiguration(new File(ess.getDataFolder(), "custom_items.yml"), "/custom_items.yml");
        this.ess = ess;
    }

    @Override
    public ItemStack apply(String item) {
        item = item.toLowerCase();
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

    public List<String> getAliasesFor(String item) throws Exception {
        final List<String> results = new ArrayList<>();
        if (item != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (item.equalsIgnoreCase(ess.getItemDb().name(ess.getItemDb().get(entry.getValue())))) {
                    results.add(entry.getKey());
                }
            }
        }
        return results;
    }

    @Override
    public void reloadConfig() {
        map.clear();
        config.load();

        final Map<String, Object> section = ConfigurateUtil.getRawMap(config.getSection("aliases"));
        if (section.isEmpty()) {
            ess.getLogger().warning("No aliases found in custom_items.yml.");
            return;
        }

        for (final Map.Entry<String, Object> entry : section.entrySet()) {
            if (!(entry.getValue() instanceof String)) {
                continue;
            }
            final String alias = entry.getKey().toLowerCase();
            final String target = (String) entry.getValue();

            if (existsInItemDb(target)) {
                map.put(alias, target);
            }
        }
    }

    public void setAlias(String alias, final String target) {
        alias = alias.toLowerCase();
        if (map.containsKey(alias) && map.get(alias).equalsIgnoreCase(target)) {
            return;
        }

        map.put(alias, target);
        save();
    }

    public void removeAlias(final String alias) {
        if (map.remove(alias.toLowerCase()) != null) {
            save();
        }
    }

    private void save() {
        config.setRaw("aliases", map);
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
