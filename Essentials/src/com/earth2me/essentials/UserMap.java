package com.earth2me.essentials;

import com.earth2me.essentials.api.IUserMap;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import net.ess3.api.IEssentials;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;

public class UserMap extends CacheLoader<UUID, User> implements IConf, IUserMap {
	private final transient IEssentials ess;
	private final transient LoadingCache<UUID, User> users;
	private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<UUID>();
	private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<String, UUID>();
	private final transient ConcurrentSkipListMap<UUID, ArrayList<String>> history = new ConcurrentSkipListMap<UUID, ArrayList<String>>();
	private UUIDMap uuidMap;
	
	public UserMap( final IEssentials ess ) {
		super();
		this.ess = ess;
		uuidMap = new UUIDMap( ess );
		// RemovalListener<UUID, User> remListener = new UserMapRemovalListener();
		// users =
		// CacheBuilder.newBuilder().maximumSize(ess.getSettings().getMaxUserCacheCount()).softValues().removalListener(remListener).build(this);
		users = CacheBuilder.newBuilder().maximumSize( ess.getSettings().getMaxUserCacheCount() ).softValues()
				.build( this );
	}
	
	private void loadAllUsersAsync( final IEssentials ess ) {
		ess.runTaskAsynchronously( new Runnable() {
			@Override
			public void run() {
				synchronized ( users ) {
					final File userdir = new File( ess.getDataFolder(), "userdata" );
					if ( !userdir.exists() ) {
						return;
					}
					keys.clear();
					users.invalidateAll();
					for ( String string : userdir.list() ) {
						if ( !string.endsWith( ".yml" ) ) {
							continue;
						}
						final String name = string.substring( 0, string.length() - 4 );
						try {
							keys.add( UUID.fromString( name ) );
						} catch ( IllegalArgumentException ex ) {
							// Ignore these users till they rejoin.
						}
					}
					uuidMap.loadAllUsers( names, history );
				}
			}
		} );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#userExists(java.util.UUID)
	 */
	public boolean userExists( final UUID uuid ) {
		return keys.contains( uuid );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUser(java.lang.String)
	 */
	public User getUser( final String name ) {
		try {
			final String sanitizedName = StringUtil.safeString( name );
			if ( names.containsKey( sanitizedName ) ) {
				final UUID uuid = names.get( sanitizedName );
				return getUser( uuid );
			}
			
			final File userFile = getUserFileFromString( sanitizedName );
			if ( userFile.exists() ) {
				ess.getLogger().info( "Importing user " + name + " to usermap." );
				User user = new User( new OfflinePlayer( sanitizedName, ess.getServer() ), ess );
				trackUUID( user.getBase().getUniqueId(), user.getName(), true );
				return user;
			}
			return null;
		} catch ( UncheckedExecutionException ex ) {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUser(java.util.UUID)
	 */
	public User getUser( final UUID uuid ) {
		try {
			return users.get( uuid );
		} catch ( ExecutionException ex ) {
			return null;
		} catch ( UncheckedExecutionException ex ) {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#trackUUID(java.util.UUID, java.lang.String, boolean)
	 */
	public void trackUUID( final UUID uuid, final String name, boolean replace ) {
		if ( uuid != null ) {
			keys.add( uuid );
			if ( name != null && name.length() > 0 ) {
				final String keyName = StringUtil.safeString( name );
				if ( !names.containsKey( keyName ) ) {
					names.put( keyName, uuid );
					uuidMap.writeUUIDMap();
				} else if ( !names.get( keyName ).equals( uuid ) ) {
					if ( replace ) {
						ess.getLogger().info( "Found new UUID for " + name + ". Replacing "
								+ names.get( keyName ).toString() + " with " + uuid.toString() );
						names.put( keyName, uuid );
						uuidMap.writeUUIDMap();
					} else {
						if ( ess.getSettings().isDebug() ) {
							ess.getLogger().info( "Found old UUID for " + name + " (" + uuid.toString()
									+ "). Not adding to usermap." );
						}
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#load(java.lang.String)
	 */
	@Override
	public User load( final UUID uuid ) throws Exception {
		Player player = ess.getServer().getPlayer( uuid );
		if ( player != null ) {
			final User user = new User( player, ess );
			trackUUID( uuid, user.getName(), true );
			return user;
		}
		
		final File userFile = getUserFileFromID( uuid );
		
		if ( userFile.exists() ) {
			player = new OfflinePlayer( uuid, ess.getServer() );
			final User user = new User( player, ess );
			( ( OfflinePlayer ) player ).setName( user.getLastAccountName() );
			trackUUID( uuid, user.getName(), false );
			return user;
		}
		
		throw new Exception( "User not found!" );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#reloadConfig()
	 */
	@Override
	public void reloadConfig() {
		getUUIDMap().forceWriteUUIDMap();
		loadAllUsersAsync( ess );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#invalidateAll()
	 */
	public void invalidateAll() {
		users.invalidateAll();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#removeUser(java.lang.String)
	 */
	public void removeUser( final String name ) {
		if ( names == null ) {
			ess.getLogger().warning( "Name collection is null, cannot remove user." );
			return;
		}
		UUID uuid = names.get( name );
		if ( uuid != null ) {
			keys.remove( uuid );
			users.invalidate( uuid );
		}
		names.remove( name );
		names.remove( StringUtil.safeString( name ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getAllUniqueUsers()
	 */
	public Set<UUID> getAllUniqueUsers() {
		return Collections.unmodifiableSet( keys.clone() );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUniqueUsers()
	 */
	public int getUniqueUsers() {
		return keys.size();
	}
	
	public Map<String, UUID> getNames() {
		return Collections.unmodifiableMap( names );
	}
	
	protected ConcurrentSkipListMap<UUID, ArrayList<String>> getHistory() {
		return history;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUserHistory(java.util.UUID)
	 */
	public List<String> getUserHistory( final UUID uuid ) {
		return history.get( uuid );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUUIDMap()
	 */
	public UUIDMap getUUIDMap() {
		return uuidMap;
	}
	
	private File getUserFileFromID( final UUID uuid ) {
		final File userFolder = new File( ess.getDataFolder(), "userdata" );
		return new File( userFolder, uuid.toString() + ".yml" );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.earth2me.essentials.IUserMap#getUserFileFromString(java.lang.String)
	 */
	public File getUserFileFromString( final String name ) {
		final File userFolder = new File( ess.getDataFolder(), "userdata" );
		return new File( userFolder, StringUtil.sanitizeFileName( name ) + ".yml" );
	}
	// class UserMapRemovalListener implements RemovalListener
	// {
	// @Override
	// public void onRemoval(final RemovalNotification notification)
	// {
	// Object value = notification.getValue();
	// if (value != null)
	// {
	// ((User)value).cleanup();
	// }
	// }
	// }
}
