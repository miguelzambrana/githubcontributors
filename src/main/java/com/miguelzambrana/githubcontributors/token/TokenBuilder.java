package com.miguelzambrana.githubcontributors.token;

import com.miguelzambrana.githubcontributors.configuration.Configuration;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by miki on 4/04/17.
 */
public class TokenBuilder {

    /**
     *
     * @param expireTime
     * @param initTime
     * @return
     */
    public static boolean validExpireTime ( String expireTime , long initTime )
    {
        try
        {
            // Parse expireTime to long value
            long expireTimeValue = Long.parseLong(expireTime);

            // and compare if expireTime is bigger than currentTime (initTime defined)
            if ( expireTimeValue >= initTime )
                 return true;
        }
        catch ( Exception e ) {}

        return false;
    }

    /**
     *
     * @param token
     * @param topOperation
     * @param location
     * @param expireTime
     * @return
     */
    public static boolean validToken ( String token , String topOperation , String location , String expireTime ) {

        // Check if token or expireTime is empty, is not valid token
        if ( StringUtils.isEmpty(token) || StringUtils.isEmpty(expireTime) ) {
             return false;
        }

        // Get generated token for current parameters
        String generatedToken = TokenBuilder.generateToken ( topOperation, location, expireTime );

        // Return if token is the same than generated token
        return generatedToken.equals(token);
    }

    /**
     *
     * @param topOperation
     * @param location
     * @param expireTime
     * @return
     */
    public static String generateToken ( String topOperation , String location , String expireTime ) {
        // Generate string with parameters and private key
        StringBuilder stringBuilder = new StringBuilder(topOperation)
                .append(location).append(expireTime).append(Configuration.TokenPrivateKey);

        // Apply Sha1 on the generated hash
        return DigestUtils.sha1Hex(stringBuilder.toString());
    }
}
