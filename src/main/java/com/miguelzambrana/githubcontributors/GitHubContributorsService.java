package com.miguelzambrana.githubcontributors;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.httpservice.HTTPService;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubContributorsService {

    public static final Logger logger = LogManager.getLogger(GitHubContributorsService.class.getName());

    public static void main ( String[] args ) {
        logger.info("Read Properties files");
        Configuration.loadProperties();

        logger.info("Preparing HazelCast Cache");
        CacheClient.getInstance();

        logger.info("Starting Service Monitor");
        Monitor.initiateSchedules();

        logger.info("Starting HTTP Service");
        HTTPService.startHTTPService();

        logger.info("Ready for shots!!");
    }

}
