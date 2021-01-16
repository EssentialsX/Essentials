package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.PlayerList;
import com.earth2me.essentials.User;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import net.essentialsx.discord.util.DiscordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.earth2me.essentials.I18n.tl;

public class ListCommand extends InteractionCommand {

    public ListCommand(EssentialsJDA jda) {
        super(jda, "list", "Gets a list of online players");
        addArgument(new InteractionCommandArgument("group", "(Optional) A specific group to limit your search by", InteractionCommandArgumentType.STRING, false));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final boolean showHidden = DiscordUtil.hasRoles(event.getMember(), getAdminSnowflakes());
        final List<String> output = new ArrayList<>();
        final IEssentials ess = jda.getPlugin().getEss();

        output.add(PlayerList.listSummary(ess, null, showHidden));
        final Map<String, List<User>> playerList = PlayerList.getPlayerLists(ess, null, showHidden);

        final String group = event.getStringArgument("group");
        if (group != null) {
            try {
                output.add(PlayerList.listGroupUsers(ess, playerList, group));
            } catch (Exception e) {
                output.add(tl("errorWithMessage", e.getMessage()));
            }
        } else {
            output.addAll(PlayerList.prepareGroupedList(ess, getName(), playerList));
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String str : output) {
            stringBuilder.append(str).append("\n");
        }
        event.reply(stringBuilder.substring(0, stringBuilder.length() - 2));
    }
}
