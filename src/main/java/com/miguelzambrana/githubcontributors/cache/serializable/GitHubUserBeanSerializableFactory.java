package com.miguelzambrana.githubcontributors.cache.serializable;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubUserBeanSerializableFactory implements DataSerializableFactory {

    public static final int FACTORY_ID             = 1;
    public static final int GITHUB_USER_BEAN_TYPE  = 1;

    @Override
    public GitHubUserBean create (int typeId )
    {
        if (typeId == GITHUB_USER_BEAN_TYPE) {
            return new GitHubUserBean ();
        } else {
            return null;
        }
    }

}
