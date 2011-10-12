package f00f.net.irc.martyr;

/**
 * Defines an outgoing command.  Outgoing commands are very simple
 * because all they need to do is be rendered.  Outgoing commands do
 * not change our state.
 */
public interface OutCommand extends Command
{

    /**
     * Forms a string appropriate to send to the server, if required.
     * Some commands will have no such string, as they are received and not
     * sent.  The string returned is sent to the server verbatim.
     *
     * @return Rendered string
     */
    String render();

}




