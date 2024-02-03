package com.earth2me.essentials.utils;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.messaging.IMessageRecipient;
import net.ess3.api.IUser;

public final class CommonPlaceholders {
    private CommonPlaceholders() {
    }

    public static AdventureUtil.ParsedPlaceholder enableDisable(final CommandSource source, final boolean enable) {
        return AdventureUtil.parsed(source.tl(enable ? "enabled" : "disabled"));
    }

    public static AdventureUtil.ParsedPlaceholder trueFalse(final CommandSource source, final boolean condition) {
        return AdventureUtil.parsed(source.tl(condition ? "true" : "false"));
    }

    public static AdventureUtil.ParsedPlaceholder displayNameRecipient(final IMessageRecipient recipient) {
        return AdventureUtil.parsed(AdventureUtil.legacyToMini(recipient.getDisplayName()));
    }

    public static AdventureUtil.ParsedPlaceholder displayName(final IUser user) {
        return AdventureUtil.parsed(AdventureUtil.legacyToMini(user.getDisplayName()));
    }
}
