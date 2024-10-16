package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FloatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Commandfireball extends EssentialsCommand {

    public static final String FIREBALL_META_KEY = "ess_fireball_proj";

    private static final Map<String, Class<? extends Projectile>> types;

    static {
        final ImmutableMap.Builder<String, Class<? extends Projectile>> builder = ImmutableMap.<String, Class<? extends Projectile>>builder()
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

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_21_R01)) {
            builder.put("windcharge", WindCharge.class);
        }

        types = builder.build();
    }

    public Commandfireball() {
        super("fireball");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final String type = args.length > 0 && types.containsKey(args[0]) ? args[0] : "fireball";
        double speed = 2;
        final boolean ride = args.length > 2 && args[2].equalsIgnoreCase("ride") && user.isAuthorized("essentials.fireball.ride");

        if (args.length > 1) {
            try {
                speed = FloatUtil.parseDouble(args[1]);
                speed = Double.max(0, Double.min(speed, ess.getSettings().getMaxProjectileSpeed()));
            } catch (final Exception ignored) {
            }
        }

        if (!user.isAuthorized("essentials.fireball." + type)) {
            throw new TranslatableException("noPerm", "essentials.fireball." + type);
        }

        final Vector direction = user.getBase().getEyeLocation().getDirection().multiply(speed);
        final Projectile projectile = user.getWorld().spawn(user.getBase().getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), types.get(type));
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
