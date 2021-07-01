package net.essentialsx.discord.interactions;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.essentialsx.api.v2.services.discord.InteractionChannel;

public class InteractionChannelImpl implements InteractionChannel {
    private final GuildChannel channel;

    public InteractionChannelImpl(GuildChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    public GuildChannel getJdaObject() {
        return channel;
    }

    @Override
    public String getId() {
        return channel.getId();
    }
}
