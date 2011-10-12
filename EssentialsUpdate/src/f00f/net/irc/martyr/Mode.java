package f00f.net.irc.martyr;

/**
 * Any class which is to represent a mode must implement this
 * interface.  They must also implement equals(...) so that if the
 * parameter for either mode is null they are equal based on the
 * character, and if both parameters are not null, base the equal
 * on the character and the parameters being equal.
 */
public interface Mode
{
	/**
	 * A Mode can be constructed and asked to make copies of itself.
     *
     * @return New Mode instance
	 */
	Mode newInstance();
	
	/**
	 * The character that represents this mode (ie o for operator)
     *
     * @return Character representation of mode
	 */
	char getChar();
	
	/**
	 * Should return true if this mode requires a parameter.
     *
     * @return True or false if a param is required for mode
	 */
	boolean requiresParam();

	/**
	 * This mode should be recorded in the list of channel modes.  This
	 * would NOT include such things as operator status, as it is recored
	 * with the Member object.
     *
     * @return True or false of the mode should be recorded in the list of channels
	 */
	boolean recordInChannel();
	
	/**
	 * Determines if there can be multiple versions of this mode in
	 * the channel.
     *
     * @return True or false if only one instance of mode can exist per channel
	 */
	boolean onePerChannel();
	
	/**
	 * Returns the parameter that was set with setParam(...)
     *
     * @return Parameter that was set previously
	 */
	String getParam();
	
	/**
	 * Sets the parameter that can be retrieved with getParam()
     *
     * @param str Parameter to set on mode
	 */
	void setParam( String str );
	
	/**
	 * Sets the sign of the operation.  Must be positive (granting),
	 * negative (revoking) or nosign (neutral operation).
     *
     * @param sign Sign (+/-) of the mode
	 */
	void setSign( Sign sign );
	
	/**
	 * @return the sign of this mode.
	 */
	Sign getSign();

	/**
	 * Finally, the Sign enumeration.
	 */
	public class Sign
	{
		public static final Sign POSITIVE = new Sign( "positive" );
		public static final Sign NEGATIVE = new Sign( "negative" );
		public static final Sign NOSIGN = new Sign( "nosign" );
	
		private String name;
		private Sign( String name )
		{
			this.name = name;
		}
	
		public String toString()
		{
			return name;
		}
	}
	
}
	



	
