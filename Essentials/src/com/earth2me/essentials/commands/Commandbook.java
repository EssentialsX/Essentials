package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
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
		if (item.getType() == Material.WRITTEN_BOOK)
		{
			ItemMeta meta = item.getItemMeta();
			ItemStack newItem = new ItemStack(Material.BOOK_AND_QUILL, item.getAmount());
			newItem.setItemMeta(meta);
			user.setItemInHand(newItem);
			user.sendMessage("You can now edit the contents of this book.");
		}
		else if (item.getType() == Material.BOOK_AND_QUILL)
		{
			ItemMeta meta = item.getItemMeta();
			ItemStack newItem = new ItemStack(Material.WRITTEN_BOOK, item.getAmount());
			newItem.setItemMeta(meta);
			user.setItemInHand(newItem);
			user.sendMessage("This book is now locked and signed.");
		}
		else
		{
			throw new Exception("You are not holding a writable book.");
		}
	}
}