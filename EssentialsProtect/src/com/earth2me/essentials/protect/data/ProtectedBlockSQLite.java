package com.earth2me.essentials.protect.data;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProtectedBlockSQLite extends ProtectedBlockJDBC {

	public ProtectedBlockSQLite(String url) throws PropertyVetoException {
		super("org.sqlite.JDBC", url);
	}
   
	private static final String QueryCreateTable =
		"CREATE TABLE IF NOT EXISTS EssentialsProtect ("
		+ "worldName TEXT ,playerName TEXT, "
		+ "x NUMERIC, y NUMERIC, z NUMERIC)";

	@Override
	protected PreparedStatement getStatementCreateTable(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryCreateTable);
	}
	
	private static final String QueryUpdateFrom2_0Table =
		"CREATE INDEX IF NOT EXISTS position ON EssentialsProtect ("
		+ "worldName, x, z, y)";

	@Override
	protected PreparedStatement getStatementUpdateFrom2_0Table(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryUpdateFrom2_0Table);
	}
	private static final String QueryDeleteAll = "DELETE FROM EssentialsProtect;";

	@Override
	protected PreparedStatement getStatementDeleteAll(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryDeleteAll);
	}
	private static final String QueryInsert =
		"INSERT INTO EssentialsProtect (worldName, x, y, z, playerName) VALUES (?, ?, ?, ?, ?);";

	@Override
	protected PreparedStatement getStatementInsert(Connection conn, String world, int x, int y, int z, String playerName) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(QueryInsert);
		ps.setString(1, world);
		ps.setInt(2, x);
		ps.setInt(3, y);
		ps.setInt(4, z);
		ps.setString(5, playerName);
		return ps;
	}
	private static final String QueryPlayerCountByLocation =
		"SELECT COUNT(playerName), SUM(playerName = ?) FROM EssentialsProtect "
		+ "WHERE worldName = ? AND x = ? AND y = ? AND z = ? GROUP BY x;";

	@Override
	protected PreparedStatement getStatementPlayerCountByLocation(Connection conn, String world, int x, int y, int z, String playerName) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(QueryPlayerCountByLocation);
		ps.setString(1, playerName);
		ps.setString(2, world);
		ps.setInt(3, x);
		ps.setInt(4, y);
		ps.setInt(5, z);
		return ps;
	}
	private static final String QueryPlayersByLocation =
		"SELECT playerName FROM EssentialsProtect WHERE worldname = ? AND x = ? AND y = ? AND z = ?;";

	@Override
	protected PreparedStatement getStatementPlayersByLocation(Connection conn, String world, int x, int y, int z) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(QueryPlayersByLocation);
		ps.setString(1, world);
		ps.setInt(2, x);
		ps.setInt(3, y);
		ps.setInt(4, z);
		return ps;
	}
	private static final String QueryDeleteByLocation =
		"DELETE FROM EssentialsProtect WHERE worldName = ? AND x = ? AND y = ? AND z = ?;";

	@Override
	protected PreparedStatement getStatementDeleteByLocation(Connection conn, String world, int x, int y, int z) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(QueryDeleteByLocation);
		ps.setString(1, world);
		ps.setInt(2, x);
		ps.setInt(3, y);
		ps.setInt(4, z);
		return ps;
	}
	private static final String QueryAllBlocks =
		"SELECT worldName, x, y, z, playerName FROM EssentialsProtect;";

	@Override
	protected PreparedStatement getStatementAllBlocks(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryAllBlocks);
	}
}
