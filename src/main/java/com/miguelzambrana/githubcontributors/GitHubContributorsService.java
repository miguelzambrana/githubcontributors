package com.miguelzambrana.githubcontributors;

import com.miguelzambrana.githubcontributors.cache.CacheClient;
import com.miguelzambrana.githubcontributors.httpservice.HTTPService;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import com.miguelzambrana.githubcontributors.repository.BasicAuthUsersRepository;
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

        logger.info("Load Users Repository (for BasicAuth)");
        BasicAuthUsersRepository.loadRepository();

        logger.info("Starting Service Monitor");
        Monitor.initiateSchedules();

        logger.info("Starting HTTP Service");
        HTTPService.startHTTPService();

        logger.info("Ready for shots!!");
        GitHubContributorsService.showArt();
    }

    public static void showArt () {
        System.out.println(
                "   ___ _ _                _       ___            _        _ _           _                 \n" +
                "  / _ (_) |_  /\\  /\\_   _| |__   / __\\___  _ __ | |_ _ __(_) |__  _   _| |_ ___  _ __ ___ \n" +
                " / /_\\/ | __|/ /_/ / | | | '_ \\ / /  / _ \\| '_ \\| __| '__| | '_ \\| | | | __/ _ \\| '__/ __|\n" +
                "/ /_\\\\| | |_/ __  /| |_| | |_) / /__| (_) | | | | |_| |  | | |_) | |_| | || (_) | |  \\__ \\\n" +
                "\\____/|_|\\__\\/ /_/  \\__,_|_.__/\\____/\\___/|_| |_|\\__|_|  |_|_.__/ \\__,_|\\__\\___/|_|  |___/\n" +
                "                                                                                          ");
    }

}
