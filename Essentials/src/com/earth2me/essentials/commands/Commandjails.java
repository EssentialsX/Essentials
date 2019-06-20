package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;

import java.util.Collection;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandjails class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandjails extends EssentialsCommand {
    /**
     * <p>Constructor for Commandjails.</p>
     */
    public Commandjails() {
        super("jails");
    }

    /** {@inheritDoc} */
    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (ess.getJails().getCount() < 1) {
            sender.sendMessage(tl("noJailsDefined"));
        } else {
            sender.sendMessage(tl("jailList", StringUtil.joinList(" ", ess.getJails().getList())));
        }
    }
}
