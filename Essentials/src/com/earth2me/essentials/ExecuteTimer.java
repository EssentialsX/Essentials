package com.earth2me.essentials;

import java.util.ArrayList;
import java.util.List;


public class ExecuteTimer
{
	private final List<ExecuteRecord> times;

	public ExecuteTimer()
	{
		times = new ArrayList<ExecuteRecord>();
	}

	public void start()
	{
		times.clear();
		mark("start");

	}

	public void mark(final String label)
	{
		if (!times.isEmpty() || "start".equals(label))
		{
			times.add(new ExecuteRecord(label, System.currentTimeMillis()));
		}
	}

	public String end()
	{
		final StringBuilder output = new StringBuilder();
		output.append("execution time: ");
		String mark;
		long time0 = 0;
		long time1 = 0;
		long time2 = 0;
		long duration;

		for (ExecuteRecord pair : times)
		{
			mark = (String)pair.getMark();
			time2 = (Long)pair.getTime();
			if (time1 > 0)
			{
				duration = time2 - time1;
				output.append(mark).append(": ").append(duration).append("ms - ");
			}
			else
			{
				time0 = time2;
			}
			time1 = time2;
		}
		duration = time1 - time0;
		output.append("Total: ").append(duration).append("ms");
		times.clear();
		return output.toString();
	}


	static private class ExecuteRecord
	{
		private final String mark;
		private final long time;

		public ExecuteRecord(final String mark, final long time)
		{
			this.mark = mark;
			this.time = time;
		}

		public String getMark()
		{
			return mark;
		}

		public long getTime()
		{
			return time;
		}
	}
}