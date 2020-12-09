package me.ihdeveloper.api.sign_editor.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {
    /** Used for caching the NMS version */
    private static String nmsVersion = null;

    public static String getNMSVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (nmsVersion != null) {
            return nmsVersion;
        }
        Object server = Bukkit.getServer();
        Method server$getNMSServer = server.getClass().getMethod("getServer");

        Object nmsServer = server$getNMSServer.invoke(server);
        String[] names = nmsServer.getClass().getName().split("\\.");

        return nmsVersion = names[3];
    }

    public static Class<?> getNMSClass(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
    }

    public static Class<?> getCraftClass(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
    }
}
