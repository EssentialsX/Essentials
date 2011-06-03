package com.earth2me.essentials;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EssentialsDependancyChecker
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	final Essentials ess;

	public EssentialsDependancyChecker(Essentials ess)
	{
		this.ess = ess;
	}

	public void checkProtectDependancies()
	{
		final String dependancyLocation = "http://mirrors.ibiblio.org/pub/mirrors/maven2/c3p0/c3p0/0.9.1.2/c3p0-0.9.1.2.jar";
		File dependancyFile = new File("lib/c3p0-0.9.1.2.jar");
		if (!dependancyFile.exists())
		{
				logger.log(Level.INFO, Util.i18n("dependancyNotFound"));
			try
			{
				URL url = new URL(dependancyLocation);
				BufferedInputStream inStream = new BufferedInputStream(url.openStream());
				FileOutputStream fos = new FileOutputStream(dependancyFile);
				BufferedOutputStream outStream = new BufferedOutputStream(fos, 1024);

				byte[] buffer = new byte[1024];
				int len = 0;

				while ((len = inStream.read(buffer)) > 0)
				{
					outStream.write(buffer, 0, len);
				}
				outStream.close();
				fos.close();
				inStream.close();
				logger.log(Level.INFO, Util.format("dependancyDownloaded", dependancyFile.getName()));
			
			}
			catch (MalformedURLException ex)
			{
				logger.log(Level.SEVERE, Util.i18n("urlMalformed"), ex);
			}
			catch (FileNotFoundException ex)
			{
				logger.log(Level.SEVERE, Util.i18n("dependancyException"), ex);
			}
			catch (IOException ex)
			{
				logger.log(Level.SEVERE, Util.i18n("dependancyException"), ex);
			}
		}
	}
}
