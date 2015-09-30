package com.earth2me.essentials.sqlite;

import static com.earth2me.essentials.I18n.tl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.earth2me.essentials.api.IEssentialsConf;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public abstract class DatabaseEssentialsConf extends YamlConfiguration implements IEssentialsConf {
	protected static final Logger LOGGER = Logger.getLogger( "Essentials" );
	protected String templateName = null;
	protected static final Charset UTF8 = Charset.forName( "UTF-8" );
	private Class<?> resourceClass = DatabaseEssentialsConf.class;
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	private final AtomicBoolean transaction = new AtomicBoolean( false );
	
	private final String name;
	
	public DatabaseEssentialsConf( String name ) {
		super();
		this.name = name;
	}
	
	public DatabaseEssentialsConf( String name, String configString ) throws InvalidConfigurationException {
		this( name );
		//LOGGER.info( name );
		if ( configString != null ) {
			this.loadFromString( configString );
		}
	}
	
	@Override
	public void cleanup() {
		this.save();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#reset()
	 */
	public void reset() {
		try {
			super.loadFromString( "" );
		} catch ( InvalidConfigurationException e ) {
			throw new RuntimeException();
		}
	}
	
	protected abstract String loadConfig() throws SQLException;
	
	protected abstract void saveConfig( String configString ) throws SQLException;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#delete()
	 */
	public abstract void delete() throws SQLException;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#reload()
	 */
	public synchronized void reload() throws InvalidConfigurationException, SQLException {
		this.loadFromString( this.loadConfig() );
	}
	
	private void createFromTemplate() {
		try {
			URL url = Resources.getResource( this.resourceClass, templateName );
			String configString = Resources.toString( url, Charsets.UTF_8 );
			this.loadFromString( configString );
			this.saveConfig( configString );
		} catch ( IllegalArgumentException iae ) {
			LOGGER.log( Level.SEVERE, tl( "couldNotFindTemplate", templateName ), iae );
		} catch ( IOException | SQLException ioe ) {
			LOGGER.log( Level.SEVERE, tl( "failedToWriteConfig", this.name ), ioe );
		} catch ( InvalidConfigurationException e ) {
			LOGGER.log( Level.SEVERE, "The config " + this.name + " is broken" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setTemplateName(java.lang.String)
	 */
	public void setTemplateName( final String templateName ) {
		this.templateName = templateName;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setTemplateName(java.lang.String, java.lang.Class)
	 */
	public void setTemplateName( final String templateName, final Class<?> resClass ) {
		this.templateName = templateName;
		this.resourceClass = resClass;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#startTransaction()
	 */
	public void startTransaction() {
		transaction.set( true );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#stopTransaction()
	 */
	public void stopTransaction() {
		transaction.set( false );
		save();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#save()
	 */
	public void save() {
		if ( !transaction.get() ) {
			this.delayedSave();
		}
	}
	
	private Future<?> delayedSave() {
		final String data = saveToString();
		
		if ( data.length() == 0 ) {
			return null;
		}
		
		return EXECUTOR_SERVICE.submit( new WriteRunner<String>( new SQLConsumer<String>() {
			@Override
			public void accept( String t ) throws SQLException {
				DatabaseEssentialsConf.this.saveConfig( t );
			}
		}, data ) );
	}
	
	private static class WriteRunner<T> implements Runnable {
		private final SQLConsumer<T> saving;
		private final T data;
		
		private WriteRunner( final SQLConsumer<T> saving, final T data ) {
			this.saving = saving;
			this.data = data;
		}
		
		@Override
		public void run() {
			try {
				this.saving.accept( this.data );
			} catch ( Exception e ) {
				LOGGER.log( Level.SEVERE, e.getMessage(), e );
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#hasProperty(java.lang.String)
	 */
	public boolean hasProperty( final String path ) {
		return isSet( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getLocation(java.lang.String, org.bukkit.Server)
	 */
	public Location getLocation( final String path, final Server server ) throws InvalidWorldException {
		final String worldString = ( path == null ? "" : path + "." ) + "world";
		final String worldName = getString( worldString );
		if ( worldName == null || worldName.isEmpty() ) {
			return null;
		}
		final World world = server.getWorld( worldName );
		if ( world == null ) {
			throw new InvalidWorldException( worldName );
		}
		return new Location( world, getDouble( ( path == null ? "" : path + "." ) + "x", 0 ), getDouble( ( path == null
				? "" : path + "." ) + "y", 0 ), getDouble( ( path == null ? "" : path + "." ) + "z", 0 ),
				( float ) getDouble( ( path == null ? "" : path + "." ) + "yaw", 0 ),
				( float ) getDouble( ( path == null ? "" : path + "." ) + "pitch", 0 ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, org.bukkit.Location)
	 */
	public void setProperty( final String path, final Location loc ) {
		set( ( path == null ? "" : path + "." ) + "world", loc.getWorld().getName() );
		set( ( path == null ? "" : path + "." ) + "x", loc.getX() );
		set( ( path == null ? "" : path + "." ) + "y", loc.getY() );
		set( ( path == null ? "" : path + "." ) + "z", loc.getZ() );
		set( ( path == null ? "" : path + "." ) + "yaw", loc.getYaw() );
		set( ( path == null ? "" : path + "." ) + "pitch", loc.getPitch() );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getItemStack(java.lang.String)
	 */
	@Override
	public ItemStack getItemStack( final String path ) {
		final ItemStack stack = new ItemStack( Material.valueOf( getString( path + ".type", "AIR" ) ), getInt( path
				+ ".amount", 1 ), ( short ) getInt( path + ".damage", 0 ) );
		final ConfigurationSection enchants = getConfigurationSection( path + ".enchant" );
		if ( enchants != null ) {
			for ( String enchant : enchants.getKeys( false ) ) {
				final Enchantment enchantment = Enchantment.getByName( enchant.toUpperCase( Locale.ENGLISH ) );
				if ( enchantment == null ) {
					continue;
				}
				final int level = getInt( path + ".enchant." + enchant, enchantment.getStartLevel() );
				stack.addUnsafeEnchantment( enchantment, level );
			}
		}
		return stack;
		/*
		 * , (byte)getInt(path + ".data", 0)
		 */
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, org.bukkit.inventory.ItemStack)
	 */
	public void setProperty( final String path, final ItemStack stack ) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put( "type", stack.getType().toString() );
		map.put( "amount", stack.getAmount() );
		map.put( "damage", stack.getDurability() );
		Map<Enchantment, Integer> enchantments = stack.getEnchantments();
		if ( !enchantments.isEmpty() ) {
			Map<String, Integer> enchant = new HashMap<String, Integer>();
			for ( Map.Entry<Enchantment, Integer> entry : enchantments.entrySet() ) {
				enchant.put( entry.getKey().getName().toLowerCase( Locale.ENGLISH ), entry.getValue() );
			}
			map.put( "enchant", enchant );
		}
		// getData().getData() is broken
		// map.put("data", stack.getDurability());
		set( path, map );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, java.util.List)
	 */
	public void setProperty( String path, List object ) {
		set( path, new ArrayList( object ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, java.util.Map)
	 */
	public void setProperty( String path, Map object ) {
		set( path, new LinkedHashMap( object ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getProperty(java.lang.String)
	 */
	public Object getProperty( String path ) {
		return get( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, java.math.BigDecimal)
	 */
	public void setProperty( final String path, final BigDecimal bigDecimal ) {
		set( path, bigDecimal.toString() );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty( String path, Object object ) {
		set( path, object );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#removeProperty(java.lang.String)
	 */
	public void removeProperty( String path ) {
		set( path, null );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#get(java.lang.String)
	 */
	@Override
	public synchronized Object get( String path ) {
		return super.get( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#get(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized Object get( String path, Object def ) {
		return super.get( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public synchronized BigDecimal getBigDecimal( final String path, final BigDecimal def ) {
		final String input = super.getString( path );
		return toBigDecimal( input, def );
	}
	
	public static BigDecimal toBigDecimal( final String input, final BigDecimal def ) {
		if ( input == null || input.isEmpty() ) {
			return def;
		} else {
			try {
				return new BigDecimal( input, MathContext.DECIMAL128 );
			} catch ( NumberFormatException e ) {
				return def;
			} catch ( ArithmeticException e ) {
				return def;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getBoolean(java.lang.String)
	 */
	@Override
	public synchronized boolean getBoolean( String path ) {
		return super.getBoolean( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getBoolean(java.lang.String, boolean)
	 */
	@Override
	public synchronized boolean getBoolean( String path, boolean def ) {
		return super.getBoolean( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getBooleanList(java.lang.String)
	 */
	@Override
	public synchronized List<Boolean> getBooleanList( String path ) {
		return super.getBooleanList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getByteList(java.lang.String)
	 */
	@Override
	public synchronized List<Byte> getByteList( String path ) {
		return super.getByteList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getCharacterList(java.lang.String)
	 */
	@Override
	public synchronized List<Character> getCharacterList( String path ) {
		return super.getCharacterList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getConfigurationSection(java.lang.String)
	 */
	@Override
	public synchronized ConfigurationSection getConfigurationSection( String path ) {
		return super.getConfigurationSection( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getDouble(java.lang.String)
	 */
	@Override
	public synchronized double getDouble( String path ) {
		return super.getDouble( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getDouble(java.lang.String, double)
	 */
	@Override
	public synchronized double getDouble( final String path, final double def ) {
		return super.getDouble( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getDoubleList(java.lang.String)
	 */
	@Override
	public synchronized List<Double> getDoubleList( String path ) {
		return super.getDoubleList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getFloatList(java.lang.String)
	 */
	@Override
	public synchronized List<Float> getFloatList( String path ) {
		return super.getFloatList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getInt(java.lang.String)
	 */
	@Override
	public synchronized int getInt( String path ) {
		return super.getInt( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getInt(java.lang.String, int)
	 */
	@Override
	public synchronized int getInt( String path, int def ) {
		return super.getInt( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getIntegerList(java.lang.String)
	 */
	@Override
	public synchronized List<Integer> getIntegerList( String path ) {
		return super.getIntegerList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getItemStack(java.lang.String,
	 * org.bukkit.inventory.ItemStack)
	 */
	@Override
	public synchronized ItemStack getItemStack( String path, ItemStack def ) {
		return super.getItemStack( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getKeys(boolean)
	 */
	@Override
	public synchronized Set<String> getKeys( boolean deep ) {
		return super.getKeys( deep );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getList(java.lang.String)
	 */
	@Override
	public synchronized List<?> getList( String path ) {
		return super.getList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getList(java.lang.String, java.util.List)
	 */
	@Override
	public synchronized List<?> getList( String path, List<?> def ) {
		return super.getList( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getLong(java.lang.String)
	 */
	@Override
	public synchronized long getLong( String path ) {
		return super.getLong( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getLong(java.lang.String, long)
	 */
	@Override
	public synchronized long getLong( final String path, final long def ) {
		return super.getLong( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getLongList(java.lang.String)
	 */
	@Override
	public synchronized List<Long> getLongList( String path ) {
		return super.getLongList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getMap()
	 */
	public synchronized Map<String, Object> getMap() {
		return map;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getMapList(java.lang.String)
	 */
	@Override
	public synchronized List<Map<?, ?>> getMapList( String path ) {
		return super.getMapList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getOfflinePlayer(java.lang.String)
	 */
	@Override
	public synchronized OfflinePlayer getOfflinePlayer( String path ) {
		return super.getOfflinePlayer( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getOfflinePlayer(java.lang.String, org.bukkit.OfflinePlayer)
	 */
	@Override
	public synchronized OfflinePlayer getOfflinePlayer( String path, OfflinePlayer def ) {
		return super.getOfflinePlayer( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getShortList(java.lang.String)
	 */
	@Override
	public synchronized List<Short> getShortList( String path ) {
		return super.getShortList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getString(java.lang.String)
	 */
	@Override
	public synchronized String getString( String path ) {
		return super.getString( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getString(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String getString( String path, String def ) {
		return super.getString( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getStringList(java.lang.String)
	 */
	@Override
	public synchronized List<String> getStringList( String path ) {
		return super.getStringList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getValues(boolean)
	 */
	@Override
	public synchronized Map<String, Object> getValues( boolean deep ) {
		return super.getValues( deep );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getVector(java.lang.String)
	 */
	@Override
	public synchronized Vector getVector( String path ) {
		return super.getVector( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#getVector(java.lang.String, org.bukkit.util.Vector)
	 */
	@Override
	public synchronized Vector getVector( String path, Vector def ) {
		return super.getVector( path, def );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isBoolean(java.lang.String)
	 */
	@Override
	public synchronized boolean isBoolean( String path ) {
		return super.isBoolean( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isConfigurationSection(java.lang.String)
	 */
	@Override
	public synchronized boolean isConfigurationSection( String path ) {
		return super.isConfigurationSection( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isDouble(java.lang.String)
	 */
	@Override
	public synchronized boolean isDouble( String path ) {
		return super.isDouble( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isInt(java.lang.String)
	 */
	@Override
	public synchronized boolean isInt( String path ) {
		return super.isInt( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isItemStack(java.lang.String)
	 */
	@Override
	public synchronized boolean isItemStack( String path ) {
		return super.isItemStack( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isList(java.lang.String)
	 */
	@Override
	public synchronized boolean isList( String path ) {
		return super.isList( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isLong(java.lang.String)
	 */
	@Override
	public synchronized boolean isLong( String path ) {
		return super.isLong( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isOfflinePlayer(java.lang.String)
	 */
	@Override
	public synchronized boolean isOfflinePlayer( String path ) {
		return super.isOfflinePlayer( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isSet(java.lang.String)
	 */
	@Override
	public synchronized boolean isSet( String path ) {
		return super.isSet( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isString(java.lang.String)
	 */
	@Override
	public synchronized boolean isString( String path ) {
		return super.isString( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#isVector(java.lang.String)
	 */
	@Override
	public synchronized boolean isVector( String path ) {
		return super.isVector( path );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.sqlite.IEssentialsConf#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized void set( String path, Object value ) {
		super.set( path, value );
	}
	
}