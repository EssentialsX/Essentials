package com.earth2me.essentials;

import com.earth2me.essentials.config.EssentialsConfiguration;
import net.ess3.api.IEssentials;
import net.ess3.api.IItemGroups;
import org.bukkit.Material;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemGroups implements IItemGroups {

    private final EssentialsConfiguration config;
    private final Map<String, List<Material>> itemGroups = new HashMap<>();

    public ItemGroups(final IEssentials ess) {
        this.config = new EssentialsConfiguration(new File(ess.getDataFolder(), "groups.yml"));
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        synchronized (itemGroups) {
            config.load();
            itemGroups.clear();

            for (String id : config.getKeys()) {
                final List<String> list = config.getList(id, String.class);
                itemGroups.put(id, list.stream().map(Material::matchMaterial).collect(Collectors.toList()));
            }
        }
    }

    @Override
    public List<Material> getItemGroup(String group) {
        return itemGroups.getOrDefault(group, Collections.emptyList());
    }

    @Override
    public Set<String> getItemGroups() {
        return itemGroups.keySet();
    }
}
