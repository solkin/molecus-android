package com.tomclaw.molecus.molecus.dto;

import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
public class UserInfo extends BaseUserInfo implements Unobfuscatable {

    private Integer online;
    private Subscription subscription;

    public UserInfo() {
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public boolean isOnline() {
        return online != null && online == 1;
    }

    public enum Subscription {
        none,
        from,
        to,
        both
    }
}
