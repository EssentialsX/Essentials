package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandbook extends EssentialsCommand {
    public Commandbook() {
        super("book");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack item = user.getItemInHand();
        final String player = user.getName();
        if (item.getType() == Material.WRITTEN_BOOK) {
            BookMeta bmeta = (BookMeta) item.getItemMeta();

            if (args.length > 1 && args[0].equalsIgnoreCase("author")) {
                if (user.isAuthorized("essentials.book.author") && (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others"))) {
                    bmeta.setAuthor(args[1]);
                    item.setItemMeta(bmeta);
                    user.sendMessage(tl("bookAuthorSet", getFinalArg(args, 1)));
                } else {
                    throw new Exception(tl("denyChangeAuthor"));
                }
            } else if (args.length > 1 && args[0].equalsIgnoreCase("title")) {
                if (user.isAuthorized("essentials.book.title") && (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others"))) {
                    bmeta.setTitle(args[1]);
                    item.setItemMeta(bmeta);
                    user.sendMessage(tl("bookTitleSet", getFinalArg(args, 1)));
                } else {
                    throw new Exception(tl("denyChangeTitle"));
                }
            } else {
                if (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others")) {
                    ItemStack newItem = new ItemStack(Material.WRITABLE_BOOK, item.getAmount());
                    newItem.setItemMeta(bmeta);
                    InventoryWorkaround.setItemInMainHand(user.getBase(), newItem);
                    user.sendMessage(tl("editBookContents"));
                } else {
                    throw new Exception(tl("denyBookEdit"));
                }
            }
        } else if (item.getType() == Material.WRITABLE_BOOK) {
            BookMeta bmeta = (BookMeta) item.getItemMeta();
            if (!user.isAuthorized("essentials.book.author")) {
                bmeta.setAuthor(player);
            }
            ItemStack newItem = new ItemStack(Material.WRITTEN_BOOK, item.getAmount());
            newItem.setItemMeta(bmeta);
            InventoryWorkaround.setItemInMainHand(user.getBase(), newItem);
            user.sendMessage(tl("bookLocked"));
        } else {
            throw new Exception(tl("holdBook"));
        }
    }

    private boolean isAuthor(BookMeta bmeta, String player) {
        String author = bmeta.getAuthor();
        return author != null && author.equalsIgnoreCase(player);
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        // Right now, we aren't testing what's held in the player's hand - we could, but it's not necessarily worth it
        if (args.length == 1) {
            List<String> options = Lists.newArrayList("sign", "unsign");  // sign and unsign aren't real, but work
            if (user.isAuthorized("essentials.book.author")) {
                options.add("author");
            }
            if (user.isAuthorized("essentials.book.title")) {
                options.add("title");
            }
            return options;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("author") && user.isAuthorized("essentials.book.author")) {
            List<String> options = getPlayers(server, user);
            options.add("Herobrine"); // #EasterEgg
            return options;
        } else {
            return Collections.emptyList();
        }
    }
}
