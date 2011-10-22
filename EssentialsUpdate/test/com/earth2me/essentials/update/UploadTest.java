package com.earth2me.essentials.update;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Test;


public class UploadTest extends TestCase
{
	@Test
	public void testPastieUpload()
	{
		try
		{
			final PastieUpload pastie = new PastieUpload();
			assertNotNull(pastie);
			//final String url = pastie.send("test");
			//System.out.println(url);
		}
		catch (IOException ex)
		{
			Logger.getLogger(UploadTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
