package com.miguelzambrana.githubcontributors.cache;

import com.hazelcast.core.IMap;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.githubapi.GitHubInterface;
import com.miguelzambrana.githubcontributors.exceptions.ContributorsException;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by miki on 3/04/17.
 */
public class TopContributorsCache {

    private static IMap<String,List<GitHubUserBean>> hazelcastMap =
            CacheClient.getInstance().getClient().getMap ( "GITHUB_TOP" );

    public static final Logger logger = LogManager.getLogger(TopContributorsCache.class.getName());

    public static List<GitHubUserBean> getTopUsers ( String location ) throws ContributorsException {

        // Define GitHubUser list
        List<GitHubUserBean> topUsers = null;

        try
        {
            // Get Result Top List from Cache
            topUsers = hazelcastMap.get(location);

            // If topUsers is not null, return values
            if ( topUsers != null ) {
                 // Add hit Cache to Monitor
                 Monitor.addRequestFromCache();

                 return topUsers;
            }

            // If don't have data from Cache, do request to GitHub API
            topUsers = GitHubInterface.gitHubRequest(location);

            // Add hit Cache to Monitor
            Monitor.addRequestFromGitHub();

            // If result is not null, put to Cache for an hour...
            if ( topUsers != null ) {
                 hazelcastMap.put(location, topUsers, 1, TimeUnit.HOURS);
            }

        } catch ( ContributorsException contributorsException ) {
            // If catch some ContributorException, throw to Handler in order to show
            throw contributorsException;
        }
        catch ( Exception e ) {
            // If some strange exception catch, throw up
            ContributorsException contributorsException =
                    new ContributorsException ( "Unknown behaviour: " + e.getMessage() , 1003 );

            throw contributorsException;
        }

        // Return top users
        return topUsers;
    }

    public static int cacheSize () {
        // Return cache size (number of locations inserted on cache)
        return hazelcastMap.size();
    }
}
