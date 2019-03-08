package com.earth2me.essentials.discord;

import com.earth2me.essentials.IEssentials;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DiscordFormatter {

    private final IEssentials ess;
    private Map<String, Function<Player, String>> playerReplacers = new HashMap<>();

    public DiscordFormatter() {
        throw new UnsupportedOperationException("Public instantiation not allowed");
    }

    DiscordFormatter(IEssentials ess) {
        this.ess = ess;

        playerReplacers.put("username", Player::getName);
        playerReplacers.put("displayname", Player::getDisplayName);
        playerReplacers.put("nickname", p -> ess.getUser(p).getNick(false, false, false));
        playerReplacers.put("uuid", p -> p.getUniqueId().toString());
        playerReplacers.put("prefix", p -> ess.getPermissionsHandler().getPrefix(p));
        playerReplacers.put("suffix", p -> ess.getPermissionsHandler().getSuffix(p));
    }

    String format(DiscordSettings.ChannelDefinition channel, Map<String, Object> tokenMap) {
        String format = channel.getFormat();
        for (Map.Entry<String, Object> token : tokenMap.entrySet()) {
            String toReplace = token.getKey();
            Object replaceWith = token.getValue();
            if (replaceWith instanceof Player) {
                format = doReplacement(format, toReplace, (Player) replaceWith);
            } else if (replaceWith instanceof String) {
                format = doReplacement(format, toReplace, (String) replaceWith);
            } else {
                format = format.replace("{" + toReplace + "}", replaceWith.toString());
            }
        }
        return format;
    }

    String doReplacement(String format, String toReplace, Player replaceWith) {
        for (String tokenPart : playerReplacers.keySet()) {
            final String token = "{" + toReplace + "." + tokenPart + "}";
            if (!format.contains(token)) continue;
            format = format.replace(token, playerReplacers.get(tokenPart).apply(replaceWith));
        }
        return format;
    }

    String doReplacement(String format, String toReplace, String replaceWith) {
        return format.replace("{" + toReplace + "}", replaceWith);
    }
}
