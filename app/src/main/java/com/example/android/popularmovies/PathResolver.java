package com.example.android.popularmovies;

import android.net.Uri;

/**
 * Created by marti on 10/03/2018.
 */

public class PathResolver {
        public static String resolveImageURL(String imageUri) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w500")
                    .appendEncodedPath(imageUri);
            return builder.build().toString();
        }
}
