package com.earth2me.essentials.protect;


public enum ProtectConfig
{
	datatype("protect.datatype", "sqlite"),
	mysqlDB("protect.mysqlDb", "jdbc:mysql://localhost:3306/minecraft"),
	dbUsername("protect.username", "root"),
	dbPassword("protect.password", ""),
	memstore("protect.memstore", false),
	disable_contactdmg("protect.disable.contactdmg", false),
	disable_lavadmg("protect.disable.lavadmg", false),
	disable_pvp("protect.disable.pvp", false),
	disable_projectiles("protect.disable.projectiles", false),
	disable_fall("protect.disable.fall", false),
	disable_suffocate("protect.disable.suffocate", false),
	disable_firedmg("protect.disable.firedmg", false),
	disable_lightning("protect.disable.lightning", false),
	disable_drown("protect.disable.drown", false),
	disable_wither("protect.disable.wither", false),
	disable_weather_storm("protect.disable.weather.storm", false),
	disable_weather_lightning("protect.disable.weather.lightning", false),
	disable_weather_thunder("protect.disable.weather.thunder", false),
	prevent_fire_spread("protect.prevent.fire-spread", true),
	prevent_flint_fire("protect.prevent.flint-fire", false),
	prevent_lava_fire_spread("protect.prevent.lava-fire-spread", true),
	prevent_lightning_fire_spread("protect.prevent.lightning-fire-spread", true),
	prevent_water_flow("protect.prevent.water-flow", false),
	prevent_lava_flow("protect.prevent.lava-flow", false),
	prevent_water_bucket_flow("protect.prevent.water-bucket-flow", false),
	prevent_portal_creation("protect.prevent.portal-creation", false),
	prevent_block_on_rail("protect.protect.prevent-block-on-rails", false),
	prevent_tnt_explosion("protect.prevent.tnt-explosion", false),
	prevent_tnt_playerdmg("protect.prevent.tnt-playerdamage", false),
	prevent_fireball_explosion("protect.prevent.fireball-explosion", false),
	prevent_fireball_fire("protect.prevent.fireball-fire", false),
	prevent_fireball_playerdmg("protect.prevent.fireball-playerdamage", false),
	prevent_witherskull_explosion("protect.prevent.witherskull-explosion", false),
	prevent_witherskull_playerdmg("protect.prevent.witherskull-playerdamage", false),
	prevent_wither_spawnexplosion("protect.prevent.wither-spawnexplosion", false),
	prevent_creeper_explosion("protect.prevent.creeper-explosion", true),
	prevent_creeper_playerdmg("protect.prevent.creeper-playerdamage", false),
	prevent_creeper_blockdmg("protect.prevent.creeper-blockdamage", false),
	prevent_enderman_pickup("protect.prevent.enderman-pickup", false),
	prevent_villager_death("protect.prevent.villager-death", false),
	prevent_enderdragon_blockdmg("protect.prevent.enderdragon-blockdamage", true),
	prevent_entitytarget("protect.prevent.entitytarget", false),
	protect_rails("protect.protect.rails", true),
	protect_below_rails("protect.protect.block-below", true),
	protect_signs("protect.protect.signs", true),
	protect_against_signs("protect.protect.block-below", true),
	enderdragon_fakeexplosions("protect.enderdragon-fakeexplosions", false);
	private final String configName;
	private final String defValueString;
	private final boolean defValueBoolean;
	private final boolean isList;
	private final boolean isString;

	private ProtectConfig(final String configName)
	{
		this(configName, null, false, true, false);
	}

	private ProtectConfig(final String configName, final String defValueString)
	{
		this(configName, defValueString, false, false, true);
	}

	private ProtectConfig(final String configName, final boolean defValueBoolean)
	{
		this(configName, null, defValueBoolean, false, false);
	}

	private ProtectConfig(final String configName, final String defValueString, final boolean defValueBoolean, final boolean isList, final boolean isString)
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
