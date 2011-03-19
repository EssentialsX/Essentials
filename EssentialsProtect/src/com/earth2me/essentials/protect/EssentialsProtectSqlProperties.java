package com.earth2me.essentials.protect;

public class EssentialsProtectSqlProperties {

	public static String EssentialsProtect="CREATE TABLE IF NOT EXISTS `EssentialsProtect` (`id` int(11) NOT NULL AUTO_INCREMENT, `worldName` varchar(150) NOT NULL, `playerName` varchar(150) NOT NULL, `x` int(11) NOT NULL, `y` int(11) NOT NULL, `z` int(11) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=latin1";
	public static String EssentialsProtect_sqlite = "CREATE TABLE IF NOT EXISTS EssentialsProtect (id INTEGER PRIMARY KEY, worldName TEXT ,playerName TEXT, x NUMERIC, y NUMERIC, z NUMERIC)";
    public static String CountByLocation="SELECT COUNT(*) from EssentialsProtect where worldName = ? and x = ? and y = ? and z = ? limit 10";
	public static String CountByPLayerLocation="SELECT COUNT(*) from EssentialsProtect where worldName = ? and playerName =? and  x = ? and y = ? and z = ? limit 10";
	public static String DeleteByLocation="DELETE FROM EssentialsProtect WHERE worldName=? and x=? and y=? and z=?";
	public static String Insert="INSERT INTO EssentialsProtect (worldName, playerName, x, y, z) VALUES (?,?,?,?,?)";
	public static String PlayerByLocation="SELECT playerName FROM EssentialsProtect WHERE worldname = ? and x = ? and y = ? and z = ? LIMIT 10";
}
