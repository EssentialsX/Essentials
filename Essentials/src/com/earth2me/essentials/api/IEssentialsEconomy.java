package com.earth2me.essentials.api;


public interface IEssentialsEconomy
{
	double getMoney(String name) throws UserDoesNotExistException;

	void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException;

	void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException;

	void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException;

	void divide(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException;

	void multiply(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException;

	void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException;

	boolean hasEnough(String name, double amount) throws UserDoesNotExistException;

	boolean hasMore(String name, double amount) throws UserDoesNotExistException;

	boolean hasLess(String name, double amount) throws UserDoesNotExistException;

	boolean isNegative(String name) throws UserDoesNotExistException;

	String format(double amount);

	boolean playerExists(String name);

	boolean isNPC(String name) throws UserDoesNotExistException;

	boolean createNPC(String name);

	void removeNPC(String name) throws UserDoesNotExistException;
}
