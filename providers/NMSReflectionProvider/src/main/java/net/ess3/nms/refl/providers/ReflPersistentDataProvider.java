package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.PersistentDataProvider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Stores persistent data on 1.18-1.13 in a manner that's consistent with PDC on 1.14+ to enable 
 * seamless upgrades.
 */
public class ReflPersistentDataProvider implements PersistentDataProvider {
    private static final String PDC_ROOT_TAG = "PublicBukkitValues";
    private static final String ROOT_TAG = "tag";
    private final String namespace;
    private final MethodHandle itemHandleGetterHandle;
    private final MethodHandle getTagHandle;
    private final MethodHandle tagSetterHandle;
    private final MethodHandle newCompoundHandle;
    private final MethodHandle getCompoundHandle;
    private final MethodHandle setCompoundHandle;
    private final MethodHandle setStringHandle;
    private final MethodHandle removeHandle;
    private final MethodHandle getStringHandle;

    public ReflPersistentDataProvider(Plugin plugin) {
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);

        MethodHandle itemHandleGetterHandle = null;
        MethodHandle getTagHandle = null;
        MethodHandle tagSetterHandle = null;
        MethodHandle newCompoundHandle = null;
        MethodHandle getCompoundHandle = null;
        MethodHandle setCompoundHandle = null;
        MethodHandle setStringHandle = null;
        MethodHandle removeHandle = null;
        MethodHandle getStringHandle = null;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Field handleGetter = ReflUtil.getOBCClass("inventory.CraftItemStack").getDeclaredField("handle");
            handleGetter.setAccessible(true);
            itemHandleGetterHandle = lookup.unreflectGetter(handleGetter);
            final Field tagSetter = ReflUtil.getNMSClass("ItemStack").getDeclaredField("tag");
            tagSetter.setAccessible(true);
            tagSetterHandle = lookup.unreflectSetter(tagSetter);
            getTagHandle = lookup.findVirtual(ReflUtil.getNMSClass("ItemStack"), "getTag", MethodType.methodType(ReflUtil.getNMSClass("NBTTagCompound")));
            newCompoundHandle = lookup.findConstructor(ReflUtil.getNMSClass("NBTTagCompound"), MethodType.methodType(void.class));
            getCompoundHandle = lookup.findVirtual(ReflUtil.getNMSClass("NBTTagCompound"), "getCompound", MethodType.methodType(ReflUtil.getNMSClass("NBTTagCompound"), String.class));
            setCompoundHandle = lookup.findVirtual(ReflUtil.getNMSClass("NBTTagCompound"), "set", MethodType.methodType(void.class, String.class, ReflUtil.getNMSClass("NBTBase")));
            setStringHandle = lookup.findVirtual(ReflUtil.getNMSClass("NBTTagCompound"), "setString", MethodType.methodType(void.class, String.class, String.class));
            removeHandle = lookup.findVirtual(ReflUtil.getNMSClass("NBTTagCompound"), "remove", MethodType.methodType(void.class, String.class));
            getStringHandle = lookup.findVirtual(ReflUtil.getNMSClass("NBTTagCompound"), "getString", MethodType.methodType(String.class, String.class));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.itemHandleGetterHandle = itemHandleGetterHandle;
        this.getTagHandle = getTagHandle;
        this.tagSetterHandle = tagSetterHandle;
        this.newCompoundHandle = newCompoundHandle;
        this.getCompoundHandle = getCompoundHandle;
        this.setCompoundHandle = setCompoundHandle;
        this.setStringHandle = setStringHandle;
        this.removeHandle = removeHandle;
        this.getStringHandle = getStringHandle;
    }

    private String getPersistentString(ItemStack itemStack, String key) throws Throwable {
        final Object nmsItem = itemHandleGetterHandle.invoke(itemStack);
        final Object itemRootTag = getTagHandle.invoke(nmsItem);
        if (itemRootTag == null) {
            return null;
        }
        final Object tagCompound = getCompoundHandle.invoke(itemRootTag, ROOT_TAG);
        final Object publicBukkitValuesCompound = getCompoundHandle.invoke(tagCompound, PDC_ROOT_TAG);
        return (String) getStringHandle.invoke(publicBukkitValuesCompound, key);
    }

    private void setPersistentString(ItemStack itemStack, String key, String value) throws Throwable {
        final Object nmsItem = itemHandleGetterHandle.invoke(itemStack);
        Object itemRootTag = getTagHandle.invoke(nmsItem);
        if (itemRootTag == null) {
            itemRootTag = newCompoundHandle.invoke();
            tagSetterHandle.invoke(nmsItem, itemRootTag);
        }
        final Object tagCompound = getCompoundHandle.invoke(itemRootTag, ROOT_TAG);
        final Object publicBukkitValuesCompound = getCompoundHandle.invoke(tagCompound, PDC_ROOT_TAG);
        if (value == null) {
            removeHandle.invoke(publicBukkitValuesCompound, key);
        } else {
            setStringHandle.invoke(publicBukkitValuesCompound, key, value);
        }
        setCompoundHandle.invoke(tagCompound, PDC_ROOT_TAG, publicBukkitValuesCompound);
        setCompoundHandle.invoke(itemRootTag, ROOT_TAG, tagCompound);
    }

    @Override
    public void set(ItemStack itemStack, String key, String value) {
        try {
            setPersistentString(itemStack, namespace + ":" + key, value);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public String getString(ItemStack itemStack, String key) {
        try {
            return getPersistentString(itemStack, namespace + ":" + key);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void remove(ItemStack itemStack, String key) {
        try {
            setPersistentString(itemStack, namespace + ":" + key, null);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public String getDescription() {
        return "1.13 >= Persistent Data Container Provider";
    }
}
