package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Server;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Commandpowertoollist extends EssentialsCommand {
    public Commandpowertoollist() {
        super("powertoollist");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (!user.hasPowerTools()) {
            user.sendTl("noPowerTools");
            return;
        }
        final Map<String, List<String>> powertools = user.getAllPowertools();
        for (Map.Entry<String, List<String>> entry : powertools.entrySet()) {
            final String itemName = entry.getKey().toLowerCase(Locale.ENGLISH).replaceAll("_", " ");
            final List<String> commands = entry.getValue();
            user.sendTl("powerToolList", StringUtil.joinList(commands), itemName);
        }
    }
}
