package net.essentialsx.discord.interactions;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.essentialsx.api.v2.services.discord.InteractionChannel;

public class InteractionChannelImpl implements InteractionChannel {
    private final GuildMessageChannel channel;

    public InteractionChannelImpl(GuildMessageChannel channel) {
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
