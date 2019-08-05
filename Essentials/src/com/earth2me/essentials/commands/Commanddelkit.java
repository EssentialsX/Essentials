package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class Commanddelkit extends EssentialsCommand {
    public Commanddelkit() {
        super("delkit");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            final String kitList = ess.getKits().listKits(ess, null);
            sender.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
            throw new NoChargeException();
        } else {
            final String kitName = args[0];
            final Kit kit = new Kit(kitName, ess);

            if (sender.getPlayer() != null) {
                kit.checkPerms(ess.getUser(sender.getPlayer()));
            }

            ess.getKits().removeKit(kitName);
            sender.sendMessage(tl("deleteKit", kit));
        }
    }
}
