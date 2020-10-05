package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface IEssentialsXMPP extends Plugin {
    String getAddress(final Player user);

    String getAddress(final String name);

    List<String> getSpyUsers();

    IUser getUserByAddress(final String address);

    boolean sendMessage(final Player user, final String message);

    boolean sendMessage(final String address, final String message);

    void setAddress(final Player user, final String address);

    boolean toggleSpy(final Player user);

    void broadcastMessage(final IUser sender, final String message, final String xmppAddress);

    IEssentials getEss();
}
