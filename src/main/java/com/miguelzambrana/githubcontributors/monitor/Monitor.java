package com.miguelzambrana.githubcontributors.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by miki on 4/04/17.
 */
public class Monitor {

    private static final int NUM_THREADS = 1;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(NUM_THREADS);

    public static long numHTTPRequests     = 0;
    public static long numCacheHits        = 0;
    public static long numGitHubRequests   = 0;
    public static long numErrorRequests    = 0;
    public static long totalRequestTime    = 0;

    public static void initiateSchedules()
    {
        // Clean Media maps each 30 minutes
        scheduler.scheduleAtFixedRate(new MonitorInfo(), 1, 1, TimeUnit.MINUTES);
    }

    public static synchronized void addRequestFromCache () {
        Monitor.numCacheHits++;
    }

    public static synchronized void addRequestFromGitHub () {
        Monitor.numGitHubRequests++;
    }

    public static synchronized void addErrorRequest () {
        Monitor.numErrorRequests++;
    }

    public static synchronized void addRequestTime ( long requestTime ) {
        Monitor.numHTTPRequests++;
        Monitor.totalRequestTime += requestTime;
    }
}
