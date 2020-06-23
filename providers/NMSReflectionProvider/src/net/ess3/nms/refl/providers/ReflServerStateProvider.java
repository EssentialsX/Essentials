package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.ServerStateProvider;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.logging.Logger;

public class ReflServerStateProvider implements ServerStateProvider {
    private final Object nmsServer;
    private final MethodHandle nmsIsRunning;

    public ReflServerStateProvider(Logger logger) {
        Object serverObject = null;
        MethodHandle isRunning = null;
        Class<?> nmsClass = ReflUtil.getNMSClass("MinecraftServer");
        try {
            serverObject = nmsClass.getMethod("getServer").invoke(null);
            isRunning = MethodHandles.lookup().findVirtual(nmsClass, "isRunning", MethodType.methodType(boolean.class));
        } catch (Exception e) {
            logger.severe("fFUCKCKKKKKK");
        }
        nmsServer = serverObject;
        nmsIsRunning = isRunning;
    }

    @Override
    public boolean isStopping() {
        if (nmsServer != null && nmsIsRunning != null) {
            try {
                return !(boolean) nmsIsRunning.invoke(nmsServer);
            } catch (Throwable throwable) {
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
