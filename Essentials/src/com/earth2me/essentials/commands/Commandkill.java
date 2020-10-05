package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandkill extends EssentialsLoopCommand {
    public Commandkill() {
        super("kill");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) throws PlayerExemptException {
        final Player matchPlayer = user.getBase();
        if (sender.isPlayer() && user.isAuthorized("essentials.kill.exempt") && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.kill.force")) {
            throw new PlayerExemptException(tl("killExempt", matchPlayer.getDisplayName()));
        }
        final EntityDamageEvent ede = new EntityDamageEvent(matchPlayer, sender.isPlayer() && sender.getPlayer().getName().equals(matchPlayer.getName()) ? EntityDamageEvent.DamageCause.SUICIDE : EntityDamageEvent.DamageCause.CUSTOM, Float.MAX_VALUE);
        server.getPluginManager().callEvent(ede);
        if (ede.isCancelled() && sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.kill.force")) {
            return;
        }
        ede.getEntity().setLastDamageCause(ede);
        matchPlayer.setHealth(0);
        sender.sendMessage(tl("kill", matchPlayer.getDisplayName()));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
