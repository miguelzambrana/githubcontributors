package com.miguelzambrana.githubcontributors.bean;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.miguelzambrana.githubcontributors.cache.serializable.GitHubUserBeanSerializableFactory;

import java.io.IOException;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubUserBean implements IdentifiedDataSerializable
{
    private int    userId;
    private String login;
    private String location;

    public GitHubUserBean () {}

    public GitHubUserBean ( int userId , String login , String location ) {
        this.userId     = userId;
        this.login      = login;
        this.location   = location;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
    public void readData ( ObjectDataInput in ) throws IOException
    {
        userId      = in.readInt();
        login       = in.readUTF();
        location    = in.readUTF();
    }

    @Override
    public void writeData ( ObjectDataOutput out ) throws IOException
    {
        out.writeInt (userId);
        out.writeUTF (login);
        out.writeUTF (location);
    }

    @Override
    public int getFactoryId() {
        return GitHubUserBeanSerializableFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return GitHubUserBeanSerializableFactory.GITHUB_USER_BEAN_TYPE;
    }

    @Override
    public String toString() {
        return "GitHubUserBean{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
