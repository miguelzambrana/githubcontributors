package com.miguelzambrana.githubcontributors.githubapi;

import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by miki on 5/04/17.
 */
public class GitHubInterfaceCheck extends TestCase {

    @Test
    public void testGithubInterfaceBarcelona()
    {
        try
        {
            List<GitHubUserBean> topBarcelona = GitHubInterface.gitHubRequest("Barcelona");

            // topBarcelona have more than 60 results
            assertEquals ( true , ( topBarcelona.size() > 0 ) );
            assertEquals ( 60 , topBarcelona.size() , 10 );
        }
        catch ( ContributorsException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testGithubInterfaceUnknownLocation()
    {
        try
        {
            List<GitHubUserBean> topUnknown = GitHubInterface.gitHubRequest("MeLoInventoCiudadDeVacaciones");

            // we expect 0 results for Unknown city
            assertEquals ( 0 , topUnknown.size() , 0 );
        }
        catch ( ContributorsException e )
        {
            e.printStackTrace();
        }
    }
}
