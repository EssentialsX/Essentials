package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;


public class Commandbook extends EssentialsCommand
{
	public Commandbook()
	{
		super("book");
	}

	//TODO: Translate this
	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{

		ItemStack item = user.getItemInHand();
		String player = user.getName();
		if (item.getType() == Material.WRITTEN_BOOK)
		{
			ItemMeta meta = item.getItemMeta();
			BookMeta bmeta = (BookMeta)meta;
			if (args[0].equalsIgnoreCase("author"))
			{
				if (user.isAuthorized("essentals.book.author"))
				{
					ItemStack newbook = new ItemStack(Material.WRITTEN_BOOK, 1);
					bmeta.setAuthor(args[1]);
					newbook.setItemMeta(bmeta);
					user.setItemInHand(newbook);
					user.sendMessage(_("bookAuthorSet", args[1]));
				}
				else
				{
					user.sendMessage(_("denyChangeAuthor"));
				}
			}
			else if (args[0].equalsIgnoreCase("title"))
			{
				if (user.isAuthorized("essentials.book.title"))
				{

					if (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.title.others"))
					{
						ItemStack newbook = new ItemStack(Material.WRITTEN_BOOK, 1);
						bmeta.setTitle(args[1]);
						newbook.setItemMeta(bmeta);
						user.setItemInHand(newbook);
						user.sendMessage(_("bookTitleSet", args[1]));
					}
					else
					{
						user.sendMessage(_("denyChangeTitle"));
					}
				}
				else
				{
					user.sendMessage(_("denyChangeTitle"));
				}
			}
			else
			{
				if (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others"))
				{
					ItemStack newItem = new ItemStack(Material.BOOK_AND_QUILL, item.getAmount());
					newItem.setItemMeta(meta);
					user.setItemInHand(newItem);
					user.sendMessage(_("editBookContents"));
				}
				else
				{
					user.sendMessage(_("denyBookEdit"));
				}
			}
		}
		else if (item.getType() == Material.BOOK_AND_QUILL)
		{
			ItemMeta meta = item.getItemMeta();
			BookMeta bmeta = (BookMeta)meta;
			bmeta.setAuthor(player);
			ItemStack newItem = new ItemStack(Material.WRITTEN_BOOK, item.getAmount());
			newItem.setItemMeta(bmeta);
			user.setItemInHand(newItem);
			user.sendMessage(_("bookLocked"));
		}
		else
		{
			throw new Exception(_("holdBook"));
		}
	}

	private boolean isAuthor(BookMeta bmeta, String player)
	{
		if (bmeta.getAuthor().equalsIgnoreCase(player))
		{
			return true;
		}
		return false;
	}
}