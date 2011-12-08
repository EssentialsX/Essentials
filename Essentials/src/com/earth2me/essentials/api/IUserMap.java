package com.earth2me.essentials.api;

import java.io.File;
import java.util.Set;


public interface IUserMap
{
	boolean userExists(final String name);

	IUser getUser(final String name);

	void removeUser(final String name);

	Set<String> getAllUniqueUsers();

	int getUniqueUsers();

	File getUserFile(final String name);
}
