package com.esp.model;

import java.io.Serializable;

public class TwitterUser implements Serializable {
    private static final long serialVersionUID = 1L;
    String screenName;
    String language;
    String accessToken;
    String accessTokenSecret;

    public TwitterUser() {}

    public TwitterUser(String accessToken, String accessTokenSecret, String name ) {
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.screenName=name;
    }

    public String getScreenName() {
    	
        return screenName;
    }

    public void setScreenName(String screenName) {
        
    	this.screenName = screenName;
    	System.out.println(screenName);
    
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        System.out.println(language);
    }

    public String getAccessToken() {
        return accessToken;
        
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        System.out.println(accessToken);
    
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
        System.out.println(accessTokenSecret);
    
    }
}
