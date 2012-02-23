package com.earth2me.essentials.api;

import com.earth2me.essentials.settings.Kit;
import java.util.Collection;


public interface IKits extends IReload
{
	Kit getKit(String kit) throws Exception;

	void sendKit(IUser user, String kit) throws Exception;

	void sendKit(IUser user, Kit kit) throws Exception;

	Collection<String> getList() throws Exception;

	boolean isEmpty();
}
