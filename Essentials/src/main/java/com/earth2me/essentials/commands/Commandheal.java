package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;

public class Commandheal extends EssentialsLoopCommand {
    public Commandheal() {
        super("heal");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (!user.isAuthorized("essentials.heal.cooldown.bypass")) {
            user.healCooldown();
        }

        if (args.length > 0 && user.isAuthorized("essentials.heal.others")) {
            loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
            return;
        }

        updatePlayer(server, user.getSource(), user, args);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) throws PlayerExemptException {
        try {
            final Player player = user.getBase();

            if (player.getHealth() == 0) {
                throw new PlayerExemptException("healDead");
            }

            final double amount = player.getMaxHealth() - player.getHealth();
            final EntityRegainHealthEvent erhe = new EntityRegainHealthEvent(player, amount, RegainReason.CUSTOM);
            ess.getServer().getPluginManager().callEvent(erhe);
            if (erhe.isCancelled()) {
                throw new QuietAbortException();
            }

            double newAmount = player.getHealth() + erhe.getAmount();
            if (newAmount > player.getMaxHealth()) {
                newAmount = player.getMaxHealth();
            }

            player.setHealth(newAmount);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            user.sendTl("heal");
            if (ess.getSettings().isRemovingEffectsOnHeal()) {
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            }
            sender.sendTl("healOther", user.getDisplayName());
        } catch (final QuietAbortException e) {
            //Handle Quietly
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.heal.others")) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();

        }
    }
}
