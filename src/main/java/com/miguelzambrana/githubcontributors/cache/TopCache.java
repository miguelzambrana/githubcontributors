package com.miguelzambrana.githubcontributors.cache;

import com.hazelcast.core.IMap;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.githubapi.GitHubInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by miki on 3/04/17.
 */
public class TopCache {

    private static IMap<String,List<GitHubUserBean>> hazelcastMap =
            CacheClient.getInstance().getClient().getMap ( "GITHUB_TOP" );

    public static final Logger logger = LogManager.getLogger(TopCache.class.getName());

    public static List<GitHubUserBean> getTopUsers ( String location ) {

        // Define GitHubUser list
        List<GitHubUserBean> topUsers = null;

        try {

            // Get Result Top List from Cache
            topUsers = hazelcastMap.get(location);

            // If topUsers is not null, return values
            if ( topUsers != null ) {
                 logger.info("Read from Cache");
                 return topUsers;
            }

            // If don't have data from Cache, do request to GitHub API
            logger.info("Do request to GitHub");
            topUsers = GitHubInterface.gitHubRequest(location);

            // If result is not null, put to Cache for an hour...
            if ( topUsers != null ) {
                 hazelcastMap.put(location, topUsers, 1, TimeUnit.HOURS);
            }

        } catch ( Exception e ) {
            logger.error(e.getMessage());
        }

        // Return top users
        return topUsers;
    }
}
