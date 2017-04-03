package com.miguelzambrana.githubcontributors.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;

public class CacheClient
{
    public static final Logger logger           = LogManager.getLogger(CacheClient.class.getName());
    public static final String HZ_CLUSTER_GROUP = "githubcontributors";

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
            Config cfg;

            try
            {
                cfg = new XmlConfigBuilder("conf/hazelcast.xml").build();
            }
            catch ( FileNotFoundException e )
            {
                logger.error ( "Error opening Hazelcast Config: " + e.getMessage() );

                return;
            }

            cfg.getGroupConfig().setName(CacheClient.HZ_CLUSTER_GROUP);
            cfg.setInstanceName(CacheClient.HZ_CLUSTER_GROUP);

            Hazelcast.newHazelcastInstance(cfg);
        }
        catch ( Exception e ) { logger.error("Exception info", e); }
    }

    public void closeClient ()
    {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(CacheClient.HZ_CLUSTER_GROUP);

        if ( instance != null )
        {
            instance.shutdown();
        }
    }

    public HazelcastInstance getClient ()
    {
        return Hazelcast.getHazelcastInstanceByName(CacheClient.HZ_CLUSTER_GROUP);
    }

}
