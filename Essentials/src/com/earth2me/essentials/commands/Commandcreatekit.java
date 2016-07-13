package com.earth2me.essentials.commands;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;

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
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.earth2me.essentials.I18n.tl;

public class Commandcreatekit extends EssentialsCommand {

    private static final String PASTE_URL = "https://api.github.com/gists";
    private static final String SHORTENER_URL = "https://git.io";
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
        long delay = Long.valueOf(args[1]);
        String kitname = args[0];
        ItemStack[] items = user.getBase().getInventory().getContents();
        List<String> list = new ArrayList<>();
        for (ItemStack is : items) {
            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                String serialized = ess.getItemDb().serialize(is);
                list.add(serialized);
            }
        }
        // Some users might want to directly write to config knowing the consequences. *shrug*
        if (!ess.getSettings().isPastebinCreateKit()) {
            ess.getSettings().addKit(kitname, list, delay);
            user.sendMessage(tl("createdKit", kitname, list.size(), delay));
        } else {
            ConfigurationSection config = new MemoryConfiguration();
            config.set("kits." + kitname + ".delay", delay);
            config.set("kits." + kitname + ".items", list);

            final Yaml yaml = new Yaml(yamlConstructor, yamlRepresenter, yamlOptions);
            String fileContents = "# Copy the kit code below into the kits section in your config.yml file\n";
            fileContents += yaml.dump(config.getValues(false));

            gist(user.getSource(), kitname, delay, fileContents);
        }
    }

    /**
     * SEE https://developer.github.com/v3/gists/#create-a-gist
     */
    private void gist(final CommandSource sender, final String kitName, final long delay, final String contents) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_URL).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        StringWriter sw = new StringWriter();
                        new JsonWriter(sw).beginObject()
                            .name("description").value(sender.getSender().getName() + ": /createkit " + kitName)
                            .name("public").value(false)
                            .name("files")
                                .beginObject().name("kit.yml")
                                    .beginObject().name("content").value(contents)
                                    .endObject()
                                .endObject()
                            .endObject();
                        os.write(sw.toString().getBytes());
                    }
                    // Error
                    if (connection.getResponseCode() >= 400) {
                        sender.sendMessage(tl("createKitFailed", kitName));
                        String message = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
                        ess.getLogger().severe("Error creating kit: " + message);
                        return;
                    }

                    // Read URl
                    Map<String, String> map = GSON.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8),
                        new TypeToken<Map<String, Object>>() {}.getType());
                    String pasteUrl = map.get("html_url");
                    connection.disconnect();
                    
                    /* ================================
                     * >> Shorten URL to fit in chat
                     * ================================ */
                    {
                        connection = (HttpURLConnection) new URL(SHORTENER_URL).openConnection();
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setDoOutput(true);
                        pasteUrl = "url=" + pasteUrl;
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(pasteUrl.getBytes());
                        }
                        pasteUrl = connection.getHeaderField("Location");
                    }

                    String separator = tl("createKitSeparator");
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
                } catch (Exception e) {
                    sender.sendMessage(tl("createKitFailed", kitName));
                    e.printStackTrace();
                }
            }
        });
    }
}
