package com.earth2me.essentials.register.payment;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;


/**
 * The <code>Methods</code> initializes Methods that utilize the Method interface
 * based on a "first come, first served" basis.
 *
 * Allowing you to check whether a payment method exists or not.
 *
 * <blockquote><pre>
 *  Methods methods = new Methods();
 * </pre></blockquote>
 *
 * Methods also allows you to set a preferred method of payment before it captures
 * payment plugins in the initialization process.
 *
 * <blockquote><pre>
 *  Methods methods = new Methods("iConomy");
 * </pre></blockquote>
 *
 * @author: Nijikokun <nijikokun@shortmail.com> (@nijikokun)
 * @copyright: Copyright (C) 2011
 * @license: AOL license <http://aol.nexua.org>
 */
public class Methods
{
	private boolean self = false;
	private Method Method = null;
	private String preferred = "";
	private Set<Method> Methods = new HashSet<Method>();
	private Set<String> Dependencies = new HashSet<String>();
	private Set<Method> Attachables = new HashSet<Method>();

	/**
	 * Initialize Method class
	 */
	public Methods()
	{
		this._init();
	}

	/**
	 * Initializes <code>Methods</code> class utilizing a "preferred" payment method check before
	 * returning the first method that was initialized.
	 * 
	 * @param preferred Payment method that is most preferred for this setup.
	 */
	public Methods(String preferred)
	{
		this._init();

		if (this.Dependencies.contains(preferred))
		{
			this.preferred = preferred;
		}
	}

	/**
	 * Implement all methods along with their respective name & class.
	 *
	 * @see #Methods()
	 * @see #Methods(java.lang.String)
	 */
	private void _init()
	{
		this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo4());
		this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo5());
		this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo6());
		this.addMethod("BOSEconomy", new com.earth2me.essentials.register.payment.methods.BOSE6());
		this.addMethod("BOSEconomy", new com.earth2me.essentials.register.payment.methods.BOSE7());
		this.addMethod("MultiCurrency", new com.earth2me.essentials.register.payment.methods.MCUR());
	}

	/**
	 * Returns an array of payment method names that have been loaded
	 * through the <code>_init</code> method.
	 *
	 * @return <code>Set<String></code> - Array of payment methods that are loaded.
	 * @see #setMethod(org.bukkit.plugin.Plugin)
	 */
	public Set<String> getDependencies()
	{
		return Dependencies;
	}

	/**
	 * Interprets Plugin class data to verify whether it is compatible with an existing payment
	 * method to use for payments and other various economic activity.
	 *
	 * @param plugin Plugin data from bukkit, Internal Class file.
	 * @return Method <em>or</em> Null
	 */
	public Method createMethod(Plugin plugin)
	{
		for (Method method : Methods)
		{
			if (method.isCompatible(plugin))
			{
				method.setPlugin(plugin);
				return method;
			}
		}

		return null;
	}

	private void addMethod(String name, Method method)
	{
		Dependencies.add(name);
		Methods.add(method);
	}

	/**
	 * Verifies if Register has set a payment method for usage yet.
	 *
	 * @return <code>boolean</code>
	 * @see #setMethod(org.bukkit.plugin.Plugin)
	 * @see #checkDisabled(org.bukkit.plugin.Plugin)
	 */
	public boolean hasMethod()
	{
		return (Method != null);
	}

	/**
	 * Checks Plugin Class against a multitude of checks to verify it's usability
	 * as a payment method.
	 *
	 * @param method Plugin data from bukkit, Internal Class file.
	 * @return <code>boolean</code> True on success, False on failure.
	 */
	public boolean setMethod(Plugin method)
	{
		if (hasMethod())
		{
			return true;
		}
		if (self)
		{
			self = false;
			return false;
		}

		int count = 0;
		boolean match = false;
		Plugin plugin = null;
		PluginManager manager = method.getServer().getPluginManager();

		for (String name : this.getDependencies())
		{
			if (hasMethod())
			{
				break;
			}
			if (method.getDescription().getName().equals(name))
			{
				plugin = method;
			}
			else
			{
				plugin = manager.getPlugin(name);
			}
			if (plugin == null)
			{
				continue;
			}

			Method current = this.createMethod(plugin);
			if (current == null)
			{
				continue;
			}

			if (this.preferred.isEmpty())
			{
				this.Method = current;
			}
			else
			{
				this.Attachables.add(current);
			}
		}

		if (!this.preferred.isEmpty())
		{
			do
			{
				if (hasMethod())
				{
					match = true;
				}
				else
				{
					for (Method attached : this.Attachables)
					{
						if (attached == null)
						{
							continue;
						}

						if (hasMethod())
						{
							match = true;
							break;
						}

						if (this.preferred.isEmpty())
						{
							this.Method = attached;
						}

						if (count == 0)
						{
							if (this.preferred.equalsIgnoreCase(attached.getName()))
							{
								this.Method = attached;
							}
						}
						else
						{
							this.Method = attached;
						}
					}

					count++;
				}
			}
			while (!match);
		}

		return hasMethod();
	}

	/**
	 * Grab the existing and initialized (hopefully) Method Class.
	 *
	 * @return <code>Method</code> <em>or</em> <code>Null</code>
	 */
	public Method getMethod()
	{
		return Method;
	}

	/**
	 * Verify is a plugin is disabled, only does this if we there is an existing payment
	 * method initialized in Register.
	 *
	 * @param method Plugin data from bukkit, Internal Class file.
	 * @return <code>boolean</code>
	 */
	public boolean checkDisabled(Plugin method)
	{
		if (!hasMethod())
		{
			return true;
		}
		if (Method.isCompatible(method))
		{
			Method = null;
		}
		return (Method == null);
	}
}
