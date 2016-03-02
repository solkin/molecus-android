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

    private String userid;
    @Unique
    private String nick;
    private String avatar;

    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
