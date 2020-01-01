package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IConf;

import java.io.File;
import java.util.*;


public class UserManager implements IConf {
    private final transient EssentialsConf users;
    private final transient List<String> spyusers = Collections.synchronizedList(new ArrayList<>());
    private static final String ADDRESS = "address";
    private static final String SPY = "spy";

    UserManager(final File folder) {
        users = new EssentialsConf(new File(folder, "users.yml"));
        reloadConfig();
    }

    final boolean isSpy(final String username) {
        return users.getBoolean(username.toLowerCase(Locale.ENGLISH) + "." + SPY, false);
    }

    void setSpy(final String username, final boolean spy) {
        setUser(username.toLowerCase(Locale.ENGLISH), getAddress(username), spy);
    }

    final String getAddress(final String username) {
        return users.getString(username.toLowerCase(Locale.ENGLISH) + "." + ADDRESS, null);
    }

    final String getUserByAddress(final String search) {
        final Set<String> usernames = users.getKeys(false);
        for (String username : usernames) {
            final String address = users.getString(username + "." + ADDRESS, null);
            if (search.equalsIgnoreCase(address)) {
                return username;
            }
        }
        return null;
    }

    void setAddress(final String username, final String address) {
        setUser(username.toLowerCase(Locale.ENGLISH), address, isSpy(username));
    }

    List<String> getSpyUsers() {
        return spyusers;
    }

    private void setUser(final String username, final String address, final boolean spy) {
        final Map<String, Object> userdata = new HashMap<>();
        userdata.put(ADDRESS, address);
        userdata.put(SPY, spy);
        users.setProperty(username, userdata);
        users.save();
        reloadConfig();
    }

    @Override
    public final void reloadConfig() {
        users.load();
        spyusers.clear();
        final Set<String> keys = users.getKeys(false);
        for (String key : keys) {
            if (isSpy(key)) {
                final String address = getAddress(key);
                if (address != null) {
                    spyusers.add(address);
                }
            }
        }
    }
}
