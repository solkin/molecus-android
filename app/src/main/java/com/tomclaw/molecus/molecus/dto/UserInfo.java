package com.tomclaw.molecus.molecus.dto;

import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
public class UserInfo implements Unobfuscatable {

    private String userid;
    private String nick;
    private String avatar;

    public UserInfo() {
    }

    public String getUserId() {
        return userid;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return DevHelper.fixUrls(avatar);
    }
}
