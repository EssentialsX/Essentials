package com.earth2me.essentials.api;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.earth2me.essentials.UUIDMap;
import com.earth2me.essentials.User;

public interface IUserMap {
	
	boolean userExists( UUID uuid );
	
	User getUser( String name );
	
	User getUser( UUID uuid );
	
	void trackUUID( UUID uuid, String name, boolean replace );
	
	User load( UUID uuid ) throws Exception;
	
	void reloadConfig();
	
	void invalidateAll();
	
	void removeUser( String name );
	
	Set<UUID> getAllUniqueUsers();
	
	int getUniqueUsers();
	
	List<String> getUserHistory( UUID uuid );
	
	UUIDMap getUUIDMap();
	
	File getUserFileFromString( String name );

	Map<String, UUID> getNames();
	
	//	class UserMapRemovalListener implements RemovalListener
	//	{
	//		@Override
	//		public void onRemoval(final RemovalNotification notification)
	//		{
	//			Object value = notification.getValue();
	//			if (value != null)
	//			{
	//				((User)value).cleanup();
	//			}
	//		}
	//	}
	
}