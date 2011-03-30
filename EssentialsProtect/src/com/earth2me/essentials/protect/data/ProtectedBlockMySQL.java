package com.earth2me.essentials.protect.data;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProtectedBlockMySQL extends ProtectedBlockJDBC {

	public ProtectedBlockMySQL(String url, String username, String password) throws PropertyVetoException {
		super("com.mysql.jdbc.Driver", url, username, password);
	}

	private static final String QueryCreateTable =
		"CREATE TABLE IF NOT EXISTS `EssentialsProtectedBlocks` ("
		+ "`worldName` varchar(150) NOT NULL,"
		+ "`x` int(11) NOT NULL, `y` int(11) NOT NULL, `z` int(11) NOT NULL,"
		+ "`playerName` varchar(150) NOT NULL,"
		+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

	@Override
	protected PreparedStatement getStatementCreateTable(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryCreateTable);
	}
	private static final String QueryDeleteAll = "DELETE FROM EssentialsProtectedBlocks;";

	@Override
	protected PreparedStatement getStatementDeleteAll(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryDeleteAll);
	}
	private static final String QueryInsert =
		"INSERT INTO EssentialsProtectedBlocks (worldName, x, y, z, playerName) VALUES (?, ?, ?, ?, ?);";

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
	private static final String QueryCountByPlayer =
		"SELECT COUNT(playerName), SUM(playerName = ?) FROM EssentialsProtectedBlocks "
		+ "WHERE worldName = ? AND x = ? AND y = ? AND z = ? GROUP BY x;";

	@Override
	protected PreparedStatement getStatementPlayerCountByLocation(Connection conn, String world, int x, int y, int z, String playerName) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(QueryCountByPlayer);
		ps.setString(1, playerName);
		ps.setString(2, world);
		ps.setInt(3, x);
		ps.setInt(4, y);
		ps.setInt(5, z);
		return ps;
	}
	private static final String QueryPlayersByLocation =
		"SELECT playerName FROM EssentialsProtectedBlocks WHERE worldname = ? AND x = ? AND y = ? AND z = ?;";

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
		"DELETE FROM EssentialsProtectedBlocks WHERE worldName = ? AND x = ? AND y = ? AND z = ?;";

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
		"SELECT worldName, x, y, z, playerName FROM EssentialsProtectedBlocks;";

	@Override
	protected PreparedStatement getStatementAllBlocks(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryAllBlocks);
	}
}
