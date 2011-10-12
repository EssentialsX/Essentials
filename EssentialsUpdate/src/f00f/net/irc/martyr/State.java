package f00f.net.irc.martyr;

/**
 * A simple container for state constants.  The state constants here
 * are used to specify what state the protocol is in.  The State
 * object is both the state representitive and the state container.
 * This was done so that state could be typesafe and valuesafe.
 *
 */
public class State
{

    public static final State UNCONNECTED = new State("unconnected");
    public static final State UNREGISTERED = new State("unregistered");
    public static final State REGISTERED = new State("registered");
    public static final State UNKNOWN = new State("unknown/any");

    private String stateName;

    private State( String str )
    {
        stateName = str;
    }

    public String toString()
    {
        return stateName;
    }

}

