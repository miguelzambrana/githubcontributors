package com.miguelzambrana.githubcontributors.bean;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubUserBean
{
    private int id;
    private String login;
    private String location;

    public GitHubUserBean ( int id , String login , String location ) {
        this.id         = id;
        this.login      = login;
        this.location   = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "GitHubUserBean{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
