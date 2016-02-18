package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.Response;
import com.tomclaw.molecus.molecus.dto.Collective;
import com.tomclaw.molecus.molecus.dto.Social;

/**
 * Created by Solkin on 18.11.2015.
 */
public class UserInfoResponse extends Response {

    private String nick;
    private String avatar;
    private String gender;
    private long born;
    private String country;
    private String region;
    private String city;
    private String phone;
    private String[] hobbies;
    private String about;
    private Collective[] collectives;
    private Social social;

    public UserInfoResponse(int status) {
        super(status);
    }

    public UserInfoResponse(int status, String nick, String avatar, String gender,
                            long born, String country, String region, String city,
                            String phone, String[] hobbies, String about,
                            Collective[] collectives, Social social) {
        super(status);
        this.nick = nick;
        this.avatar = avatar;
        this.gender = gender;
        this.born = born;
        this.country = country;
        this.region = region;
        this.city = city;
        this.phone = phone;
        this.hobbies = hobbies;
        this.about = about;
        this.collectives = collectives;
        this.social = social;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getGender() {
        return gender;
    }

    public long getBorn() {
        return born;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public String getAbout() {
        return about;
    }

    public Collective[] getCollectives() {
        return collectives;
    }

    public Social getSocial() {
        return social;
    }
}
