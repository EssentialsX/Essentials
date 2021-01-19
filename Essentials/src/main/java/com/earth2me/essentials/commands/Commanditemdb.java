package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

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

        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
            itemId = itemStack.getType().getId() + ":" + itemStack.getDurability();
        }

        sender.sendMessage(tl("itemType", itemStack.getType().toString(), itemId));

        // Don't send IDs twice
        if (!tl("itemType").contains("{1}") && !itemId.equals("none")) {
            sender.sendMessage(tl("itemId", itemId));
        }

        if (itemHeld && itemStack.getType() != Material.AIR) {
            final int maxuses = itemStack.getType().getMaxDurability();
            final int durability = (maxuses + 1) - itemStack.getDurability();
            if (maxuses != 0) {
                sender.sendMessage(tl("durability", Integer.toString(durability)));
            }
        }

        List<String> nameList = ess.getItemDb().nameList(itemStack);
        nameList.addAll(ess.getCustomItemResolver().getAliasesFor(ess.getItemDb().name(itemStack)));
        Collections.sort(nameList);

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        final String itemNameList = StringUtil.joinList(", ", nameList);
        sender.sendMessage(tl("itemNames", itemNameList));
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
