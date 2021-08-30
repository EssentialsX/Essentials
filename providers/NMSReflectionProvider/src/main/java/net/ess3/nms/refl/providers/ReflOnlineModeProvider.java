package net.ess3.nms.refl.providers;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ReflOnlineModeProvider {
    private final MethodHandle spigotBungeeGetter;
    private final MethodHandle paperBungeeGetter;

    public ReflOnlineModeProvider() {
        MethodHandle spigotBungeeGetter = null;
        MethodHandle paperBungeeGetter = null;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            spigotBungeeGetter = lookup.findStaticGetter(Class.forName("org.spigotmc.SpigotConfig"), "bungee", boolean.class);
            paperBungeeGetter = lookup.findStaticGetter(Class.forName("com.destroystokyo.paper.PaperConfig"), "bungeeOnlineMode", boolean.class);
        } catch (Throwable ignored) {
        }
        this.spigotBungeeGetter = spigotBungeeGetter;
        this.paperBungeeGetter = paperBungeeGetter;
    }

    public String getOnlineModeString() {
        if (spigotBungeeGetter == null) {
            return Bukkit.getOnlineMode() ? "Online Mode" : "Offline Mode";
        }

        try {
            return ((boolean) spigotBungeeGetter.invoke() && (paperBungeeGetter == null || (boolean) paperBungeeGetter.invoke())) ? "Bungee Mode" : "Offline Mode";
        } catch (Throwable ignored) {
            return "Unknown";
        }
    }
}
