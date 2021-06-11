package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.provider.SpawnerBlockProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.CreatureSpawner;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commandspawner extends EssentialsCommand {

    private static final Material MOB_SPAWNER = EnumUtil.getMaterial("SPAWNER", "MOB_SPAWNER");

    public Commandspawner() {
        super("spawner");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1 || args[0].length() < 2) {
            throw new NotEnoughArgumentsException(tl("mobsAvailable", StringUtil.joinList(Mob.getMobList())));
        }

        final Location target = LocationUtil.getTarget(user.getBase());

        if (target.getBlock().getType() != MOB_SPAWNER) {
            throw new Exception(tl("mobSpawnTarget"));
        }

        final String name = args[0];
        int delay = 0;

        final Mob mob = Mob.fromName(name);
        if (mob == null) {
            throw new Exception(tl("invalidMob"));
        }

        if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("disabledToSpawnMob"));
        }
        if (!user.isAuthorized("essentials.spawner." + mob.name.toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("noPermToSpawnMob"));
        }

        if (args.length > 1 && NumberUtil.isInt(args[1]) && user.isAuthorized("essentials.spawner.delay")) {
            delay = Integer.parseInt(args[1]);
        }
        final Trade charge = new Trade("spawner-" + mob.name.toLowerCase(Locale.ENGLISH), ess);
        charge.isAffordableFor(user);
        try {
            final CreatureSpawner spawner = (CreatureSpawner) target.getBlock().getState();
            spawner.setSpawnedType(mob.getType());
            if (delay > 0) {
                final SpawnerBlockProvider spawnerBlockProvider = ess.getSpawnerBlockProvider();
                spawnerBlockProvider.setMinSpawnDelay(spawner, 1);
                spawnerBlockProvider.setMaxSpawnDelay(spawner, Integer.MAX_VALUE);
                spawnerBlockProvider.setMinSpawnDelay(spawner, delay);
                spawnerBlockProvider.setMaxSpawnDelay(spawner, delay);
            }
            spawner.setDelay(delay);
            spawner.update();
        } catch (final Throwable ex) {
            throw new Exception(tl("mobSpawnError"), ex);
        }
        charge.charge(user);
        user.sendMessage(tl("setSpawner", mob.name));

    }
}
