package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.util.Set;


/**
 * <p>BanLookup class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class BanLookup {
    /**
     * <p>isBanned.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @param user a {@link com.earth2me.essentials.User} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean isBanned(IEssentials ess, User user) {
        return isBanned(ess, user.getName());
    }

    /**
     * <p>isBanned.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean isBanned(IEssentials ess, String name) {
        return getBanEntry(ess, name) != null;
    }

    /**
     * <p>getBanEntry.</p>
     *
     * @param ess a {@link net.ess3.api.IEssentials} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.bukkit.BanEntry} object.
     */
    public static BanEntry getBanEntry(IEssentials ess, String name) {
        Set<BanEntry> benteries = ess.getServer().getBanList(BanList.Type.NAME).getBanEntries();
        for (BanEntry banEnt : benteries) {
            if (banEnt.getTarget().equals(name)) {
                return banEnt;
            }
        }
        return null;
    }

}
