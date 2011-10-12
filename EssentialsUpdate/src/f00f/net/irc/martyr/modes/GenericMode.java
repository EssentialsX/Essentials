package f00f.net.irc.martyr.modes;

import f00f.net.irc.martyr.Mode;

/**
 * GenericNode uses the character to specify the hash code.  Thus, two
 * mode types are the same, in a hash table, even if they have
 * different parameters or positive/negative values.
 */
public abstract class GenericMode implements Mode
{
	private String str;
	private Mode.Sign sign = Mode.Sign.NOSIGN;

	public void setParam( String str )
	{
		this.str = str;
	}
	
	public String getParam()
	{
		return str;
	}

	public void setSign( Mode.Sign sign )
	{
		this.sign = sign;
	}

	public Mode.Sign getSign()
	{
		return sign;
	}

	public String toString()
	{
		String pString = " ";
		if( sign != Mode.Sign.NOSIGN )
			pString += ( sign == Mode.Sign.POSITIVE ? "+" : "-" );
		String className = this.getClass().getName();
		className = className.substring( className.indexOf('$')+1 );
		
		String result = className + pString + getChar();
		if( requiresParam() )
		{
			result += " " + getParam();
		}

		return result;
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof Mode )
		{
			Mode oMode = (Mode)o;
			
			if( oMode.getParam() == null || this.getParam() == null )
				return oMode.getChar() == this.getChar();

			if( oMode.getParam() == null && this.getParam() != null )
				return false;
			if( oMode.getParam() == null && this.getParam() == null )
				return oMode.getChar() == this.getChar();
			
			return oMode.getChar() == this.getChar() && 
				oMode.getParam().equals(this.getParam());
		}
		return false;
	}

	public int hashCode()
	{
		return (int)getChar();
	}
}

