package com.miguelzambrana.githubcontributors;

import com.miguelzambrana.githubcontributors.bean.GitHubUserBean;
import com.miguelzambrana.githubcontributors.githubapi.GitHubInterface;

import java.util.List;

/**
 * Created by miki on 3/04/17.
 */
public class GitHubContributorsService {

    public static void main ( String[] args ) {

        System.out.println("Hi I'm GihHubContributorsService!!");

        List<GitHubUserBean> topUsers = GitHubInterface.gitHubRequest("Barcelona");

        int currentCount = 1;
        for ( GitHubUserBean userBean : topUsers ) {
            System.out.println("#" + currentCount + " User: " + userBean.getLogin() + "(" + userBean.getId() + ")" +
                    " Location: " + userBean.getLocation());
            currentCount++;
        }

    }

}
