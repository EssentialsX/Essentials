package com.earth2me.essentials.commands;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.earth2me.essentials.I18n.tl;

public class Commandcondense extends EssentialsCommand {
    private final Map<ItemStack, SimpleRecipe> condenseList = new HashMap<>();

    public Commandcondense() {
        super("condense");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        List<ItemStack> is = new ArrayList<>();

        boolean validateReverse = false;
        if (args.length > 0) {
            is = ess.getItemDb().getMatching(user, args);
        } else {
            for (final ItemStack stack : Inventories.getInventory(user.getBase(), false)) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack);
            }
            validateReverse = true;
        }

        boolean didConvert = false;
        for (final ItemStack itemStack : is) {
            if (condenseStack(user, itemStack, validateReverse)) {
                didConvert = true;
            }
        }
        user.getBase().updateInventory();

        if (didConvert) {
            user.sendMessage(tl("itemsConverted"));
        } else {
            user.sendMessage(tl("itemsNotConverted"));
            throw new NoChargeException();
        }
    }

    private boolean condenseStack(final User user, final ItemStack stack, final boolean validateReverse) throws ChargeException, MaxMoneyException {
        final SimpleRecipe condenseType = getCondenseType(stack);
        if (condenseType != null) {
            final ItemStack input = condenseType.getInput();
            final ItemStack result = condenseType.getResult();

            if (validateReverse) {
                boolean pass = false;
                for (final Recipe revRecipe : ess.getServer().getRecipesFor(input)) {
                    if (getStackOnRecipeMatch(revRecipe, result) != null) {
                        pass = true;
                        break;
                    }
                }
                if (!pass) {
                    return false;
                }
            }

            int amount = 0;

            for (final ItemStack contents : Inventories.getInventory(user.getBase(), false)) {
                if (contents != null && contents.isSimilar(stack)) {
                    amount += contents.getAmount();
                }
            }

            final int output = (amount / input.getAmount()) * result.getAmount();
            amount -= amount % input.getAmount();

            if (amount > 0) {
                input.setAmount(amount);
                result.setAmount(output);
                final Trade remove = new Trade(input, ess);
                final Trade add = new Trade(result, ess);
                remove.charge(user);
                add.pay(user, OverflowType.DROP);
                return true;
            }
        }
        return false;
    }

    private SimpleRecipe getCondenseType(final ItemStack stack) {
        if (condenseList.containsKey(stack)) {
            return condenseList.get(stack);
        }

        final Iterator<Recipe> intr = ess.getServer().recipeIterator();
        final List<SimpleRecipe> bestRecipes = new ArrayList<>();
        while (intr.hasNext()) {
            final Recipe recipe = intr.next();
            final Collection<ItemStack> recipeItems = getStackOnRecipeMatch(recipe, stack);

            if (recipeItems != null && (recipeItems.size() == 4 || recipeItems.size() == 9) && (recipeItems.size() > recipe.getResult().getAmount())) {
                final ItemStack input = stack.clone();
                input.setAmount(recipeItems.size());
                final SimpleRecipe newRecipe = new SimpleRecipe(recipe.getResult(), input);
                bestRecipes.add(newRecipe);
            }
        }
        if (!bestRecipes.isEmpty()) {
            if (bestRecipes.size() > 1) {
                bestRecipes.sort(SimpleRecipeComparator.INSTANCE);
            }
            final SimpleRecipe recipe = bestRecipes.get(0);
            condenseList.put(stack, recipe);
            return recipe;
        }
        condenseList.put(stack, null);
        return null;
    }

    private Collection<ItemStack> getStackOnRecipeMatch(final Recipe recipe, final ItemStack stack) {
        final Collection<ItemStack> inputList;

        if (recipe instanceof ShapedRecipe) {
            final ShapedRecipe sRecipe = (ShapedRecipe) recipe;
            if (sRecipe.getShape().length != sRecipe.getShape()[0].length()) {
                // Only accept square recipes
                return null;
            }
            inputList = sRecipe.getIngredientMap().values();
        } else if (recipe instanceof ShapelessRecipe) {
            final ShapelessRecipe slRecipe = (ShapelessRecipe) recipe;
            inputList = slRecipe.getIngredientList();
        } else {
            return null;
        }

        boolean match = true;
        final Iterator<ItemStack> iter = inputList.iterator();
        while (iter.hasNext()) {
            final ItemStack inputSlot = iter.next();
            if (inputSlot == null) {
                iter.remove();
                continue;
            }
            if (VersionUtil.PRE_FLATTENING && inputSlot.getDurability() == Short.MAX_VALUE) {
                inputSlot.setDurability((short) 0);
            }
            if (!inputSlot.isSimilar(stack)) {
                match = false;
            }
        }

        if (match) {
            return inputList;
        }
        return null;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getMatchingItems(args[0]);
        } else {
            return Collections.emptyList();
        }
    }

    private static final class SimpleRecipe implements Recipe {
        private final ItemStack result;
        private final ItemStack input;

        private SimpleRecipe(final ItemStack result, final ItemStack input) {
            this.result = result;
            this.input = input;
        }

        @Override
        public ItemStack getResult() {
            return result.clone();
        }

        public ItemStack getInput() {
            return input.clone();
        }
    }

    private static class SimpleRecipeComparator implements Comparator<SimpleRecipe> {

        private static final SimpleRecipeComparator INSTANCE = new SimpleRecipeComparator();

        @Override
        public int compare(final SimpleRecipe o1, final SimpleRecipe o2) {
            return Integer.compare(o2.getInput().getAmount(), o1.getInput().getAmount());
        }
    }
}
