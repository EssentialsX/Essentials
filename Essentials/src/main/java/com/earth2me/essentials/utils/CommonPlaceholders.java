package com.earth2me.essentials.utils;

import com.earth2me.essentials.CommandSource;

public final class CommonPlaceholders {
    private CommonPlaceholders() {
    }

    public static AdventureUtil.ParsedPlaceholder enableDisable(final CommandSource source, final boolean enable) {
        return AdventureUtil.parsed(source.tl(enable ? "enabled" : "disabled"));
    }

    public static AdventureUtil.ParsedPlaceholder trueFalse(final CommandSource source, final boolean condition) {
        return AdventureUtil.parsed(source.tl(condition ? "true" : "false"));
    }
}
