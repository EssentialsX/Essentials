package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.earth2me.essentials.I18n.tl;

public class Commandcreatekit extends EssentialsCommand {
    private static final String PASTE_URL = "https://paste.gg/";
    private static final String PASTE_UPLOAD_URL = "https://api.paste.gg/v1/pastes";
    private static final Gson GSON = new Gson();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Commandcreatekit() {
        super("createkit");
    }

    // /createkit <name> <delay>
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 2) {
            throw new NotEnoughArgumentsException();
        }

        // Command handler will auto fail if this fails.
        final long delay = Long.parseLong(args[1]);
        final String kitname = args[0];
        final ItemStack[] items = user.getBase().getInventory().getContents();
        final List<String> list = new ArrayList<>();

        boolean useSerializationProvider = ess.getSettings().isUseBetterKits();

        if (useSerializationProvider && ess.getSerializationProvider() == null) {
            ess.showError(user.getSource(), new Exception(tl("createKitUnsupported")), commandLabel);
            useSerializationProvider = false;
        }

        for (ItemStack is : items) {
            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                final String serialized;
                if (useSerializationProvider) {
                    serialized = "@" + Base64Coder.encodeLines(ess.getSerializationProvider().serializeItem(is));
                } else {
                    serialized = ess.getItemDb().serialize(is);
                }
                list.add(serialized);
            }
        }
        // Some users might want to directly write to config knowing the consequences. *shrug*
        if (!ess.getSettings().isPastebinCreateKit()) {
            ess.getKits().addKit(kitname, list, delay);
            user.sendMessage(tl("createdKit", kitname, list.size(), delay));
        } else {
            uploadPaste(user.getSource(), kitname, delay, list);
        }
    }

    private void uploadPaste(final CommandSource sender, final String kitName, final long delay, final List<String> list) {
        executorService.submit(() -> {
            try {
                final StringWriter sw = new StringWriter();
                final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).indent(2).nodeStyle(NodeStyle.BLOCK).build();

                final ConfigurationNode config = loader.createNode();
                config.node("kits", kitName, "delay").set(delay);
                config.node("kits", kitName, "items").set(list);

                sw.append("# Copy the kit code below into the kits section in your config.yml file\n");
                loader.save(config);

                final String fileContents = sw.toString();

                final HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_UPLOAD_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "EssentialsX plugin");
                connection.setRequestProperty("Content-Type", "application/json");
                final JsonObject body = new JsonObject();
                final JsonArray files = new JsonArray();
                final JsonObject file = new JsonObject();
                final JsonObject content = new JsonObject();
                content.addProperty("format", "text");
                content.addProperty("value", fileContents);
                file.add("content", content);
                files.add(file);
                body.add("files", files);

                try (final OutputStream os = connection.getOutputStream()) {
                    os.write(body.toString().getBytes(Charsets.UTF_8));
                }
                // Error
                if (connection.getResponseCode() >= 400) {
                    sender.sendMessage(tl("createKitFailed", kitName));
                    final String message = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
                    ess.getLogger().severe("Error creating kit: " + message);
                    return;
                }

                // Read URL
                final JsonObject object = GSON.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8), JsonObject.class);
                final String pasteUrl = PASTE_URL + object.get("result").getAsJsonObject().get("id").getAsString();
                connection.disconnect();

                final String separator = tl("createKitSeparator");
                String delayFormat = "0";
                if (delay > 0) {
                    delayFormat = DateUtil.formatDateDiff(System.currentTimeMillis() + (delay * 1000));
                }
                sender.sendMessage(separator);
                sender.sendMessage(tl("createKitSuccess", kitName, delayFormat, pasteUrl));
                sender.sendMessage(separator);
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().info(sender.getSender().getName() + " created a kit: " + pasteUrl);
                }
            } catch (final Exception e) {
                sender.sendMessage(tl("createKitFailed", kitName));
                e.printStackTrace();
            }
        });
    }
}
