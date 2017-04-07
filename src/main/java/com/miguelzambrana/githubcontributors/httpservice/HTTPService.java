package com.miguelzambrana.githubcontributors.httpservice;

import com.miguelzambrana.githubcontributors.httpservice.basicauth.MapIdentityManager;
import com.miguelzambrana.githubcontributors.httpservice.handlers.GitHubHandler;
import com.miguelzambrana.githubcontributors.httpservice.handlers.HelloHandler;
import com.miguelzambrana.githubcontributors.httpservice.handlers.TokenHandler;
import com.miguelzambrana.githubcontributors.httpservice.handlers.VersionHandler;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.repository.BasicAuthUsersRepository;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miki on 3/04/17.
 */
public class HTTPService {

    private static final Logger logger = LogManager.getLogger(HTTPService.class);

    public static void startHTTPService ()
    {
        // Create an Identity Manager with Users (for BasicAuth)
        final IdentityManager identityManager = new MapIdentityManager(BasicAuthUsersRepository.getUsers());

        // Create Undertow service on defined port
        Undertow server = Undertow.builder()
                .addHttpListener(Configuration.HttpServicePort, Configuration.ServiceHttpHost)
                .setHandler(addSecurity(HTTPService.getHandlerChain(), identityManager))
                .build();
        server.start();
    }

    public static HttpHandler getHandlerChain() {

        // Define path template
        HttpHandler pathHandler = Handlers.pathTemplate()
                .add("/{topUsers}/{location}"                   , new GitHubHandler())
                .add("/tokenGenerator/{topUsers}/{location}"    , new TokenHandler())
                .add("/version"                                 , new VersionHandler())
                .add(""                                         , new HelloHandler());

        // Set response headers Handle
        HttpHandler headerHandler = headerHandler(pathHandler);

        // Return Handler
        return headerHandler;
    }

    /**
     * addSecurity for BasicAuth
     * @param toWrap
     * @param identityManager
     * @return
     */
    private static HttpHandler addSecurity(final HttpHandler toWrap, final IdentityManager identityManager) {
        HttpHandler handler = toWrap;

        // If BasicAuth is enabled, configure handler for BasicAuth with identityManager (users)
        if ( Configuration.BasicAuthEnabled ) {
            handler = new AuthenticationCallHandler(handler);
            handler = new AuthenticationConstraintHandler(handler);
            final List<AuthenticationMechanism> mechanisms = Collections.<AuthenticationMechanism>singletonList(
                    new BasicAuthenticationMechanism("Miki"));
            handler = new AuthenticationMechanismsHandler(handler, mechanisms);
            handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
        }

        return handler;
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
