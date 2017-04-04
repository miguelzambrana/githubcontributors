package com.miguelzambrana.githubcontributors.bean;

/**
 * Created by miki on 4/04/17.
 */
public class TokenGenerationMessage {
    private String  message;
    private String  token;
    private String  expireTime;

    public TokenGenerationMessage(String message, String token, String expireTime ) {
        this.message        = message;
        this.token          = token;
        this.expireTime     = expireTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
