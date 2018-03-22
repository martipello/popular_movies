package com.example.android.popularmovies;


import com.example.android.popularmovies.ObjectsAndAdapters.MovieObject;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieReviewObject;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieTrailerObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

//http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
public interface MoviesInterface {
        @GET("movie/{sortBy}")
        Call<MoviePageResults> sortMoviesBy(@Path("sortBy") String sortBy);
        @GET("movie/{id}")
        Call<ArrayList<MovieObject>> movieID(@Path("id") String id);
        @GET("movie/{id}/reviews")
        Call<MovieReviewObject> getMovieReviews(@Path("id") String id);
        @GET("movie/{id}/videos")
        Call<ArrayList<MovieTrailerObject>> getMovieTrailer(@Path("id") String id);
}
