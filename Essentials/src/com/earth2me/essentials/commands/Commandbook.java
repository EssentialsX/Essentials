package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static com.earth2me.essentials.I18n.tl;


public class Commandbook extends EssentialsCommand {
    public Commandbook() {
        super("book");
    }

    //TODO: Translate this
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack item = user.getBase().getItemInHand();
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
                    ItemStack newItem = new ItemStack(Material.BOOK_AND_QUILL, item.getAmount());
                    newItem.setItemMeta(bmeta);
                    user.getBase().setItemInHand(newItem);
                    user.sendMessage(tl("editBookContents"));
                } else {
                    throw new Exception(tl("denyBookEdit"));
                }
            }
        } else if (item.getType() == Material.BOOK_AND_QUILL) {
            BookMeta bmeta = (BookMeta) item.getItemMeta();
            if (!user.isAuthorized("essentials.book.author")) {
                bmeta.setAuthor(player);
            }
            ItemStack newItem = new ItemStack(Material.WRITTEN_BOOK, item.getAmount());
            newItem.setItemMeta(bmeta);
            user.getBase().setItemInHand(newItem);
            user.sendMessage(tl("bookLocked"));
        } else {
            throw new Exception(tl("holdBook"));
        }
    }

    private boolean isAuthor(BookMeta bmeta, String player) {
        String author = bmeta.getAuthor();
        return author != null && author.equalsIgnoreCase(player);
    }
}
