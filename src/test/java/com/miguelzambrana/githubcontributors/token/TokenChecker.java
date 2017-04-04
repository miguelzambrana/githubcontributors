package com.miguelzambrana.githubcontributors.token;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by miki on 5/04/17.
 */
public class TokenChecker extends TestCase {

    @Test
    public void testTokenCheck()
    {
        String topOperation     = "top20";
        String location         = "Barcelona";
        String expireTime       = "2491342022491";
        String expectedToken    = "31082ed919af2f9600a598d12be56e82387f9a71";

        String generatedToken   = TokenBuilder.generateToken(topOperation, location, expireTime);

        assertEquals  (expectedToken, generatedToken);
    }

}
