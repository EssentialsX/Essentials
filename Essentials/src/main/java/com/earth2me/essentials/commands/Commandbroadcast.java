package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.adventure.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

public class Commandbroadcast extends EssentialsCommand {
    public Commandbroadcast() {
        super("broadcast");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String message = FormatUtil.replaceFormat(getFinalArg(args, 0)).replace("\\n", "\n");
        ess.broadcastTl("broadcast",
                AdventureUtil.parsed(ess.getAdventureFacet().legacyToMiniWithUrls(ess.getAdventureFacet().escapeTags(message))),
                sender.getDisplayName());
    }
}
