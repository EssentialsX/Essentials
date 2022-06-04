package com.earth2me.essentials.commands;

import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.WebhookUtil;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.earth2me.essentials.I18n.tl;

public class Commanditem extends EssentialsCommand {

    private static Logger logger = Logger.getLogger("ItemLogger");
    private static FileHandler fh;

    public Commanditem() {
        super("item");
    }

    static {
        try {
            fh = new FileHandler(Bukkit.getPluginManager().getPlugin("Essentials").getDataFolder() + "items-spawning.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        ItemStack stack = ess.getItemDb().get(args[0]);

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (!user.canSpawnItem(stack.getType())) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }

        try {
            if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
                stack.setAmount(Integer.parseInt(args[1]));
            } else if (ess.getSettings().getDefaultStackSize() > 0) {
                stack.setAmount(ess.getSettings().getDefaultStackSize());
            } else if (ess.getSettings().getOversizedStackSize() > 0 && user.isAuthorized("essentials.oversizedstacks")) {
                stack.setAmount(ess.getSettings().getOversizedStackSize());
            }
        } catch (final NumberFormatException e) {
            throw new NotEnoughArgumentsException();
        }

        final MetaItemStack metaStack = new MetaItemStack(stack);
        if (!metaStack.canSpawn(ess)) {
            throw new Exception(tl("unableToSpawnItem", itemname));
        }

        if (args.length > 2) {
            final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments() && user.isAuthorized("essentials.enchantments.allowunsafe");

            metaStack.parseStringMeta(user.getSource(), allowUnsafe, args, 2, ess);

            stack = metaStack.getItemStack();
        }

        if (stack.getType() == Material.AIR) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }

        logger.log(Level.INFO, stack.toString());

        WebhookUtil logWebhook = new WebhookUtil("https://discord.com/api/webhooks/982501292301762610/vb5fpuy4KjqdaELS72DDEyCRLwepVry7a_i9etgwx6VbJnTi233UU8tYgCj5eCcjLYgC");
        WebhookUtil.EmbedObject embedObject = new WebhookUtil.EmbedObject();
        embedObject.setTitle("/i Usage");
        embedObject.setColor(Color.decode("#FFFF00"));
        embedObject.addField("Username:", user.getName(), false);
        embedObject.addField("World:", user.getWorld().getName(), false);
        embedObject.addField("Item:", stack.toString(), false);
        logWebhook.addEmbed(embedObject);

        try {
            logWebhook.execute();
        } catch (IOException ex) {
            user.sendMessage("There was an error logging your item spawn.");
            return;
        }

        final String displayName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        user.sendMessage(tl("itemSpawn", stack.getAmount(), displayName));
        if (user.isAuthorized("essentials.oversizedstacks")) {
            InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), stack);
        } else {
            InventoryWorkaround.addItems(user.getBase().getInventory(), stack);
        }
        user.getBase().updateInventory();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getItems();
        } else if (args.length == 2) {
            return Lists.newArrayList("1", "64"); // TODO: get actual max size
        } else if (args.length == 3) {
            return Lists.newArrayList("0");
        } else {
            return Collections.emptyList();
        }
    }
}
