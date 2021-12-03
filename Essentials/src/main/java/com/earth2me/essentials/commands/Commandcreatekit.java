package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.PasteUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandcreatekit extends EssentialsCommand {
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
        ess.runTaskAsynchronously(() -> {
            try {
                final StringWriter sw = new StringWriter();
                final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).indent(2).nodeStyle(NodeStyle.BLOCK).build();

                final ConfigurationNode config = loader.createNode();
                config.node("kits", kitName, "delay").set(delay);
                config.node("kits", kitName, "items").set(list);

                sw.append("# Copy the kit code below into the kits section in your config.yml file\n");
                loader.save(config);

                final String fileContents = sw.toString();

                final CompletableFuture<PasteUtil.PasteResult> future = PasteUtil.createPaste(Collections.singletonList(new PasteUtil.PasteFile("kit_" + kitName + ".yml", fileContents)));
                future.thenAccept(result -> {
                    if (result != null) {
                        final String separator = tl("createKitSeparator");
                        final String delayFormat = delay <= 0 ? "0" : DateUtil.formatDateDiff(System.currentTimeMillis() + (delay * 1000));
                        sender.sendMessage(separator);
                        sender.sendMessage(tl("createKitSuccess", kitName, delayFormat, result.getPasteUrl()));
                        sender.sendMessage(separator);
                        if (ess.getSettings().isDebug()) {
                            ess.getLogger().info(sender.getSender().getName() + " created a kit: " + result.getPasteUrl());
                        }
                    }
                });
                future.exceptionally(throwable -> {
                    sender.sendMessage(tl("createKitFailed", kitName));
                    ess.getLogger().log(Level.SEVERE, "Error creating kit: ", throwable);
                    return null;
                });
            } catch (Exception e) {
                sender.sendMessage(tl("createKitFailed", kitName));
                ess.getLogger().log(Level.SEVERE, "Error creating kit: ", e);
            }
        });
    }
}
