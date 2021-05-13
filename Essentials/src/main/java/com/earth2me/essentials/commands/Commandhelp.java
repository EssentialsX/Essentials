package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.HelpInput;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.earth2me.essentials.I18n.tl;

public class Commandhelp extends EssentialsCommand {
    public Commandhelp() {
        super("help");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final IText output;
        String pageStr = args.length > 0 ? args[0] : null;
        String chapterPageStr = args.length > 1 ? args[1] : null;
        String command = commandLabel;
        final IText input = new TextInput(user.getSource(), "help", false, ess);

        if (input.getLines().isEmpty()) {
            if (pageStr != null && pageStr.startsWith("/")) {
                final String cmd = pageStr.substring(1);
                for (final Map.Entry<String, Command> knownCmd : ess.getKnownCommandsProvider().getKnownCommands().entrySet()) {
                    if (knownCmd.getKey().equalsIgnoreCase(cmd)) {
                        user.sendMessage(tl("commandHelpLine1", cmd));
                        user.sendMessage(tl("commandHelpLine2", knownCmd.getValue().getDescription()));
                        user.sendMessage(tl("commandHelpLine4", knownCmd.getValue().getAliases().toString()));
                        user.sendMessage(tl("commandHelpLine3"));
                        final boolean isEssCommand = knownCmd.getValue() instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) knownCmd.getValue()).getPlugin().equals(ess);
                        final IEssentialsCommand essCommand = isEssCommand ? ess.getCommandMap().get(knownCmd.getValue().getName()) : null;
                        if (essCommand != null && !essCommand.getUsageStrings().isEmpty()) {
                            for (Map.Entry<String, String> usage : essCommand.getUsageStrings().entrySet()) {
                                user.sendMessage(tl("commandHelpLineUsage", usage.getKey().replace("<command>", cmd), usage.getValue()));
                            }
                        } else {
                            user.sendMessage(knownCmd.getValue().getUsage());
                        }
                        return;
                    }
                }
            }

            if (NumberUtil.isInt(pageStr) || pageStr == null) {
                output = new HelpInput(user, "", ess);
            } else {
                if (pageStr.length() > 26) {
                    pageStr = pageStr.substring(0, 25);
                }
                output = new HelpInput(user, pageStr.toLowerCase(Locale.ENGLISH), ess);
                command = command.concat(" ").concat(pageStr);
                pageStr = chapterPageStr;
            }
            chapterPageStr = null;
        } else {
            user.setDisplayNick();
            output = new KeywordReplacer(input, user.getSource(), ess);
        }
        final TextPager pager = new TextPager(output);
        pager.showPage(pageStr, chapterPageStr, command, user.getSource());
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage(tl("helpConsole"));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> suggestions = new ArrayList<>(getCommands(server));
            suggestions.addAll(getPlugins(server));
            return suggestions;
        } else {
            return Collections.emptyList();
        }
    }
}
