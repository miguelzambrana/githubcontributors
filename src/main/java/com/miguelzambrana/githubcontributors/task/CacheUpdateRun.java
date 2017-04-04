package com.miguelzambrana.githubcontributors.task;

import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.cache.TopContributorsCache;

import java.util.List;

/**
 * Created by miki on 4/04/17.
 * Runnable that update the Cache values asynchronously
 */
public class CacheUpdateRun implements Runnable {
    private String                  location;
    private List<GitHubUserBean>    topUsers;

    public CacheUpdateRun ( String location , List<GitHubUserBean> topUsers ) {
        this.location   = location;
        this.topUsers   = topUsers;
    }

    @Override
    public void run () {
        // If result is not null, update TopContributors cache
        if ( topUsers != null ) {
             TopContributorsCache.updateCaches(location, topUsers);
        }
    }
}
