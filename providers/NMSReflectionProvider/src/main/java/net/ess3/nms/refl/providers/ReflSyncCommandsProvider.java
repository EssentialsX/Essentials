package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.SyncCommandsProvider;
import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ReflSyncCommandsProvider implements SyncCommandsProvider {
    private final MethodHandle nmsSyncCommands;

    public ReflSyncCommandsProvider() {
        MethodHandle syncCommands = null;
        final Class<?> nmsClass = ReflUtil.getOBCClass("CraftServer");
        try {
            syncCommands = MethodHandles.lookup().findVirtual(nmsClass, "syncCommands", MethodType.methodType(void.class));
        } catch (final Exception ignored) {
            // This will fail below 1.13, this is okay, we will fail silently!
        }
        nmsSyncCommands = syncCommands;
    }

    @Override
    public String getDescription() {
        return "NMS Reflection Sync Commands Provider";
    }

    @Override
    public void syncCommands() {
        if (nmsSyncCommands != null) {
            try {
                nmsSyncCommands.invoke(Bukkit.getServer());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
