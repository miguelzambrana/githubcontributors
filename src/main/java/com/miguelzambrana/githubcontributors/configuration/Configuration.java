package com.miguelzambrana.githubcontributors.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * Created by miki on 4/04/17.
 */
public class Configuration
{
    public  static final Logger logger = LogManager.getLogger(Configuration.class.getName());

    public  static int              HttpServicePort         = 8080;
    public  static String           HazelCastGroup          = "GitHubContributors";
    public  static int              ThreadExecutorPoolSize  = 32;
    public  static int              ThreadExecutorQueueSize = 1000000;
    public  static String           ServiceHttpHost         = "localhost";
    public  static boolean          TokenAuthEnabled        = true;
    public  static boolean          TokenGeneratorEnabled   = true;
    public  static String           TokenPrivateKey         = "NewRelicGo";
    public  static String           GitHubAccessToken       = "93a0f3c55f46655af06e364c880cbe9714b36ba6";

    public  static final String     ServiceVersion          = "1.0.4";
    public  static final String     ConfigFileName          = "./conf/configuration.properties";

    private static Properties       properties = null;

    public static void loadProperties ()
    {
        try
        {
            // Create new Properties
            Configuration.properties = new Properties();

            // Open file (from config filename)
            File file = new File(Configuration.ConfigFileName);

            // If not exists, show error and return
            if ( !file.exists() ) {
                logger.error ("Configuration File Not Found");
                return;
            }

            // Load properties from file
            try ( FileInputStream in = new FileInputStream(Configuration.ConfigFileName) ) {
                properties.load(in);
            }

            // Refresh Configuration parameters
            Configuration.refreshConfigParameters();
        }
        catch( Exception ex )
        {
            logger.error(ex);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void refreshConfigParameters()
    {
        for ( Map.Entry<Object,Object> entry : properties.entrySet() )
        {
            String key   = (String) entry.getKey();
            String value = (String) entry.getValue();

            try
            {
                Field field = Configuration.class.getDeclaredField(key);
                field.setAccessible(true);

                if( field.getType().equals (long.class) )
                {
                    field.setLong( null,  new Long(value).longValue() );
                }
                else if( field.getType().equals ( int.class ) )
                {
                    field.setInt(  null,  new Integer(value).intValue() );
                }
                else if( field.getType().equals ( double.class ) )
                {
                    field.setDouble(  null,  new Double(value).doubleValue() );
                }
                else if( field.getType().equals ( boolean.class ) )
                {
                    field.setBoolean(null,  new Boolean(value).booleanValue() );
                }
                else
                {
                    field.set(null, value);
                }

                logger.info ("Configuration Read Flag " + key + " :: " + value );
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
    }

}
