package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import java.io.IOException;


public class SignInfo extends EssentialsSign
{
	public SignInfo()
	{
		super("Info");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{		
		validateTrade(sign, 3, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 3, ess);
		charge.isAffordableFor(player);
		
		String chapter = sign.getLine(1);
		String page = sign.getLine(2);
		
		final IText input;
		try
		{
			input = new TextInput(player, "info", true, ess);
			final IText output = new KeywordReplacer(input, player, ess);
			final TextPager pager = new TextPager(output);
			pager.showPage(chapter, page, null, player);
		
		}
		catch (IOException ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
		
		charge.charge(player);					
		return true;
	}
}
