package net.essentialsx.discord.interactions;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InteractionController extends ListenerAdapter {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final static Gson GSON = new Gson();
    private final static String CALLBACK_BASE = "https://discord.com/api/v8/interactions/{id}/{token}/callback";
    private final static RequestBody ACK_EAT_REQ = RequestBody.create(JSON, "{\"type\": 2}");
    private final static JsonObject ALLOWED_MENTIONS;

    static {
        final JsonObject allowMentions = new JsonObject();
        allowMentions.add("parse", GSON.toJsonTree(ImmutableList.of("users")).getAsJsonArray());
        allowMentions.add("users", GSON.toJsonTree(ImmutableList.of()).getAsJsonArray());
        ALLOWED_MENTIONS = allowMentions;
    }

    private final EssentialsJDA jda;
    private final String apiRegister;
    private final String apiFollowup;

    private final Map<String, InteractionCommand> commandMap = new HashMap<>();

    public InteractionController(EssentialsJDA jda) {
        this.jda = jda;
        this.apiRegister = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/commands";
        this.apiFollowup = "https://discord.com/api/webhooks/" + jda.getJda().getSelfUser().getId() + "/{token}";

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
        final DataObject data = payload.getObject("data");
        final DataArray options = data.getArray("options");

        new Thread(() -> {
            try {
                final Response response = post(CALLBACK_BASE.replace("{id}", id).replace("{token}", token), ACK_EAT_REQ).execute();
                if (!response.isSuccessful()) {
                    logger.info("Error while responding to interaction: " + response.body().string());
                    return;
                }
                response.close();

                final InteractionCommand command = commandMap.get(data.getString("name"));
                command.onCommand(new InteractionEvent(token, options, InteractionController.this));
            } catch (IOException e) {
                logger.severe("Error while responding to interaction: " + e.getMessage());
                e.printStackTrace();
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
        final JsonObject body = new JsonObject();
        body.addProperty("type", 3);
        body.addProperty("content", message);
        body.add("allowed_mentions", ALLOWED_MENTIONS);
        body.addProperty("flags", 1 << 6);

        post(apiFollowup.replace("{token}", interactionToken), RequestBody.create(JSON, body.toString())).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
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
        (post(apiRegister, RequestBody.create(JSON, commandJson))).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final JsonObject jsonObject = GSON.fromJson(response.body().string(), JsonObject.class);
                    logger.info("Registered guild command: " + jsonObject.get("name").getAsString() + " (with id " + jsonObject.get("id").getAsString() + ")");
                    commandMap.put(command.getName(), command);
                    return;
                }
                logger.info("Error while registering command, raw response: " + response.body().string());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.severe("Error while registering command: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        commandMap.clear();
    }

    private Call post(String url, RequestBody body) {
        return jda.getJda().getHttpClient().newCall(new Request.Builder()
                .url(url)
                .header("Authorization", jda.getJda().getToken())
                .header("Content-Type", "application/json")
                .post(body)
                .build());
    }
}
