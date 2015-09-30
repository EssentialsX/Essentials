package com.earth2me.essentials;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import net.ess3.api.IEssentials;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.api.IUserMap;
import com.earth2me.essentials.sqlite.UserDatabase;
import com.earth2me.essentials.sqlite.UserDatabase.UserEntry;
import com.earth2me.essentials.sqlite.UserDatabase.UserHistoryEntry;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class SqlUserMap extends CacheLoader<UUID, User> implements IConf, IUserMap {
	private final transient IEssentials ess;
	private final transient UserDatabase database;
	private final transient LoadingCache<UUID, User> users;
	private final transient ConcurrentSkipListSet<UUID> keys = new ConcurrentSkipListSet<UUID>();
	private final transient ConcurrentSkipListMap<String, UUID> names = new ConcurrentSkipListMap<String, UUID>();
	private final transient ConcurrentSkipListMap<UUID, ArrayList<String>> history = new ConcurrentSkipListMap<UUID, ArrayList<String>>();
	private SqlUUIDMap uuidMap;
	
	public SqlUserMap( final IEssentials ess, final UserDatabase database ) {
		super();
		this.ess = ess;
		this.database = database;
		uuidMap = new SqlUUIDMap( ess, database );
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
					try {
						keys.clear();
						users.invalidateAll();
						keys.addAll( SqlUserMap.this.database.selectAllDistinctUUIDs() );
						uuidMap.loadAllUsers( names, history );
					} catch ( SQLException e ) {
						throw new RuntimeException( e );
					}
				}
			}
		} );
	}
	
	protected UserDatabase getDatabase() {
		return this.database;
	}
	
	public boolean userExists( final UUID uuid ) {
		return keys.contains( uuid );
	}
	
	public User getUser( final String name ) {
		try {
			final String sanitizedName = StringUtil.safeString( name );
			if ( names.containsKey( sanitizedName ) ) {
				final UUID uuid = names.get( sanitizedName );
				return getUser( uuid );
			}
			
			Player player = Bukkit.getPlayerExact( sanitizedName );
			if(player != null) {
				names.put( sanitizedName, player.getUniqueId() );
				try {
					this.database.insertName( player.getUniqueId(), sanitizedName );
				} catch ( SQLException e ) {
					throw new RuntimeException( e );
				}
				return getUser(player.getUniqueId());
			}
			
			Set<UserHistoryEntry> search = this.database.searchExact( sanitizedName, 1 );
			Iterator<UserHistoryEntry> it = search.iterator();
			if(it.hasNext() ) {
				UserHistoryEntry e = it.next();
				final UUID uuid = e.getUuid();
				this.names.put( e.getUsername(), e.getUuid() );
				return getUser( uuid );				
			}
			
			/*
			 * final File userFile = getUserFileFromString( sanitizedName ); if ( userFile.exists() ) {
			 * ess.getLogger().info( "Importing user " + name + " to usermap." ); User user = new User( new
			 * OfflinePlayer( sanitizedName, ess.getServer() ), ess ); trackUUID( user.getBase().getUniqueId(),
			 * user.getName(), true ); return user; }
			 */
			return null;
		} catch ( UncheckedExecutionException | SQLException ex ) {
			return null;
		}
	}
	
	public User getUser( final UUID uuid ) {
		try {
			return users.get( uuid );
		} catch ( ExecutionException ex ) {
			//ess.getLogger().log( Level.SEVERE, "ExecutionException", ex );
			return null;
		} catch ( UncheckedExecutionException ex ) {
			//ess.getLogger().log( Level.SEVERE, "UncheckedExecutionException", ex );
			return null;
		}
	}
	
	public void trackUUID( final UUID uuid, final String name, boolean replace ) {
		if ( uuid != null ) {
			keys.add( uuid );
			if ( name != null && name.length() > 0 ) {
				final String keyName = StringUtil.safeString( name );
				
				boolean contained = names.containsKey( keyName );
				
				if(replace || !contained ) {
					names.put( keyName, uuid );
					try {
						this.database.insertName( uuid, keyName );
					} catch ( SQLException e ) {
						throw new RuntimeException( e );
					}
				} else {
					if ( ess.getSettings().isDebug() ) {
						ess.getLogger().info( "Found old UUID for " + name + " (" + uuid.toString()
								+ "). Not adding to usermap." );
					}
				}
				/*
				if ( !names.containsKey( keyName ) ) {
					names.put( keyName, uuid );
					try {
						this.database.insertName( uuid, keyName );
					} catch ( SQLException e ) {
						throw new RuntimeException( e );
					}
				} else if ( !names.get( keyName ).equals( uuid ) ) {
					if ( replace ) {
						ess.getLogger().info( "Found new UUID for " + name + ". Replacing "
								+ names.get( keyName ).toString() + " with " + uuid.toString() );
						names.put( keyName, uuid );
						try {
							this.database.insertName( uuid, keyName );
						} catch ( SQLException e ) {
							throw new RuntimeException( e );
						}
					} else {
						if ( ess.getSettings().isDebug() ) {
							ess.getLogger().info( "Found old UUID for " + name + " (" + uuid.toString()
									+ "). Not adding to usermap." );
						}
					}
				}*/
			}
		}
	}
	
	@Override
	public User load( final UUID uuid ) throws Exception {
		Preconditions.checkNotNull( uuid );
		Player player = ess.getServer().getPlayer( uuid );
		if ( player != null ) {
			final User user = new User( player, ess );
			trackUUID( uuid, player.getName(), true );
			return user;
		}
		
		UserEntry e = this.database.get( uuid );
		if ( e != null ) {
			player = new OfflinePlayer( uuid, ess.getServer() );
			final User user = new User( player, ess );
			( ( OfflinePlayer ) player ).setName( user.getLastAccountName() );
			trackUUID( uuid, user.getName(), false );
			return user;
		}
		
		throw new Exception( "User not found!" );
	}
	
	@Override
	public void reloadConfig() {
		getUUIDMap().forceWriteUUIDMap();
		loadAllUsersAsync( ess );
	}
	
	public void invalidateAll() {
		users.invalidateAll();
	}
	
	public void removeUser( final String name ) {
		if ( name == null ) {
			ess.getLogger().warning( "Name collection is null, cannot remove user." );
			return;
		}
		String keyname = StringUtil.safeString( name );
		UUID uuid = names.get( keyname );
		if ( uuid != null ) {
			keys.remove( uuid );
			users.invalidate( uuid );
		}
		names.remove( keyname );
	}
	
	public Set<UUID> getAllUniqueUsers() {
		return Collections.unmodifiableSet( keys.clone() );
	}
	
	public int getUniqueUsers() {
		return keys.size();
	}
	
	public Map<String, UUID> getNames() {
		return Collections.unmodifiableMap( names );
	}
	
	protected ConcurrentSkipListMap<UUID, ArrayList<String>> getHistory() {
		return history;
	}
	
	public List<String> getUserHistory( final UUID uuid ) {
		return history.get( uuid );
	}
	
	public SqlUUIDMap getUUIDMap() {
		return uuidMap;
	}
	
	@Override
	public File getUserFileFromString( String name ) {
		throw new UnsupportedOperationException();
	}
	
	/*
	 * private File getUserFileFromID( final UUID uuid ) { final File userFolder = new File( ess.getDataFolder(),
	 * "userdata" ); return new File( userFolder, uuid.toString() + ".yml" ); }
	 * 
	 * public File getUserFileFromString( final String name ) { final File userFolder = new File( ess.getDataFolder(),
	 * "userdata" ); return new File( userFolder, StringUtil.sanitizeFileName( name ) + ".yml" ); }
	 */
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
