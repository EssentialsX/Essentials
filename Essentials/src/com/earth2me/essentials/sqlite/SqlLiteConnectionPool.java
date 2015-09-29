package com.earth2me.essentials.sqlite;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.bukkit.Bukkit;

import lombok.Getter;

import com.google.common.base.Preconditions;

public class SqlLiteConnectionPool implements DataSource {
	
	private class SqlLiteForwardingConnection extends ForwardingConnection {
		
		public SqlLiteForwardingConnection( Connection handle ) {
			super( handle );
		}
		
		@Override
		public synchronized final void close() throws SQLException {
			if ( !this.isClosedForwarding() ) {
				this.closeForwarding();
				SqlLiteConnectionPool.this.scheduleIdleTimeoutTask();
				SqlLiteConnectionPool.this.semaphore.release();
			}
			
		}
		
	}
	
	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	
	@Getter
	private volatile boolean shutdown = false;
	
	private final File databaseFile;
	@Getter
	private int loginTimeout = 0;
	@Getter
	private long idleTimeout = 300000;
	@Getter
	private long connectionTimeout = 30000;
	
	private ScheduledFuture<?> idleTimeoutTask = null;
	// private final ReentrantLock lock = new ReentrantLock();
	private Connection connection = null;
	private final Semaphore semaphore = new Semaphore( 1 );
	
	public SqlLiteConnectionPool( File databaseFile ) {
		Preconditions.checkNotNull( databaseFile );
		Preconditions.checkArgument( !databaseFile.isDirectory() );
		
		try {
			Class.forName( "org.sqlite.JDBC" );
		} catch ( ClassNotFoundException e ) {
			throw new RuntimeException( e );
		}
		this.databaseFile = databaseFile;
		
		// Shutdown in any case...
		Runtime.getRuntime().addShutdownHook( new Thread() {
			@Override
			public void run() {
				SqlLiteConnectionPool.this.shutdown();
			}
		} );
	}
	
	public synchronized void shutdown() {
		if ( this.shutdown ) {
			return;
		}
		this.shutdown = true;
		int acquired = this.semaphore.drainPermits();
		if ( this.connection != null ) {
			try {
				this.connection.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
			this.connection = null;
		}
	}
	
	private synchronized void scheduleIdleTimeoutTask() {
		if ( idleTimeoutTask != null ) {
			idleTimeoutTask.cancel( false );
		}
		
		if ( this.shutdown ) {
			if ( this.connection != null ) {
				try {
					this.connection.close();
				} catch ( SQLException e ) {
					e.printStackTrace();
				}
				this.connection = null;
			}
			return;
		}
		
		idleTimeoutTask = EXECUTOR_SERVICE.schedule( new Runnable() {
			public void run() {
				if ( !semaphore.tryAcquire() ) {
					return;
				}
				try {
					if ( SqlLiteConnectionPool.this.connection != null ) {
						SqlLiteConnectionPool.this.connection.close();
						SqlLiteConnectionPool.this.connection = null;
					}
				} catch ( SQLException e ) {
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}
		}, this.idleTimeout, TimeUnit.MILLISECONDS );
		
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if ( shutdown ) {
			throw new SQLException( "pool closed" );
		}
		try {
			if ( !this.semaphore.tryAcquire( this.connectionTimeout, TimeUnit.MILLISECONDS ) ) {
				throw new SQLTimeoutException( "connection pool timeout" );
			}
			synchronized ( this ) {
				if ( shutdown ) {
					throw new SQLException( "pool closed" );
				}
				if ( this.connection == null ) {
					String url = "jdbc:sqlite:" + this.databaseFile.getAbsolutePath();
					Bukkit.getLogger().info( url );
					this.connection = DriverManager.getConnection( url );
				}
			}
			return new SqlLiteForwardingConnection( connection );
		} catch ( InterruptedException e ) {
			throw new SQLException( e );
		}
	}
	
	@Override
	public Connection getConnection( String username, String password ) throws SQLException {
		return getConnection();
	}
	
	public void setIdleTimeout( long millis ) {
		if ( millis <= 0 )
			throw new IllegalArgumentException();
		this.idleTimeout = millis;
	}
	
	public void setConnectionTimeout( long millis ) {
		if ( millis <= 0 )
			throw new IllegalArgumentException();
		this.connectionTimeout = millis;
	}
	
	@Override
	public void setLoginTimeout( int seconds ) throws SQLException {
		if ( seconds < 0 )
			throw new IllegalArgumentException();
		this.loginTimeout = seconds;
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}
	
	@Override
	public void setLogWriter( PrintWriter out ) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public <T> T unwrap( Class<T> iface ) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public boolean isWrapperFor( Class<?> iface ) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
}