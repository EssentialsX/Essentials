package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.earth2me.essentials.I18n.tl;

public class Commandcreatekit extends EssentialsCommand {

    private static final String PASTE_URL = "https://hastebin.com/";
    private static final String PASTE_UPLOAD_URL = PASTE_URL + "documents";
    private static final Gson GSON = new Gson();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final YamlConstructor yamlConstructor = new YamlConstructor();

    public Commandcreatekit() {
        super("createkit");
        yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
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
        for (final ItemStack is : items) {
            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                final String serialized = ess.getItemDb().serialize(is);
                list.add(serialized);
            }
        }
        // Some users might want to directly write to config knowing the consequences. *shrug*
        if (!ess.getSettings().isPastebinCreateKit()) {
            ess.getKits().addKit(kitname, list, delay);
            user.sendMessage(tl("createdKit", kitname, list.size(), delay));
        } else {
            final ConfigurationSection config = new MemoryConfiguration();
            config.set("kits." + kitname + ".delay", delay);
            config.set("kits." + kitname + ".items", list);

            final Yaml yaml = new Yaml(yamlConstructor, yamlRepresenter, yamlOptions);
            String fileContents = "# Copy the kit code below into the kits section in your config.yml file\n";
            fileContents += yaml.dump(config.getValues(false));

            uploadPaste(user.getSource(), kitname, delay, fileContents);
        }
    }

    private void uploadPaste(final CommandSource sender, final String kitName, final long delay, final String contents) {
        executorService.submit(() -> {
            try {
                final HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_UPLOAD_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "EssentialsX plugin");
                try (final OutputStream os = connection.getOutputStream()) {
                    os.write(contents.getBytes(Charsets.UTF_8));
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
                final String pasteUrl = PASTE_URL + object.get("key").getAsString();
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
