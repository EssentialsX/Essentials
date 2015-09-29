package com.earth2me.essentials.sqlite;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLConsumer<T> {
	
	void accept( T t ) throws SQLException;
	
}