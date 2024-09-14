package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.RegistryUtil;
import org.bukkit.Location;
import org.bukkit.Server;
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

    private static Ocelot spawnOcelot(final Server server, final User user) throws Mob.MobException {
        final Ocelot ocelot = (Ocelot) Mob.OCELOT.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        //noinspection deprecation
        final Object[] values = RegistryUtil.values(Ocelot.Type.class);

        final int i = random.nextInt(values.length);
        //noinspection deprecation
        ocelot.setCatType((Ocelot.Type) values[i]);
        ((Tameable) ocelot).setTamed(true);
        ocelot.setBaby();
        ocelot.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));
        return ocelot;
    }

    private static Entity spawnCat(final Server server, final User user) throws Mob.MobException {
        final Cat cat = (Cat) Mob.CAT.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        final Object[] values = RegistryUtil.values(Cat.Type.class);

        final int i = random.nextInt(values.length);
        cat.setCatType((Cat.Type) values[i]);
        cat.setTamed(true);
        cat.setBaby();
        cat.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));
        return cat;
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Entity ocelot = Mob.CAT.getType() == null ? spawnOcelot(server, user) : spawnCat(server, user);
        ess.scheduleSyncDelayedTask(() -> {
            final Location loc = ocelot.getLocation();
            ocelot.remove();
            loc.getWorld().createExplosion(loc, 0F);
        }, 20);

    }
}
