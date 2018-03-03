package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static com.neximation.essentials.I18n.tl;


public class Commandkickall extends EssentialsCommand {
    public Commandkickall() {
        super("kickall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        String kickReason = args.length > 0 ? getFinalArg(args, 0) : tl("kickDefault");
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        for (Player onlinePlayer : ess.getOnlinePlayers()) {
            if (!sender.isPlayer() || !onlinePlayer.getName().equalsIgnoreCase(sender.getPlayer().getName())) {
                onlinePlayer.kickPlayer(kickReason);
            }
        }
        sender.sendMessage(tl("kickedAll"));
    }
}
