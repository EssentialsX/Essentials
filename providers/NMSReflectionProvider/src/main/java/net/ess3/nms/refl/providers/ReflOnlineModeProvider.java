package net.ess3.nms.refl.providers;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ReflOnlineModeProvider {
    private final MethodHandle spigotBungeeGetter;
    private final MethodHandle paperBungeeGetter;
    private final boolean fancyPaperCheck;

    public ReflOnlineModeProvider() {
        MethodHandle spigotBungeeGetter = null;
        MethodHandle paperBungeeGetter = null;
        boolean fancyCheck = false;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            spigotBungeeGetter = lookup.findStaticGetter(Class.forName("org.spigotmc.SpigotConfig"), "bungee", boolean.class);
            final Class<?> paperConfig = Class.forName("com.destroystokyo.paper.PaperConfig");
            paperBungeeGetter = lookup.findStaticGetter(paperConfig, "bungeeOnlineMode", boolean.class);
            paperBungeeGetter = lookup.findStatic(paperConfig, "isProxyOnlineMode", MethodType.methodType(boolean.class));
            fancyCheck = true;
        } catch (Throwable ignored) {
        }
        this.spigotBungeeGetter = spigotBungeeGetter;
        this.paperBungeeGetter = paperBungeeGetter;
        this.fancyPaperCheck = fancyCheck;
    }

    public String getOnlineModeString() {
        if (spigotBungeeGetter == null) {
            return Bukkit.getOnlineMode() ? "Online Mode" : "Offline Mode";
        }

        try {
            if (Bukkit.getOnlineMode()) {
                return "Online Mode";
            }

            if (fancyPaperCheck) {
                if ((boolean) paperBungeeGetter.invoke()) {
                    // Could be Velocity or Bungee, so do not specify.
                    return "Proxy Mode";
                }
                return "Offline Mode";
            }

            if ((boolean) spigotBungeeGetter.invoke() && (paperBungeeGetter == null || (boolean) paperBungeeGetter.invoke())) {
                return "Bungee Mode";
            }

            return "Offline Mode";
        } catch (Throwable ignored) {
            return "Unknown";
        }
    }
}
