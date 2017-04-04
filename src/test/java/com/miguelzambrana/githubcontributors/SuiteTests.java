package com.miguelzambrana.githubcontributors;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.httpservice.HTTPService;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by miki on 4/04/17.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
    com.miguelzambrana.githubcontributors.webservice.BasicWebContainer.class,
    com.miguelzambrana.githubcontributors.token.TokenChecker.class,
    com.miguelzambrana.githubcontributors.githubapi.GitHubInterfaceCheck.class,
    com.miguelzambrana.githubcontributors.cache.CacheCheck.class,
})

public class SuiteTests {

    @BeforeClass
    public static void setUp()
    {
        try
        {
            System.out.println("Start Suite Tests");

            System.out.println("Read Properties files");
            Configuration.loadProperties();

            System.out.println("Preparing HazelCast Cache");
            CacheClient.getInstance();

            System.out.println("Starting Service Monitor");
            Monitor.initiateSchedules();

            System.out.println("Starting HTTP Service");
            HTTPService.startHTTPService();

            System.out.println("Ready for shots!!");
            GitHubContributorsService.showArt();

            // Wait for start tests... (Time to WebContainer is ready)
            Thread.sleep(5000);
        }
        catch ( Exception e ) { e.printStackTrace(); }
    }

    @AfterClass
    public static void tearDown()
    {
        System.out.println("Finish!!!");
    }
}
