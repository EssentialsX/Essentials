package com.earth2me.essentials;

import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.events.KitClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

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
            throw new Exception(tl("kitNotFound"));
        }
    }

    public String getName() {
        return kitName;
    }

    public void checkPerms(final User user) throws Exception {
        if (!user.isAuthorized("essentials.kits." + kitName)) {
            throw new Exception(tl("noKitPermission", "essentials.kits." + kitName));
        }
    }

    public void checkDelay(final User user) throws Exception {
        final long nextUse = getNextUse(user);

        if (nextUse == 0L) {
        } else if (nextUse < 0L) {
            user.sendMessage(tl("kitOnce"));
            throw new NoChargeException();
        } else {
            user.sendMessage(tl("kitTimed", DateUtil.formatDateDiff(nextUse)));
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
            throw new Exception(tl("kitError2"));
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
        return getItems();
    }

    public List<String> getItems() throws Exception {
        if (kit == null) {
            throw new Exception(tl("kitNotFound"));
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
            throw new Exception(tl("kitError2"), e);
        }
    }

    public boolean expandItems(final User user) throws Exception {
        return expandItems(user, getItems(user));
    }

    public boolean expandItems(final User user, final List<String> items) throws Exception {
        try {
            final IText input = new SimpleTextInput(items);
            final IText output = new KeywordReplacer(input, user.getSource(), ess, true, true);

            final KitClaimEvent event = new KitClaimEvent(user, this);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            boolean spew = false;
            final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
            final boolean autoEquip = ess.getSettings().isKitAutoEquip();
            final List<ItemStack> itemList = new ArrayList<>();
            final List<String> commandQueue = new ArrayList<>();
            final List<String> moneyQueue = new ArrayList<>();
            final String currencySymbol = ess.getSettings().getCurrencySymbol().isEmpty() ? "$" : ess.getSettings().getCurrencySymbol();
            for (final String kitItem : output.getLines()) {
                if (kitItem.startsWith("$") || kitItem.startsWith(currencySymbol)) {
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

                final ItemStack stack;

                if (kitItem.startsWith("@")) {
                    if (ess.getSerializationProvider() == null) {
                        ess.getLogger().log(Level.WARNING, tl("kitError3", kitName, user.getName()));
                        continue;
                    }
                    stack = ess.getSerializationProvider().deserializeItem(Base64Coder.decodeLines(kitItem.substring(1)));
                } else {
                    final String[] parts = kitItem.split(" +");
                    final ItemStack parseStack = ess.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

                    if (parseStack.getType() == Material.AIR) {
                        continue;
                    }

                    final MetaItemStack metaStack = new MetaItemStack(parseStack);

                    if (parts.length > 2) {
                        // We pass a null sender here because kits should not do perm checks
                        metaStack.parseStringMeta(null, allowUnsafe, parts, 2, ess);
                    }

                    stack = metaStack.getItemStack();
                }

                if (autoEquip) {
                    final Material material = stack.getType();
                    final PlayerInventory inventory = user.getBase().getInventory();
                    if (MaterialUtil.isHelmet(material) && isEmptyStack(inventory.getHelmet())) {
                        inventory.setHelmet(stack);
                        continue;
                    } else if (MaterialUtil.isChestplate(material) && isEmptyStack(inventory.getChestplate())) {
                        inventory.setChestplate(stack);
                        continue;
                    } else if (MaterialUtil.isLeggings(material) && isEmptyStack(inventory.getLeggings())) {
                        inventory.setLeggings(stack);
                        continue;
                    } else if (MaterialUtil.isBoots(material) && isEmptyStack(inventory.getBoots())) {
                        inventory.setBoots(stack);
                        continue;
                    }
                }

                itemList.add(stack);
            }

            final Map<Integer, ItemStack> overfilled;
            final boolean allowOversizedStacks = user.isAuthorized("essentials.oversizedstacks");
            final boolean isDropItemsIfFull = ess.getSettings().isDropItemsIfFull();
            if (isDropItemsIfFull) {
                if (allowOversizedStacks) {
                    overfilled = InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), itemList.toArray(new ItemStack[0]));
                } else {
                    overfilled = InventoryWorkaround.addItems(user.getBase().getInventory(), itemList.toArray(new ItemStack[0]));
                }
                for (final ItemStack itemStack : overfilled.values()) {
                    int spillAmount = itemStack.getAmount();
                    if (!allowOversizedStacks) {
                        itemStack.setAmount(Math.min(spillAmount, itemStack.getMaxStackSize()));
                    }
                    while (spillAmount > 0) {
                        user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
                        spillAmount -= itemStack.getAmount();
                    }
                    spew = true;
                }
            } else {
                if (allowOversizedStacks) {
                    overfilled = InventoryWorkaround.addAllOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), itemList.toArray(new ItemStack[0]));
                } else {
                    overfilled = InventoryWorkaround.addAllItems(user.getBase().getInventory(), itemList.toArray(new ItemStack[0]));
                }
                if (overfilled != null) {
                    user.sendMessage(tl("kitInvFullNoDrop"));
                    return false;
                }
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
                user.sendMessage(tl("kitInvFull"));
            }
        } catch (final Exception e) {
            user.getBase().updateInventory();
            ess.getLogger().log(Level.WARNING, e.getMessage());
            throw new Exception(tl("kitError2"), e);
        }
        return true;
    }

    private boolean isEmptyStack(ItemStack stack) {
        return stack == null || MaterialUtil.isAir(stack.getType());
    }
}
