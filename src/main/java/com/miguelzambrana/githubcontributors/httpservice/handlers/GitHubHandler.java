package com.miguelzambrana.githubcontributors.httpservice.handlers;

import com.google.gson.Gson;
import com.miguelzambrana.githubcontributors.bean.ErrorMessage;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.cache.TopContributorsCache;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubHandler implements HttpHandler {

    public static final Logger logger = LogManager.getLogger(GitHubHandler.class.getName());
    public static final String jsonResponseContent  = "application/json";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Non-bloq request
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        // Current Time on start request
        long initTime = System.currentTimeMillis();

        try
        {
            // Get paths from request
            String[] paths = exchange.getRequestPath().split("/");

            // Request segments
            String sOperation = "";
            String sLocation  = "";

            // Get query segments from request path
            int currentSegment = 0;
            for ( String currentPath : paths ) {
                if (!StringUtils.isEmpty(currentPath)) {
                    switch ( currentSegment ) {
                        case 0: sOperation = currentPath.toLowerCase(); break;
                        case 1: sLocation  = currentPath; break;
                    }
                    currentSegment++;
                }
            }

            if ( StringUtils.isEmpty(sOperation) || StringUtils.isEmpty(sLocation) )
            {
                // Generate exception and throw up
                ContributorsException exception = new ContributorsException ( "You need to specify segments " +
                        "for top operation, and location", 1000);

                throw exception;
            }
            else
            {
                // If we have location, get it from Cache
                List<GitHubUserBean> topUsers = TopContributorsCache.getTopUsers(sLocation);

                // Operation number to search
                int topOperationUsers;

                // If operation starts with top (top5, top10, top30, top50)
                if ( sOperation.startsWith("top") ) {
                    // Get Number of users
                    topOperationUsers = Integer.valueOf(sOperation.replace("top", ""));
                } else {
                    // If some strange exception catch, throw up
                    ContributorsException contributorsException =
                            new ContributorsException ( "Operation Unknown, it should be like top5, top10, top50,..." , 1001 );

                    throw contributorsException;
                }

                // More than top 50 is not allowed...
                if ( ( topOperationUsers > 50 ) || ( topOperationUsers <= 0 ) ) {
                    // If some strange exception catch, throw up
                    ContributorsException contributorsException =
                            new ContributorsException ( "Operation with more than top50 or negative is not allowed" , 1002 );

                    throw contributorsException;
                }

                // Result list to show
                List<GitHubUserBean> currentList;

                if ( topOperationUsers <= topUsers.size() ) {
                    // Get sublist for current top users
                    currentList = topUsers.subList(0, topOperationUsers);
                } else {
                    currentList = topUsers;
                }

                // Create JSON result
                String jsonResponse = new Gson().toJson(currentList);

                // And response Json result
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, GitHubHandler.jsonResponseContent);
                exchange.getResponseSender().send(jsonResponse);
            }
        }
        catch (ContributorsException e)
        {
            // Create message response bean
            ErrorMessage errorMessage = new ErrorMessage ( e.getMessage() , e.getErrorCode() );

            // Create JSON result from message
            String jsonResponse = new Gson().toJson(errorMessage);

            // If top operation of location is empty, can't process it...
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, GitHubHandler.jsonResponseContent);
            exchange.getResponseSender().send(jsonResponse);

            // Add error request to Monitor
            Monitor.addErrorRequest();
        }
        catch (Exception e)
        {
            // Create message response bean
            ErrorMessage errorMessage = new ErrorMessage ( e.getMessage() , -1 );

            // Create JSON result from message
            String jsonResponse = new Gson().toJson(errorMessage);

            // If top operation of location is empty, can't process it...
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, GitHubHandler.jsonResponseContent);
            exchange.getResponseSender().send(jsonResponse);

            // Add error request to Monitor
            Monitor.addErrorRequest();
        }
        finally
        {
            // Calculate request total time (in millis)
            long requestTime = ( System.currentTimeMillis() - initTime );

            // Add request time to Monitor
            Monitor.addRequestTime(requestTime);
        }
    }

}
