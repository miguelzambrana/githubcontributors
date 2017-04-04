package com.miguelzambrana.githubcontributors.httpservice.handlers;

import com.miguelzambrana.githubcontributors.configuration.Configuration;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * Created by miki on 3/04/17.
 */
public class VersionHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Non-bloq request
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        // Show Service version
        String version = Configuration.ServiceVersion;
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(version);
    }

}
