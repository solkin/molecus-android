package com.tomclaw.molecus.main.controllers;

import android.support.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orm.SugarRecord;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeBackground;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.tomclaw.molecus.core.Serials.SUGAR;

/**
 * Created by solkin on 02/03/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UserInfoCache {

    private final LoadingCache<String, UserInfo> userInfoCache = CacheBuilder.newBuilder()
            .initialCapacity(128)
            .build(new CacheLoader<String, UserInfo>() {
                @Override
                public UserInfo load(@NonNull String key) throws Exception {
                    List<UserInfo> userInfoList = SugarRecord.find(UserInfo.class, "nick = ?", key);
                    if (userInfoList.isEmpty()) {
                        throw new UserInfoNotFoundException(key);
                    }
                    return userInfoList.get(0);
                }
            });

    @Background(serial = SUGAR)
    public void saveUsersInfo(List<UserInfo> userInfoList, UserInfoSaveCallback callback) {
        mergeCached(userInfoList);
        SugarRecord.saveInTx(userInfoList);
        callback.onSaved(userInfoList);
    }

    @Background(serial = SUGAR)
    public void getUserInfo(String nick, UserInfoCallback callback) {
        try {
            UserInfo userInfo = getUserInfoSync(nick);
            callback.onUserInfo(userInfo);
        } catch (UserInfoNotFoundException ex) {
            callback.onNotFound();
        }
    }

    @SupposeBackground(serial = SUGAR)
    UserInfo getUserInfoSync(String nick) throws UserInfoNotFoundException {
        try {
            return userInfoCache.get(nick);
        } catch (ExecutionException e) {
            throw new UserInfoNotFoundException(nick);
        }
    }

    @SupposeBackground(serial = SUGAR)
    void save(Map<String, UserInfo> values) {
        SugarRecord.saveInTx(values.values());
        userInfoCache.putAll(values);
    }

    @SupposeBackground(serial = SUGAR)
    void mergeCached(List<UserInfo> userInfoList) {
        for (int c = 0; c < userInfoList.size(); c++) {
            UserInfo userInfo = userInfoList.get(c);
            String nick = userInfo.getNick();
            try {
                UserInfo storedProject = userInfoCache.get(nick);
                storedProject.update(userInfo);
                userInfoList.set(c, storedProject);
            } catch (ExecutionException e) {
                userInfoCache.put(nick, userInfo);
            }
        }
    }

    public class UserInfoNotFoundException extends Exception {
        private String nick;

        public UserInfoNotFoundException(String nick) {
            this.nick = nick;
        }

        @Override
        public String toString() {
            return "UserInfoNotFoundException{" +
                    "nick='" + nick + '\'' +
                    '}';
        }
    }

    public interface UserInfoCallback {
        void onUserInfo(UserInfo userInfo);

        void onNotFound();
    }

    public interface UserInfoSaveCallback {
        void onSaved(List<UserInfo> userInfoList);
    }
}
