package com.earth2me.essentials.chat;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;


class ChatStore {
    private final User user;
    private final String type;
    private final Trade charge;
    private long radius;

    ChatStore(final IEssentials ess, final User user, final String type) {
        this.user = user;
        this.type = type;
        this.charge = new Trade(getLongType(), ess);
    }

    User getUser() {
        return user;
    }

    Trade getCharge() {
        return charge;
    }

    String getType() {
        return type;
    }

    final String getLongType() {
        return type.length() == 0 ? "chat" : "chat-" + type;
    }

    long getRadius() {
        return radius;
    }

    void setRadius(long radius) {
        this.radius = radius;
    }
}
