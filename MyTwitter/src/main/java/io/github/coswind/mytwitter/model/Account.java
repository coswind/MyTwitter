package io.github.coswind.mytwitter.model;

import twitter4j.auth.AccessToken;

/**
 * Created by coswind on 14-2-19.
 */
public class Account {
    private AccessToken accessToken;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accessToken=" + accessToken +
                '}';
    }
}
