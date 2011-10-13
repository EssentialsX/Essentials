package com.earth2me.essentials;

import junit.framework.TestCase;
import com.earth2me.essentials.settings.Settings;
import com.earth2me.essentials.storage.StorageObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import org.junit.Test;


public class StorageTest extends TestCase
{
	@Test
	public void testSettings()
	{
		assertTrue(StorageObject.class.isAssignableFrom(Settings.class));
		final ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
		final Reader reader = new InputStreamReader(bais);
		final Settings settings = StorageObject.load(Settings.class, reader);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(baos);
		settings.save(writer);
		writer.close();
		byte[] written = baos.toByteArray();
		System.out.println(new String(written));
		final ByteArrayInputStream bais2 = new ByteArrayInputStream(written);
		final Reader reader2 = new InputStreamReader(bais2);
		final Settings settings2 = StorageObject.load(Settings.class, reader2);
		assertEquals("Default and rewritten config should be equal", settings, settings2);
	}
}
