package net.ess3.nms.flattened;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.ess3.nms.ItemDbProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.stream.Collectors;

public class FlatItemDbProvider extends ItemDbProvider {
    private static Gson gson = new Gson();


    @Override
    public Material resolve(String name) {
        return null;
    }

    @Override
    public boolean supportsLegacyIds() {
        return false;
    }

    @Override
    public int getLegacyId(Material material) {
        return -1;
    }

    @Override
    public Material getFromLegacyId(int id) {
        return null;
    }

    @Override
    public String getPrimaryName(ItemStack item) {
        return null;
    }

    @Override
    public List<String> getNames(ItemStack item) {
        return null;
    }

    @Override
    public void rebuild(List<String> lines) {
        String json = lines.stream().collect(Collectors.joining("\n"));
        JsonArray jsonArray = (new JsonParser()).parse(json).getAsJsonArray();
        jsonArray.forEach(element -> {
            if (element.isJsonObject()) {
            }
        });
    }

    @Override
    public boolean tryProvider() {
        // Build the database initially so that we can actually test the provider
        this.rebuild(this.loadResource("/items.json"));
        return super.tryProvider();
    }

    @Override
    public String getHumanName() {
        return "Post-1.13 item database provider";
    }

    private class MaterialData {
        private Material material;
        private PotionType potionEnum;
        private String potionModifier;
    }
}
