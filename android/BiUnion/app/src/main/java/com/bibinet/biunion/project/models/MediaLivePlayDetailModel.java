/**
 * Copyright 2017 bejson.com
 */
package com.bibinet.biunion.project.models;


public class MediaLivePlayDetailModel extends BaseModel {
    private String playUrl;
    private String encryptionPlayUrl;
    private String userName;
    private String userLogo;

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getEncryptionPlayUrl() {
        return encryptionPlayUrl;
    }

    public void setEncryptionPlayUrl(String encryptionPlayUrl) {
        this.encryptionPlayUrl = encryptionPlayUrl;
    }
}