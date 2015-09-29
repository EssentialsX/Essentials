package com.earth2me.essentials.sqlite;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForwardingConnection implements Connection {
	
	private volatile boolean closed = false;
	
	private final Connection handle;
	
	protected synchronized boolean isClosedForwarding() {
		return this.closed;
	}
	
	protected synchronized void closeForwarding() {
		this.closed = true;
	}
	
	private synchronized final Connection getCheckState() throws SQLException {
		if ( closed )
			throw new SQLException( "Connection closed" );
		return handle;
	}
	
	@Override
	public <T> T unwrap( Class<T> iface ) throws SQLException {
		return getCheckState().unwrap( iface );
	}
	
	@Override
	public boolean isWrapperFor( Class<?> iface ) throws SQLException {
		return getCheckState().isWrapperFor( iface );
	}
	
	@Override
	public Statement createStatement() throws SQLException {
		return getCheckState().createStatement();
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql ) throws SQLException {
		return getCheckState().prepareStatement( sql );
	}
	
	@Override
	public CallableStatement prepareCall( String sql ) throws SQLException {
		return getCheckState().prepareCall( sql );
	}
	
	@Override
	public String nativeSQL( String sql ) throws SQLException {
		return getCheckState().nativeSQL( sql );
	}
	
	@Override
	public void setAutoCommit( boolean autoCommit ) throws SQLException {
		getCheckState().setAutoCommit( autoCommit );
	}
	
	@Override
	public boolean getAutoCommit() throws SQLException {
		return getCheckState().getAutoCommit();
	}
	
	@Override
	public void commit() throws SQLException {
		getCheckState().commit();
	}
	
	@Override
	public void rollback() throws SQLException {
		getCheckState().rollback();
	}
	
	@Override
	public synchronized void close() throws SQLException {
		if ( closed )
			return;
		this.closed = true;
		this.handle.close();
	}
	
	@Override
	public synchronized boolean isClosed() throws SQLException {
		if ( this.closed )
			return this.closed;
		return this.handle.isClosed();
	}
	
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return getCheckState().getMetaData();
	}
	
	@Override
	public void setReadOnly( boolean readOnly ) throws SQLException {
		getCheckState().setReadOnly( readOnly );
	}
	
	@Override
	public boolean isReadOnly() throws SQLException {
		return getCheckState().isReadOnly();
	}
	
	@Override
	public void setCatalog( String catalog ) throws SQLException {
		getCheckState().setCatalog( catalog );
	}
	
	@Override
	public String getCatalog() throws SQLException {
		return getCheckState().getCatalog();
	}
	
	@Override
	public void setTransactionIsolation( int level ) throws SQLException {
		getCheckState().setTransactionIsolation( level );
	}
	
	@Override
	public int getTransactionIsolation() throws SQLException {
		return getCheckState().getTransactionIsolation();
	}
	
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return getCheckState().getWarnings();
	}
	
	@Override
	public void clearWarnings() throws SQLException {
		getCheckState().clearWarnings();
	}
	
	@Override
	public Statement createStatement( int resultSetType, int resultSetConcurrency ) throws SQLException {
		return getCheckState().createStatement();
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency )
			throws SQLException {
		return getCheckState().prepareStatement( sql, resultSetType, resultSetConcurrency );
	}
	
	@Override
	public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency ) throws SQLException {
		return getCheckState().prepareCall( sql, resultSetType, resultSetConcurrency );
	}
	
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return getCheckState().getTypeMap();
	}
	
	@Override
	public void setTypeMap( Map<String, Class<?>> map ) throws SQLException {
		getCheckState().setTypeMap( map );
	}
	
	@Override
	public void setHoldability( int holdability ) throws SQLException {
		getCheckState().setHoldability( holdability );
	}
	
	@Override
	public int getHoldability() throws SQLException {
		return getCheckState().getHoldability();
	}
	
	@Override
	public Savepoint setSavepoint() throws SQLException {
		return getCheckState().setSavepoint();
	}
	
	@Override
	public Savepoint setSavepoint( String name ) throws SQLException {
		return getCheckState().setSavepoint( name );
	}
	
	@Override
	public void rollback( Savepoint savepoint ) throws SQLException {
		getCheckState().rollback( savepoint );
	}
	
	@Override
	public void releaseSavepoint( Savepoint savepoint ) throws SQLException {
		getCheckState().releaseSavepoint( savepoint );
	}
	
	@Override
	public Statement createStatement( int resultSetType, int resultSetConcurrency, int resultSetHoldability )
			throws SQLException {
		return getCheckState().createStatement( resultSetType, resultSetConcurrency, resultSetHoldability );
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability ) throws SQLException {
		return getCheckState().prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
	}
	
	@Override
	public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability ) throws SQLException {
		return getCheckState().prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql, int autoGeneratedKeys ) throws SQLException {
		return getCheckState().prepareStatement( sql, autoGeneratedKeys );
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql, int[] columnIndexes ) throws SQLException {
		return getCheckState().prepareStatement( sql, columnIndexes );
	}
	
	@Override
	public PreparedStatement prepareStatement( String sql, String[] columnNames ) throws SQLException {
		return getCheckState().prepareStatement( sql, columnNames );
	}
	
	@Override
	public Clob createClob() throws SQLException {
		return getCheckState().createClob();
	}
	
	@Override
	public Blob createBlob() throws SQLException {
		return getCheckState().createBlob();
	}
	
	@Override
	public NClob createNClob() throws SQLException {
		return getCheckState().createNClob();
	}
	
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return getCheckState().createSQLXML();
	}
	
	@Override
	public boolean isValid( int timeout ) throws SQLException {
		if ( this.closed )
			return false;
		return this.handle.isValid( timeout );
	}
	
	@Override
	public void setClientInfo( String name, String value ) throws SQLClientInfoException {
		try {
			getCheckState().setClientInfo( name, value );
		} catch ( SQLException e ) {
			throw new SQLClientInfoException();
		}
	}
	
	@Override
	public void setClientInfo( Properties properties ) throws SQLClientInfoException {
		try {
			getCheckState().setClientInfo( properties );
		} catch ( SQLException e ) {
			throw new SQLClientInfoException();
		}
	}
	
	@Override
	public String getClientInfo( String name ) throws SQLException {
		return getCheckState().getClientInfo( name );
	}
	
	@Override
	public Properties getClientInfo() throws SQLException {
		return getCheckState().getClientInfo();
	}
	
	@Override
	public Array createArrayOf( String typeName, Object[] elements ) throws SQLException {
		return getCheckState().createArrayOf( typeName, elements );
	}
	
	@Override
	public Struct createStruct( String typeName, Object[] attributes ) throws SQLException {
		return getCheckState().createStruct( typeName, attributes );
	}
	
	@Override
	public void setSchema( String schema ) throws SQLException {
		getCheckState().setSchema( schema );
	}
	
	@Override
	public String getSchema() throws SQLException {
		return getCheckState().getSchema();
	}
	
	@Override
	public void abort( Executor executor ) throws SQLException {
		getCheckState().abort( executor );
	}
	
	@Override
	public void setNetworkTimeout( Executor executor, int milliseconds ) throws SQLException {
		getCheckState().setNetworkTimeout( executor, milliseconds );
	}
	
	@Override
	public int getNetworkTimeout() throws SQLException {
		return getCheckState().getNetworkTimeout();
	}
	
}