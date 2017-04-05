package com.miguelzambrana.githubcontributors.httpservice.handlers;

import com.google.gson.Gson;
import com.miguelzambrana.githubcontributors.bean.ErrorMessage;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.cache.TopContributorsCache;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import com.miguelzambrana.githubcontributors.monitor.Monitor;
import com.miguelzambrana.githubcontributors.token.TokenBuilder;
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
            // Request segments and parameters
            String sOperation = "";
            String sLocation  = "";
            String expireTime = null;
            String token      = null;

            // Get query segments from query parameters
            if (!exchange.getQueryParameters().isEmpty()) {

                if ( exchange.getQueryParameters().get("topUsers") != null )
                     sOperation   = exchange.getQueryParameters().get("topUsers").element().toLowerCase();

                if ( exchange.getQueryParameters().get("location") != null )
                     sLocation    = exchange.getQueryParameters().get("location").element();

                if ( exchange.getQueryParameters().get("expireTime") != null )
                     expireTime   = exchange.getQueryParameters().get("expireTime").element();

                if ( exchange.getQueryParameters().get("token") != null )
                     token        = exchange.getQueryParameters().get("token").element();
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
                // If Token is enabled, we need to check values
                if ( Configuration.TokenAuthEnabled )
                {
                    if ( StringUtils.isEmpty(expireTime) || StringUtils.isEmpty(token) ) {
                        // If token or expireTime is empty, notify to user
                        throw new ContributorsException("Parameters token and expireTime are mandatory", 1010);
                    }

                    if ( !TokenBuilder.validExpireTime ( expireTime , initTime ) ) {
                        // If token has expire, notify to user
                        throw new ContributorsException("ExpireTime has expired, you need to renew token", 1011);
                    }

                    if ( !TokenBuilder.validToken ( token , sOperation , sLocation , expireTime ) ) {
                        // If token is not valid, we need to show it
                        throw new ContributorsException("Token is not valid for current request", 1012);
                    }
                }

                // If we have location, get it from Cache
                List<GitHubUserBean> topUsers = TopContributorsCache.getTopUsers(sLocation);

                // Operation number to search
                int topOperationUsers = getTopOperationUsers ( sOperation );

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
            ErrorMessage errorMessage = new ErrorMessage ( "Service Exception: " + e.getMessage() , -1 );

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

    public int getTopOperationUsers ( String sOperation ) throws ContributorsException
    {
        int topOperationUsers;

        // If operation starts with top (top5, top10, top30, top50)
        if ( sOperation.startsWith("top") ) {
            // Get Number of users
            topOperationUsers = Integer.valueOf(sOperation.replace("top", ""));
        } else {
            // If some strange exception catch, throw up
            throw new ContributorsException ( "Operation Unknown, it should be like top5, top10, top50,..." , 1001 );
        }

        // More than top 50 is not allowed...
        if ( ( topOperationUsers > 50 ) || ( topOperationUsers <= 0 ) ) {
            // If some strange exception catch, throw up
            throw new ContributorsException ( "Operation with more than top50 or negative is not allowed" , 1002 );
        }

        return topOperationUsers;
    }

}
