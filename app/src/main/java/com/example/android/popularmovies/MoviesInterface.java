package com.example.android.popularmovies;


import android.graphics.Movie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
public interface MoviesInterface {
        @GET("movie/{sortBy}")
        Call<MoviePageResults> sortMoviesBy(@Path("sortBy") String sortBy);
        @GET("movie/{id}")
        Call<ArrayList<MovieObject>> movieID(@Path("id") String id);
}
