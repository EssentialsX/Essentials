package com.earth2me.essentials;

import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.KitClaimEvent;
import net.essentialsx.api.v2.events.KitPreExpandItemsEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Kit {
    final IEssentials ess;
    final String kitName;
    final Map<String, Object> kit;
    final Trade charge;

    public Kit(final String kitName, final IEssentials ess) throws Exception {
        this.kitName = kitName;
        this.ess = ess;
        this.kit = ess.getKits().getKit(kitName);
        this.charge = new Trade("kit-" + kitName, new Trade("kit-kit", ess), ess);

        if (kit == null) {
            throw new TranslatableException("kitNotFound");
        }
    }

    public String getName() {
        return kitName;
    }

    public void checkPerms(final User user) throws Exception {
        if (!user.isAuthorized("essentials.kits." + kitName)) {
            throw new TranslatableException("noKitPermission", "essentials.kits." + kitName);
        }
    }

    public void checkDelay(final User user) throws Exception {
        final long nextUse = getNextUse(user);

        if (nextUse == 0L) {
        } else if (nextUse < 0L) {
            user.sendTl("kitOnce");
            throw new NoChargeException();
        } else {
            user.sendTl("kitTimed", DateUtil.formatDateDiff(nextUse));
            throw new NoChargeException();
        }
    }

    public void checkAffordable(final User user) throws Exception {
        charge.isAffordableFor(user);
    }

    public void setTime(final User user) throws Exception {
        final Calendar time = new GregorianCalendar();
        user.setKitTimestamp(kitName, time.getTimeInMillis());
    }

    public void resetTime(final User user) {
        user.setKitTimestamp(kitName, 0);
    }

    public void chargeUser(final User user) throws Exception {
        charge.charge(user);
    }

    public long getNextUse(final User user) throws Exception {
        if (user.isAuthorized("essentials.kit.exemptdelay")) {
            return 0L;
        }

        final Calendar time = new GregorianCalendar();

        double delay = 0;
        try {
            // Make sure delay is valid
            delay = kit.containsKey("delay") ? ((Number) kit.get("delay")).doubleValue() : 0.0d;
        } catch (final Exception e) {
            throw new TranslatableException("kitError2");
        }

        // When was the last kit used?
        final long lastTime = user.getKitTimestamp(kitName);

        // When can be use the kit again?
        final Calendar delayTime = new GregorianCalendar();
        delayTime.setTimeInMillis(lastTime);
        delayTime.add(Calendar.SECOND, (int) delay);
        delayTime.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));

        if (lastTime == 0L || lastTime > time.getTimeInMillis()) {
            // If we have no record of kit use, or its corrupted, give them benefit of the doubt.
            return 0L;
        } else if (delay < 0d) {
            // If the kit has a negative kit time, it can only be used once.
            return -1;
        } else if (delayTime.before(time)) {
            // If the kit was used in the past, but outside the delay time, it can be used.
            return 0L;
        } else {
            // If the kit has been used recently, return the next time it can be used.
            return delayTime.getTimeInMillis();
        }
    }

    @Deprecated
    public List<String> getItems(final User user) throws Exception {
        return getBasicItems();
    }

    public List<String> getBasicItems() throws Exception {
        if (kit == null) {
            throw new TranslatableException("kitNotFound");
        }
        try {
            final List<String> itemList = new ArrayList<>();
            final Object kitItems = kit.get("items");
            if (kitItems instanceof List) {
                for (final Object item : (List) kitItems) {
                    if (item instanceof String) {
                        itemList.add(item.toString());
                        continue;
                    }
                    throw new Exception("Invalid kit item: " + item.toString());
                }
                return itemList;
            }
            throw new Exception("Invalid item list");
        } catch (final Exception e) {
            ess.getLogger().log(Level.WARNING, "Error parsing kit " + kitName + ": " + e.getMessage());
            throw new TranslatableException(e,"kitError2");
        }
    }

    public List<String> getGearItems() throws Exception {
        if (kit == null) {
            throw new TranslatableException("kitNotFound");
        }
        try {
            final List<String> itemList = new ArrayList<>();
            final String[] gearConfigName = {"boots", "leggings", "chestplate", "helmet", "offhand"};
            for (String itemName : gearConfigName) {
                final Object item = kit.get(itemName);
                if (item == null) {
                    itemList.add(null);
                    continue;
                }

                if (item instanceof String) {
                    itemList.add(item.toString());
                    continue;
                }
                throw new Exception("Invalid kit item: " + item.toString());
            }

            return itemList;
        } catch (final Exception e) {
            ess.getLogger().log(Level.WARNING, "Error parsing kit " + kitName + ": " + e.getMessage());
            throw new TranslatableException(e,"kitError2");
        }
    }

    public boolean expandItems(final User user) throws Exception {
        return expandItems(user, getItems(user), getGearItems());
    }

    public boolean expandItems(final User user, final List<String> items, final List<String> gearItems) throws Exception {
        try {
            final IText basicInput = new SimpleTextInput(items);
            final IText basicOutput = new KeywordReplacer(basicInput, user.getSource(), ess, true, true);

            final List<String> nonNullGearItems = gearItems.stream().filter(is -> is != null).collect(Collectors.toList());
            final IText gearInput = new SimpleTextInput(nonNullGearItems);
            final IText gearOutput = new KeywordReplacer(gearInput, user.getSource(), ess, true, true);

            final KitClaimEvent event = new KitClaimEvent(user, this);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            boolean spew = false;
            final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
            final boolean autoEquip = ess.getSettings().isKitAutoEquip();
            final List<ItemStack> itemList = new ArrayList<>();
            final List<ItemStack> gearList = new ArrayList<>();
            final List<String> commandQueue = new ArrayList<>();
            final List<String> moneyQueue = new ArrayList<>();
            final String currencySymbol = ess.getSettings().getCurrencySymbol().isEmpty() ? "$" : ess.getSettings().getCurrencySymbol();

            populateKitLists(user, basicOutput, moneyQueue, commandQueue, itemList, allowUnsafe, currencySymbol);
            populateKitLists(user, gearOutput, moneyQueue, commandQueue, gearList, allowUnsafe, currencySymbol);

            final int maxStackSize = user.isAuthorized("essentials.oversizedstacks") ? ess.getSettings().getOversizedStackSize() : 0;
            final boolean isDropItemsIfFull = ess.getSettings().isDropItemsIfFull();

            final List<ItemStack> totalItems = new ArrayList<>(itemList);
            totalItems.addAll(gearList.stream().filter(is -> is != null).collect(Collectors.toList()));
            final KitPreExpandItemsEvent itemsEvent = new KitPreExpandItemsEvent(user, kitName, totalItems);
            Bukkit.getPluginManager().callEvent(itemsEvent);

            final List<Integer> nullGearItemsIndexes = findNullIndexes(gearItems);

            final ItemStack[] gearArray = addNullIndexes(gearList, nullGearItemsIndexes).toArray(new ItemStack[0]);
            final List<ItemStack> leftovers = Inventories.addGear(user.getBase(), gearArray);

            itemList.addAll(leftovers);

            final ItemStack[] itemArray = itemList.toArray(new ItemStack[0]);

            if (!isDropItemsIfFull && !Inventories.hasSpace(user.getBase(), maxStackSize, autoEquip, itemArray)) {
                user.sendTl("kitInvFullNoDrop");
                return false;
            }

            final Map<Integer, ItemStack> leftover = Inventories.addItem(user.getBase(), maxStackSize, autoEquip, itemArray);
            if (!isDropItemsIfFull && !leftover.isEmpty()) {
                // Inventories#hasSpace should prevent this state from EVER being reached; If it does, something has gone terribly wrong, and we should just give up and hope people report it :(
                throw new IllegalStateException("Something has gone terribly wrong while adding items to the user's inventory. Please report this to the EssentialsX developers. Items left over: " + leftover + ". Original items: " + Arrays.toString(itemArray));
            }

            for (final ItemStack itemStack : leftover.values()) {
                int spillAmount = itemStack.getAmount();
                if (maxStackSize != 0) {
                    itemStack.setAmount(Math.min(spillAmount, itemStack.getMaxStackSize()));
                }
                while (spillAmount > 0) {
                    user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
                    spillAmount -= itemStack.getAmount();
                }
                spew = true;
            }
            user.getBase().updateInventory();

            // Process money & command queues
            // Done after all items have been processed so commands are not run and money is not given if
            // an error occurs during the item giving process
            for (final String valueString : moneyQueue) {
                final BigDecimal value = new BigDecimal(valueString.trim());
                final Trade t = new Trade(value, ess);
                t.pay(user, OverflowType.DROP);
            }

            for (final String cmd : commandQueue) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

            if (spew) {
                user.sendTl("kitInvFull");
            }
        } catch (final Exception e) {
            user.getBase().updateInventory();
            ess.getLogger().log(Level.WARNING, e.getMessage());
            throw new TranslatableException(e, "kitError2");
        }
        return true;
    }

    private void populateKitLists(User user, IText output, List<String> moneyQueue, List<String> commandQueue, List<ItemStack> itemList, boolean allowUnsafe, String currencySymbol) throws Exception {
        for (final String kitItem : output.getLines()) {
            if (kitItem.startsWith(currencySymbol)) {
                moneyQueue.add(NumberUtil.sanitizeCurrencyString(kitItem, ess));
                continue;
            }

            if (kitItem.startsWith("/")) {
                String command = kitItem.substring(1);
                final String name = user.getName();
                command = command.replace("{player}", name);
                commandQueue.add(command);
                continue;
            }

            final ItemStack stack = parseItemStack(kitItem, user, allowUnsafe);
            if (stack == null) {
                continue;
            }

            itemList.add(stack);
        }
    }

    private ItemStack parseItemStack(String kitItem, User user, boolean allowUnsafe) throws Exception {
        if (kitItem.startsWith("@")) {
            if (ess.getSerializationProvider() == null) {
                ess.getLogger().log(Level.WARNING, AdventureUtil.miniToLegacy(tlLiteral("kitError3", kitName, user.getName())));
                return null;
            }
            return ess.getSerializationProvider().deserializeItem(Base64Coder.decodeLines(kitItem.substring(1)));
        } else {
            final String[] parts = kitItem.split(" +");
            final ItemStack parseStack = ess.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

            if (parseStack.getType() == Material.AIR) {
                return null;
            }

            final MetaItemStack metaStack = new MetaItemStack(parseStack);

            if (parts.length > 2) {
                // We pass a null sender here because kits should not do perm checks
                metaStack.parseStringMeta(null, allowUnsafe, parts, 2, ess);
            }

            return metaStack.getItemStack();
        }
    }

    private List<Integer> findNullIndexes(List<String> list) {
        final List<Integer> nullIndexes = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                nullIndexes.add(i);
            }
        }

        return nullIndexes;
    }

    private List<ItemStack> addNullIndexes(List<ItemStack> list, List<Integer> nullIndexes) {
        final List<ItemStack> newList = new ArrayList<>(list);

        for (int nullIndex : nullIndexes) {
            newList.add(nullIndex, null);
        }

        return newList;
    }
}
