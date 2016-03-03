package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.Response;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import java.util.List;

public class SubscribersResponse extends Response {

    private List<UserInfo> users;

    public SubscribersResponse(int status) {
        super(status);
    }

    public SubscribersResponse(int status, List<UserInfo> users) {
        super(status);
        this.users = users;
    }

    public List<UserInfo> getUsers() {
        return users;
    }
}
