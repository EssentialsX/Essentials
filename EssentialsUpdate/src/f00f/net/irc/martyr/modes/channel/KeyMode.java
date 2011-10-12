package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.Mode;

/**
 *    <p>Channel Key - When a channel key is set (by using the mode
 *    'k'), servers MUST reject their local users request to join the
 *    channel unless this key is given.</p>
 * 
 *    <p>The channel key MUST only be made visible to the channel members in
 *    the reply sent by the server to a MODE query.</p>
 * (From RFC2811)
 */ 
public class KeyMode extends GenericChannelMask
{
	public boolean requiresParam()
	{
		return true;
	}
	
	public char getChar()
	{
		return 'k';
	}
	
	public Mode newInstance()
	{
		return new KeyMode();
	}
}

