package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

import static com.earth2me.essentials.I18n.tl;

public class Commandbeezooka extends EssentialsCommand {

    public Commandbeezooka() {
        super("beezooka");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_15_R01)) {
            user.sendMessage(tl("unsupportedFeature"));
            return;
        }

        final Entity bee = Mob.BEE.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        bee.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));

        ess.scheduleEntityDelayedTask(bee, () -> {
            final Location loc = bee.getLocation();
            bee.remove();
            loc.getWorld().createExplosion(loc, 0F);
        }, 20);
    }

}
