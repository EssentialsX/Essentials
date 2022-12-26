package com.earth2me.essentials;

import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SpawnMob {

    private static final Material GOLDEN_HELMET = EnumUtil.getMaterial("GOLDEN_HELMET", "GOLD_HELMET");
    private static final Material GOLDEN_CHESTPLATE = EnumUtil.getMaterial("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE");
    private static final Material GOLDEN_LEGGINGS = EnumUtil.getMaterial("GOLDEN_LEGGINGS", "GOLD_LEGGINGS");
    private static final Material GOLDEN_BOOTS = EnumUtil.getMaterial("GOLDEN_BOOTS", "GOLD_BOOTS");
    private static final Material GOLDEN_SWORD = EnumUtil.getMaterial("GOLDEN_SWORD", "GOLD_SWORD");

    private SpawnMob() {
    }

    public static String mobList(final User user) {
        final Set<String> mobList = Mob.getMobList();
        final Set<String> availableList = new HashSet<>();
        for (final String mob : mobList) {
            if (user.isAuthorized("essentials.spawnmob." + mob.toLowerCase(Locale.ENGLISH))) {
                availableList.add(mob);
            }
        }
        if (availableList.isEmpty()) {
            availableList.add(user.playerTl("none"));
        }
        return StringUtil.joinList(availableList);
    }

    public static List<String> mobParts(final String mobString) {
        final String[] mobParts = mobString.split(",");

        final List<String> mobs = new ArrayList<>();

        for (final String mobPart : mobParts) {
            final String[] mobDatas = mobPart.split(":");
            mobs.add(mobDatas[0]);
        }
        return mobs;
    }

    public static List<String> mobData(final String mobString) {
        final String[] mobParts = mobString.split(",");

        final List<String> mobData = new ArrayList<>();

        for (final String mobPart : mobParts) {
            final String[] mobDatas = mobPart.split(":");
            if (mobDatas.length == 1) {
                if (mobPart.contains(":")) {
                    mobData.add("");
                } else {
                    mobData.add(null);
                }
            } else {
                mobData.add(mobDatas[1]);
            }
        }

        return mobData;
    }

    // This method spawns a mob where the user is looking, owned by user
    public static void spawnmob(final IEssentials ess, final Server server, final User user, final List<String> parts, final List<String> data, final int mobCount) throws Exception {
        final Block block = LocationUtil.getTarget(user.getBase()).getBlock();
        if (block == null) {
            throw new TranslatableException("unableToSpawnMob");
        }
        spawnmob(ess, server, user.getSource(), user, block.getLocation(), parts, data, mobCount);
    }

    // This method spawns a mob at target, owned by target
    public static void spawnmob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final List<String> parts, final List<String> data, final int mobCount) throws Exception {
        spawnmob(ess, server, sender, target, target.getLocation(), parts, data, mobCount);
    }

    // This method spawns a mob at loc, owned by target
    public static void spawnmob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final Location loc, final List<String> parts, final List<String> data, int mobCount) throws Exception {
        final Location sloc = LocationUtil.getSafeDestination(ess, loc);

        for (final String part : parts) {
            final Mob mob = Mob.fromName(part);
            checkSpawnable(ess, sender, mob);
        }

        final int serverLimit = ess.getSettings().getSpawnMobLimit();
        int effectiveLimit = serverLimit / parts.size();

        if (effectiveLimit < 1) {
            effectiveLimit = 1;
            while (parts.size() > serverLimit) {
                parts.remove(serverLimit);
            }
        }

        if (mobCount > effectiveLimit) {
            mobCount = effectiveLimit;
            sender.sendTl("mobSpawnLimit");
        }

        final Mob mob = Mob.fromName(parts.get(0)); // Get the first mob
        try {
            for (int i = 0; i < mobCount; i++) {
                spawnMob(ess, server, sender, target, sloc, parts, data);
            }
            sender.sendMessage(mobCount * parts.size() + " " + mob.name.toLowerCase(Locale.ENGLISH) + mob.suffix + " " + sender.tl("spawned"));
        } catch (final MobException e1) {
            throw new TranslatableException(e1, "unableToSpawnMob");
        } catch (final NumberFormatException e2) {
            throw new TranslatableException(e2, "numberRequired");
        } catch (final NullPointerException np) {
            throw new TranslatableException(np, "soloMob");
        }
    }

    private static void spawnMob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final Location sloc, final List<String> parts, final List<String> data) throws Exception {
        Mob mob;
        Entity spawnedMob = null;
        Entity spawnedMount;

        for (int i = 0; i < parts.size(); i++) {
            if (i == 0) {
                mob = Mob.fromName(parts.get(i));
                spawnedMob = mob.spawn(sloc.getWorld(), server, sloc);
                defaultMobData(mob.getType(), spawnedMob);

                if (data.get(i) != null) {
                    changeMobData(sender, mob.getType(), spawnedMob, data.get(i).toLowerCase(Locale.ENGLISH), target);
                }
            }

            final int next = i + 1;
            // If it's the last mob in the list, don't set the mount
            if (next < parts.size()) {
                final Mob mMob = Mob.fromName(parts.get(next));
                spawnedMount = mMob.spawn(sloc.getWorld(), server, sloc);
                defaultMobData(mMob.getType(), spawnedMount);

                if (data.get(next) != null) {
                    changeMobData(sender, mMob.getType(), spawnedMount, data.get(next).toLowerCase(Locale.ENGLISH), target);
                }

                spawnedMob.setPassenger(spawnedMount);

                spawnedMob = spawnedMount;
            }
        }
    }

    private static void checkSpawnable(final IEssentials ess, final CommandSource sender, final Mob mob) throws Exception {
        if (mob == null || mob.getType() == null) {
            throw new TranslatableException("invalidMob");
        }

        if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH))) {
            throw new TranslatableException("disabledToSpawnMob");
        }

        if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.spawnmob." + mob.name.toLowerCase(Locale.ENGLISH))) {
            throw new TranslatableException("noPermToSpawnMob");
        }
    }

    private static void changeMobData(final CommandSource sender, final EntityType type, final Entity spawned, final String inputData, final User target) throws Exception {
        String data = inputData;

        if (data.isEmpty()) {
            sender.sendTl("mobDataList", StringUtil.joinList(MobData.getValidHelp(spawned)));
        }

        if (spawned instanceof Zombie) {
            ((Zombie) spawned).setBaby(false);
        } else if (spawned instanceof Ageable) {
            ((Ageable) spawned).setAdult();
        }

        if (spawned instanceof Zombie || type == EntityType.SKELETON) {
            if (inputData.contains("armor") || inputData.contains("armour")) {
                final EntityEquipment invent = ((LivingEntity) spawned).getEquipment();
                if (inputData.contains("noarmor") || inputData.contains("noarmour")) {
                    invent.clear();
                } else if (inputData.contains("netherite") && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
                    invent.setBoots(new ItemStack(Material.NETHERITE_BOOTS, 1));
                    invent.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS, 1));
                    invent.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE, 1));
                    invent.setHelmet(new ItemStack(Material.NETHERITE_HELMET, 1));
                } else if (inputData.contains("diamond")) {
                    invent.setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
                    invent.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
                    invent.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
                    invent.setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
                } else if (inputData.contains("gold")) {
                    invent.setBoots(new ItemStack(GOLDEN_BOOTS, 1));
                    invent.setLeggings(new ItemStack(GOLDEN_LEGGINGS, 1));
                    invent.setChestplate(new ItemStack(GOLDEN_CHESTPLATE, 1));
                    invent.setHelmet(new ItemStack(GOLDEN_HELMET, 1));
                } else if (inputData.contains("leather")) {
                    invent.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
                    invent.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
                    invent.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                    invent.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
                } else if (inputData.contains("iron")) {
                    invent.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
                    invent.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
                    invent.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
                    invent.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
                }
                invent.setBootsDropChance(0f);
                invent.setLeggingsDropChance(0f);
                invent.setChestplateDropChance(0f);
                invent.setHelmetDropChance(0f);
            }

        }

        MobData newData = MobData.fromData(spawned, data);
        while (newData != null) {
            newData.setData(spawned, target.getBase(), data);
            data = data.replace(newData.getMatched(), "");
            newData = MobData.fromData(spawned, data);
        }
    }

    private static void defaultMobData(final EntityType type, final Entity spawned) {
        if (type == EntityType.SKELETON) {
            final EntityEquipment invent = ((LivingEntity) spawned).getEquipment();
            Inventories.setItemInMainHand(invent, new ItemStack(Material.BOW, 1));
            Inventories.setItemInMainHandDropChance(invent, 0.1f);
        }

        if (type == MobCompat.ZOMBIFIED_PIGLIN) {
            final PigZombie zombie = (PigZombie) spawned;
            setVillager(zombie, false);

            final EntityEquipment invent = zombie.getEquipment();
            Inventories.setItemInMainHand(invent, new ItemStack(GOLDEN_SWORD, 1));
            Inventories.setItemInMainHandDropChance(invent, 0.1f);
        }

        if (type == EntityType.ZOMBIE) {
            final Zombie zombie = (Zombie) spawned;
            setVillager(zombie, false);
        }

        if (type == EntityType.HORSE) {
            ((Horse) spawned).setJumpStrength(1.2);
        }
    }

    @SuppressWarnings("deprecation")
    private static void setVillager(final Zombie zombie, final boolean villager) {
        try {
            zombie.setVillager(villager);
        } catch (final Exception ignored) {
        }
    }
}
