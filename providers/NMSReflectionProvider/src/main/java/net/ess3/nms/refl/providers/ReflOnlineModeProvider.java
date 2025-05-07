package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.OnlineModeProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@ProviderData(description = "Reflection Online Mode Provider")
public class ReflOnlineModeProvider implements OnlineModeProvider {
    private final MethodHandle spigotBungeeGetter;
    private final MethodHandle paperBungeeGetter;
    private final Object paperProxiesInstance;
    private final boolean fancyPaperCheck;

    public ReflOnlineModeProvider() {
        MethodHandle spigotBungeeGetter = null;
        MethodHandle paperBungeeGetter = null;
        Object paperProxiesInstance = null;
        boolean fancyCheck = false;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            spigotBungeeGetter = lookup.findStaticGetter(Class.forName("org.spigotmc.SpigotConfig"), "bungee", boolean.class);
            final Class<?> newPaperConfigClass = ReflUtil.getClassCached("io.papermc.paper.configuration.GlobalConfiguration");
            if (newPaperConfigClass != null) {
                final Class<?> proxiesClass = Class.forName("io.papermc.paper.configuration.GlobalConfiguration$Proxies");
                final Object globalConfig = lookup.findStatic(newPaperConfigClass, "get", MethodType.methodType(newPaperConfigClass)).invoke();
                paperProxiesInstance = lookup.findGetter(newPaperConfigClass, "proxies", proxiesClass).invoke(globalConfig);
                paperBungeeGetter = lookup.findVirtual(proxiesClass, "isProxyOnlineMode", MethodType.methodType(boolean.class));
                fancyCheck = true;
            } else {
                final Class<?> paperConfig = Class.forName("com.destroystokyo.paper.PaperConfig");
                paperBungeeGetter = lookup.findStaticGetter(paperConfig, "bungeeOnlineMode", boolean.class);
                paperBungeeGetter = lookup.findStatic(paperConfig, "isProxyOnlineMode", MethodType.methodType(boolean.class));
                fancyCheck = true;
            }
        } catch (Throwable ignored) {
        }
        this.spigotBungeeGetter = spigotBungeeGetter;
        this.paperBungeeGetter = paperBungeeGetter;
        this.paperProxiesInstance = paperProxiesInstance;
        this.fancyPaperCheck = fancyCheck;
    }

    @Override
    public String getOnlineModeString() {
        if (spigotBungeeGetter == null) {
            return Bukkit.getOnlineMode() ? "Online Mode" : "Offline Mode";
        }

        try {
            if (Bukkit.getOnlineMode()) {
                return "Online Mode";
            }

            if (fancyPaperCheck) {
                if ((boolean) (paperProxiesInstance != null ? paperBungeeGetter.invoke(paperProxiesInstance) : paperBungeeGetter.invoke())) {
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
