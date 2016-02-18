package com.tomclaw.molecus.core;

import android.content.Context;
import com.tomclaw.molecus.util.GsonSingleton;
import com.tomclaw.molecus.util.Logger;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.*;

/**
 * Created by Igor on 03.07.2015.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UserHolder {

    private static final String STORAGE_FOLDER = "user";
    private static final String USER_FILE = "user.dat";

    @RootContext
    Context context;

    private User user;

    private File storage;
    private File userFile;

    @AfterInject
    void init() {
        user = new User();
        storage = context.getDir(STORAGE_FOLDER, Context.MODE_PRIVATE);
        userFile = new File(storage, USER_FILE);
        load();
    }

    public void load() {
        FileInputStream stream = null;
        try {
            if (!userFile.exists() && !userFile.createNewFile()) {
                throw new IOException();
            }
            stream = new FileInputStream(userFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Logger.log(total.toString());
            User loadedUser = GsonSingleton.getInstance().fromJson(total.toString(), User.class);
            if (loadedUser != null) {
                user = loadedUser;
            }
        } catch (Throwable ex) {
            Logger.log("error while reading user data file", ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public void store() {
        FileOutputStream stream = null;
        try {
            if (!userFile.exists() && !userFile.createNewFile()) {
                throw new IOException();
            }
            stream = new FileOutputStream(userFile);
            OutputStreamWriter w = new OutputStreamWriter(stream);
            String json = GsonSingleton.getInstance().toJson(user);
            w.write(json);
            w.flush();
        } catch (Throwable ex) {
            Logger.log("error while writing user data file", ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public User getUser() {
        return user;
    }
}
