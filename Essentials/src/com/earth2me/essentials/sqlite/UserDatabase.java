package com.earth2me.essentials.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.sql.DataSource;

import lombok.Data;
import lombok.Getter;
import net.ess3.api.IEssentials;

import org.bukkit.configuration.InvalidConfigurationException;

import com.earth2me.essentials.api.IUserEntry;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class UserDatabase {
	protected static final Logger LOGGER = Logger.getLogger( "Essentials" );
	
	private final IEssentials ess;
	
	private final DataSource dataSource;
	
	public UserDatabase( final IEssentials ess, DataSource dataSource ) throws SQLException {
		Preconditions.checkNotNull( ess );
		Preconditions.checkNotNull( dataSource );
		this.ess = ess;
		this.dataSource = dataSource;
		this.createTables();
	}
	
	private void createTables() throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "CREATE TABLE IF NOT EXISTS `userdata` "
					+ "( `uuid` CHAR(36) COLLATE NOCASE NOT NULL , "
					+ "`userconfig` TEXT COLLATE NOCASE NOT NULL DEFAULT '\"\"' , "
					+ "`timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , " + "PRIMARY KEY (`uuid`) );" ) ) {
				stmnt.executeUpdate();
			}
			try ( PreparedStatement stmnt = con.prepareStatement( "CREATE TABLE IF NOT EXISTS `names` "
					+ "( `uuid` char(36) COLLATE NOCASE NOT NULL , "
					+ "`username` varchar(16) COLLATE NOCASE NOT NULL , "
					+ "`timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,"
					+ "PRIMARY KEY (`uuid`,`username`));" ) ) {
				stmnt.executeUpdate();
			}
		}
	}
	
	public UserEntry getOrCreate( UUID uuid, String defaultData ) throws SQLException, InvalidConfigurationException {
		Preconditions.checkNotNull( uuid );
		UserEntry result = get( uuid );
		if ( result == null ) {
			result = create( uuid, defaultData );
		}
		return result;
	}
	
	public UserEntry create( UUID uuid, String configString ) throws InvalidConfigurationException {
		Preconditions.checkNotNull( uuid );
		return new UserEntry( uuid, configString );
	}
	
	public synchronized UserEntry get( UUID uuid ) throws SQLException, InvalidConfigurationException {
		Preconditions.checkNotNull( uuid );
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "SELECT * FROM userdata WHERE uuid = ?;" ) ) {
				stmnt.setString( 1, uuid.toString() );
				try ( ResultSet rs = stmnt.executeQuery() ) {
					if ( rs.next() ) {
						if ( !uuid.equals( UUID.fromString( rs.getString( "uuid" ) ) ) ) {
							throw new RuntimeException( "uuid changed after request" );
						}
						
						return new UserEntry( uuid, rs.getString( "userconfig" ) );
					}
				}
			}
		}
		return null;
	}
	
	public synchronized UUID get( String username ) throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "SELECT uuid FROM names WHERE username = ? "
					+ "ORDER BY timestamp DESC LIMIT 1;" ) ) {
				stmnt.setString( 1, username );
				try ( ResultSet rs = stmnt.executeQuery() ) {
					if ( rs.next() ) {
						return UUID.fromString( rs.getString( "uuid" ) );
					}
				}
			}
		}
		return null;
	}
	
	public synchronized void remove( UUID uuid ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "DELETE FROM userdata WHERE uuid = ?;" ) ) {
				stmnt.setString( 1, uuid.toString() );
				stmnt.executeUpdate();
			}
			try ( PreparedStatement stmnt = con.prepareStatement( "DELETE FROM names WHERE uuid = ?;" ) ) {
				stmnt.setString( 1, uuid.toString() );
				stmnt.executeUpdate();
			}
		}
	}
	
	public synchronized Set<UserHistoryEntry> search( String username, int limit ) throws SQLException {
		Preconditions.checkArgument( limit > 0 );
		Set<UserHistoryEntry> result = Sets.newHashSet();
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "SELECT uuid, username FROM names WHERE username LIKE ? ORDER BY timestamp DESC LIMIT "
							+ limit + ";" ) ) {
				stmnt.setString( 1, username );
				try ( ResultSet rs = stmnt.executeQuery() ) {
					while ( rs.next() ) {
						result.add( new UserHistoryEntry( UUID.fromString( rs.getString( "uuid" ) ), rs
								.getString( "username" ), rs.getTimestamp( "timestamp" ) ) );
					}
				}
			}
		}
		return result;
	}
	
	public synchronized Set<UserHistoryEntry> searchExact( UUID uuid, int limit ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		Preconditions.checkArgument( limit > 0 );
		Set<UserHistoryEntry> result = Sets.newHashSet();
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "SELECT uuid, username FROM names WHERE uuid = ? ORDER BY timestamp DESC LIMIT "
							+ limit + ";" ) ) {
				stmnt.setString( 1, uuid.toString() );
				try ( ResultSet rs = stmnt.executeQuery() ) {
					while ( rs.next() ) {
						result.add( new UserHistoryEntry( UUID.fromString( rs.getString( "uuid" ) ), rs
								.getString( "username" ), rs.getTimestamp( "timestamp" ) ) );
					}
				}
			}
		}
		return result;
	}
	
	public synchronized Set<UserHistoryEntry> searchExact( String username, int limit ) throws SQLException {
		Preconditions.checkArgument( limit > 0 );
		Set<UserHistoryEntry> result = Sets.newHashSet();
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "SELECT uuid, username FROM names WHERE username = ? ORDER BY timestamp DESC LIMIT "
							+ limit + ";" ) ) {
				stmnt.setString( 1, username );
				try ( ResultSet rs = stmnt.executeQuery() ) {
					while ( rs.next() ) {
						result.add( new UserHistoryEntry( UUID.fromString( rs.getString( "uuid" ) ), rs
								.getString( "username" ), rs.getTimestamp( "timestamp" ) ) );
					}
				}
			}
		}
		return result;
	}
	
	public synchronized void insertNames( Map<String, UUID> names ) throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "INSERT OR REPLACE INTO names (uuid, username) VALUES (?,?);" ) ) {
				
				for ( Map.Entry<String, UUID> e : names.entrySet() ) {
					stmnt.setString( 1, e.getValue().toString() );
					stmnt.setString( 2, e.getKey() );
					stmnt.addBatch();
				}
				stmnt.executeBatch();
			}
		}
	}
	
	public synchronized void insertName( UUID uuid, String username ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		Preconditions.checkNotNull( username );
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "INSERT OR REPLACE INTO names (uuid, username) VALUES (?,?);" ) ) {
				stmnt.setString( 1, uuid.toString() );
				stmnt.setString( 2, username );
				stmnt.executeUpdate();
			}
		}
	}
	
	public synchronized Map<String, UUID> selectAllNames() throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "SELECT username, uuid FROM names GROUP BY (uuid) ORDER BY timestamp DESC;" ) ) {
				try ( ResultSet rs = stmnt.executeQuery() ) {
					Map<String, UUID> result = Maps.newHashMap();
					while ( rs.next() ) {
						result.put( rs.getString( "username" ), UUID.fromString( rs.getString( "uuid" ) ) );
					}
					return result;
				}
			}
		}
	}
	
	public synchronized ListMultimap<UUID, String> selectNameHistory() throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con
					.prepareStatement( "SELECT username, uuid FROM names ORDER BY timestamp DESC;" ) ) {
				try ( ResultSet rs = stmnt.executeQuery() ) {
					ListMultimap<UUID, String> result = Multimaps
							.newListMultimap( new HashMap<UUID, Collection<String>>(), new Supplier<ArrayList<String>>() {
								public ArrayList<String> get() {
									return new ArrayList<String>();
								}
							} );
					while ( rs.next() ) {
						result.put( UUID.fromString( rs.getString( "uuid" ) ), rs.getString( "username" ) );
					}
					return result;
				}
			}
		}
	}
	
	public synchronized Set<UUID> selectAllDistinctUUIDs() throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "SELECT DISTINCT uuid FROM names;" ) ) {
				try ( ResultSet rs = stmnt.executeQuery() ) {
					Set<UUID> result = Sets.newHashSet();
					while ( rs.next() ) {
						result.add( UUID.fromString( rs.getString( 1 ) ) );
					}
					return result;
				}
			}
		}
	}
	
	public synchronized int countAllDistinctUUIDs() throws SQLException {
		try ( Connection con = this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "SELECT COUNT(*) FROM userdata;" ) ) {
				try ( ResultSet rs = stmnt.executeQuery() ) {
					if ( rs.next() ) {
						return rs.getInt( 1 );
					}
				}
			}
		}
		return 0;
	}
	
	public synchronized void insertConfig( final UUID uuid, String configString ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		Preconditions.checkNotNull( configString );
		try ( Connection con = UserDatabase.this.dataSource.getConnection() ) {
			try ( final PreparedStatement ps = con
					.prepareStatement( "INSERT OR REPLACE INTO `userdata` (`uuid`, `userconfig`, `timestamp`) "
							+ "VALUES (?,?,CURRENT_TIMESTAMP);" ) ) {
				ps.setString( 1, uuid.toString() );
				ps.setString( 2, configString );
				ps.executeUpdate();
			}
		}
	}
	
	public synchronized void insertConfigs( final Map<UUID, String> configs ) throws SQLException {
		Preconditions.checkNotNull( configs );
		try ( Connection con = UserDatabase.this.dataSource.getConnection() ) {
			try ( final PreparedStatement ps = con
					.prepareStatement( "INSERT OR REPLACE INTO `userdata` (`uuid`, `userconfig`, `timestamp`) "
							+ "VALUES (?,?,CURRENT_TIMESTAMP);" ) ) {
				for ( Map.Entry<UUID, String> e : configs.entrySet() ) {
					ps.setString( 1, e.getKey().toString() );
					ps.setString( 2, e.getValue() );
					ps.addBatch();
				}
				ps.executeBatch();
			}
		}
	}
	
	public synchronized void deleteConfig( UUID uuid ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		try ( Connection con = UserDatabase.this.dataSource.getConnection() ) {
			try ( PreparedStatement stmnt = con.prepareStatement( "DELETE FROM userdata WHERE uuid = ?;" ) ) {
				stmnt.setString( 1, uuid.toString() );
			}
		}
	}
	
	public synchronized String loadConfig( UUID uuid ) throws SQLException {
		Preconditions.checkNotNull( uuid );
		try ( Connection con = UserDatabase.this.dataSource.getConnection() ) {
			try ( final PreparedStatement ps = con.prepareStatement( "SELECT userconfig FROM userdata WHERE uuid = ?;" ) ) {
				ps.setString( 1, uuid.toString() );
				try ( final ResultSet rs = ps.executeQuery() ) {
					if ( rs.next() ) {
						return rs.getString( 1 );
					}
				}
			}
		}
		return "";
	}
	
	@Data
	public class UserHistoryEntry {
		private final UUID uuid;
		private final String username;
		private final Timestamp timestamp;
	}
	
	public class UserEntry extends DatabaseEssentialsConf implements IUserEntry {
		
		@Getter
		public final UUID uuid;
		
		private UserEntry( final UUID uuid, final String configString ) throws InvalidConfigurationException {
			super( "userconfig." + uuid.toString(), configString );
			Preconditions.checkNotNull( uuid );
			this.uuid = uuid;
		}
		
		@Override
		public void delete() throws SQLException {
			UserDatabase.this.deleteConfig( uuid );
		}
		
		@Override
		protected String loadConfig() throws SQLException {
			return UserDatabase.this.loadConfig( uuid );
		}
		
		@Override
		protected void saveConfig( final String configString ) throws SQLException {
			UserDatabase.this.insertConfig( this.uuid, configString );
		}
		
	}
	
}