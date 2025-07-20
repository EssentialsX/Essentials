package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.util.Set;

public final class BanLookup {

    private BanLookup() {
    }

    public static Boolean isBanned(final IEssentials ess, final User user) {
        return isBanned(ess, user.getName());
    }

    public static Boolean isBanned(final IEssentials ess, final String name) {
        return getBanEntry(ess, name) != null;
    }

    public static BanEntry getBanEntry(final IEssentials ess, final String name) {
        final Set benteries = ess.getServer().getBanList(BanList.Type.NAME).getBanEntries();
        for (final Object ob : benteries) {
            final BanEntry banEnt = (BanEntry) ob;
            if (banEnt.getTarget().equals(name)) {
                return banEnt;
            }
        }
        return null;
    }

}
