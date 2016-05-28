package com.tomclaw.molecus.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.tomclaw.molecus.util.Logger;
import com.tomclaw.molecus.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 4/23/13
 * Time: 10:55 AM
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private boolean isDropTables = false;
    private boolean isExportDb = false;

    public DatabaseHelper(Context context) {
        super(context, Settings.DB_NAME, null, Settings.DB_VERSION);
        if (isDropTables) {
            onCreate(getWritableDatabase());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Drop tables on mock mode.
            if (isDropTables) {
                db.execSQL("DROP TABLE IF EXISTS " + GlobalProvider.REQUEST_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + GlobalProvider.PROJECTS_TABLE);
            }

            // Creating roster database.
            db.execSQL(GlobalProvider.DB_CREATE_REQUEST_TABLE_SCRIPT);
            db.execSQL(GlobalProvider.DB_CREATE_PROJECTS_TABLE_SCRIPT);

            db.execSQL(GlobalProvider.DB_CREATE_PROJECTS_INDEX_SCRIPT);

            Logger.log("DB created: " + db.toString());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (isExportDb) {
            exportDb(db);
        }
    }

    private void exportDb(SQLiteDatabase db) {
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        FileChannel source;
        FileChannel destination;
        String currentDBPath = db.getPath();
        String backupDBPath = Settings.DB_NAME + ".db";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateRandomText(Random r) {
        int wordCount = 10 + r.nextInt(13);
        return generateRandomText(r, wordCount);
    }

    public static String generateRandomText(Random r, int wordCount) {

        StringBuilder sb = new StringBuilder(wordCount);
        for (int i = 0; i < wordCount; i++) { // For each letter in the word
            sb.append(generateRandomWord(r, i == 0)).append((i < (wordCount - 1)) ? " " : "."); // Add it to the String
        }
        return sb.toString();
    }

    private static String generateRandomWord(Random r) {
        return generateRandomWord(r, true);
    }

    private static String generateRandomWord(Random r, boolean capitalize) {
        String word = StringUtil.generateRandomString(r, 4, 10);
        if (capitalize) {
            return String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
        } else {
            return word;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Yo!
        Logger.log("Now we need to upgrade database from " + oldVersion + " to " + newVersion);
        switch (oldVersion) {
            case 1: {
                /*db.execSQL("ALTER TABLE " + GlobalProvider.ROSTER_BUDDY_TABLE
                        + " ADD COLUMN " + GlobalProvider.ROSTER_BUDDY_DRAFT + " text");*/
            }
        }
        Logger.log("Database upgrade completed");
    }
}
