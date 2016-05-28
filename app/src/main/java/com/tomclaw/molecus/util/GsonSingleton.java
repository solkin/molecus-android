package com.tomclaw.molecus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;

/**
 * Created by solkin on 01/03/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class GsonSingleton {

    private Gson gson;

    @AfterInject
    void init() {
        gson = new GsonBuilder().create();
    }

    public Gson getGson() {
        return gson;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> classOfT)
            throws com.google.gson.JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type type)
            throws com.google.gson.JsonSyntaxException {
        return gson.fromJson(json, type);
    }
}
