package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;

import org.bukkit.Server;

public class Commandpaytoggle extends EssentialsCommand {

    public Commandpaytoggle() {
        super("paytoggle");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            boolean acceptingPay = !user.isAcceptingPay();
            if (commandLabel.contains("payon")) {
                acceptingPay = true;
            } else if (commandLabel.contains("payoff")) {
                acceptingPay = false;
            }
            user.setAcceptingPay(acceptingPay);
            if (acceptingPay) {
                user.sendMessage(tl("payToggleOn"));
            } else {
                user.sendMessage(tl("payToggleOff"));
            }
        } else {
            if (args[0].equalsIgnoreCase("players")) {
                StringBuilder sb = new StringBuilder();
                for (String s : user._getBlockingIndividualPay()) {
                    sb.append(s).append(" ");
                }
                String payBlockedList = sb.toString().trim();
                user.sendMessage(payBlockedList.length() > 0 ? tl("payBlockedList", payBlockedList) : tl(
                        "noPayBlocked"));
            } else {
                User player;
                try {
                    player = getPlayer(server, args, 0, true, true);
                } catch (PlayerNotFoundException ex) {
                    player = ess.getOfflineUser(args[0]);
                }
                if (player == null) {
                    throw new PlayerNotFoundException();
                }
                if (player.isPayBlockExempt()) {
                    user.sendMessage(tl("payBlockExempt"));
                } else if (user.isPlayerPayBlocked(player)) {
                    user.setPlayerPayBlocked(player, false);
                    user.sendMessage(tl("unPayBlockPlayer", player.getName()));
                } else if (!user.isPlayerPayBlocked(player)) {
                    user.setPlayerPayBlocked(player, true);
                    user.sendMessage(tl("payBlockPlayer", player.getName()));
                }
            }
        }
    }
}

