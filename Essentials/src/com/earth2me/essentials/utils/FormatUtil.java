package com.earth2me.essentials.utils;

import java.util.regex.Pattern;
import net.ess3.api.IUser;


public class FormatUtil
{
	//Vanilla patterns used to strip existing formats
	static final transient Pattern VANILLA_PATTERN = Pattern.compile("\u00a7+[0-9A-FK-ORa-fk-or]?");
	static final transient Pattern VANILLA_COLOR_PATTERN = Pattern.compile("\u00a7+[0-9A-Fa-f]");
	static final transient Pattern VANILLA_MAGIC_PATTERN = Pattern.compile("\u00a7+[Kk]");
	static final transient Pattern VANILLA_FORMAT_PATTERN = Pattern.compile("\u00a7+[L-ORl-or]");
	//Essentials '&' convention colour codes
	static final transient Pattern REPLACE_ALL_PATTERN = Pattern.compile("(?<!&)&([0-9a-fk-orA-FK-OR])");
	static final transient Pattern REPLACE_COLOR_PATTERN = Pattern.compile("(?<!&)&([0-9a-fA-F])");
	static final transient Pattern REPLACE_MAGIC_PATTERN = Pattern.compile("(?<!&)&([Kk])");
	static final transient Pattern REPLACE_FORMAT_PATTERN = Pattern.compile("(?<!&)&([l-orL-OR])");
	static final transient Pattern REPLACE_PATTERN = Pattern.compile("&&(?=[0-9a-fk-orA-FK-OR])");
	//Used to prepare xmpp output
	static final transient Pattern LOGCOLOR_PATTERN = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]");
	static final transient Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
	public static final Pattern IPPATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	//This method is used to simply strip the native minecraft colour codes
	public static String stripFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return stripColor(input, VANILLA_PATTERN);
	}

	//This method is used to simply strip the & convention colour codes
	public static String stripEssentialsFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return stripColor(input, REPLACE_ALL_PATTERN);
	}

	//This is the general permission sensitive message format function, checks for urls.
	public static String formatMessage(final IUser user, final String permBase, final String input)
	{
		if (input == null)
		{
			return null;
		}
		String message = formatString(user, permBase, input);
		if (!user.isAuthorized(permBase + ".url"))
		{
			message = FormatUtil.blockURL(message);
		}
		return message;
	}

	//This method is used to simply replace the ess colour codes with minecraft ones, ie &c
	public static String replaceFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return replaceColor(input, REPLACE_ALL_PATTERN);
	}

	static String replaceColor(final String input, final Pattern pattern)
	{
		return REPLACE_PATTERN.matcher(pattern.matcher(input).replaceAll("\u00a7$1")).replaceAll("&");
	}

	//This is the general permission sensitive message format function, does not touch urls.
	public static String formatString(final IUser user, final String permBase, final String input)
	{
		if (input == null)
		{
			return null;
		}
		String message;
		if (user.isAuthorized(permBase + ".color") || user.isAuthorized(permBase + ".colour"))
		{
			message = replaceColor(input, REPLACE_COLOR_PATTERN);
		}
		else
		{
			message = stripColor(input, VANILLA_COLOR_PATTERN);
		}
		if (user.isAuthorized(permBase + ".magic"))
		{
			message = replaceColor(message, REPLACE_MAGIC_PATTERN);
		}
		else
		{
			message = stripColor(message, VANILLA_MAGIC_PATTERN);
		}
		if (user.isAuthorized(permBase + ".format"))
		{
			message = replaceColor(message, REPLACE_FORMAT_PATTERN);
		}
		else
		{
			message = stripColor(message, VANILLA_FORMAT_PATTERN);
		}
		return message;
	}

	public static String stripLogColorFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return stripColor(input, LOGCOLOR_PATTERN);
	}

	static String stripColor(final String input, final Pattern pattern)
	{
		return pattern.matcher(input).replaceAll("");
	}

	public static String lastCode(final String input)
	{
		int pos = input.lastIndexOf('\u00a7');
		if (pos == -1 || (pos + 1) == input.length())
		{
			return "";
		}
		return input.substring(pos, pos + 2);
	}

	static String blockURL(final String input)
	{
		if (input == null)
		{
			return null;
		}
		String text = URL_PATTERN.matcher(input).replaceAll("$1 $2");
		while (URL_PATTERN.matcher(text).find())
		{
			text = URL_PATTERN.matcher(text).replaceAll("$1 $2");
		}
		return text;
	}

	public static boolean validIP(String ipAddress)
	{
		return IPPATTERN.matcher(ipAddress).matches();
	}
}
