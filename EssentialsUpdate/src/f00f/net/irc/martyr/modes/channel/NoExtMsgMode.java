package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>No Messages To Channel From Clients On The Outside - When the
 *    channel flag 'n' is set, only channel members MAY send messages
 *    to the channel.</p>
 * 
 *    <p>This flag only affects users.</p>
 * (From RFC2811)
 */
public class NoExtMsgMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return false;
	}
	
	public char getChar()
	{
		return 'n';
	}
	
	public Mode newInstance()
	{
		return new NoExtMsgMode();
	}
}

