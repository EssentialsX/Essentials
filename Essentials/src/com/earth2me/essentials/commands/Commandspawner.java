package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class Commandspawner extends EssentialsCommand {
    public Commandspawner() {
        super("spawner");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1 || args[0].length() < 2) {
            throw new NotEnoughArgumentsException(tl("mobsAvailable", StringUtil.joinList(Mob.getMobList())));
        }

        final Location target = LocationUtil.getTarget(user.getBase());
        Material MOB_SPAWNER = EnumUtil.getMaterial("SPAWNER", "MOB_SPAWNER");

        if (target == null || target.getBlock().getType() != MOB_SPAWNER) {
            throw new Exception(tl("mobSpawnTarget"));
        }

        String name = args[0];
        int delay = 0;

        Mob mob = null;
        mob = Mob.fromName(name);
        if (mob == null) {
            throw new Exception(tl("invalidMob"));
        }
        if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("disabledToSpawnMob"));
        }
        if (!user.isAuthorized("essentials.spawner." + mob.name.toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("noPermToSpawnMob"));
        }
        if (args.length > 1) {
            if (NumberUtil.isInt(args[1])) {
                delay = Integer.parseInt(args[1]);
            }
        }
        final Trade charge = new Trade("spawner-" + mob.name.toLowerCase(Locale.ENGLISH), ess);
        charge.isAffordableFor(user);
        try {
            Block block = target.getBlock();
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawner.setSpawnedType(mob.getType());
            if (delay > 0) {
                if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_12_2_R01)) {
                    spawner.setMinSpawnDelay(0);
                    spawner.setMaxSpawnDelay(Integer.MAX_VALUE);
                    spawner.setMinSpawnDelay(delay);
                    spawner.setMaxSpawnDelay(delay);
                } else {
                    Class<?> craftWorld = ReflUtil.getOBCClass("CraftWorld");
                    Class<?> tileEntityMobSpawner = ReflUtil.getNMSClass("TileEntityMobSpawner");
                    Class<?> mobSpawnerAbstract = ReflUtil.getNMSClass("MobSpawnerAbstract");
                    Method getSpawner = tileEntityMobSpawner.getDeclaredMethod("getSpawner");
                    Method getTileEntityAt = craftWorld.getDeclaredMethod("getTileEntityAt", int.class, int.class, int.class);
                    Object craftTileEntity = getTileEntityAt.invoke(block.getWorld(), block.getX(), block.getY(), block.getZ());
                    Object nmsSpawner = getSpawner.invoke(craftTileEntity);
                    Field minSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "minSpawnDelay");
                    Field maxSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "maxSpawnDelay");
                    if (minSpawnDelay != null && maxSpawnDelay != null) {
                        minSpawnDelay.setInt(nmsSpawner, delay);
                        maxSpawnDelay.setInt(nmsSpawner, delay);
                    }
                }
            }
            spawner.setDelay(delay);
            spawner.update();
        } catch (Throwable ex) {
            throw new Exception(tl("mobSpawnError"), ex);
        }
        charge.charge(user);
        user.sendMessage(tl("setSpawner", mob.name));

    }
}
