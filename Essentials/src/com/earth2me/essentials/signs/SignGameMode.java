package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;


public class SignGameMode extends EssentialsSign
{
	public SignGameMode()
	{
		super("GameMode");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 1, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 1, ess);
		final String mode = sign.getLine(2).trim();
		
		if(mode.isEmpty())
		{
			throw new SignException(_("invalidSignLine", 3));
		}
		
		charge.isAffordableFor(player);

		performSetMode(mode, player);
		player.sendMessage(_("gameMode", _(player.getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
		charge.charge(player);
		return true;
	}
	
	private void performSetMode(String mode, Player player)
	{
			if (mode.contains("survi") || mode.equalsIgnoreCase("0"))
			{
				player.setGameMode(GameMode.SURVIVAL);
			}
			else if (mode.contains("creat") || mode.equalsIgnoreCase("1"))
			{
				player.setGameMode(GameMode.CREATIVE);
			}
			else if (mode.contains("advent") || mode.equalsIgnoreCase("2"))
			{
				player.setGameMode(GameMode.ADVENTURE);
			}
	}
	
}
