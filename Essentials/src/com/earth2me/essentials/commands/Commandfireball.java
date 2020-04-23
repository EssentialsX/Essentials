package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FloatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class Commandfireball extends EssentialsCommand {

    public static final String FIREBALL_META_KEY = "ess_fireball_proj";

    private static final Map<String, Class<? extends Projectile>> types;

    static {
        ImmutableMap.Builder<String, Class<? extends Projectile>> builder = ImmutableMap.<String, Class<? extends Projectile>>builder()
                .put("fireball", Fireball.class)
                .put("small", SmallFireball.class)
                .put("large", LargeFireball.class)
                .put("arrow", Arrow.class)
                .put("skull", WitherSkull.class)
                .put("egg", Egg.class)
                .put("snowball", Snowball.class)
                .put("expbottle", ThrownExpBottle.class);

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_9_R01)) {
            builder.put("dragon", DragonFireball.class)
                    .put("splashpotion", SplashPotion.class)
                    .put("lingeringpotion", LingeringPotion.class);
        }

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_13_0_R01)) {
            builder.put("trident", Trident.class);
        }

        types = builder.build();
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
            } catch (Exception ignored) {
            }
        }

        if (args.length > 2 && args[2].equalsIgnoreCase("ride") && user.isAuthorized("essentials.fireball.ride")) {
            ride = true;
        }

        if (!user.isAuthorized("essentials.fireball." + type)) {
            throw new Exception(tl("noPerm", "essentials.fireball." + type));
        }

        final Vector direction = user.getBase().getEyeLocation().getDirection().multiply(speed);
        Projectile projectile = user.getWorld().spawn(user.getBase().getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), types.get(type));
        projectile.setShooter(user.getBase());
        projectile.setVelocity(direction);
        projectile.setMetadata(FIREBALL_META_KEY, new FixedMetadataValue(ess, true));

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
