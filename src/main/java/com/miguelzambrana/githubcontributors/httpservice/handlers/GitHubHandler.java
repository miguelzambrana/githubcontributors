package com.miguelzambrana.githubcontributors.httpservice.handlers;

import com.google.gson.Gson;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.githubapi.GitHubInterface;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        // Get paths from request
        String[] paths = exchange.getRequestPath().split("/");

        // Request segments
        String sTop = "";
        String sLocation = "";

        // Get query segments from request path
        int currentSegment = 0;
        for ( String currentPath : paths ) {
            if (!StringUtils.isEmpty(currentPath)) {
                switch ( currentSegment ) {
                    case 0: sTop = currentPath; break;
                    case 1: sLocation = currentPath; break;
                }
                currentSegment++;
            }
        }

        if ( StringUtils.isEmpty(sTop) || StringUtils.isEmpty(sLocation) )
        {
            // If top operation of location is empty, can't process it...
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Can't process operation!!");
        }
        else
            {
            System.out.println("Do request for " + sTop + " and location " + sLocation);

            // Else, process current operation
            List<GitHubUserBean> topUsers = GitHubInterface.gitHubRequest(sLocation);

            // Get Number of users
            int topNumber = Integer.valueOf(sTop.replace("top",""));

            // Get sublist for current top users
            List<GitHubUserBean> currentList = topUsers.subList(0, topNumber);

            // Create JSON result
            String jsonResponse = new Gson().toJson(currentList);

            // And response Json result
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        }
    }

}
