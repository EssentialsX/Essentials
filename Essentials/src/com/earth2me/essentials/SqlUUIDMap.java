package com.earth2me.essentials;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.earth2me.essentials.sqlite.UserDatabase;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;

public class SqlUUIDMap extends UUIDMap {
	private final transient net.ess3.api.IEssentials ess;
	private final UserDatabase database;
	
	public SqlUUIDMap( final net.ess3.api.IEssentials ess, UserDatabase database ) {
		super( ess );
		this.ess = ess;
		this.database = database;
	}
	
	@Override
	public void loadAllUsers( final ConcurrentSkipListMap<String, UUID> names,
			final ConcurrentSkipListMap<UUID, ArrayList<String>> history ) {
		try {
			
			names.clear();
			history.clear();
			
			names.putAll( this.database.selectAllNames() );
			
			for ( Entry<UUID, Collection<String>> e : this.database.selectNameHistory().asMap().entrySet() ) {
				if ( e.getValue().size() > 0 ) {
					ArrayList<String> list = Lists.newArrayList( e.getValue() );
					
				}
			}
		} catch ( SQLException ex ) {
			Bukkit.getLogger().log( Level.SEVERE, ex.getMessage(), ex );
		}
	}
	
	@Override
	public void writeUUIDMap() {
		_writeUUIDMap();
	}
	
	@Override
	public void forceWriteUUIDMap() {
		if ( ess.getSettings().isDebug() ) {
			ess.getLogger().log( Level.INFO, "Forcing usermap write to disk" );
		}
		try {
			Future<?> future = _writeUUIDMap();
			if ( future != null ) {
				future.get();
			}
		} catch ( InterruptedException ex ) {
			ess.getLogger().log( Level.SEVERE, ex.getMessage(), ex );
		} catch ( ExecutionException ex ) {
			ess.getLogger().log( Level.SEVERE, ex.getMessage(), ex );
		}
	}
	
	@Override
	public Future<?> _writeUUIDMap() {
		final Map<String, UUID> names = ess.getUserMap().getNames();
		if ( names.size() < 1 ) {
			return null;
		}
		try {
			this.database.insertNames( names );
		} catch ( Exception e ) {
			Futures.immediateFailedFuture( e );
		}
		return Futures.immediateFuture( true );
	}
}