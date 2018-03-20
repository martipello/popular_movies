package com.example.android.popularmovies.ObjectsAndAdapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marti on 15/03/2018.
 */

public class MovieTrailerObject {

    @Expose
    @SerializedName("key")
    private String key;
    @Expose
    @SerializedName("name")
    private String name;

    public MovieTrailerObject(String key, String name){
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return "https://www.youtube.com/watch?v=" + key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class MovieTrailerObjectList {
        @SerializedName("results")
        private List<MovieTrailerObject> movieTrailerObject;
        public List<MovieTrailerObject> getMovieTrailerObjectLists() {
            return movieTrailerObject;
        }
    }

}
