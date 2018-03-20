package com.example.android.popularmovies.ObjectsAndAdapters;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marti on 07/03/2018.
 */

public class MovieObject implements Parcelable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieObject createFromParcel(Parcel parcel) {
            return new MovieObject(parcel);
        }

        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };

    public static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w500";

    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("poster_path")
    private String poster;
    @Expose
    @SerializedName("overview")
    private String overview;
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("backdrop_path")
    private String backdrop;
    @Expose
    @SerializedName("vote_average")
    private double rating;
    @Expose
    @SerializedName("release_date")
    private String release_dates;

    public MovieObject(int id, String title, String overview, String poster, double rating, String release_dates){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster = poster;
        this.rating = rating;
        this.release_dates = release_dates;
    }

    public String getTitle() {
            return title;
        }

    public void setTitle(String title) {
            this.title = title;
        }

    public String getPoster() {
            return poster;
        }

    public void setPoster(String poster) {
            this.poster = poster;
        }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdrop() {
            return backdrop;
        }

    public void setBackdrop(String backdrop) {
            this.backdrop = backdrop;
        }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRelease_dates() {
        return release_dates;
    }

    public void setRelease_dates(String release_dates) {
        this.release_dates = release_dates;
    }

    public MovieObject(Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.overview =  in.readString();
        this.poster =  in.readString();
        this.rating =  in.readDouble();
        this.release_dates =  in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.overview);
        parcel.writeString(this.poster);
        parcel.writeDouble(this.rating);
        parcel.writeString(this.release_dates);
    }

    public static class MovieObjectList {
        private List<MovieObject> MovieObjectLists;
        public List<MovieObject> getMovieObjectLists() {
            return MovieObjectLists;
        }
    }
}
