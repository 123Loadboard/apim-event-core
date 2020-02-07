package org.one23lb.apim.event.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration
{
	private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

	public static Properties get() throws IOException
	{
		final Properties props = new Properties(System.getProperties());

		final String filename = props.getProperty("config");

		if (filename != null)
		{
			try (final FileInputStream fis = new FileInputStream(filename))
			{
				props.load(fis);
			}

			LOG.info("Loaded configuration properties from " + filename);
		}
		else
		{
			final File file = new File("config.properties");

			if (file.exists())
			{
				try (final FileInputStream fis = new FileInputStream(file))
				{
					props.load(fis);
				}

				LOG.info("Loaded configuration properties from " + file);
			}
			else
			{
				LOG.info("Loaded configuration properties from system properties only.");
			}
		}

		return props;
	}
}
