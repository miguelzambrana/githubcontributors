package com.miguelzambrana.githubcontributors.httpservice;

import com.miguelzambrana.githubcontributors.httpservice.handlers.GitHubHandler;
import com.miguelzambrana.githubcontributors.httpservice.handlers.HelloHandler;
import com.miguelzambrana.githubcontributors.httpservice.handlers.VersionHandler;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by miki on 3/04/17.
 */
public class HTTPService {

    private static final Logger logger = LogManager.getLogger(HTTPService.class);

    public static void startHTTPService ()
    {
        // Create Undertow service on defined port
        // TODO Set port defined by Properties
        Undertow server = Undertow.builder()
                .addHttpListener(Configuration.HttpServicePort, Configuration.ServiceHttpHost)
                .setHandler(HTTPService.getHandlerChain())
                .build();
        server.start();
    }

    public static HttpHandler getHandlerChain() {

        // Define path template
        HttpHandler pathHandler = Handlers.pathTemplate()
                .add("/{topUsers}/{location}"   , new GitHubHandler())
                .add("/version"                 , new VersionHandler())
                .add(""                         , new HelloHandler());

        // Set response headers Handle
        HttpHandler headerHandler = headerHandler(pathHandler);

        // Return Handler
        return headerHandler;
    }

    private static HttpHandler headerHandler(HttpHandler next){

        // Define response headers
        HttpHandler headerHandler = new HttpHandler() {

            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                exchange.getResponseHeaders().put(Headers.DATE, System.currentTimeMillis());
                exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, System.currentTimeMillis());
                exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*" );
                exchange.getResponseHeaders().put(Headers.CONNECTION, "close" );
                next.handleRequest(exchange);
            }
        };
        return headerHandler;
    }
}
