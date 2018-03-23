package com.example.android.popularmovies.ObjectsAndAdapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieTrailerObject {

    @Expose
    @SerializedName("key")
    private String key;
    @Expose
    @SerializedName("name")
    private String name;
    @SerializedName("results")
    private ArrayList<MovieTrailerObject> trailerResults;

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

    public ArrayList<MovieTrailerObject> getResults() {
        return trailerResults;
    }

    public void setResults(ArrayList<MovieTrailerObject> results) {
        this.trailerResults = results;
    }

    public static class MovieTrailerObjectList {
        @SerializedName("results")
        private List<MovieTrailerObject> movieTrailerObject;
        public List<MovieTrailerObject> getMovieTrailerObjectLists() {
            return movieTrailerObject;
        }
    }

}
