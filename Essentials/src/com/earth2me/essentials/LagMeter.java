package com.earth2me.essentials;

import java.util.LinkedList;


public class LagMeter implements Runnable
{
	private transient long lastPoll = System.currentTimeMillis() - 3000;
	private final transient LinkedList<Float> history = new LinkedList<Float>();

	@Override
	public void run()
	{
		long now = System.currentTimeMillis();
		long timeSpent = (now - lastPoll) / 1000;
		if (timeSpent == 0)
		{
			timeSpent = 1;
		}
		if (history.size() > 10)
		{
			history.remove();
		}
		float tps = 40f / timeSpent;
		if (tps <= 20)
		{
			history.add(tps);
		}
		lastPoll = now;
	}

	public float getAverageTPS()
	{
		float avg = 0;
		for (Float f : history)
		{
			if (f != null)
			{
				avg += f;
			}
		}
		return avg / history.size();
	}
}
