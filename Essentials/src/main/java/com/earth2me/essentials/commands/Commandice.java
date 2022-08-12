package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.IUser;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandice extends EssentialsLoopCommand {
    public Commandice() {
        super("ice");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_17_R01)) {
            sender.sendMessage(tl("unsupportedFeature"));
            return;
        }

        if (args.length == 0 && !sender.isPlayer()) {
            throw new NotEnoughArgumentsException();
        }

        if (args.length > 0 && sender.isAuthorized("essentials.ice.others", ess)) {
            loopOnlinePlayers(server, sender, false, true, args[0], null);
            return;
        }
        //noinspection ConstantConditions
        freezePlayer(sender.getUser(ess));
    }

    @Override
    protected void updatePlayer(Server server, CommandSource sender, User user, String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException {
        freezePlayer(user);
        sender.sendMessage(tl("iceOther", user.getDisplayName()));
    }

    private void freezePlayer(final IUser user) {
        user.getBase().setFreezeTicks(user.getBase().getMaxFreezeTicks());
        user.sendMessage(tl("ice"));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.ice.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
