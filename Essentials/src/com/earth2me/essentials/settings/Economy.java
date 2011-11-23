package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Economy implements StorageObject
{
	@Comment("Defines the balance with which new players begin. Defaults to 0.")
	private double startingBalance = 0.0;
	@MapValueType(Double.class)
	@Comment("Defines the cost to use the given commands PER USE")
	private Map<String, Double> commandCosts = new HashMap<String, Double>();
	@Comment("Set this to a currency symbol you want to use.")
	private String currencySymbol = "$";

	public String getCurrencySymbol()
	{
		return currencySymbol == null || currencySymbol.isEmpty() ? "$" : currencySymbol.substring(0, 1);
	}
	private final transient static double MAXMONEY = 10000000000000.0;
	@Comment(
	{
		"Set the maximum amount of money a player can have",
		"The amount is always limited to 10 trillions because of the limitations of a java double"
	})
	private double maxMoney = MAXMONEY;

	public double getMaxMoney()
	{
		return Math.abs(maxMoney) > MAXMONEY ? MAXMONEY : Math.abs(maxMoney);
	}
	@Comment("Enable this to log all interactions with trade/buy/sell signs and sell command")
	private boolean logEnabled = false;
	private Worth worth = new Worth();
}
