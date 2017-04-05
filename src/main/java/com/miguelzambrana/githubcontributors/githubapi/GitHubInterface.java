package com.miguelzambrana.githubcontributors.githubapi;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.configuration.Configuration;
import com.miguelzambrana.githubcontributors.exception.ContributorsException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubInterface {

    // Sample Request :
    //  > https://api.github.com/search/users?q=tom+location:%3EBarcelona&sort=repositories&order=desc

    /**
     * Get GitHubUserBean list for current location order by number of repositories
     * @param location
     * @return
     */
    public static List<GitHubUserBean> gitHubRequest ( String location ) throws ContributorsException {

        // Create empty user list
        List<GitHubUserBean> topUsers = new ArrayList<>();

        try
        {
            // API Search Page
            int currentPage = 1;

            // We try to get always top 50 users from GitHub search API (From 2 first pages)
            while ( ( topUsers.size() < 50 ) && ( currentPage < 3 ) ) {

                // Do request to GitHub Search API
                // Query: get users for location, sort by repositories desc (current page)
                HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.github.com/search/users")
                        //.basicAuth("account","username")
                        .header("accept", "application/json")
                        .queryString("q", "location:=" + location)
                        .queryString("sort", "repositories")
                        .queryString("order", "desc")
                        .queryString("page", currentPage)
                        .asJson();

                // Increment current page for the next search
                currentPage++;

                // If message response have total_count key, it means message is valid
                if ( jsonResponse.getBody().getObject().has("total_count") ) {
                    // Get response total count
                    int totalCount = jsonResponse.getBody().getObject().getInt("total_count");

                    // If totalCount is bigger than 0, get users and put to list
                    if (totalCount > 0) {
                        // Explore items array...
                        for (Object user : jsonResponse.getBody().getObject().getJSONArray("items")) {
                            // Get Login and userId parameters from Json object
                            String login = ((JSONObject) user).getString("login");
                            int userId = ((JSONObject) user).getInt("id");

                            // Add current user to list
                            GitHubUserBean gitHubUserBean = new GitHubUserBean(userId, login, location);
                            topUsers.add(gitHubUserBean);
                        }
                    } else {
                        // If totalCount is 0, we can break here...
                        // Create ContributorsException and throw up
                        throw new ContributorsException("GitHub API returns 0 entries for location " + location, 1004);
                    }
                }
                else {
                    // Get Github api message
                    String apiMessage = jsonResponse.getBody().getObject().getString("message");

                    // Create ContributorsException and throw up
                    throw new ContributorsException("GitHub API Message: " + apiMessage, 1005);
                }
            }

        } catch (ContributorsException e) {
            throw e;
        } catch (UnirestException e) {
            // Create ContributorsException and throw up
            throw new ContributorsException("Unirest Lib Exception: " + e.getMessage(), 1006);
        } catch (Exception e) {
            // Create ContributorsException and throw up
            throw new ContributorsException("Abnormal behaviour: " + e.getMessage(), 1007);
        }

        return topUsers;
    }

}
