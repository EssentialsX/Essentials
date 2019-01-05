package net.ess3.nms.refl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflUtil {
    public static final NMSVersion V1_8_R1 = NMSVersion.fromString("v1_8_R1");
    public static final NMSVersion V1_8_R2 = NMSVersion.fromString("v1_8_R2");
    public static final NMSVersion V1_8_R3 = NMSVersion.fromString("v1_8_R3");
    public static final NMSVersion V1_9_R1 = NMSVersion.fromString("v1_9_R1");
    public static final NMSVersion V1_10_R1 = NMSVersion.fromString("v1_10_R1");
    public static final NMSVersion V1_11_R1 = NMSVersion.fromString("v1_11_R1");
    public static final NMSVersion V1_12_R1 = NMSVersion.fromString("v1_12_R1");
    public static final NMSVersion V1_13_R1 = NMSVersion.fromString("v1_13_R1");
    public static final NMSVersion V1_13_R2 = NMSVersion.fromString("v1_13_R2");
    private static NMSVersion nmsVersionObject;
    private static String nmsVersion;

    public static String getNMSVersion() {
        if (nmsVersion == null) {
            String name = Bukkit.getServer().getClass().getName();
            String[] parts = name.split("\\.");
            nmsVersion = parts[3];
        }
        return nmsVersion;
    }
    
    public static NMSVersion getNmsVersionObject() {
        if (nmsVersionObject == null) {
            nmsVersionObject = NMSVersion.fromString(getNMSVersion());
        }
        return nmsVersionObject;
    }

    public static Class<?> getNMSClass(String className) {
        return getClassCached("net.minecraft.server." + getNMSVersion() + "." + className);
    }

    public static Class<?> getOBCClass(String className) {
        return getClassCached("org.bukkit.craftbukkit." + getNMSVersion() + "." + className);
    }

    private static Map<String, Class<?>> classCache = new HashMap<>();

    public static Class<?> getClassCached(String className) {
        if (classCache.containsKey(className)) {
            return classCache.get(className);
        }
        try {
            Class<?> classForName = Class.forName(className);
            classCache.put(className, classForName);
            return classForName;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Table<Class<?>, String, Method> methodCache = HashBasedTable.create();

    public static Method getMethodCached(Class<?> clazz, String methodName) {
        if (methodCache.contains(clazz, methodName)) {
            return methodCache.get(clazz, methodName);
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            methodCache.put(clazz, methodName, method);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Table<Class<?>, MethodParams, Method> methodParamCache = HashBasedTable.create();

    public static Method getMethodCached(Class<?> clazz, String methodName, Class<?>... params) {
        MethodParams methodParams = new MethodParams(methodName, params);
        if (methodParamCache.contains(clazz, methodParams)) {
            return methodParamCache.get(clazz, methodParams);
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            methodParamCache.put(clazz, methodParams, method);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Table<Class<?>, String, Field> fieldCache = HashBasedTable.create();

    public static Field getFieldCached(Class<?> clazz, String fieldName) {
        if (fieldCache.contains(clazz, fieldName)) {
            return fieldCache.get(clazz, fieldName);
        }
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldCache.put(clazz, fieldName, field);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Map<Class<?>, Constructor<?>> constructorCache = new HashMap<>();

    public static Constructor<?> getConstructorCached(Class<?> clazz) {
        if (constructorCache.containsKey(clazz)) {
            return constructorCache.get(clazz);
        }
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructorCache.put(clazz, constructor);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Table<Class<?>, ConstructorParams, Constructor<?>> constructorParamCache = HashBasedTable.create();

    public static Constructor<?> getConstructorCached(Class<?> clazz, Class<?>... params) {
        ConstructorParams constructorParams = new ConstructorParams(params);
        if (constructorParamCache.contains(clazz, constructorParams)) {
            return constructorParamCache.get(clazz, constructorParams);
        }
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(params);
            constructor.setAccessible(true);
            constructorParamCache.put(clazz, constructorParams, constructor);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    // Adapted from @minecrafter
    private static class MethodParams {
        private final String name;
        private final Class<?>[] params;

        MethodParams(final String name, final Class<?>[] params) {
            this.name = name;
            this.params = params;
        }

        // Ugly autogenned Lombok code
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MethodParams)) {
                return false;
            }
            final MethodParams that = (MethodParams) o;
            if (!that.canEqual(this)) {
                return false;
            }
            final Object thisName = this.name;
            final Object thatName = that.name;
            if (thisName == null) {
                if (thatName == null) {
                    return Arrays.deepEquals(this.params, that.params);
                }
            } else if (thisName.equals(thatName)) {
                return Arrays.deepEquals(this.params, that.params);
            }
            return false;
        }

        boolean canEqual(final Object that) {
            return that instanceof MethodParams;
        }

        @Override
        public int hashCode() {
            int result = 1;
            final Object thisName = this.name;
            result = result * 31 + ((thisName == null) ? 0 : thisName.hashCode());
            result = result * 31 + Arrays.deepHashCode(this.params);
            return result;
        }
    }

    // Necessary for deepequals
    private static class ConstructorParams {
        private final Class<?>[] params;

        ConstructorParams(Class<?>[] params) {
            this.params = params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConstructorParams that = (ConstructorParams) o;

            return Arrays.deepEquals(params, that.params);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(params);
        }
    }

    /**
     * https://gist.github.com/SupaHam/dad1db6406596c5f8e4b221ff473831c
     * 
     * @author SupaHam (<a href="https://github.com/SupaHam">https://github.com/SupaHam</a>)
     */
    public static class NMSVersion implements Comparable<NMSVersion> {
        private static final Pattern VERSION_PATTERN = Pattern.compile("^v(\\d+)_(\\d+)_R(\\d+)");
        private final int major;
        private final int minor;
        private final int release;
        
        public static NMSVersion fromString(String string) {
            Preconditions.checkNotNull(string, "string cannot be null.");
            Matcher matcher = VERSION_PATTERN.matcher(string);
            if (!matcher.matches()) {
                if (!Bukkit.getName().equals("Essentials Fake Server")) {
                    throw new IllegalArgumentException(string + " is not in valid version format. e.g. v1_10_R1");
                }
                matcher = VERSION_PATTERN.matcher(V1_12_R1.toString());
                Preconditions.checkArgument(matcher.matches(), string + " is not in valid version format. e.g. v1_10_R1");
            }
            return new NMSVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        }

        private NMSVersion(int major, int minor, int release) {
            this.major = major;
            this.minor = minor;
            this.release = release;
        }
        
        public boolean isHigherThan(NMSVersion o) {
            return compareTo(o) > 0;
        }
        
        public boolean isHigherThanOrEqualTo(NMSVersion o) {
            return compareTo(o) >= 0;
        }
        
        public boolean isLowerThan(NMSVersion o) {
            return compareTo(o) < 0;
        }
        
        public boolean isLowerThanOrEqualTo(NMSVersion o) {
            return compareTo(o) <= 0;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getRelease() {
            return release;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NMSVersion that = (NMSVersion) o;
            return major == that.major &&
                minor == that.minor &&
                release == that.release;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(major, minor, release);
        }

        @Override
        public String toString() {
            return "v" + major + "_" + minor  + "_R" + release;
        }

        @Override
        public int compareTo(NMSVersion o) {
            if (major < o.major) {
                return -1;
            } else if (major > o.major) {
                return 1;
            } else { // equal major
                if (minor < o.minor) {
                    return -1;
                } else if (minor > o.minor) {
                    return 1;
                } else {
                    return Integer.compare(release, o.release);
                }
            }
        }
    }
}
