package net.essentialsx.discord.interactions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

public class InteractionController {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final EssentialsJDA jda;
    private final String apiBase;
    private final Gson gson = new Gson();

    public InteractionController(EssentialsJDA jda) {
        this.jda = jda;
        this.apiBase = "https://discord.com/api/v8/applications/" + jda.getJda().getSelfUser().getId() + "/guilds/" + jda.getGuild().getId() + "/";
    }

    public void registerCommand(InteractionCommand command) {
        final String commandJson = command.serialize().toString();
        final Request request = new Request.Builder()
                .url(apiBase + "commands")
                .header("Authorization", jda.getJda().getToken())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(JSON, commandJson))
                .build();
        jda.getJda().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
                    logger.info("Registered guild command: " + jsonObject.get("name").getAsString() + " (with id " + jsonObject.get("id").getAsString() + ")");
                    response.close();
                    return;
                }
                logger.info("Error while registering command, raw response: " + response.body().string());
                response.close();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.severe("Error while registering command: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {

    }
}
