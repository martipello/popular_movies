package com.example.android.popularmovies;

import com.example.android.popularmovies.ObjectsAndAdapters.MovieObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by marti on 10/03/2018.
 */

public class MoviePageResults {
    @SerializedName("page")
    private int pageNumber;
    @SerializedName("results")
    private ArrayList<MovieObject> movieResults;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public int getPage() {
        return pageNumber;
    }

    public void setPage(int page) {
        this.pageNumber = page;
    }

    public ArrayList<MovieObject> getResults() {
        return movieResults;
    }

    public void setResults(ArrayList<MovieObject> results) {
        this.movieResults = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
