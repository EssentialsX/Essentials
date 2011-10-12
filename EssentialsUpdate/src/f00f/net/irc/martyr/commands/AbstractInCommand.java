package f00f.net.irc.martyr.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import f00f.net.irc.martyr.CommandRegister;
import f00f.net.irc.martyr.InCommand;
import f00f.net.irc.martyr.State;
import f00f.net.irc.martyr.clientstate.ClientState;

/**
 * Defines a generic command.  Most commands will simply have to
 * override the getIrcIdentifier method and implement the parse and
 * render methods using convenience methods.
 */
public abstract class AbstractInCommand implements InCommand
{

    protected Map<String,String> attributes = new HashMap<String,String>();

    protected AbstractInCommand()
    {
    }

    protected AbstractInCommand( String[] attributeNames )
    {
        for (String attributeName : attributeNames) {
            attributes.put(attributeName, null);
        }
    }

    public String getAttribute( String key )
    {
        return attributes.get( key );
    }

    public Iterator getAttributeKeys()
    {
        return Collections.unmodifiableSet( attributes.keySet() ).iterator();
    }

    protected void setAttribute( String key, String value )
    {
        attributes.put( key, value );
    }

    private String sourceString;

    /**
     * Some commands, when received by the server, can only occur in one
     * state.  Thus, when this command is received, the protocol should
     * assume that it is that state.  A command can use the 'unknown'
     * state to indicate it can be received in any state (for example,
     * ping).  Most commands will occur in the REGISTERED state, so for a
     * few exeptions, commands can leave this alone.
     */
    public State getState()
    {
        return State.REGISTERED;
    }

    /**
     * Every command should know how to register itself (or not) with the
     * command parsing engine.  If a command is available under mutiple
     * identifiers, then this method can be overridden and the addCommand
     * method can be called multiple times.
     */
    public void selfRegister( CommandRegister commandRegister )
    {
        commandRegister.addCommand( getIrcIdentifier(), this );
    }

    /**
     * Parses a string and produces a formed command object, if it can.
     * Should return null if it cannot form the command object.
     */
    public abstract InCommand parse( String prefix, String identifier, String params );

    /**
     * By default, commands do not update the client state.
     */
    public boolean updateClientState( ClientState state )
    {
        return false;
    }

    /**
     * Utility method to make parsing easy.  Provides parameter n, where
     * n=0 is the first parameter.  Parses out the : and considers
     * anything after a : to be one string, the final parameter.
     *
     * If the index doesn't exist, returns null.  Should it throw
     * IndexOutOfBoundsException?  No, some commands may have optional
     * fields.
     *
     * @param params String with parameters in it
     * @param num Position number of parameter to be requested
     * @return Parameter specified by id in params string
     */
    public String getParameter( String params, int num )
    {
        int colonIndex = params.indexOf( " :" );
        colonIndex++; // Skip the space, we just needed it to be sure it's really a "rest of line" colon
        String textParam = null;
        String spaceParams;

        if( colonIndex < 0 )
        {
            spaceParams = params;
        }
        else if( colonIndex == 0 )
        {
            if( num == 0 )
                return params.substring( 1, params.length() );
            else
                return null;
                // throw exception?
        }
        else
        {
            // colon index > 0, so we have at least one parameter before
            // the final parameter.
            spaceParams = params.substring( 0, colonIndex ).trim();
            textParam = params.substring( colonIndex + 1, params.length() );
        }

        StringTokenizer tokens = new StringTokenizer( spaceParams, " " );

        while( tokens.hasMoreTokens() && num > 0 )
        {
            // strip off tokensi
            --num;
            tokens.nextToken();
        }

        if( num == 0 && tokens.hasMoreTokens() )
            return tokens.nextToken();
        if( num == 0 && !tokens.hasMoreTokens() )
            return textParam;


        return null;
        // throw exception?
    }

    public int getIntParameter( String params, int paramnum, int defaultNum )
    {
        try
        {
            return Integer.parseInt( getParameter( params, paramnum ) );
        }
        catch( NumberFormatException nfe )
        {
            return defaultNum;
        }

    }

    public void setSourceString( String source )
    {
        this.sourceString = source;
    }

    public String getSourceString()
    {
        return sourceString;
    }

}


