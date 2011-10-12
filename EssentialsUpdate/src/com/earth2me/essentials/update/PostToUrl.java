package com.earth2me.essentials.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;


public class PostToUrl
{
	private final transient URL url;
	private final transient String boundary;
	private final transient Random random = new Random();
	private final static String CRLF = "\r\n";
	private final static Charset UTF8 = Charset.forName("utf-8");

	public PostToUrl(final URL url)
	{
		this.url = url;
		final byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		this.boundary = "----------" + new BigInteger(bytes).toString(Character.MAX_RADIX) + "_$";
	}

	public String send(final Map<String, Object> data) throws IOException
	{
		final URLConnection connection = url.openConnection();
		connection.setRequestProperty("content-type", "multipart/form-data; boundary=" + boundary);
		final StringBuilder dataBuilder = new StringBuilder();
		for (Map.Entry<String, Object> entry : data.entrySet())
		{
			if (entry.getValue() instanceof String)
			{
				dataBuilder.append("--").append(boundary).append(CRLF);
				dataBuilder.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append('"').append(CRLF);
				dataBuilder.append(CRLF);
				dataBuilder.append(entry.getValue()).append(CRLF);
			}
			// TODO: Add support for file upload
		}
		dataBuilder.append("--").append(boundary).append("--").append(CRLF);
		dataBuilder.append(CRLF);
		connection.setDoOutput(true);
		final byte[] message = dataBuilder.toString().getBytes(UTF8);
		connection.setRequestProperty("content-length", Integer.toString(message.length));
		connection.connect();
		final OutputStream stream = connection.getOutputStream();
		stream.write(message);
		stream.close();
		final BufferedReader page = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF8));
		final StringBuilder input = new StringBuilder();
		String line;
		while ((line = page.readLine()) != null)
		{
			input.append(line).append("\n");
		}
		page.close();
		return input.toString();
	}
}
