package com.miguelzambrana.githubcontributors.monitor;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.cache.TopContributorsCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by miki on 4/04/17.
 */
class MonitorInfo implements Runnable {

    public static final Logger logger = LogManager.getLogger(MonitorInfo.class.getName());

    public MonitorInfo () {}

    @Override
    public void run () {

        float percentHitCache       = (float) ( ( (double) Monitor.numCacheHits / (double) Monitor.numHTTPRequests ) * 100.0 );
        float percentMissCache      = (float) ( ( (double) Monitor.numGitHubRequests / (double) Monitor.numHTTPRequests ) * 100.0 );
        float percentErrorRequest   = (float) ( ( (double) Monitor.numErrorRequests / (double) Monitor.numHTTPRequests ) * 100.0 );
        float avgRequestTime        = (float) ( ( (double) Monitor.totalRequestTime / (double) Monitor.numHTTPRequests ) );

        logger.info("** Monitor Report **********************************");
        logger.info(" > Total HTTP Requests:     " + Monitor.numHTTPRequests );
        logger.info(" > Total Hit Cache:         " + Monitor.numCacheHits + " (" + percentHitCache + "%)" );
        logger.info(" > Total GitHub Requests:   " + Monitor.numGitHubRequests + " (" + percentMissCache + "%)" );
        logger.info(" > Total Error Requests:    " + Monitor.numErrorRequests + " (" + percentErrorRequest + "%)" );
        logger.info(" > Avg. request time:       " + avgRequestTime + "ms" );
        logger.info(" > Cache Contributors size: " + TopContributorsCache.cacheSize() );
        logger.info(" > Hazelcast nodes:         " + CacheClient.getInstance().getHazelcastNodes() );
        logger.info("****************************************************");
    }
}
