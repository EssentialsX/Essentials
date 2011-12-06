package com.earth2me.essentials.user;


public interface IUserData
{
	UserData getData();

	void aquireReadLock();

	void aquireWriteLock();

	void close();
}
