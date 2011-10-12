package f00f.net.irc.martyr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @since 0.3.2
 * */
public class CronManager
{
	private Timer timer;

	public CronManager()
	{
		timer = new Timer();
	}

	/**
	 * @param task TimerTask to schedule
	 * @param time When to schedule task
	 */
	public void schedule(TimerTask task, Date time)
	{
		timer.schedule(task, time);
	}

	/**
	 * @param task TimerTask to schedule
	 * @param firstTime When to run first
	 * @param period How often to run
	 */
	public void schedule(TimerTask task, Date firstTime, long period) 
	{
		timer.schedule(task, firstTime, period);
	}

	/**
	 * @param task TimerTask to schedule
	 * @param delay How long to wait before running
	 */
	public void schedule(TimerTask task, long delay) 
	{
		timer.schedule(task, delay);
	}

	/**
	 * @param task TimerTask to schedule
	 * @param delay How long to wait before running
	 * @param period How often to run
	 */
	public void schedule(TimerTask task, long delay, long period) 
	{
		timer.schedule(task, delay, period);
	}

	/**
	 * @param task TimerTask to schedule
	 * @param firstTime When first to run
	 * @param period How often to run
	 */
	public void scheduleAtFixedRate(
		TimerTask task,
		Date firstTime,
		long period) 
	{
		timer.scheduleAtFixedRate(task, firstTime, period);
	}

	/**
	 * @param task TimerTask to schedule
	 * @param delay When first to run
	 * @param period How often to run
	 */
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) 
	{
		timer.scheduleAtFixedRate(task, delay, period);
	}

}
