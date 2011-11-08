package com.earth2me.essentials.update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Version implements Comparable<Version>
{
	public enum Type
	{
		STABLE, PREVIEW, DEVELOPER
	}

	public int getMajor()
	{
		return major;
	}

	public int getMinor()
	{
		return minor;
	}

	public int getBuild()
	{
		return build;
	}

	public Type getType()
	{
		return type;
	}
	private final transient int major;
	private final transient int minor;
	private final transient int build;
	private final transient Type type;

	public Version(final String versionString)
	{
		final Matcher matcher = Pattern.compile("(Pre|Dev)?([0-9]+)[_\\.]([0-9]+)[_\\.]([0-9]+).*").matcher(versionString);
		if (!matcher.matches() || matcher.groupCount() < 4)
		{
			type = Type.DEVELOPER;
			major = 99;
			minor = build = 0;
			return;
		}
		if (versionString.startsWith("Pre"))
		{
			type = Type.PREVIEW;
		}
		else if (versionString.startsWith("Dev"))
		{
			type = Type.DEVELOPER;
		}
		else
		{
			type = Type.STABLE;
		}
		major = Integer.parseInt(matcher.group(2));
		minor = Integer.parseInt(matcher.group(3));
		build = Integer.parseInt(matcher.group(4));
	}

	@Override
	public int compareTo(final Version other)
	{
		int ret = 0;
		if (other.getType() == Type.DEVELOPER && getType() != Type.DEVELOPER)
		{
			ret = -1;
		}
		else if (getType() == Type.DEVELOPER && other.getType() != Type.DEVELOPER)
		{
			ret = 1;
		}
		else if (other.getMajor() > getMajor())
		{
			ret = -1;
		}
		else if (getMajor() > other.getMajor())
		{
			ret = 1;
		}
		else if (other.getMinor() > getMinor())
		{
			ret = -1;
		}
		else if (getMinor() > other.getMinor())
		{
			ret = 1;
		}
		else if (other.getBuild() > getBuild())
		{
			ret = -1;
		}
		else if (getBuild() > other.getBuild())
		{
			ret = 1;
		}
		else if (other.getType() == Type.STABLE && getType() == Type.PREVIEW)
		{
			ret = -1;
		}
		else if (getType() == Type.STABLE && other.getType() == Type.PREVIEW)
		{
			ret = 1;
		}
		return ret;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Version other = (Version)obj;
		if (this.major != other.major)
		{
			return false;
		}
		if (this.minor != other.minor)
		{
			return false;
		}
		if (this.build != other.build)
		{
			return false;
		}
		if (this.type != other.type)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 71 * hash + this.major;
		hash = 71 * hash + this.minor;
		hash = 71 * hash + this.build;
		hash = 71 * hash + (this.type == null ? 0 : this.type.hashCode());
		return hash;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		if (type == Type.DEVELOPER)
		{
			builder.append("Dev");
		}
		if (type == Type.PREVIEW)
		{
			builder.append("Pre");
		}
		builder.append(major);
		builder.append('.');
		builder.append(minor);
		builder.append('.');
		builder.append(build);
		return builder.toString();
	}
}
