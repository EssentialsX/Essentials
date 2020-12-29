package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableList;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.awt.Color;
import java.util.List;

public final class DiscordUtil {
    public final static List<Message.MentionType> NO_GROUP_MENTIONS;

    static {
        final ImmutableList.Builder<Message.MentionType> types = new ImmutableList.Builder<>();
        types.add(Message.MentionType.USER);
        types.add(Message.MentionType.CHANNEL);
        types.add(Message.MentionType.EMOTE);
        NO_GROUP_MENTIONS = types.build();
    }

    private DiscordUtil() {
    }

    public static String getRoleColorFormat(Member member) {
        final Color color = member.getColor();
        if (color != null && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            // Essentials' FormatUtil allows us to not have to use bungee's chatcolor since bukkit's own one doesn't support rgb
            return FormatUtil.replaceFormat("&#" + Integer.toHexString(color.getRGB()).substring(2));
        }
        return "";
    }

    /**
     * Checks is the supplied user has at least one of the supplied roles.
     * @param member          The member to check.
     * @param roleDefinitions A list with either the name or id of roles.
     * @return true if member has role.
     */
    public static boolean hasRoles(Member member, List<String> roleDefinitions) {
        for (Role role : member.getRoles()) {
            for (String roleDefinition : roleDefinitions) {
                roleDefinition = roleDefinition.trim();
                if (role.getId().equals(roleDefinition) || role.getName().equalsIgnoreCase(roleDefinition)) {
                    return true;
                }
            }
        }
        return false;
    }
}
