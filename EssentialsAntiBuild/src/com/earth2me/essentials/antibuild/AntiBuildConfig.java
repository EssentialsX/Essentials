package com.earth2me.essentials.antibuild;


public enum AntiBuildConfig
{
	disable_build("protect.disable.build", true),
	disable_use("protect.disable.use", true),
	alert_on_placement("protect.alert.on-placement"),
	alert_on_use("protect.alert.on-use"),
	alert_on_break("protect.alert.on-break"),
	blacklist_placement("protect.blacklist.placement"),
	blacklist_usage("protect.blacklist.usage"),
	blacklist_break("protect.blacklist.break"),
	blacklist_piston("protect.blacklist.piston");
	private final String configName;
	private final String defValueString;
	private final boolean defValueBoolean;
	private final boolean isList;
	private final boolean isString;

	private AntiBuildConfig(final String configName)
	{
		this(configName, null, false, true, false);
	}

	private AntiBuildConfig(final String configName, final boolean defValueBoolean)
	{
		this(configName, null, defValueBoolean, false, false);
	}

	private AntiBuildConfig(final String configName, final String defValueString, final boolean defValueBoolean, final boolean isList, final boolean isString)
	{
		this.configName = configName;
		this.defValueString = defValueString;
		this.defValueBoolean = defValueBoolean;
		this.isList = isList;
		this.isString = isString;
	}

	/**
	 * @return the configName
	 */
	public String getConfigName()
	{
		return configName;
	}

	/**
	 * @return the default value String
	 */
	public String getDefaultValueString()
	{
		return defValueString;
	}

	/**
	 * @return the default value boolean
	 */
	public boolean getDefaultValueBoolean()
	{
		return defValueBoolean;
	}

	public boolean isString()
	{
		return isString;
	}

	public boolean isList()
	{
		return isList;
	}
}
