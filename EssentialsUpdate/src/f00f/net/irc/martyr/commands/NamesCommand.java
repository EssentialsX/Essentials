package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.OutCommand;

import java.util.List;
import java.util.ArrayList;

/**
 * Defines the NAMES command, which is used to get the members of certain channels, or all of them.
 *
 * @author Daniel Henninger
 */
public class NamesCommand implements OutCommand
{

    /* List of channels we will request membership of. */
    List<String> channels = new ArrayList<String>();

    /**
     * No parameter passed to the NAMES command represents a request for all channels.
     */
    public NamesCommand()
    {
        // Nothing to do
    }

    /**
     * Request the membership of a single channel.
     *
     * @param channel Channel you want to request membership of.
     */
    public NamesCommand(String channel)
    {
        this.channels.add(channel);
    }

    /**
     * Request the membership of multiple channels.
     *
     * @param channels List of channels you want to retrieve the membership list of.
     */
    public NamesCommand(List<String> channels)
    {
        this.channels.addAll(channels);
    }

    /**
     * @see f00f.net.irc.martyr.OutCommand#render()
     */
    public String render()
    {
        String ret = getIrcIdentifier();
        if (channels.size() > 0) {
            ret = ret + " ";
            Boolean isFirst = true;
            for (String channel : channels) {
                if (isFirst) {
                    ret = ret + channel;
                    isFirst = false;
                }
                else {
                    ret = ret + "," + channel;
                }
            }
        }
        return ret;
    }

    /**
     * @see f00f.net.irc.martyr.Command#getIrcIdentifier()
     */
    public String getIrcIdentifier()
    {
        return "NAMES";
    }

}
