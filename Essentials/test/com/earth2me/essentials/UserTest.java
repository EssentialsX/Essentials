package com.earth2me.essentials;

import junit.framework.TestCase;
import org.bukkit.Location;


public class UserTest extends TestCase
{
	private OfflinePlayer base1;

	public UserTest(String testName)
	{
		super(testName);
		base1 = new OfflinePlayer("TestPlayer1");
	}

	private void should(String what)
	{
		System.out.println(getName() + " should " + what);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testUpdate()
	{
		should("update an existing player with the same name, rather than creating a new player");
		User.get(base1);
		int size1 = User.size();
		OfflinePlayer base1alt = new OfflinePlayer(base1.getName());
		assertEquals(base1alt, User.get(base1alt).getBase());
		assertTrue(size1 == User.size());
	}

	public void testHome() throws Exception
	{
		should("return the home set by setHome");
		Location home = new Location(null, 1, 2, 3, 4, 5);
		User user = User.get(base1);
		user.setHome(home);
		assertEquals(user.getHome(), home);
	}

	public void testMoney()
	{
		should("properly set, take, give, and get money");
		User user = User.get(base1);
		double i;
		user.setMoney(i = 100.5);
		user.takeMoney(50);
		i -= 50;
		user.giveMoney(25);
		i += 25;
		assertEquals(user.getMoney(), i);
	}
	
	public void testGetGroup()
	{
		should("return the default group");
		User user = User.get(base1);
		assertEquals(user.getGroup(), "default");
	}
}
