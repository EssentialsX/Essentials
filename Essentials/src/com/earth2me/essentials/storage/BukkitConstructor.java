package com.earth2me.essentials.storage;

import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BukkitConstructor extends CustomClassLoaderConstructor {
    public BukkitConstructor(final Class<?> clazz, final Plugin plugin) {
        super(clazz, plugin.getClass().getClassLoader());
        yamlClassConstructors.put(NodeId.scalar, new ConstructBukkitScalar());
        yamlClassConstructors.put(NodeId.mapping, new ConstructBukkitMapping());

        PropertyUtils propertyUtils = getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        setPropertyUtils(propertyUtils);
    }

    private Method constructScalarMethod = null;

    protected String constructScalarRefl(ScalarNode scalarNode) {
        try {
            if (constructScalarMethod == null) {
                constructScalarMethod = BaseConstructor.class.getDeclaredMethod("constructScalar", ScalarNode.class);
            }
            return (String) constructScalarMethod.invoke(this, scalarNode);
        } catch (NoSuchMethodException
                | SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }


    private class ConstructBukkitScalar extends ConstructScalar {

        @Override
        public Object construct(final Node node) {
            if (node.getType().equals(Material.class)) {
                final String val = constructScalarRefl((ScalarNode) node);
                return Material.matchMaterial(val);
            }

            if (node.getType().equals(MaterialData.class)) {
                final String val = constructScalarRefl((ScalarNode) node);
                if (val.isEmpty()) {
                    return null;
                }
                final String[] split = val.split("[:+',;.]", 2);
                if (split.length == 0) {
                    return null;
                }

                Material mat = Material.matchMaterial(split[0]);

                if (mat == null) {
                    return null;
                }
                byte data = 0;
                if (split.length == 2 && NumberUtil.isInt(split[1])) {
                    data = Byte.parseByte(split[1]);
                }
                return new MaterialData(mat, data);
            }
            if (node.getType().equals(ItemStack.class)) {
                final String val = constructScalarRefl((ScalarNode) node);
                if (val.isEmpty()) {
                    return null;
                }
                final String[] split1 = val.split("\\W");
                if (split1.length == 0) {
                    return null;
                }
                final String[] split2 = split1[0].split("[:+',;.]", 2);
                if (split2.length == 0) {
                    return null;
                }

                Material mat = Material.matchMaterial(split2[0]);

                if (mat == null) {
                    return null;
                }
                short data = 0;
                if (split2.length == 2 && NumberUtil.isInt(split2[1])) {
                    data = Short.parseShort(split2[1]);
                }
                int size = mat.getMaxStackSize();
                if (split1.length > 1 && NumberUtil.isInt(split1[1])) {
                    size = Integer.parseInt(split1[1]);
                }
                final ItemStack stack = new ItemStack(mat, size, data);
                if (split1.length > 2) {
                    for (int i = 2; i < split1.length; i++) {
                        final String[] split3 = split1[0].split("[:+',;.]", 2);
                        if (split3.length < 1) {
                            continue;
                        }
                        Enchantment enchantment = Enchantments.getByName(split3[0]);
                        if (enchantment == null) {
                            continue;
                        }
                        int level = enchantment.getStartLevel();
                        if (split3.length == 2 && NumberUtil.isInt(split3[1])) {
                            level = Integer.parseInt(split3[1]);
                        }
                        if (level < enchantment.getStartLevel()) {
                            level = enchantment.getStartLevel();
                        }
                        if (level > enchantment.getMaxLevel()) {
                            level = enchantment.getMaxLevel();
                        }
                        stack.addUnsafeEnchantment(enchantment, level);
                    }
                }
                return stack;
            }
            if (node.getType().equals(EnchantmentLevel.class)) {
                final String val = constructScalarRefl((ScalarNode) node);
                if (val.isEmpty()) {
                    return null;
                }
                final String[] split = val.split("[:+',;.]", 2);
                if (split.length == 0) {
                    return null;
                }
                Enchantment enchant = Enchantments.getByName(split[0]);
                if (enchant == null) {
                    return null;
                }
                int level = enchant.getStartLevel();
                if (split.length == 2 && NumberUtil.isInt(split[1])) {
                    level = Integer.parseInt(split[1]);
                }
                if (level < enchant.getStartLevel()) {
                    level = enchant.getStartLevel();
                }
                if (level > enchant.getMaxLevel()) {
                    level = enchant.getMaxLevel();
                }
                return new EnchantmentLevel(enchant, level);
            }
            return super.construct(node);
        }
    }

    private class ConstructBukkitMapping extends ConstructMapping {
        
        @Override
        public Object construct(final Node node) {
            if (node.getType().equals(Location.class)) {
                //TODO: NPE checks
                final MappingNode mnode = (MappingNode) node;
                String worldName = "";
                double x = 0, y = 0, z = 0;
                float yaw = 0, pitch = 0;
                if (mnode.getValue().size() < 4) {
                    return null;
                }
                for (NodeTuple nodeTuple : mnode.getValue()) {
                    final String key = constructScalarRefl((ScalarNode) nodeTuple.getKeyNode());
                    final ScalarNode snode = (ScalarNode) nodeTuple.getValueNode();
                    if (key.equalsIgnoreCase("world")) {
                        worldName = constructScalarRefl(snode);
                    }
                    if (key.equalsIgnoreCase("x")) {
                        x = Double.parseDouble(constructScalarRefl(snode));
                    }
                    if (key.equalsIgnoreCase("y")) {
                        y = Double.parseDouble(constructScalarRefl(snode));
                    }
                    if (key.equalsIgnoreCase("z")) {
                        z = Double.parseDouble(constructScalarRefl(snode));
                    }
                    if (key.equalsIgnoreCase("yaw")) {
                        yaw = Float.parseFloat(constructScalarRefl(snode));
                    }
                    if (key.equalsIgnoreCase("pitch")) {
                        pitch = Float.parseFloat(constructScalarRefl(snode));
                    }
                }
                if (worldName == null || worldName.isEmpty()) {
                    return null;
                }
                final World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    return null;
                }
                return new Location(world, x, y, z, yaw, pitch);
            }
            return super.construct(node);
        }
    }
}
