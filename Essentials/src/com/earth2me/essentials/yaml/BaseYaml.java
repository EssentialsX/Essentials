package com.earth2me.essentials.yaml;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class BaseYaml<T>
{

	protected BaseYaml()
	{
	}
	
	protected abstract Class<? extends T> getClazz();
	
	public T load() {
		try
		{
			return getClazz().newInstance();
		}
		catch (InstantiationException ex)
		{
			Logger.getLogger(BaseYaml.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IllegalAccessException ex)
		{
			Logger.getLogger(BaseYaml.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
