package com.nijiko.coelho.iConomy;

import com.nijiko.coelho.iConomy.system.Bank;
import java.util.logging.Logger;


/**
 * This is not iConomy and I take NO credit for iConomy!
 * This is FayConomy, a iConomy Essentials Eco bridge!
 * @author Xeology
 */

public class iConomy{
	public static Bank Bank = new Bank();
	private static final Logger logger = Logger.getLogger("Minecraft");

	//Fake bank
	public static Bank getBank()
	{
		return Bank;
	}
}
