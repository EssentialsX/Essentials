package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Channel Operator Status - The mode 'o' is used to toggle the
 *    operator status of a channel member.</p> (From RFC2811)
 *  
 * <p>Note that OperMode is recorded in the channel, but checking the op
 * status of a member will give you a true list of who is and isn't an
 * operator.  This is because we don't know the entire list of modes
 * when entering a channel.</p>
 */
public class OperMode extends GenericChannelMode
{
	public boolean requiresParam()
	{
		return true;
	}
	
	public char getChar()
	{
		return 'o';
	}
	
	public Mode newInstance()
	{
		return new OperMode();
	}
}

