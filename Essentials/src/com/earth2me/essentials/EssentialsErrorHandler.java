package com.earth2me.essentials;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


class EssentialsErrorHandler extends Handler
{
	private final Map<BigInteger, String> errors = new HashMap<BigInteger, String>();
	private final List<LogRecord> records = new LinkedList<LogRecord>();

	public EssentialsErrorHandler()
	{
	}

	@Override
	public void publish(LogRecord lr)
	{
		if (lr.getThrown() == null || lr.getLevel().intValue() < Level.WARNING.intValue())
		{
			return;
		}
		synchronized (records)
		{
			records.add(lr);
		}
	}

	@Override
	public void flush()
	{
		synchronized (records)
		{
			sortRecords();
		}
	}

	@Override
	public void close() throws SecurityException
	{
		synchronized (records)
		{
			sortRecords();
		}
	}

	private void sortRecords()
	{
		for (LogRecord lr : records)
		{
			try
			{
				if (lr.getThrown() == null)
				{
					return;
				}
				Throwable tr = lr.getThrown();
				StackTraceElement[] elements = tr.getStackTrace();
				if (elements == null || elements.length <= 0)
				{
					return;
				}
				boolean essentialsFound = false;
				for (StackTraceElement stackTraceElement : elements)
				{
					if (stackTraceElement.getClassName().contains("com.earth2me.essentials"))
					{
						essentialsFound = true;
						break;
					}
				}
				if (!essentialsFound && tr.getCause() != null)
				{
					Throwable cause = tr.getCause();
					StackTraceElement[] elements2 = cause.getStackTrace();
					if (elements2 != null)
					{
						for (StackTraceElement stackTraceElement : elements2)
						{
							if (stackTraceElement.getClassName().contains("com.earth2me.essentials"))
							{
								essentialsFound = true;
								break;
							}
						}
					}
				}
				StringBuilder sb = new StringBuilder();
				sb.append("[").append(lr.getLevel().getName()).append("] ").append(lr.getMessage()).append("\n");
				sb.append(tr.getMessage()).append("\n");
				for (StackTraceElement stackTraceElement : tr.getStackTrace())
				{
					sb.append(stackTraceElement.toString()).append("\n");
				}
				if (tr.getCause() != null && tr.getCause().getStackTrace() != null)
				{
					sb.append(tr.getCause().getMessage()).append("\n");
					for (StackTraceElement stackTraceElement : tr.getCause().getStackTrace())
					{
						sb.append(stackTraceElement.toString()).append("\n");
					}
				}
				String errorReport = sb.toString();
				byte[] bytesOfMessage = errorReport.getBytes("UTF-8");
				MessageDigest md = MessageDigest.getInstance("MD5");
				BigInteger bi = new BigInteger(md.digest(bytesOfMessage));
				errors.put(bi, errorReport);
			}
			catch (Throwable t)
			{
				//Ignore all exceptions inside the exception handler
			}
		}
		records.clear();
	}

	Map<BigInteger, String> getErrors()
	{
		return errors;
	}
}
