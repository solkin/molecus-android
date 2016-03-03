package com.tomclaw.molecus.molecus.dto;

import com.orm.dsl.Table;
import com.orm.dsl.Unique;
import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.ExcludeField;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
@Table
public class UserInfo implements Unobfuscatable {

    @ExcludeField
    private Long id;

    @Unique
    private String nick;
    private String avatar;
    private Integer online;
    private Subscription subscription;

    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return DevHelper.fixUrls(avatar);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public boolean isOnline() {
        return online != null && online == 1;
    }

    public void update(UserInfo userInfo) {
        this.nick = userInfo.nick;
        this.avatar = userInfo.avatar;
        if (userInfo.online != null) {
            this.online = userInfo.online;
        }
        if (userInfo.subscription != null) {
            this.subscription = userInfo.subscription;
        }
    }

    public enum Subscription {
        none,
        from,
        to,
        both
    }
}
