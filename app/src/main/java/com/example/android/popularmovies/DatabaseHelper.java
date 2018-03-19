package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marti on 18/03/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favourites";
    static final String MOVIE_TABLE_NAME = "movies";
    static final int DATABASE_VERSION = 1;


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_DB_TABLE =
                " CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME +
                        " ( " + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieEntry.MOVIE_DATABASE_ID + " INTEGER, " +
                        MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.BACKDROP_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.RATING + " DOUBLE NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_DB_TABLE);
    }
    //" UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
