package com.example.android.popularmovies.ObjectsAndAdapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public static class MovieTrailerObjectList {
        @SerializedName("results")
        private List<MovieReviewObject> movieTrailerObject;
        public List<MovieReviewObject> getMovieTrailerObjectLists() {
            return movieTrailerObject;
        }
    }

}
