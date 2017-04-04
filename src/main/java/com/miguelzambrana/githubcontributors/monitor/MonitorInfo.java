package com.miguelzambrana.githubcontributors.monitor;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.cache.TopContributorsCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

/**
 * Created by miki on 4/04/17.
 */
class MonitorInfo implements Runnable {

    public static final Logger logger = LogManager.getLogger(MonitorInfo.class.getName());

    public MonitorInfo () {}

    @Override
    public void run () {

        // Calculate percent and average values
        double percentHitCache       = ( ( (double) Monitor.numCacheHits / (double) Monitor.numHTTPRequests ) * 100.0 );
        double percentMissCache      = ( ( (double) Monitor.numGitHubRequests / (double) Monitor.numHTTPRequests ) * 100.0 );
        double percentErrorRequest   = ( ( (double) Monitor.numErrorRequests / (double) Monitor.numHTTPRequests ) * 100.0 );
        double avgRequestTime        = ( ( (double) Monitor.totalRequestTime / (double) Monitor.numHTTPRequests ) );

        // DecimalFormat object (with two decimals)
        DecimalFormat df = new DecimalFormat("#.##");

        // Show Monitor info
        logger.info("** Monitor Report **********************************");
        logger.info(" > Total HTTP Requests:     {}" , Monitor.numHTTPRequests );
        logger.info(" > Total Hit Cache:         {} ({}%)" , Monitor.numCacheHits , df.format(percentHitCache) );
        logger.info(" > Total GitHub Requests:   {} ({}%)" , Monitor.numGitHubRequests , df.format(percentMissCache) );
        logger.info(" > Total Error Requests:    {} ({}%)" , Monitor.numErrorRequests ,  df.format(percentErrorRequest) );
        logger.info(" > Avg. Req. Process time:  {}ms", df.format(avgRequestTime) );
        logger.info(" > Cache Updated Tasks:     {}" , TopContributorsCache.completedUpdateTasks() );
        logger.info(" > Cache Contributors size: {}" , TopContributorsCache.cacheSize() );
        logger.info(" > Hazelcast nodes:         {}" , CacheClient.getInstance().getHazelcastNodes() );
        logger.info("****************************************************");
    }
}
