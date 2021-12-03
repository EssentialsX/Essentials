package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.AbstractAchievementEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class AdvancementListenerProvider implements Listener {
    private final Object language;
    private final MethodHandle languageGetOrDefault;

    public AdvancementListenerProvider() throws Throwable {
        final Class<?> languageClass;
        if (ReflUtil.isMojMap()) {
            languageClass = ReflUtil.getClassCached("net.minecraft.locale.Language");
        } else if (ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_17_R1)) {
            languageClass = ReflUtil.getClassCached("net.minecraft.locale.LocaleLanguage");
        } else {
            languageClass = ReflUtil.getNMSClass("LocaleLanguage");
        }
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        //noinspection ConstantConditions
        language = lookup.findStatic(languageClass, ReflUtil.isMojMap() ? "getInstance" : "a", MethodType.methodType(languageClass)).invoke();
        languageGetOrDefault = lookup.findVirtual(languageClass, ReflUtil.isMojMap() ? "getOrDefault" : "a", MethodType.methodType(String.class, String.class));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(final PlayerAdvancementDoneEvent event) {
        try {
            final String key = "advancements." + event.getAdvancement().getKey().getKey().replace('/', '.') + ".title";
            final String translation = (String) languageGetOrDefault.invoke(language, key);
            if (!key.equals(translation)) {
                Bukkit.getPluginManager().callEvent(new AbstractAchievementEvent(event.getPlayer(), translation));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
