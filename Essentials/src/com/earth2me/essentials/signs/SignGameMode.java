package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class SignGameMode extends EssentialsSign {
    public SignGameMode() {
        super("GameMode");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        final String gamemode = sign.getLine(1);
        if (gamemode.isEmpty()) {
            sign.setLine(1, "Survival");
        }

        validateTrade(sign, 2, ess);

        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, ess);
        final String mode = sign.getLine(1).trim();

        if (mode.isEmpty()) {
            throw new SignException(tl("invalidSignLine", 2));
        }

        charge.isAffordableFor(player);

        performSetMode(mode.toLowerCase(Locale.ENGLISH), player.getBase());
        player.sendMessage(tl("gameMode", tl(player.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
        Trade.log("Sign", "gameMode", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
        charge.charge(player);
        return true;
    }

    private void performSetMode(final String mode, final Player player) throws SignException {
        if (mode.contains("survi") || mode.equalsIgnoreCase("0")) {
            player.setGameMode(GameMode.SURVIVAL);
        } else if (mode.contains("creat") || mode.equalsIgnoreCase("1")) {
            player.setGameMode(GameMode.CREATIVE);
        } else if (mode.contains("advent") || mode.equalsIgnoreCase("2")) {
            player.setGameMode(GameMode.ADVENTURE);
        } else if (mode.contains("spec") || mode.equalsIgnoreCase("3")) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            throw new SignException(tl("invalidSignLine", 2));
        }
    }
}
