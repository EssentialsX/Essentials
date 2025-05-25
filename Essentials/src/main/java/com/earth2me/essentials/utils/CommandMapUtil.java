package com.earth2me.essentials.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.ess3.api.IEssentials;
import net.ess3.provider.FormattedCommandAliasProvider;
import org.bukkit.command.Command;
import org.bukkit.command.FormattedCommandAlias;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public final class CommandMapUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private CommandMapUtil() {
        throw new UnsupportedOperationException();
    }

    public static String toJsonPretty(IEssentials ess, Map<String, Command> knownCommandMap) {
        final JsonObject json = toJson(ess, knownCommandMap);
        return GSON.toJson(json);
    }

    public static JsonObject toJson(IEssentials ess, Map<String, Command> knownCommandMap) {
        final JsonObject json = new JsonObject();
        for (Map.Entry<String, Command> entry : knownCommandMap.entrySet()) {
            json.add(entry.getKey(), toJson(ess, entry.getValue()));
        }
        return json;
    }

    public static JsonObject toJson(IEssentials ess, Command value) {
        if (value == null) {
            return null;
        }

        final JsonObject json = new JsonObject();
        json.addProperty("name", value.getName());
        json.addProperty("description", value.getDescription());
        json.addProperty("type", value.getClass().getSimpleName());
        json.addProperty("raw", value.toString());

        if (value.getClass().getSimpleName().equals("VanillaCommandWrapper")) {
            json.addProperty("source", "vanilla");
        } else if (value.getClass().getName().contains("org.spigotmc")) {
            json.addProperty("source", "spigot");
        } else if (value instanceof PluginCommand) {
            final Plugin plugin = ((PluginCommand) value).getPlugin();
            json.addProperty("source", plugin.getName() + " " + plugin.getDescription().getVersion());
        } else if (value instanceof BukkitCommand) {
            json.addProperty("source", "bukkit");
        } else if (value instanceof FormattedCommandAlias) {
            json.addProperty("source", "commands.yml");
            final JsonArray formatStrings = new JsonArray();
            for (final String entry : ess.provider(FormattedCommandAliasProvider.class).getFormatStrings((FormattedCommandAlias) value)) {
                formatStrings.add(new JsonPrimitive(entry));
            }
            json.add("bukkit_aliases", formatStrings);
        }
        return json;
    }

}
