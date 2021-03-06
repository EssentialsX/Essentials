package net.essentialsx.discord.interactions;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Member;
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

import static com.earth2me.essentials.I18n.tl;

public class InteractionController extends ListenerAdapter {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");

    private final EssentialsJDA jda;

    private final String apiCallback = "https://discord.com/api/v8/interactions/{id}/{token}/callback";
    private final String apiRegister;
    private final String apiDelete;
    private final String apiFollowup;

    private final Map<String, InteractionCommand> commandMap = new HashMap<>();
    private final List<String> commandIds = new ArrayList<>();

    public InteractionController(EssentialsJDA jda) {
        this.jda = jda;
        this.apiRegister = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/commands";
        this.apiDelete = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/commands/{id}";
        this.apiFollowup = "https://discord.com/api/webhooks/" + jda.getJda().getSelfUser().getId() + "/{token}/messages/@original";

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
        final DataArray options = data.hasKey("options") ? data.getArray("options") : null;

        new Thread(() -> {
            try {
                final InteractionCommand command = commandMap.get(data.getString("name"));
                final Response response = post(apiCallback.replace("{id}", id).replace("{token}", token),
                        command.isEphemeral() ? DiscordUtil.ACK_DEFER_EPHEMERAL : DiscordUtil.ACK_DEFER).execute();
                if (!response.isSuccessful()) {
                    //noinspection ConstantConditions
                    logger.info("Error while responding to interaction: " + response.body().string());
                    return;
                }
                response.close();

                final Member member = jda.getGuild().retrieveMemberById(payload.getObject("member").getObject("user").getString("id")).complete();
                jda.getPlugin().getEss().scheduleSyncDelayedTask(() ->
                        command.onPreCommand(new InteractionEvent(member, token, channelId, options, InteractionController.this)));
            } catch (IOException e) {
                logger.severe("Error while responding to interaction: " + e.getMessage());
                if (jda.isDebug()) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends a message in response to a user who created an interaction.
     *
     * @param interactionToken The authorization token of the interaction.
     * @param message          The message to be sent.
     */
    public void editInteractionResponse(String interactionToken, String message) {
        message = FormatUtil.stripFormat(message).replace("§", ""); // Don't ask

        final JsonObject body = new JsonObject();
        body.addProperty("content", message);
        body.add("allowed_mentions", DiscordUtil.RAW_NO_GROUP_MENTIONS);

        patch(apiFollowup.replace("{token}", interactionToken), RequestBody.create(DiscordUtil.JSON_TYPE, body.toString())).enqueue(new Callback() {
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
        if (!command.isEnabled()) {
            return;
        }

        final String commandJson = command.serialize().toString();
        post(apiRegister, RequestBody.create(DiscordUtil.JSON_TYPE, commandJson)).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    commandMap.put(command.getName(), command);
                    //noinspection ConstantConditions
                    final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                    commandIds.add(responseObj.get("id").getAsString());
                    if (jda.isDebug()) {
                        logger.info("Registered guild command: " + command.getName());
                        logger.info("Registration payload: " + responseObj.toString());
                    }
                    return;
                }
                //noinspection ConstantConditions
                final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                if (responseObj.has("code") && responseObj.get("code").getAsInt() == 50001) {
                    logger.severe(tl("discordErrorCommand"));
                    return;
                }

                logger.warning("Error while registering command, raw response: " + responseObj.toString());
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

    private Call patch(String url, RequestBody body) {
        return jda.getJda().getHttpClient().newCall(builder(url)
                .patch(body)
                .build());
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
