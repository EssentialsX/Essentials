package com.earth2me.essentials.craftbukkit;

import net.ess3.nms.refl.ReflUtil;
import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ServerState {

    private static final MethodHandle isStopping; //Only in Paper
    private static final MethodHandle nmsHasStopped;
    private static final MethodHandle nmsIsRunning;
    private static final Object nmsServer;

    static {
        MethodHandle isStoppingHandle = null;
        MethodHandle nmsHasStoppedHandle = null;
        MethodHandle nmsIsRunningHandle = null;
        Object nmsServerObject = null;
        try {
            isStoppingHandle = MethodHandles.lookup().findStatic(Bukkit.class, "isStopping", MethodType.methodType(boolean.class));
        } catch (Throwable e) {
            try {
                Class<?> nmsClass = ReflUtil.getNMSClass("MinecraftServer");
                if (nmsClass != null) {
                    nmsServerObject = ReflUtil.getMethodCached(nmsClass, "getServer").invoke(null);
                    nmsIsRunningHandle = MethodHandles.lookup().findVirtual(nmsClass, "isRunning", MethodType.methodType(boolean.class));
                    nmsHasStoppedHandle = MethodHandles.lookup().findVirtual(nmsClass, "hasStopped", MethodType.methodType(boolean.class));
                }
            } catch (Throwable ignored) {
            }
        }
        isStopping = isStoppingHandle;
        nmsHasStopped = nmsHasStoppedHandle;
        nmsIsRunning = nmsIsRunningHandle;
        nmsServer = nmsServerObject;
    }

    public static boolean isStopping() {
        boolean stopping = false;
        if (isStopping != null) {
            try {
                stopping = (boolean) isStopping.invoke();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (nmsServer != null) {
            if (nmsHasStopped != null) {
                try {
                    stopping = (boolean) nmsHasStopped.invokeExact(nmsServer);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else if (nmsIsRunning != null) {
                try {
                    stopping = (boolean) nmsIsRunning.invokeExact(nmsServer);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return stopping;
    }
}
