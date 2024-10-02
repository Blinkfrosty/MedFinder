package com.blinkfrosty.medfinder.dataaccess;

import com.blinkfrosty.medfinder.dataaccess.datastructure.User;

public interface UserCallback {
    void onUserRetrieved(User user);
    void onError(Exception e);
}