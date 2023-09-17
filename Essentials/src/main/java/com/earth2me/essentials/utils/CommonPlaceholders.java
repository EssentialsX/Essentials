package com.earth2me.essentials.utils;

import com.earth2me.essentials.CommandSource;

public final class CommonPlaceholders {
    private CommonPlaceholders() {
    }

    public static AdventureUtil.ParsedPlaceholder enableDisable(CommandSource source, boolean enable) {
        return AdventureUtil.parsed(source.tl(enable ? "enabled" : "disabled"));
    }

    public static AdventureUtil.ParsedPlaceholder trueFalse(CommandSource source, boolean condition) {
        return AdventureUtil.parsed(source.tl(condition ? "true" : "false"));
    }
}
