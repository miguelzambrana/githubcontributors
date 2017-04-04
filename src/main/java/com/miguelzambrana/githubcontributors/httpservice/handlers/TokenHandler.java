package com.miguelzambrana.githubcontributors.httpservice.handlers;

import com.google.gson.Gson;
import com.miguelzambrana.githubcontributors.bean.TokenGenerationMessage;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.token.TokenBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.lang.StringUtils;

/**
 * Created by miki on 4/04/17.
 */
public class TokenHandler implements HttpHandler {

    public final static long    expireTimeOffset    = 3600 * 1000;

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Non-bloq request
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        // If TokenGenerator module is enabled
        if ( Configuration.TokenGeneratorEnabled ) {
            // Request segments and parameters
            String sOperation = "";
            String sLocation  = "";
            String expireTime = null;

            // Get query segments from query parameters
            if (!exchange.getQueryParameters().isEmpty()) {

                if ( exchange.getQueryParameters().get("topUsers") != null )
                    sOperation   = exchange.getQueryParameters().get("topUsers").element();

                if ( exchange.getQueryParameters().get("location") != null )
                    sLocation    = exchange.getQueryParameters().get("location").element();

                if ( exchange.getQueryParameters().get("expireTime") != null )
                    expireTime   = exchange.getQueryParameters().get("expireTime").element();
            }

            // If expireTime is null, generate one for one hour of time
            if ( StringUtils.isEmpty(expireTime) ) {
                 expireTime = String.valueOf(System.currentTimeMillis() + expireTimeOffset);
            }

            // Generate token for request parameters
            String generatedToken = TokenBuilder.generateToken ( sOperation , sLocation , expireTime );

            // Create token response message
            TokenGenerationMessage tokenGenerationMessage =
                    new TokenGenerationMessage ( "Generated token for defined parameters" , generatedToken , expireTime );

            // Create JSON result
            String jsonResponse = new Gson().toJson(tokenGenerationMessage);

            // Show Token Message
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Show Module not enabled
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Module not enabled");
        }

    }

}
