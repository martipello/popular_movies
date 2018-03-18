package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by marti on 17/03/2018.
 */

public class MovieProvider extends ContentProvider {

    private SQLiteDatabase sqLiteDatabase;
    static final String PROVIDER_NAME = "com.example.android.popularmovies.MovieProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/movie";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String _ID = "_id";
    static final String MOVIE_DATABASE_ID = "movie_id";
    static final String TITLE = "title";
    static final String OVERVIEW = "overview";
    static final String POSTER_PATH = "poster_path";
    static final String BACKDROP_PATH = "backdrop_path";
    static final String RELEASE_DATE = "release_date";
    static final String RATING = "rating";
    private static HashMap<String, String> MOVIE_PROJECTION_MAP;
    static final int MOVIE = 1;
    static final int MOVIE_ID = 2;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movie", MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, "movie/#", MOVIE_ID);
    }
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings,
                        @Nullable String s, @Nullable String[] strings1,
                        @Nullable String s1) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MovieContract.MovieEntry.TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case MOVIE:
                qb.setProjectionMap(MOVIE_PROJECTION_MAP);
                break;
            case MOVIE_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
        }
        if (s1 == null || s1.equals("")){
            s1 = TITLE;
        }
        Cursor cursor = qb.query(sqLiteDatabase, strings,s,strings1 , null,null,s1);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case MOVIE:
                return "vnd.android.cursor.dir/vnd.example.movie";
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.movie";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowID = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME,"",contentValues );
        if (rowID > 0){
            Uri mUri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(mUri, null);
            return mUri;
        }
        throw new SQLException("failed to add movie into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOVIE:
                count = sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, s ,strings);
                break;
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = sqLiteDatabase.delete( MovieContract.MovieEntry.TABLE_NAME, _ID + " = " + id +
                        (!TextUtils.isEmpty(s) ? "AND (" + s + ')' : ""),strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
            int count = 0;
            switch (uriMatcher.match(uri)) {
                case MOVIE:
                    count = sqLiteDatabase.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, s, strings);
                    break;

                case MOVIE_ID:
                    count = sqLiteDatabase.update(MovieContract.MovieEntry.TABLE_NAME, contentValues,_ID +

                    " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(s) ? "AND ("
                    +s +')' : ""), strings);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri );
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
    }


    }
