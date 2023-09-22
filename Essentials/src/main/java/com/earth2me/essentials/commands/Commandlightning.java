package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.entity.LightningStrike;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandlightning extends EssentialsLoopCommand {
    public Commandlightning() {
        super("lightning");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || !sender.isAuthorized("essentials.lightning.others", ess)) {
            if (sender.isPlayer()) {
                sender.getPlayer().getWorld().strikeLightning(sender.getUser(ess).getTargetBlock(600).getLocation());
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        int power = 5;
        if (args.length > 1) {
            try {
                power = Integer.parseInt(args[1]);
            } catch (final NumberFormatException ignored) {
            }
        }
        final int finalPower = power;
        loopOnlinePlayersConsumer(server, sender, false, true, args[0], player -> {
            sender.sendMessage(tl("lightningUse", player.getName()));
            final LightningStrike strike = player.getBase().getWorld().strikeLightningEffect(player.getBase().getLocation());

            if (!player.isGodModeEnabled()) {
                player.getBase().damage(finalPower, strike);
            }
            if (ess.getSettings().warnOnSmite()) {
                player.sendMessage(tl("lightningSmited"));
            }
        });
        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User matchUser, final String[] args) {
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (!user.isAuthorized("essentials.lightning.others")) {
            // Can't use any params, including power
            return Collections.emptyList();
        } else {
            return super.getTabCompleteOptions(server, user, commandLabel, args);
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            return Lists.newArrayList("5");
        } else {
            return Collections.emptyList();
        }
    }
}
