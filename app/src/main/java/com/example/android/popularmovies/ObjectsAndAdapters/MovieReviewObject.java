package com.example.android.popularmovies.ObjectsAndAdapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marti on 15/03/2018.
 */

public class MovieReviewObject {


    @Expose
    @SerializedName("author")
    private String author;
    @Expose
    @SerializedName("content")
    private String content;
    @SerializedName("results")
    private ArrayList<MovieReviewObject> reviewResults;

    public MovieReviewObject(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setResults(ArrayList<MovieReviewObject> results) {
        this.reviewResults = results;
    }

    public ArrayList<MovieReviewObject> getResults() {
        return reviewResults;
    }

    public static class MovieReviewObjectList {
        @SerializedName("results")
        private List<MovieReviewObject> movieReviewObject;
        public List<MovieReviewObject> getMovieReviewObjectLists() {
            return movieReviewObject;
        }
    }

}
