package net.essentialsx.discord.interactions;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import net.essentialsx.discord.util.DiscordUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class InteractionController extends ListenerAdapter {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final EssentialsJDA jda;

    private final String apiCallback = "https://discord.com/api/v8/interactions/{id}/{token}/callback";
    private final String apiRegister;
    private final String apiDelete;
    private final String apiFollowup;
    private final RequestBody acknowledgePayload;

    private final Map<String, InteractionCommand> commandMap = new HashMap<>();
    private final List<String> commandIds = new ArrayList<>();

    public InteractionController(EssentialsJDA jda) {
        this.jda = jda;
        this.apiRegister = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/commands";
        this.apiDelete = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/commands/{id}";
        this.apiFollowup = "https://discord.com/api/webhooks/" + jda.getJda().getSelfUser().getId() + "/{token}";
        this.acknowledgePayload = RequestBody.create(JSON, "{\"type\": 2}");

        jda.getJda().addEventListener(this);
    }

    @Override
    public void onRawGateway(@NotNull RawGatewayEvent event) {
        if (!event.getType().equals("INTERACTION_CREATE")) {
            return;
        }

        final DataObject payload = event.getPayload();
        if (!payload.hasKey("data") || !payload.getObject("data").hasKey("name") || !commandMap.containsKey(payload.getObject("data").getString("name"))) {
            return;
        }

        // We got to respond quick or else discord will never listen to us again!
        final String id = payload.getString("id");
        final String token = payload.getString("token");
        final String channelId = payload.getString("channel_id");
        final DataObject data = payload.getObject("data");
        final DataArray options = data.getArray("options");
        final DataObject user = payload.getObject("member").getObject("user");
        final String username = user.getString("username");
        final String discriminator = user.getString("discriminator");

        new Thread(() -> {
            try {
                final Response response = post(apiCallback.replace("{id}", id).replace("{token}", token), acknowledgePayload).execute();
                if (!response.isSuccessful()) {
                    //noinspection ConstantConditions
                    logger.info("Error while responding to interaction: " + response.body().string());
                    return;
                }
                response.close();

                final InteractionCommand command = commandMap.get(data.getString("name"));
                command.onCommand(new InteractionEvent(username + "#" + discriminator, token, channelId, options, InteractionController.this));
            } catch (IOException e) {
                logger.severe("Error while responding to interaction: " + e.getMessage());
                if (jda.isDebug()) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends an client-side (ephemeral) message to the user who created the interaction.
     *
     * @param interactionToken The authorization token of the interaction.
     * @param message          The message to be sent.
     */
    public void sendEphemeralMessage(String interactionToken, String message) {
        message = FormatUtil.stripFormat(message);

        final JsonObject body = new JsonObject();
        body.addProperty("type", 3);
        body.addProperty("content", message);
        body.add("allowed_mentions", DiscordUtil.RAW_NO_GROUP_MENTIONS);
        body.addProperty("flags", 1 << 6);

        post(apiFollowup.replace("{token}", interactionToken), RequestBody.create(JSON, body.toString())).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //noinspection ConstantConditions
                    logger.info("Error while responding to interaction: " + response.body().string());
                    return;
                }
                response.close();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.severe("Error while responding to interaction: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void registerCommand(InteractionCommand command) {
        final String commandJson = command.serialize().toString();
        post(apiRegister, RequestBody.create(JSON, commandJson)).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    commandMap.put(command.getName(), command);
                    //noinspection ConstantConditions
                    final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                    commandIds.add(responseObj.get("id").getAsString());
                    logger.info("Registered guild command: " + command.getName());
                    if (jda.isDebug()) {
                        logger.info("Registration payload: " + responseObj.toString());
                    }
                    return;
                }
                //noinspection ConstantConditions
                logger.info("Error while registering command, raw response: " + response.body().string());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.severe("Error while registering command, " + command.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        for (String commandId : commandIds) {
            try {
                jda.getJda().getHttpClient().newCall(builder(apiDelete.replace("{id}", commandId)).delete().build()).execute();
            } catch (IOException e) {
                logger.severe("Error while deleting command: " + e.getMessage());
                if (jda.isDebug()) {
                    e.printStackTrace();
                }
            }
        }
        commandMap.clear();
    }

    private Call post(String url, RequestBody body) {
        return jda.getJda().getHttpClient().newCall(builder(url)
                .post(body)
                .build());
    }

    private Request.Builder builder(String url) {
        return new Request.Builder()
                .url(url)
                .header("Authorization", jda.getJda().getToken())
                .header("Content-Type", "application/json");
    }
}
