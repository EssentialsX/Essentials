package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.OutCommand;

/**
 * Implements a WHOIS command, to query details about a user.
 *
 */
public class WhoisCommand implements OutCommand
{
	private static final String WHOIS = "WHOIS";

	private String target;

	/**
	 * @param target the nick or mask that you wish to know about.
	 */
	public WhoisCommand( String target )
	{
		this.target = target;
	}

	/**
	 * @return "WHOIS"
	 */
	public String getIrcIdentifier()
	{
		return WHOIS;
	}

	/**
	 * Simply returns the string given in the constructor.
	 */
	public String render()
	{
		return WHOIS + " " + target;
	}
}


