package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by marti on 15/03/2018.
 */

public class MovieTrailerObject {


    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("key")
    private String key;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("type")
    private String type;

    public MovieTrailerObject(String id, String key, String name, String type){
        this.id = id;
        this.key = key;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class MovieTrailerObjectList {
        private List<MovieTrailerObject> movieTrailerObject;
        public List<MovieTrailerObject> getMovieTrailerObjectLists() {
            return movieTrailerObject;
        }
    }

}
