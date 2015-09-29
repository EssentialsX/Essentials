package com.earth2me.essentials.api;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface IEssentialsConf {
	
	void reset();
	
	void delete() throws Exception;
	
	void reload() throws Exception;
	
	void setTemplateName( String templateName );
	
	void setTemplateName( String templateName, Class<?> resClass );
	
	void startTransaction();
	
	void stopTransaction();
	
	void save();
	
	boolean hasProperty( String path );
	
	Location getLocation( String path, Server server ) throws InvalidWorldException;
	
	void setProperty( String path, Location loc );
	
	ItemStack getItemStack( String path );
	
	void setProperty( String path, ItemStack stack );
	
	void setProperty( String path, List object );
	
	void setProperty( String path, Map object );
	
	Object getProperty( String path );
	
	void setProperty( String path, BigDecimal bigDecimal );
	
	void setProperty( String path, Object object );
	
	void removeProperty( String path );
	
	Object get( String path );
	
	Object get( String path, Object def );
	
	BigDecimal getBigDecimal( String path, BigDecimal def );
	
	boolean getBoolean( String path );
	
	boolean getBoolean( String path, boolean def );
	
	List<Boolean> getBooleanList( String path );
	
	List<Byte> getByteList( String path );
	
	List<Character> getCharacterList( String path );
	
	ConfigurationSection getConfigurationSection( String path );
	
	double getDouble( String path );
	
	double getDouble( String path, double def );
	
	List<Double> getDoubleList( String path );
	
	List<Float> getFloatList( String path );
	
	int getInt( String path );
	
	int getInt( String path, int def );
	
	List<Integer> getIntegerList( String path );
	
	ItemStack getItemStack( String path, ItemStack def );
	
	Set<String> getKeys( boolean deep );
	
	List<?> getList( String path );
	
	List<?> getList( String path, List<?> def );
	
	long getLong( String path );
	
	long getLong( String path, long def );
	
	List<Long> getLongList( String path );
	
	Map<String, Object> getMap();
	
	List<Map<?, ?>> getMapList( String path );
	
	OfflinePlayer getOfflinePlayer( String path );
	
	OfflinePlayer getOfflinePlayer( String path, OfflinePlayer def );
	
	List<Short> getShortList( String path );
	
	String getString( String path );
	
	String getString( String path, String def );
	
	List<String> getStringList( String path );
	
	Map<String, Object> getValues( boolean deep );
	
	Vector getVector( String path );
	
	Vector getVector( String path, Vector def );
	
	boolean isBoolean( String path );
	
	boolean isConfigurationSection( String path );
	
	boolean isDouble( String path );
	
	boolean isInt( String path );
	
	boolean isItemStack( String path );
	
	boolean isList( String path );
	
	boolean isLong( String path );
	
	boolean isOfflinePlayer( String path );
	
	boolean isSet( String path );
	
	boolean isString( String path );
	
	boolean isVector( String path );
	
	void set( String path, Object value );
	
	void cleanup();
}