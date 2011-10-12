package f00f.net.irc.martyr.commands;

import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.CommandRegister;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Defines the ISON command, which is used to determine if a user or a list of users is online.
 *
 * @author Daniel Henninger
 */
public class IsonCommand extends AbstractCommand
{

    public static final String IDENTIFIER_PRIMARY = "ISON";
    public static final String IDENTIFIER_SECONDARY = "303";

    /* List of nicks that we will check for online status */
    List<String> nicks = new ArrayList<String>();

    /* Destination nick */
    String dest = null;

    /**
     * No parameter passed to the ISON is not valid.  This is used for factories.
     */
    public IsonCommand()
    {
        // Nothing to do
    }

    /**
     * Check online status of a single nickname.
     *
     * @param nick Nick you want to check the online status of.
     */
    public IsonCommand(String nick)
    {
        this.nicks.add(nick);
    }

    public IsonCommand(String dest, String nick) {
        this.dest = dest;
        this.nicks.add(nick);
    }

    /**
     * Check online status of a number of nicknames.
     *
     * @param nicks List of nicks you want to check the online status of.
     */
    public IsonCommand(List<String> nicks)
    {
        this.nicks.addAll(nicks);
    }

    public IsonCommand(String dest, List<String> nicks) {
        this.dest = dest;
        this.nicks.addAll(nicks);
    }

    /**
     * @see AbstractCommand#parse(String, String, String)
     */
    public InCommand parse(String prefix, String identifier, String params)
    {
        // when the command is used as a reply, the nick is parameter 0 and the rest are parameter 1.
        if ( identifier.equals( IDENTIFIER_SECONDARY ) ) {
            String nickParam = getParameter(params, 1);
            List<String> nicks = Arrays.asList(nickParam.split(" "));
            return new IsonCommand(getParameter(params, 0), nicks);
        }
        else {
            String nickParam = getParameter(params, 0);
            List<String> nicks = Arrays.asList(nickParam.split(" "));
            return new IsonCommand(nicks);
        }
    }

    /**
     * @see f00f.net.irc.martyr.commands.AbstractCommand#renderParams()
     */
    public String renderParams()
    {
        String ret = "";
        if (nicks.size() > 0) {
            Boolean isFirst = true;
            for (String nick : nicks) {
                if (isFirst) {
                    ret = ret + nick;
                    isFirst = false;
                }
                else {
                    ret = ret + " " + nick;
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
        //
        // This command uses "ISON" on outgoing, so that is why we use
        // "ISON" here instead of "303".
        //
        return IDENTIFIER_PRIMARY;
    }

    /**
     * @see AbstractCommand#selfRegister(f00f.net.irc.martyr.CommandRegister)
     */
    public void selfRegister(CommandRegister commandRegister)
    {
        commandRegister.addCommand( IDENTIFIER_PRIMARY, this );
        commandRegister.addCommand( IDENTIFIER_SECONDARY, this );
    }

    /**
     * Retrieves the target of the ISON command
     *
     * @return Target of command
     */
    public String getDest() {
        return dest;
    }

    /**
     * Retrieves the list of nicks that are online after an ISON command
     *
     * @return List of online nicks
     */
    public List<String> getNicks()
    {
        return nicks;
    }

}
