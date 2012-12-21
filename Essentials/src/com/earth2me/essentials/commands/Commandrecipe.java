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
		
		final ItemStack itemType = ess.getItemDb().get(args[0]);
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
		
		final List<Recipe> recipesOfType = ess.getServer().getRecipesFor(itemType);
		if (recipesOfType.size() < 1)
		{
			throw new Exception(_("recipeNone", getMaterialName(itemType)));
		}
		
		if (recipeNo < 0 || recipeNo >= recipesOfType.size())
		{
			throw new Exception(_("recipeBadIndex"));
		}
		
		final Recipe selectedRecipe = recipesOfType.get(recipeNo);
		sender.sendMessage(_("recipe", getMaterialName(itemType), recipeNo + 1, recipesOfType.size()));
		
		if (selectedRecipe instanceof FurnaceRecipe)
		{
			furnaceRecipe(sender, (FurnaceRecipe)selectedRecipe);
		}
		else if (selectedRecipe instanceof ShapedRecipe)
		{
			shapedRecipe(sender, (ShapedRecipe)selectedRecipe);
		}
		else if (selectedRecipe instanceof ShapelessRecipe)
		{
			shapelessRecipe(sender, (ShapelessRecipe)selectedRecipe);
		}
		
		if (recipesOfType.size() > 1 && args.length == 1)
		{
			sender.sendMessage(_("recipeMore", commandLabel, args[0], getMaterialName(itemType)));
		}
	}

	public void furnaceRecipe(final CommandSender sender, final FurnaceRecipe recipe)
	{
		sender.sendMessage(_("recipeFurnace", getMaterialName(recipe.getInput())));
	}

	public void shapedRecipe(final CommandSender sender, final ShapedRecipe recipe)
	{
		final Map<Character, ItemStack> recipeMap = recipe.getIngredientMap();
		
		if (sender instanceof Player)
		{
			final User user = ess.getUser(sender);
			user.setRecipeSee(true);
			final InventoryView view = user.openWorkbench(null, true);
			final String shapeMap = recipe.getShape().length == 2 ? " abecdfghi" : " abcdefghi";
			for (Entry<Character, ItemStack> e : ((ShapedRecipe)recipe).getIngredientMap().entrySet())
			{				
				e.getValue().setAmount(0);
				view.setItem(shapeMap.indexOf(e.getKey()), e.getValue());
			}

		}
		else
		{
			final HashMap<Material, String> colorMap = new HashMap<Material, String>();
			int i = 1;
			for (Character c : "abcdefghi".toCharArray())
			{
				ItemStack item = recipeMap.get(c);
				if (!colorMap.containsKey(item == null ? null : item.getType()))
				{
					colorMap.put(item == null ? null : item.getType(), String.valueOf(i++));
				}
			}
			final Material[][] materials = new Material[3][3];
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
	}

	public void shapelessRecipe(final CommandSender sender, final ShapelessRecipe recipe)
	{
		final List<ItemStack> ingredients = recipe.getIngredientList();
		if (sender instanceof Player)
		{
			final User user = ess.getUser(sender);
			user.setRecipeSee(true);
			final InventoryView view = user.openWorkbench(null, true);
			for (int i = 0; i < ingredients.size(); i++)
			{
				view.setItem(i + 1, ingredients.get(i));
			}

		}
		else
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
	}

	public String getMaterialName(final ItemStack stack)
	{
		if (stack == null)
		{
			return _("recipeNothing");
		}
		return getMaterialName(stack.getType());
	}

	public String getMaterialName(final Material type)
	{
		if (type == null)
		{
			return _("recipeNothing");
		}
		return type.toString().replace("_", " ").toLowerCase(Locale.ENGLISH);
	}
}