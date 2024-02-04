package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandkickall extends EssentialsCommand {
    public Commandkickall() {
        super("kickall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        String kickReason = args.length > 0 ? getFinalArg(args, 0) : AdventureUtil.miniToLegacy(tlLiteral("kickDefault"));
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        for (final Player onlinePlayer : ess.getOnlinePlayers()) {
            if (!sender.isPlayer() || !onlinePlayer.getName().equalsIgnoreCase(sender.getPlayer().getName())) {
                if (!ess.getUser(onlinePlayer).isAuthorized("essentials.kickall.exempt")) {
                    onlinePlayer.kickPlayer(kickReason);
                }
            }
        }
        sender.sendTl("kickedAll");
    }
}
