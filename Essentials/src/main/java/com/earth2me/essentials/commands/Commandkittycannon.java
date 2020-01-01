package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Tameable;

import java.util.Random;

// This command is not documented on the wiki #EasterEgg
public class Commandkittycannon extends EssentialsCommand {
    private static final Random random = new Random();

    public Commandkittycannon() {
        super("kittycannon");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Entity ocelot = Mob.CAT.getType() == null ? spawnOcelot(user.getWorld(), server, user) : spawnCat(user.getWorld(), server, user);

        class KittyCannonExplodeTask implements Runnable {
            @Override
            public void run() {
                final Location loc = ocelot.getLocation();
                ocelot.remove();
                loc.getWorld().createExplosion(loc, 0F);
            }
        }
        ess.scheduleSyncDelayedTask(new KittyCannonExplodeTask(), 20);

    }

    private static Ocelot spawnOcelot(World world, Server server, User user) throws Mob.MobException {
        final Ocelot ocelot = (Ocelot) Mob.OCELOT.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        if (ocelot == null) {
            return null;
        }
        final int i = random.nextInt(Ocelot.Type.values().length);
        ocelot.setCatType(Ocelot.Type.values()[i]);
        ((Tameable) ocelot).setTamed(true);
        ocelot.setBaby();
        ocelot.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));

        return ocelot;
    }

    private static Entity spawnCat(World world, Server server, User user) throws Mob.MobException {
        final Cat cat = (Cat) Mob.CAT.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        if (cat == null) {
            return null;
        }
        final int i = random.nextInt(Cat.Type.values().length);
        cat.setCatType(Cat.Type.values()[i]);
        cat.setTamed(true);
        cat.setBaby();
        cat.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));

        return cat;
    }
}
