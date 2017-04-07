package com.miguelzambrana.githubcontributors.repository;

import java.util.HashMap;

/**
 * Created by miki on 7/04/17.
 */
public class BasicAuthUsersRepository {

    private static final HashMap<String,char[]> users = new HashMap<>();

    /**
     * The idea about BasicAuth Users Repository, is get the users from DB, for example
     *  In this example, we will have some users defined, but it can change (for production environment)
     */

    public static void loadRepository () {
        // TODO: Load users from DB
        users.put("newrelic", "challenge".toCharArray());
        users.put("miki", "simplepass".toCharArray());
    }

    /**
     * Get loaded Users (Map)
     * @return
     */
    public static HashMap<String,char[]> getUsers () {
        return users;
    }

}
