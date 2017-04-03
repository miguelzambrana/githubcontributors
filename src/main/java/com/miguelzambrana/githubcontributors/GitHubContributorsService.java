package com.miguelzambrana.githubcontributors;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.httpservice.HTTPService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubContributorsService {

    public static final Logger logger = LogManager.getLogger(GitHubContributorsService.class.getName());

    public static void main ( String[] args ) {
        logger.info("Preparing HazelCast Cache");
        CacheClient.getInstance();

        logger.info("Starting HTTP Service");
        HTTPService.startHTTPService();

        logger.info("Ready for shots!!");
    }

}
