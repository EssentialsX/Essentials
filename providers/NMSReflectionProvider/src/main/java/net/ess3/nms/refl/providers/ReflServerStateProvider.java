package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.ServerStateProvider;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ReflServerStateProvider implements ServerStateProvider {
    private final Object nmsServer;
    private final MethodHandle nmsIsRunning;

    public ReflServerStateProvider() {
        Object serverObject = null;
        MethodHandle isRunning = null;
        final Class<?> nmsClass = ReflUtil.getNMSClass("MinecraftServer");
        try {
            serverObject = nmsClass.getMethod("getServer").invoke(null);
            isRunning = MethodHandles.lookup().findVirtual(nmsClass,
                    ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_18_R1) ? "v" : "isRunning", //TODO jmp said he may make this better
                    MethodType.methodType(boolean.class));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        nmsServer = serverObject;
        nmsIsRunning = isRunning;
    }

    @Override
    public boolean isStopping() {
        if (nmsServer != null && nmsIsRunning != null) {
            try {
                return !(boolean) nmsIsRunning.invoke(nmsServer);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "NMS Reflection Server State Provider";
    }
}
