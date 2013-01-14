package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;


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
		final ItemStack item = user.getItemInHand();
		final String player = user.getName();
		if (item.getType() == Material.WRITTEN_BOOK)
		{
			BookMeta bmeta = (BookMeta)item.getItemMeta();

			if (args[0].equalsIgnoreCase("author"))
			{
				if (user.isAuthorized("essentals.book.author"))
				{
					bmeta.setAuthor(args[1]);
					item.setItemMeta(bmeta);
					user.sendMessage(_("bookAuthorSet", args[1]));
				}
				else
				{
					throw new Exception(_("denyChangeAuthor"));
				}
			}
			else if (args[0].equalsIgnoreCase("title"))
			{
				if (user.isAuthorized("essentials.book.title") && (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others")))
				{
					bmeta.setTitle(args[1]);
					item.setItemMeta(bmeta);
					user.sendMessage(_("bookTitleSet", args[1]));
				}
				else
				{
					throw new Exception(_("denyChangeTitle"));
				}
			}
			else
			{
				if (isAuthor(bmeta, player) || user.isAuthorized("essentials.book.others"))
				{
					ItemStack newItem = new ItemStack(Material.BOOK_AND_QUILL, item.getAmount());
					newItem.setItemMeta(bmeta);
					user.setItemInHand(newItem);
					user.sendMessage(_("editBookContents"));
				}
				else
				{
					throw new Exception(_("denyBookEdit"));
				}
			}
		}
		else if (item.getType() == Material.BOOK_AND_QUILL)
		{
			BookMeta bmeta = (BookMeta)item.getItemMeta();
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
		return bmeta.getAuthor().equalsIgnoreCase(player);
	}
}