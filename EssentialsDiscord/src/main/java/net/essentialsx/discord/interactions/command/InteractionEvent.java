package net.essentialsx.discord.interactions.command;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.essentialsx.discord.interactions.InteractionController;

import java.util.HashMap;

public class InteractionEvent {
    private final String qualifiedName;
    private final String token;
    private final String channelId;
    private final DataArray options;
    private final InteractionController controller;

    public InteractionEvent(String qualifiedName, String token, String channelId, DataArray options, InteractionController controller) {
        this.qualifiedName = qualifiedName;
        this.token = token;
        this.channelId = channelId;
        this.options = options;
        this.controller = controller;
    }

    public void replyEphemeral(String message) {
        controller.sendEphemeralMessage(token, message);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getStringArgument(String key) {
        return (String) getArgument(key);
    }

    public String getChannelId() {
        return channelId;
    }

    public Object getArgument(String key) {
        for (Object option : options) {
            final HashMap<?, ?> obj = (HashMap<?, ?>) option;
            if (obj.containsKey("name") && obj.containsKey("value") && obj.get("name").equals(key)) {
                return obj.get("value");
            }
        }
        return null;
    }
}
