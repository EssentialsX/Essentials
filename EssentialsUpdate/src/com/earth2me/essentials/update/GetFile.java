package com.earth2me.essentials.update;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public class GetFile
{
	private transient URLConnection connection;
	private transient MessageDigest digest;

	public GetFile(final String urlString) throws MalformedURLException, IOException
	{
		this(new URL(urlString));
	}

	public GetFile(final URL url) throws IOException
	{
		this.connection = url.openConnection();
		this.connection.setConnectTimeout(1000);
		this.connection.setReadTimeout(5000);
		this.connection.setUseCaches(false);
		this.connection.connect();
		final int respCode = ((HttpURLConnection)this.connection).getResponseCode();
		if (respCode >= 300 && respCode < 400 && this.connection.getHeaderField("Location") != null)
		{
			connection.getInputStream().close();
			final URL redirect = new URL(this.connection.getHeaderField("Location"));
			this.connection = redirect.openConnection();
			this.connection.setConnectTimeout(1000);
			this.connection.setReadTimeout(5000);
			this.connection.setUseCaches(false);
			this.connection.connect();
		}
	}

	public void saveTo(final File file) throws IOException
	{
		try
		{
			saveTo(file, null);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public void saveTo(final File file, final String key) throws IOException, NoSuchAlgorithmException
	{
		if (key != null)
		{
			digest = MessageDigest.getInstance("SHA256");
		}
		final byte[] buffer = new byte[1024 * 8];
		boolean brokenFile = false;
		final BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
		try
		{
			final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
			try
			{
				int length;
				do
				{
					length = input.read(buffer);
					if (length >= 0)
					{
						if (key != null)
						{
							digest.update(buffer, 0, length);
						}
						output.write(buffer, 0, length);
					}
				}
				while (length >= 0);
				if (key != null)
				{
					final byte[] checksum = digest.digest();
					final String checksumString = new BigInteger(checksum).toString(36);
					if (!checksumString.equals(key))
					{
						brokenFile = true;
					}
				}
			}
			finally
			{
				output.close();
			}
			if (brokenFile && !file.delete())
			{
				Bukkit.getLogger().log(Level.SEVERE, "Could not delete file {0}", file.getPath());
			}
		}
		finally
		{
			input.close();
		}
		if (brokenFile)
		{
			throw new IOException("Checksum check failed.");
		}
	}
}
