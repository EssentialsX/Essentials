package com.earth2me.essentials.protect.data;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProtectedBlockMySQL extends ProtectedBlockJDBC {

	public ProtectedBlockMySQL(String url, String username, String password) throws PropertyVetoException {
		super("com.mysql.jdbc.Driver", url, username, password);
	}

	private static final String QueryCreateTable =
		"CREATE TABLE IF NOT EXISTS `EssentialsProtect` ("
		+ "`worldName` varchar(60) NOT NULL,"
		+ "`x` int(11) NOT NULL, `y` int(11) NOT NULL, `z` int(11) NOT NULL,"
		+ "`playerName` varchar(150) DEFAULT NULL,"
		+ "KEY `pos` (`worldName`,`x`,`z`,`y`)"
		+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

	@Override
	protected PreparedStatement getStatementCreateTable(Connection conn) throws SQLException {
		return conn.prepareStatement(QueryCreateTable);
	}
	
	private static final String QueryUpdateFrom2_0TableCheck =
		"SHOW COLUMNS FROM `EssentialsProtect` LIKE 'id';";
	private static final String QueryUpdateFrom2_0Table =
		"ALTER TABLE `EssentialsProtect` "
		+ "CHARACTER SET = utf8, ENGINE = MyISAM,"
		+ "DROP COLUMN `id`,"
		+ "CHANGE COLUMN `playerName` `playerName` VARCHAR(150) NULL AFTER `z`,"
		+ "CHANGE COLUMN `worldName` `worldName` VARCHAR(60) NOT NULL,"
		+ "ADD INDEX `position` (`worldName` ASC, `x` ASC, `z` ASC, `y` ASC),"
		+ "DROP PRIMARY KEY ;";

	@Override
	protected PreparedStatement getStatementUpdateFrom2_0Table(Connection conn) throws SQLException {
		PreparedStatement testPS = null;
		ResultSet testRS = null;
		try {
			testPS = conn.prepareStatement(QueryUpdateFrom2_0TableCheck);
			testRS = testPS.executeQuery();
			if (testRS.first()) {
				return conn.prepareStatement(QueryUpdateFrom2_0Table);
			} else {
				return conn.prepareStatement("SELECT 1;");
			}
		} finally {
			if (testRS != null) {
				try
				{
					testRS.close();
				}
				catch (SQLException ex)
				{
					Logger.getLogger(ProtectedBlockMySQL.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (testPS != null) {
				try
				{
					testPS.close();
				}
				catch (SQLException ex)
				{
					Logger.getLogger(ProtectedBlockMySQL.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
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
	private static final String QueryCountByPlayer =
		"SELECT COUNT(playerName), SUM(playerName = ?) FROM EssentialsProtect "
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
