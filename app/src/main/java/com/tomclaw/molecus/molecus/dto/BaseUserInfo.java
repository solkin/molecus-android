package com.tomclaw.molecus.molecus.dto;

import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by solkin on 28.05.16.
 */
public class BaseUserInfo implements Unobfuscatable {

    private String nick;
    private String avatar;

    public BaseUserInfo() {
    }

    public BaseUserInfo(String nick, String avatar) {
        this.nick = nick;
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return DevHelper.fixUrls(avatar);
    }
}
