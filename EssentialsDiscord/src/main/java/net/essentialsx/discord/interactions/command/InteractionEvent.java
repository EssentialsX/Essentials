package net.essentialsx.discord.interactions.command;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.essentialsx.discord.interactions.InteractionController;

import java.util.HashMap;

public class InteractionEvent {
    private final String token;
    private final DataArray options;
    private final InteractionController controller;

    public InteractionEvent(String token, DataArray options, InteractionController controller) {
        this.token = token;
        this.options = options;
        this.controller = controller;
    }

    public void replyEphemeral(String message) {
        controller.sendEphemeralMessage(token, message);
    }

    public String getStringArgument(String key) {
        return (String) getArgument(key);
    }

    public Object getArgument(String key) {
        final HashMap<?, ?> map;
        for (Object option : options) {
            final HashMap<?, ?> obj = (HashMap<?, ?>) option;
            if (obj.containsKey("name") && obj.containsKey("value") && obj.get("name").equals(key)) {
                return obj.get("value");
            }
        }
        return null;
    }
}
