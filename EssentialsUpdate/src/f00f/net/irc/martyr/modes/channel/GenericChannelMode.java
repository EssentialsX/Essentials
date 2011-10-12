package f00f.net.irc.martyr.modes.channel;

import f00f.net.irc.martyr.modes.GenericMode;

/**
 * A generic channel mode will be recorded in the channel, and there
 * will be one per channel.  Modes that can have multiple copies in
 * the channel (masks) should subclass GenericChannelMask.
 */
public abstract class GenericChannelMode extends GenericMode
{
	public boolean recordInChannel()
	{
		return true;
	}
	
	public boolean onePerChannel()
	{
		return true;
	}
}


