package com.miguelzambrana.githubcontributors.exception;

/**
 * Created by miki on 4/04/17.
 */

public class ContributorsException extends Exception
{
    private int errorCode;

    public ContributorsException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
