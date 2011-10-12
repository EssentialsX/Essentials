package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Moderated Channel Flag - The channel flag 'm' is used to
 *    control who may speak on a channel.  When it is set, only
 *    channel operators, and members who have been given the voice
 *    privilege may send messages to the channel.</p>
 * 
 *    <p>This flag only affects users.</p>
 * (From RFC2811)
 */
public class ModeratedMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return false;
	}
	
	public char getChar()
	{
		return 'm';
	}
	
	public Mode newInstance()
	{
		return new ModeratedMode();
	}
}

