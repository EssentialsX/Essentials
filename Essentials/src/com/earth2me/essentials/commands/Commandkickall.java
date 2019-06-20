package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandkickall class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandkickall extends EssentialsCommand {
    /**
     * <p>Constructor for Commandkickall.</p>
     */
    public Commandkickall() {
        super("kickall");
    }

    /** {@inheritDoc} */
    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        String kickReason = args.length > 0 ? getFinalArg(args, 0) : tl("kickDefault");
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        for (Player onlinePlayer : ess.getOnlinePlayers()) {
            if (!sender.isPlayer() || !onlinePlayer.getName().equalsIgnoreCase(sender.getPlayer().getName())) {
                if (!ess.getUser(onlinePlayer).isAuthorized("essentials.kickall.exempt")) {
                    onlinePlayer.kickPlayer(kickReason);
                }
            }
        }
        sender.sendMessage(tl("kickedAll"));
    }
}
