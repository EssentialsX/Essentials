package com.earth2me.essentials.protect.data;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.block.Block;


public abstract class ProtectedBlockJDBC implements IProtectedBlock
{
	protected static final Logger LOGGER = Logger.getLogger("Minecraft");
	protected final ComboPooledDataSource cpds;
	protected abstract PreparedStatement getStatementCreateTable(Connection conn) throws SQLException;
	protected abstract PreparedStatement getStatementUpdateFrom2_0Table(Connection conn) throws SQLException;
	protected abstract PreparedStatement getStatementDeleteAll(Connection conn) throws SQLException;
	protected abstract PreparedStatement getStatementInsert(Connection conn, String world, int x, int y, int z, String playerName) throws SQLException;
	protected abstract PreparedStatement getStatementPlayerCountByLocation(Connection conn, String world, int x, int y, int z, String playerName) throws SQLException;
	protected abstract PreparedStatement getStatementPlayersByLocation(Connection conn, String name, int x, int y, int z) throws SQLException;
	protected abstract PreparedStatement getStatementDeleteByLocation(Connection conn, String world, int x, int y, int z) throws SQLException;
	protected abstract PreparedStatement getStatementAllBlocks(Connection conn) throws SQLException;

	public ProtectedBlockJDBC(String driver, String url) throws PropertyVetoException
	{
		this(driver, url, null, null);
	}

	public ProtectedBlockJDBC(String driver, String url, String username, String password) throws PropertyVetoException
	{
		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(driver);
		cpds.setJdbcUrl(url);
		if (username != null)
		{
			cpds.setUser(username);
			cpds.setPassword(password);
		}
		cpds.setMaxStatements(20);
		createAndConvertTable();
	}

	private void createAndConvertTable()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try
		{
			conn = cpds.getConnection();
			ps = getStatementCreateTable(conn);
			ps.execute();
			ps.close();
			ps = getStatementUpdateFrom2_0Table(conn);
			ps.execute();
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public void clearProtections()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try
		{
			conn = cpds.getConnection();
			ps = getStatementDeleteAll(conn);
			ps.executeUpdate();
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public void importProtections(List<OwnedBlock> blocks)
	{
		for (OwnedBlock ownedBlock : blocks)
		{
			if (ownedBlock.playerName == null)
			{
				continue;
			}
			protectBlock(ownedBlock.world, ownedBlock.x, ownedBlock.y, ownedBlock.z, ownedBlock.playerName);
		}
	}

	@Override
	public List<OwnedBlock> exportProtections()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<OwnedBlock> blocks = new ArrayList<OwnedBlock>();
		try
		{
			conn = cpds.getConnection();
			ps = getStatementAllBlocks(conn);
			rs = ps.executeQuery();
			while (rs.next())
			{
				OwnedBlock ob = new OwnedBlock(
						rs.getInt(2),
						rs.getInt(3),
						rs.getInt(4),
						rs.getString(1),
						rs.getString(5));
				blocks.add(ob);
			}
			return blocks;
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
			return blocks;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public void protectBlock(Block block, String playerName)
	{
		protectBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), playerName);
	}

	private void protectBlock(String world, int x, int y, int z, String playerName)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try
		{
			conn = cpds.getConnection();
			ps = getStatementInsert(conn, world, x, y, z, playerName);
			ps.executeUpdate();
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public boolean isProtected(Block block, String playerName)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			conn = cpds.getConnection();
			ps = getStatementPlayerCountByLocation(conn, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), playerName);
			rs = ps.executeQuery();
			return rs.next() && rs.getInt(1) > 0 && rs.getInt(2) == 0;
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
			return true;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public List<String> getOwners(Block block)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> owners = new ArrayList<String>();
		try
		{
			conn = cpds.getConnection();
			ps = getStatementPlayersByLocation(conn, block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
			rs = ps.executeQuery();
			while (rs.next())
			{
				owners.add(rs.getString(1));
			}
			return owners;
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
			return owners;
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public int unprotectBlock(Block block)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try
		{
			conn = cpds.getConnection();
			ps = getStatementDeleteByLocation(conn, block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
			return ps.executeUpdate();
		}
		catch (SQLException ex)
		{
			LOGGER.log(Level.SEVERE, null, ex);
			return 0;
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	public void onPluginDeactivation()
	{
		cpds.close();
	}
}
