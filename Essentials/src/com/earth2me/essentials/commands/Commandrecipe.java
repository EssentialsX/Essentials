package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;


public class Commandrecipe extends EssentialsCommand
{
	public Commandrecipe()
	{
		super("recipe");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final ItemStack item = ess.getItemDb().get(args[0]);
		final List<Recipe> recipes = ess.getServer().getRecipesFor(item);
		if (recipes.size() < 1)
		{
			throw new Exception(_("recipeNone", getMaterialName(item)));
		}
		int recipeNo = 0;
		if (args.length > 1)
		{
			if (Util.isInt(args[1]))
			{
				recipeNo = Integer.parseInt(args[1]) - 1;
			}
			else
			{
				throw new Exception(_("invalidNumber"));
			}
		}
		if (recipeNo < 0 || recipeNo >= recipes.size())
		{
			throw new Exception(_("recipeBadIndex"));
		}
		final Recipe recipe = recipes.get(recipeNo);
		sender.sendMessage(_("recipe", getMaterialName(item), recipeNo + 1, recipes.size()));
		if (recipe instanceof FurnaceRecipe)
		{
			furnaceRecipe(sender, (FurnaceRecipe)recipe);
		}
		else if (recipe instanceof ShapedRecipe)
		{
			shapedRecipe(sender, (ShapedRecipe)recipe);
		}
		else if (recipe instanceof ShapelessRecipe)
		{
			shapelessRecipe(sender, (ShapelessRecipe)recipe);
		}
		if (recipes.size() > 1 && args.length == 1)
		{
			sender.sendMessage(_("recipeMore", commandLabel, args[0], getMaterialName(item)));
		}
	}

	public void furnaceRecipe(CommandSender sender, FurnaceRecipe recipe)
	{
		sender.sendMessage(_("recipeFurnace", getMaterialName(recipe.getInput())));
	}

	public void shapedRecipe(CommandSender sender, ShapedRecipe recipe)
	{
		Map<Character, ItemStack> recipeMap = recipe.getIngredientMap();
		if (!(sender instanceof Player))
		{
			HashMap<Material, String> colorMap = new HashMap<Material, String>();
			int i = 1;
			for (Character c : "abcdefghi".toCharArray())
			{
				ItemStack item = recipeMap.get(c);
				if (!colorMap.containsKey(item == null ? null : item.getType()))
				{
					colorMap.put(item == null ? null : item.getType(), String.valueOf(i++));
				}
			}
			Material[][] materials = new Material[3][3];
			for (int j = 0; j < recipe.getShape().length; j++)
			{
				for (int k = 0; k < recipe.getShape()[j].length(); k++)
				{
					ItemStack item = recipe.getIngredientMap().get(recipe.getShape()[j].toCharArray()[k]);
					materials[j][k] = item == null ? null : item.getType();
				}
			}
			sender.sendMessage(_("recipeGrid", colorMap.get(materials[0][0]), colorMap.get(materials[0][1]), colorMap.get(materials[0][2])));
			sender.sendMessage(_("recipeGrid", colorMap.get(materials[1][0]), colorMap.get(materials[1][1]), colorMap.get(materials[1][2])));
			sender.sendMessage(_("recipeGrid", colorMap.get(materials[2][0]), colorMap.get(materials[2][1]), colorMap.get(materials[2][2])));

			StringBuilder s = new StringBuilder();
			for (Material items : colorMap.keySet().toArray(new Material[colorMap.size()]))
			{
				s.append(_("recipeGridItem", colorMap.get(items), getMaterialName(items)));
			}
			sender.sendMessage(_("recipeWhere", s.toString()));
		}
		else
		{
			User user = ess.getUser(sender);
			user.setRecipeSee(true);
			InventoryView view = user.openWorkbench(null, true);
			for (Entry<Character, ItemStack> e : ((ShapedRecipe)recipe).getIngredientMap().entrySet())
			{
				view.setItem(" abcdefghi".indexOf(e.getKey()), e.getValue());
			}
		}
	}

	public void shapelessRecipe(CommandSender sender, ShapelessRecipe recipe)
	{
		List<ItemStack> ingredients = recipe.getIngredientList();
		if (!(sender instanceof Player))
		{
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < ingredients.size(); i++)
			{
				s.append(getMaterialName(ingredients.get(i)));
				if (i != ingredients.size() - 1)
				{
					s.append(",");
				}
				s.append(" ");
			}
			sender.sendMessage(_("recipeShapeless", s.toString()));
		}
		else
		{
			User user = ess.getUser(sender);
			user.setRecipeSee(true);
			InventoryView view = user.openWorkbench(null, true);
			for (int i = 0; i < ingredients.size(); i++)
			{
				view.setItem(i + 1, ingredients.get(i));
			}
		}
	}

	public String getMaterialName(ItemStack stack)
	{
		if (stack == null)
		{
			return _("recipeNothing");
		}
		return getMaterialName(stack.getType());
	}

	public String getMaterialName(Material type)
	{
		if (type == null)
		{
			return _("recipeNothing");
		}
		return type.toString().replace("_", " ").toLowerCase(Locale.ENGLISH);
	}
}