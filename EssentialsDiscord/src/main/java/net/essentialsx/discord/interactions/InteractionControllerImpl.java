package net.essentialsx.discord.interactions;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionController;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.api.v2.services.discord.InteractionException;
import net.essentialsx.discord.EssentialsJDA;
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

public class InteractionControllerImpl extends ListenerAdapter implements InteractionController {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");

    private final EssentialsJDA jda;

    private final String apiCallback = "https://discord.com/api/v8/interactions/{id}/{token}/callback";
    private final String apiRegister;
    private final String apiDelete;
    private final String apiFollowup;

    private final Map<String, InteractionCommand> commandMap = new HashMap<>();
    private final List<String> commandIds = new ArrayList<>();
    private final Map<String, InteractionCommand> batchRegistrationQueue = new HashMap<>();
    private boolean initialBatchRegistration = false;

    public InteractionControllerImpl(EssentialsJDA jda) {
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

                final InteractionMemberImpl member = new InteractionMemberImpl(jda.getGuild().retrieveMemberById(payload.getObject("member").getObject("user").getString("id")).complete());
                final Map<String, Object> args = new HashMap<>();
                if (options != null) {
                    for (Object option : options) {
                        final HashMap<?, ?> obj = (HashMap<?, ?>) option;
                        if (obj.containsKey("name") && obj.containsKey("value") && obj.containsKey("type")) {
                            switch ((Integer) obj.get("type")) {
                                case 6: {
                                    args.put((String) obj.get("name"), new InteractionMemberImpl(jda.getGuild().retrieveMemberById((String) obj.get("value")).complete()));
                                    break;
                                }
                                case 7: {
                                    args.put((String) obj.get("name"), new InteractionChannelImpl(jda.getGuild().getGuildChannelById((String) obj.get("value"))));
                                    break;
                                }
                                default: {
                                    args.put((String) obj.get("name"), obj.get("value"));
                                    break;
                                }
                            }
                        }
                    }
                }
                jda.getPlugin().getEss().scheduleSyncDelayedTask(() -> {
                    final InteractionEvent interactionEvent = new InteractionEvent(member, token, channelId, args, InteractionControllerImpl.this);
                    if (!member.hasRoles(jda.getSettings().getCommandSnowflakes(command.getName()))) {
                        interactionEvent.reply(tl("noAccessCommand"));
                        return;
                    }
                    command.onCommand(interactionEvent);
                });
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
    @Override
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

    @Override
    public InteractionCommand getCommand(String name) {
        return commandMap.get(name);
    }

    public void processBatchRegistration() {
        if (!initialBatchRegistration && !batchRegistrationQueue.isEmpty()) {
            initialBatchRegistration = true;
            final JsonArray commandList = new JsonArray();
            for (final InteractionCommand cmd : batchRegistrationQueue.values()) {
                commandList.add(cmd.serialize());
            }
            put(apiRegister, RequestBody.create(DiscordUtil.JSON_TYPE, commandList.toString())).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        //noinspection ConstantConditions
                        final JsonArray responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonArray.class);
                        if (jda.isDebug()) {
                            logger.info("Registration payload: " + responseObj);
                        }
                        for (final JsonElement e : responseObj) {
                            final JsonObject cmd = e.getAsJsonObject();
                            final String cmdName = cmd.get("name").getAsString();
                            commandMap.put(cmdName, batchRegistrationQueue.get(cmdName));
                            commandIds.add(cmd.get("id").getAsString());
                            batchRegistrationQueue.remove(cmdName);
                            if (jda.isDebug()) {
                                logger.info("Registered guild command: " + cmdName);
                            }
                        }

                        if (!batchRegistrationQueue.isEmpty()) {
                            logger.warning(batchRegistrationQueue.size() + " commands lost to registration!");
                            if (jda.isDebug()) {
                                logger.info("Registration payload: " + responseObj);
                            }
                            batchRegistrationQueue.clear();
                        }
                        return;
                    }

                    if (!batchRegistrationQueue.isEmpty()) {
                        logger.warning(batchRegistrationQueue.size() + " commands lost to registration!");
                        batchRegistrationQueue.clear();
                    }

                    //noinspection ConstantConditions
                    final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                    if (responseObj.has("code") && responseObj.get("code").getAsInt() == 50001) {
                        logger.severe(tl("discordErrorCommand"));
                        if (jda.isDebug()) {
                            logger.info("Registration payload: " + responseObj);
                        }
                        return;
                    }

                    logger.warning("Error while registering command, raw response: " + responseObj);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    logger.severe("Error while registering command: " + e.getMessage());
                    e.printStackTrace();
                    batchRegistrationQueue.clear();
                }
            });
        }
    }

    @Override
    public void registerCommand(InteractionCommand command) throws InteractionException {
        if (command.isDisabled()) {
            throw new InteractionException("The given command has been disabled!");
        }

        if (commandMap.containsKey(command.getName())) {
            throw new InteractionException("A command with that name is already registered!");
        }

        final JsonObject commandJson = command.serialize();
        if (!initialBatchRegistration) {
            if (jda.isDebug()) {
                logger.info("Marked guild command for batch registration: " + command.getName());
            }
            batchRegistrationQueue.put(command.getName(), command);
            return;
        }

        post(apiRegister, RequestBody.create(DiscordUtil.JSON_TYPE, commandJson.toString())).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    commandMap.put(command.getName(), command);
                    //noinspection ConstantConditions
                    final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                    commandIds.add(responseObj.get("id").getAsString());
                    if (jda.isDebug()) {
                        logger.info("Registered guild command: " + command.getName());
                        logger.info("Registration payload: " + responseObj);
                    }
                    return;
                }
                //noinspection ConstantConditions
                final JsonObject responseObj = DiscordUtil.GSON.fromJson(response.body().string(), JsonObject.class);
                if (responseObj.has("code") && responseObj.get("code").getAsInt() == 50001) {
                    logger.severe(tl("discordErrorCommand"));
                    return;
                }

                logger.warning("Error while registering command, raw response: " + responseObj);
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

    private Call put(String url, RequestBody body) {
        return jda.getJda().getHttpClient().newCall(builder(url)
                .put(body)
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
