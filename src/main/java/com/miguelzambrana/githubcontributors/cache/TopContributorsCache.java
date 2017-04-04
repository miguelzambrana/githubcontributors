package com.miguelzambrana.githubcontributors.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hazelcast.core.IMap;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.githubapi.GitHubInterface;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import com.miguelzambrana.githubcontributors.task.CacheUpdateRun;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by miki on 3/04/17.
 *  TopContributorsCache have two Cache levels
 *   - Level1: LocalCache based on Caffeine Maps
 *   - Level2: DistributedCache based on HazelCast
 *  If cache miss, then get from GitHub and update the 2 Cache levels
 */
public class TopContributorsCache {

    private static IMap<String,List<GitHubUserBean>> hazelcastMap =
            CacheClient.getInstance().getClient().getMap ( "GITHUB_TOP" );

    private static Cache<String, List<GitHubUserBean>> localCache =
            Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private static ThreadPoolExecutor poolExecutor =
            new ThreadPoolExecutor(Configuration.ThreadExecutorPoolSize, Configuration.ThreadExecutorPoolSize,
                    0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(Configuration.ThreadExecutorQueueSize) );

    /**
     * Get TopUsers bean from Cache or GitHub (if miss cache)
     * @param location
     * @return
     * @throws ContributorsException
     */
    public static List<GitHubUserBean> getTopUsers ( String location ) throws ContributorsException {

        // Define GitHubUser list
        List<GitHubUserBean> topUsers;

        try
        {
            // Get Result Top List from Local Cache (Level 1)
            topUsers = localCache.getIfPresent(location);

            // If topUsers is not null, return values
            if ( topUsers != null ) {
                // Add hit Cache to Monitor
                Monitor.addRequestFromCache();

                return topUsers;
            }

            // Get Result Top List from Distributed Cache (Level 2)
            topUsers = hazelcastMap.get(location);

            // If topUsers is not null, return values
            if ( topUsers != null ) {
                // Add hit Cache to Monitor
                Monitor.addRequestFromCache();

                // Storage list to Local Cache
                localCache.put(location, topUsers);

                return topUsers;
            }

            // If don't have data from Cache, do request to GitHub API
            topUsers = GitHubInterface.gitHubRequest(location);

            // Add hit Cache to Monitor
            Monitor.addRequestFromGitHub();

            // Create action to update Cache asynchronously, and send to pool Executor
            CacheUpdateRun cacheUpdateRun = new CacheUpdateRun ( location , topUsers );
            poolExecutor.submit(cacheUpdateRun);

        } catch ( ContributorsException contributorsException ) {
            // If catch some ContributorException, throw to Handler in order to show
            throw contributorsException;
        }
        catch ( Exception e ) {
            // If some strange exception catch, throw up
            throw new ContributorsException ( "Unknown behaviour: " + e.getMessage() , 1003 );
        }

        // Return top users
        return topUsers;
    }

    /**
     * Update two Cache levels with topUsers in current location
     * @param location
     * @param topUsers
     */
    public static void updateCaches ( String location , List<GitHubUserBean> topUsers ) {
        // Put topUsers in both caches
        localCache.put   (location, topUsers);
        hazelcastMap.put (location, topUsers, 1, TimeUnit.HOURS);
    }

    /**
     * Number of elements in the distributed cache
     * @return
     */
    public static int cacheSize () {
        // Return cache size (number of locations inserted on cache)
        return hazelcastMap.size();
    }

    public static long completedUpdateTasks () {
        return poolExecutor.getCompletedTaskCount();
    }
}
