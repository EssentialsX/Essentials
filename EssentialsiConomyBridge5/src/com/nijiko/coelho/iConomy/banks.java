
package com.nijiko.coelho.iConomy;

import com.earth2me.essentials.api.Economy;



public class banks
{
	
	
	public int count(String name){
		int count=0;
		int breaker = 0;
		for (int ctr = 1; breaker != 1; ctr++)
		{
			if (ctr == 1)
			{
				if (Economy.accountExist(name + "-bank"))
				{
					count = count + 1;
				}
				else
				{
					breaker = 1;
				}
			}
			if (Economy.accountExist(name + "-bank" + Integer.toString(ctr)) && ctr != 1)
			{
				count = count + 1;
			}
			else
			{
				breaker = 1;
			}
		}
					
		return count;	
	}


}
	
	
	
	
	
	
	
	
	
	

