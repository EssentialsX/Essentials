package net.essentialsx.discord.util;

import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.dv8tion.jda.api.entities.Member;

import java.awt.Color;

public final class DiscordUtil {
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
}
