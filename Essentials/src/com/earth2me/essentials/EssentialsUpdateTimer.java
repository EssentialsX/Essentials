package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;


class EssentialsUpdateTimer implements Runnable
{
	private URL url;
	private final Essentials ess;
	private static final Logger logger = Logger.getLogger("Minecraft");
	
	public EssentialsUpdateTimer(Essentials ess)
	{
		this.ess = ess;
		try
		{
			url = new URL("http://127.0.0.1:8080/check");
		}
		catch (MalformedURLException ex)
		{
			logger.log(Level.SEVERE, "Invalid url!", ex);
		}
	}

	public void run()
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append("v=").append(URLEncoder.encode(ess.getDescription().getVersion(),"UTF-8"));
			sb.append("&b=").append(URLEncoder.encode(ess.getServer().getVersion(),"UTF-8"));
			sb.append("&jv=").append(URLEncoder.encode(System.getProperty("java.version"),"UTF-8"));
			sb.append("&l=").append(URLEncoder.encode(Util.getCurrentLocale().toString(),"UTF-8"));
			sb.append("&on=").append(URLEncoder.encode(System.getProperty("os.name"),"UTF-8"));
			sb.append("&ov=").append(URLEncoder.encode(System.getProperty("os.version"),"UTF-8"));
			for (BigInteger bigInteger : ess.getErrors().keySet())
			{
				sb.append("&e[]=").append(bigInteger.toString(36));
			}
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setDoOutput(true);
			conn.connect();
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(sb.toString());
			wr.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String ret = br.readLine();
			wr.close();
			br.close();
			logger.log(Level.INFO, ret);
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Failed to open connection", ex);
		}
	}	
}
