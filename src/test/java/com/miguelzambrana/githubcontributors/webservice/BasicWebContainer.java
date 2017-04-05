package com.miguelzambrana.githubcontributors.webservice;

import com.miguelzambrana.githubcontributors.configuration.Configuration;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miki on 4/04/17.
 */

public class BasicWebContainer extends TestCase
{
    protected void setUp()
    {
        Configuration.TokenAuthEnabled = true;
    }

    @Test
    public void testWebContainer()
    {
        String url = "http://localhost:8080/version";

        String responseWebContainer = BasicWebContainer.simpleRequest(url);

        assertEquals("GitHubContributorsService v" + Configuration.ServiceVersion, responseWebContainer);
    }

    @Test
    public void testIncorrectExpireTime()
    {
        String url = "http://localhost:8080/top5/Madrid?token=817069af8e348207d469c99e00be938e342ab728&expireTime=1491342013";

        String responseWebContainer = BasicWebContainer.simpleRequest(url);

        assertEquals("{\"errorMessage\":\"ExpireTime has expired, you need to renew token\",\"errorCode\":1011}", responseWebContainer);
    }

    @Test
    public void testIncorrectToken()
    {
        String url = "http://localhost:8080/top10/Madrid?token=812a369af8e348207d469c99e00be938e342ab728&expireTime=2491342022491";

        String responseWebContainer = BasicWebContainer.simpleRequest(url);

        assertEquals("{\"errorMessage\":\"Token is not valid for current request\",\"errorCode\":1012}", responseWebContainer);
    }

    @Test
    public void testIncorrectTopNumber()
    {
        String url = "http://localhost:8080/top60/Madrid?token=810bc03f167bb4a453f4abfee0dca38b61f8a61a&expireTime=2491342022491";

        String responseWebContainer = BasicWebContainer.simpleRequest(url);

        assertEquals("{\"errorMessage\":\"Operation with more than top50 or negative is not allowed\",\"errorCode\":1002}", responseWebContainer);
    }

    @Test
    public void testValidRequest()
    {
        String url = "http://localhost:8080/top20/Madrid?token=518ec2d05ea4f675d84f1400f0fb9f237d55c433&expireTime=2491342022491";

        String responseWebContainer = BasicWebContainer.simpleRequest(url);

        assertNotSame("{\"errorMessage\":\"Operation with more than top50 or negative is not allowed\",\"errorCode\":1002}", responseWebContainer);
        assertNotSame("{\"errorMessage\":\"Token is not valid for current request\",\"errorCode\":1010}", responseWebContainer);
        assertNotSame("{\"errorMessage\":\"ExpireTime has expired, you need to renew token\",\"errorCode\":1011}", responseWebContainer);
    }

    private static String simpleRequest (String sUrl)
    {
        HttpURLConnection con   = null;
        String messageReponse   = "";

        try
        {
            con = (HttpURLConnection) new URL(sUrl).openConnection();
            con.setRequestMethod("GET");

            BufferedReader br   = new BufferedReader(new InputStreamReader((con.getInputStream())));
            StringBuilder sb    = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null)
                sb.append(output);

            messageReponse = sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( con != null )
                    con.disconnect();
            }
            catch ( Exception e ) {}
        }

        return messageReponse;
    }

    @Override
    public void tearDown() throws Exception
    {

    }

}
