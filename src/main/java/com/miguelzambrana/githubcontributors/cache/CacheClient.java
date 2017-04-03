package com.miguelzambrana.githubcontributors.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;

public class CacheClient
{
    public static final Logger logger = LogManager.getLogger(CacheClient.class.getName());

    private static class InstanceHolder
    {
        public static CacheClient instance = new CacheClient();
    }

    public static CacheClient getInstance()
    {
        return InstanceHolder.instance;
    }

    private CacheClient ()
    {
        try
        {
            Config cfg = new XmlConfigBuilder("conf/hazelcast.xml").build();

            cfg.getGroupConfig().setName(Configuration.HazelCastGroup);
            cfg.setInstanceName(Configuration.HazelCastGroup);

            Hazelcast.newHazelcastInstance(cfg);
        }
        catch ( FileNotFoundException e )
        {
            logger.error ( "Error opening Hazelcast Config: " + e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error("Exception info", e);
        }
    }

    public void closeClient ()
    {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(Configuration.HazelCastGroup);

        if ( instance != null )
        {
            instance.shutdown();
        }
    }

    public HazelcastInstance getClient ()
    {
        return Hazelcast.getHazelcastInstanceByName(Configuration.HazelCastGroup);
    }

}
