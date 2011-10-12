package f00f.net.irc.martyr.modes.channel;


/**
 * 'Masks' and other modes that can have multiple copies in a channel
 * at once should subclass this.
 */
public abstract class GenericChannelMask extends GenericChannelMode
{
	public boolean onePerChannel()
	{
		return false;
	}
}


