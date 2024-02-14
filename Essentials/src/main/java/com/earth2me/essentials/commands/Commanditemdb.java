package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commanditemdb extends EssentialsCommand {
    public Commanditemdb() {
        super("itemdb");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ItemStack itemStack = null;
        boolean itemHeld = false;
        if (args.length == 0) {
            if (sender.isPlayer() && sender.getPlayer() != null) {
                itemHeld = true;
                itemStack = ess.getUser(sender.getPlayer()).getItemInHand();
            }
            if (itemStack == null) {
                throw new NotEnoughArgumentsException();
            }
        } else {
            itemStack = ess.getItemDb().get(args[0]);
        }

        String itemId = "none";

        if (VersionUtil.PRE_FLATTENING) {
            itemId = itemStack.getType().getId() + ":" + itemStack.getDurability();
        }

        sender.sendTl("itemType", itemStack.getType().toString(), itemId);

        // Don't send IDs twice
        if (!sender.tl("itemType").contains("{1}") && !itemId.equals("none")) {
            sender.sendTl("itemId", itemId);
        }

        if (itemHeld && itemStack.getType() != Material.AIR) {
            final int maxuses = itemStack.getType().getMaxDurability();
            final int durability = (maxuses + 1) - MaterialUtil.getDamage(itemStack);
            if (maxuses != 0) {
                sender.sendTl("durability", Integer.toString(durability));
            }
        }

        List<String> nameList = ess.getItemDb().nameList(itemStack);
        nameList = nameList != null ? new ArrayList<>(nameList) : new ArrayList<>();
        nameList.addAll(ess.getCustomItemResolver().getAliasesFor(ess.getItemDb().name(itemStack)));
        if (nameList.isEmpty()) {
            return;
        }

        Collections.sort(nameList);
        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        final String itemNameList = StringUtil.joinList(", ", nameList);
        sender.sendTl("itemNames", itemNameList);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getItems();
        } else {
            return Collections.emptyList();
        }
    }
}
