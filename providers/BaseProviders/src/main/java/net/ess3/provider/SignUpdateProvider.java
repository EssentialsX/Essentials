package net.ess3.provider;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public interface SignUpdateProvider {
    boolean updateSign(Sign sign, Player player, String[] lines);
}
