package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.block.Block;


public class EssentialsProtectData
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static final String mysqlDriver = "com.mysql.jdbc.Driver";
	private static String mysqlUsername;
	private static String mysqlPassword;
	private static String mysqlDatabase;
	private static String dataType;
	private static final String sqlite = "jdbc:sqlite:plugins/Essentials/EssentialsProtect.db";
	private static final String mysqlTable;
	private static final String sqliteTable;
	private static final String insertQuery;
	private static final String countByLocationQuery;
	private static final String countByPlayerLocationQuery;
	private static final String playerByLocationQuery;
	private static final String deleteByLocationQuery;

	static
	{
		mysqlTable = EssentialsProtectSqlProperties.EssentialsProtect;
		sqliteTable = EssentialsProtectSqlProperties.EssentialsProtect_sqlite;
		insertQuery = EssentialsProtectSqlProperties.Insert;
		countByLocationQuery = EssentialsProtectSqlProperties.CountByLocation;
		countByPlayerLocationQuery = EssentialsProtectSqlProperties.CountByPLayerLocation;
		playerByLocationQuery = EssentialsProtectSqlProperties.PlayerByLocation;
		deleteByLocationQuery = EssentialsProtectSqlProperties.DeleteByLocation;
		mysqlUsername = EssentialsProtect.dataSettings.get("protect.username");
		mysqlPassword = EssentialsProtect.dataSettings.get("protect.password");
		mysqlDatabase = EssentialsProtect.dataSettings.get("protect.mysqlDb");
		dataType = EssentialsProtect.dataSettings.get("protect.datatype");
	}

	public EssentialsProtectData()
	{
	}

	public static String formatCoords(int x, int y, int z)
	{
		return x + "," + y + "," + z;
	}

	public void insertProtectionIntoDb(String worldname, String playerName, int x, int y, int z)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}
			ps = conn.prepareStatement(insertQuery);
			ps.setString(1, worldname);
			ps.setString(2, playerName);
			ps.setInt(3, x);
			ps.setInt(4, y);
			ps.setInt(5, z);
			ps.executeUpdate();

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unable to add protection into SQL", ex);
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "[EssentialsProtect] Class not found", e);
		}
		finally
		{
			try
			{
				if (ps != null)
				{
					ps.close();
				}
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			}
			catch (SQLException ex)
			{
				logger.log(Level.SEVERE, "[EssentialsProtect] Could not close connection to SQL", ex);
			}
		}
	}

	public boolean canDestroy(String worldName, String playerName, Block block)
	{
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();

		int rowCount = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(countByLocationQuery);
			ps.setString(1, worldName);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			rs = ps.executeQuery();
			rs.next();
			rowCount = rs.getInt(1);
			rs.close();
			ps.close();

			if (rowCount == 0)
			{
				return true;
			}
			else
			{
				ps = conn.prepareStatement(countByPlayerLocationQuery);
				ps.setString(1, worldName);
				ps.setString(2, playerName);
				ps.setInt(3, x);
				ps.setInt(4, y);
				ps.setInt(5, z);
				rs = ps.executeQuery();
				rs.next();
				rowCount = rs.getInt(1);

				if (rowCount == 0)
				{
					return false;
				}

			}

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unable to query Protection", ex);
		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unable to query Protection", e);
		}
		finally
		{
			try
			{
				if (ps != null)
				{
					ps.close();
				}
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			}
			catch (SQLException ex)
			{
				logger.log(Level.SEVERE, "[EssentialsProtection] Could not close connection to SQL", ex);
			}
		}
		return true;
	}

	@SuppressWarnings("CallToThreadDumpStack")
	public static void createSqlTable()
	{
		Connection conn = null;
		Statement st = null;

		try
		{
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}

			st = conn.createStatement();
			st.executeUpdate(dataType.contentEquals("mysql") ? mysqlTable : sqliteTable);
		}
		catch (SQLException s)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Could not create table for " + dataType, s);

		}
		catch (ClassNotFoundException ex)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Could not find driver for " + dataType, ex);

		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unexpected error occured whilst creating table ", e);
		}
		finally
		{
			try
			{
				if (conn != null && !conn.isClosed())
				{
					try
					{
						conn.close();
					}
					catch (SQLException e)
					{
						logger.log(Level.SEVERE, "[EssentialsProtect] Unexpected error occured whilst closing the connection", e);
					}
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getBlockOwner(String worldName, String playerName, Block block)
	{
		String returnPlayerName = null;
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(playerByLocationQuery);

			ps.setString(1, worldName);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			rs = ps.executeQuery();
			while (rs.next())
			{
				returnPlayerName = rs.getString("playerName");
			}
			rs.close();
			ps.close();

		}
		catch (SQLException ex)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unable to query EssentialsProtection", ex);
		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Unable to query EssentialsProtection", e);
		}
		finally
		{
			try
			{
				if (ps != null)
				{
					ps.close();
				}
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			}
			catch (SQLException ex)
			{
				logger.log(Level.SEVERE, "[EssentialsProtection] Could not close connection to SQL", ex);
			}

		}
		return returnPlayerName;
	}

	
	public void removeProtectionFromDB(Block block)
	{
		try
		{
			Connection conn = null;
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername,
												   mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}
			PreparedStatement ps = null;
			try
			{
				ps = conn.prepareStatement(deleteByLocationQuery);
				ps.setString(1, block.getWorld().getName());
				ps.setInt(2, block.getX());
				ps.setInt(3, block.getY());
				ps.setInt(4, block.getZ());
				ps.executeUpdate();

			}
			catch (SQLException ex)
			{
				logger.log(Level.WARNING,
						   "[EssentialsProtect] Could not delete block data from database",
						   ex);
			}
			finally
			{
				if (conn != null && !conn.isClosed())
				{
					conn.close();
				}
				if (ps != null)
				{
					ps.close();
				}
			}

		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, " [EssentialsProtect] Exception occured whilst trying to delete data from sql", e);
		}

	}

	public void removeProtectionFromDB(Block block, boolean removeBelow)
	{
		try
		{
			Connection conn = null;
			if (dataType.contentEquals("mysql"))
			{
				Class.forName(mysqlDriver);
				conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(sqlite);
			}
			PreparedStatement ps = null;
			try
			{
				ps = conn.prepareStatement(deleteByLocationQuery);
				ps.setString(1, block.getWorld().getName());
				ps.setInt(2, block.getX());
				ps.setInt(3, block.getY());
				ps.setInt(4, block.getZ());
				ps.executeUpdate();
				if (removeBelow)
				{
					ps = conn.prepareStatement(deleteByLocationQuery);
					ps.setString(1, block.getWorld().getName());
					ps.setInt(2, block.getX());
					ps.setInt(3, block.getY() - 1);
					ps.setInt(4, block.getZ());
					ps.executeUpdate();
				}

			}
			catch (SQLException ex)
			{
				logger.log(Level.WARNING, "[EssentialsProtect] Could not delete block data from database", ex);
			}
			finally
			{
				if (conn != null && !conn.isClosed())
				{
					conn.close();
				}
				if (ps != null)
				{
					ps.close();
				}
			}

		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "[EssentialsProtect] Exception occured whilst trying to delete data from sql", e);
		}

	}

	public boolean isBlockAboveProtectedRail(Block block)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (block.getTypeId() == 66)
		{
			try
			{
				if (dataType.contentEquals("mysql"))
				{
					Class.forName(mysqlDriver);
					conn = DriverManager.getConnection(mysqlDatabase, mysqlUsername, mysqlPassword);
				}
				else
				{
					Class.forName("org.sqlite.JDBC");
					conn = DriverManager.getConnection(sqlite);
				}
				int rowCount = 0;
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(countByLocationQuery);
				ps.setString(1, block.getWorld().getName());
				ps.setInt(2, block.getX());
				ps.setInt(3, block.getY());
				ps.setInt(4, block.getZ());
				rs = ps.executeQuery();
				rs.next();
				rowCount = rs.getInt(1);
				rs.close();
				ps.close();

				if (rowCount == 0)
				{

					return false;
				}
				else
				{
					return true;
				}
			}
			catch (SQLException s)
			{
				logger.log(Level.SEVERE, "[EssentialsProtect] Could not query protection", s);

			}
			catch (ClassNotFoundException ex)
			{
				logger.log(Level.SEVERE, "[EssentialsProtect] Could not find driver for " + dataType, ex);

			}
			catch (Throwable e)
			{
				logger.log(Level.SEVERE, "[EssentialsProtect] Unexpected error occured whilst creating table", e);
			}
			finally
			{
				try
				{
					if (conn != null && !conn.isClosed())
					{
						try
						{
							conn.close();
						}
						catch (SQLException e)
						{
							logger.log(Level.SEVERE, "[EssentialsProtect] Unexpected error occured whilst closing the connection", e);
						}

					}
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
