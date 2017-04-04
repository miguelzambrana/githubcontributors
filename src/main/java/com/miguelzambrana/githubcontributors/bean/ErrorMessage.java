package com.miguelzambrana.githubcontributors.bean;

/**
 * Created by miki on 4/04/17.
 */
public class ErrorMessage {
    private String  errorMessage;
    private int     errorCode;

    public ErrorMessage ( String errorMessage, int errorCode ) {
        this.errorMessage   = errorMessage;
        this.errorCode      = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
