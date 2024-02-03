package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.PlayerList;
import com.earth2me.essentials.User;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.interactions.InteractionCommandImpl;
import net.essentialsx.discord.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.earth2me.essentials.I18n.tlLiteral;

public class ListCommand extends InteractionCommandImpl {

    public ListCommand(JDADiscordService jda) {
        super(jda, "list", tlLiteral("discordCommandListDescription"));
        addArgument(new InteractionCommandArgument("group", tlLiteral("discordCommandListArgumentGroup"), InteractionCommandArgumentType.STRING, false));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final boolean showHidden = event.getMember().hasRoles(getAdminSnowflakes());
        final List<String> output = new ArrayList<>();
        final IEssentials ess = jda.getPlugin().getEss();

        output.add(PlayerList.listSummary(ess, null, showHidden));
        final Map<String, List<User>> playerList = PlayerList.getPlayerLists(ess, null, showHidden);

        final String group = event.getStringArgument("group");
        if (group != null) {
            try {
                output.add(PlayerList.listGroupUsers(ess, playerList, group));
            } catch (Exception e) {
                output.add(tlLiteral("errorWithMessage", e.getMessage()));
            }
        } else {
            output.addAll(PlayerList.prepareGroupedList(ess, null, getName(), playerList));
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String str : output) {
            stringBuilder.append(str).append("\n");
        }
        event.reply(MessageUtil.sanitizeDiscordMarkdown(stringBuilder.substring(0, stringBuilder.length() - 2)));
    }
}
