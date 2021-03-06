package net.essentialsx.discord.interactions.command;

import com.google.common.base.Joiner;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.essentialsx.discord.interactions.InteractionController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InteractionEvent {
    private final Member member;
    private final String token;
    private final String channelId;
    private final DataArray options;
    private final InteractionController controller;
    private final List<String> replyBuffer = new ArrayList<>();

    public InteractionEvent(Member member, String token, String channelId, DataArray options, InteractionController controller) {
        this.member = member;
        this.token = token;
        this.channelId = channelId;
        this.options = options;
        this.controller = controller;
    }

    public void reply(String message) {
        replyBuffer.add(message);
        controller.editInteractionResponse(token, Joiner.on('\n').join(replyBuffer));
    }

    public Member getMember() {
        return member;
    }

    public String getStringArgument(String key) {
        return (String) getArgument(key);
    }

    public String getChannelId() {
        return channelId;
    }

    public Object getArgument(String key) {
        if (options != null) {
            for (Object option : options) {
                final HashMap<?, ?> obj = (HashMap<?, ?>) option;
                if (obj.containsKey("name") && obj.containsKey("value") && obj.get("name").equals(key)) {
                    return obj.get("value");
                }
            }
        }
        return null;
    }
}
