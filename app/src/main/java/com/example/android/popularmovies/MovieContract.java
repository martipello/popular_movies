package com.example.android.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by marti on 18/03/2018.
 */

public class MovieContract {

    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_DATABASE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_PATH = "poster_path";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String RELEASE_DATE = "release_date";
        public static final String RATING = "rating";
    }

}
