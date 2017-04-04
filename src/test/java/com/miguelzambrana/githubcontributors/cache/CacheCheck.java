package com.miguelzambrana.githubcontributors.cache;

import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by miki on 5/04/17.
 */
public class CacheCheck extends TestCase {

    @Test
    public void testCacheBarcelona()
    {
        try
        {
            List<GitHubUserBean> topBarcelona = TopContributorsCache.getTopUsers("Barcelona");

            // topBarcelona have more than 60 results
            assertEquals ( true , ( topBarcelona.size() > 0 ) );
            assertEquals ( 60 , topBarcelona.size() , 10 );
        }
        catch ( ContributorsException e )
        {
            e.printStackTrace();
        }
    }

}
