package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FloatUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class Commandfireball extends EssentialsCommand {
    private static final Map<String, Class<? extends Entity>> types = new HashMap<>();

    static {
        String[] classNames = { "Fireball", "SmallFireball", "LargeFireball", "DragonFireball", "Arrow", "WitherSkull",
                "Egg", "Snowball", "ThrownExpBottle", "SplashPotion", "LingeringPotion"};
        Class<?>[] classes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            classes[i] = classNames[i].getClass();
            Class<?> c = classes[i];
            if (c != null) {
                types.put(classNames[i].toLowerCase(), (Class<? extends Entity>) c);
            }
        }
    }

    public Commandfireball() {
        super("fireball");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        String type = "fireball";
        double speed = 2;
        boolean ride = false;

        if (args.length > 0 && types.containsKey(args[0])) {
            type = args[0];
        }

        if (args.length > 1) {
            try {
                speed = FloatUtil.parseDouble(args[1]);
                speed = Double.max(0, Double.min(speed, ess.getSettings().getMaxProjectileSpeed()));
            } catch (Exception ignored) {}
        }

        if (args.length > 2 && args[2].equalsIgnoreCase("ride") && user.isAuthorized("essentials.fireball.ride")) {
            ride = true;
        }

        if (!user.isAuthorized("essentials.fireball." + type)) {
            throw new Exception(tl("noPerm", "essentials.fireball." + type));
        }

        final Vector direction = user.getBase().getEyeLocation().getDirection().multiply(speed);
        Projectile projectile = (Projectile) user.getWorld().spawn(user.getBase().getEyeLocation().add(direction.getX(),
                direction.getY(), direction.getZ()), types.get(type));
        projectile.setShooter(user.getBase());
        projectile.setVelocity(direction);

        if (ride) {
            projectile.addPassenger(user.getBase());
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return types.keySet().stream()
                .filter(type -> user.isAuthorized("essentials.fireball." + type))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "2", "3", "4", "5");
        } else {
            return Collections.emptyList();
        }
    }
}
