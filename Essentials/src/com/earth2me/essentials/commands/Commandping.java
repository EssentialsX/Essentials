package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n.tl;

import java.lang.reflect.Field;

public class Commandping extends EssentialsCommand {
    public Commandping() {
        super("ping");
    }

    @Override
    public void run(final Server server, final User user final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            final String ping = String.valueOf(getPing(user));
            user.sendMessage(tl("playerPing", ping));
        } else {
            user.sendMessage(FormatUtil.replaceFormat(getFinalArg(args, 0)));
        }
    }

    private int getPing(Player p) {
    	int pingInt = 0;
    	Object nmsPlayer = getNMSPlayer(p);
    	try {
    		Field ping = nmsPlayer.getClass().getField("ping");
    		ping.setAccessible(true);
		    pingInt = ping.getInt(nmsPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return pingInt;
    }

    private static Object getNMSPlayer(Player p) {
    	try {
    		return p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
}
