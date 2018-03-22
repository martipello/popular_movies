package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.ObjectsAndAdapters.MovieObject;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieReviewAdapter;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieReviewObject;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieTrailerAdapter;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieTrailerObject;
import com.example.android.popularmovies.tools.ImageSaver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieDetailActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    private final static String FAVOURITES = "favourites";
    private final static String POPULAR = "popular";
    private final static String SORT_ORDER = "SORT_ORDER";
    Toolbar toolbar;
    String title;
    String image;
    MovieObject movieObject;
    TextView released , overview;
    FloatingActionButton fab;
    RatingBar ratingBar;
    Context context;
    private static ItemTouchHelper mItemTouchHelper;
    private StaggeredGridLayoutManager staggeredGridLayoutManagerVertical;
    private LinearLayoutManager linearLayoutManager;
    public MovieTrailerAdapter trailerAdapter;
    public static ArrayList<MovieTrailerObject> trailerList = new ArrayList<>();
    public RecyclerView trailerRecyclerView;
    public MovieReviewAdapter reviewAdapter;
    public static ArrayList<MovieReviewObject> reviewList = new ArrayList<>();
    public RecyclerView reviewRecyclerView;
    public Bitmap myBitmap;
    public boolean favourite;
    private SQLiteDatabase movieDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        ratingBar = findViewById(R.id.ratings_bar);
        overview = findViewById(R.id.overview);
        released = findViewById(R.id.release_date);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        movieDB = databaseHelper.getWritableDatabase();
        final ImageView imageView = findViewById(R.id.expandedImage);
        final Bundle data = getIntent().getExtras();
        context = this;
        if (data != null) {
            movieObject = (MovieObject) data.getParcelable("movie");
            populateViews(movieObject);
            loadImage(movieObject,imageView);
            favourite = exists(String.valueOf(movieObject.getId()));
        }
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(title);
        toolbar.setTitle(title);
        trailerList.clear();
        int columns = getResources().getInteger(R.integer.columns);
        staggeredGridLayoutManagerVertical = new StaggeredGridLayoutManager(columns,StaggeredGridLayoutManager.VERTICAL);
        trailerRecyclerView = (RecyclerView) findViewById(R.id.trailer_list_view);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.review_list_view);
        //populate lists
        //getTrailers();
        getReviews(String.valueOf(movieObject.getId()));
        MovieDetailActivity.OnItemTouchListener itemTouchListener = new MovieDetailActivity.OnItemTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
            }
            @Override
            public void onCardLongClick(View view, int position) {
            }
        };
        trailerAdapter = new MovieTrailerAdapter(trailerList,MovieDetailActivity.this,itemTouchListener);
        reviewAdapter = new MovieReviewAdapter(reviewList,MovieDetailActivity.this);
        ItemTouchHelper.Callback callback =
                new touchHelperCallback(trailerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(trailerRecyclerView);
        trailerRecyclerView.setLayoutManager(staggeredGridLayoutManagerVertical);
        trailerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trailerRecyclerView.setAdapter(trailerAdapter);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);
        reviewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reviewRecyclerView.setAdapter(reviewAdapter);
        //toolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        fab = (FloatingActionButton) findViewById(R.id.fab);
            if (favourite){
                favourite = true;
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_white_24dp));
            }else{
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_24dp));
                favourite = false;
            }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (favourite){
                        fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_white_24dp));
                        removeFavourite(String.valueOf(movieObject.getId()));
                    }
                    else{
                        fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_white_24dp));
                        addFavourite(movieObject, myBitmap);
                    }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorAccent);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getDominantColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDominantColor(primaryDark));
        //toolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        //fab.setBackgroundColour
    }

    private void populateViews(MovieObject movieObject){
        String releaseString = String.format(getString(R.string.released), movieObject.getRelease_dates());
        String overviewString = String.format(getString(R.string.summary), movieObject.getOverview());
        title = movieObject.getTitle();
        image = movieObject.getPoster();
        overview.setText(overviewString);
        released.setText(releaseString);
        ratingBar.setNumStars((int)movieObject.getRating() / 2);
    }

    public void getTrailers(String sortOrder) {
        trailerList.clear();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter(
                        "api_key", BuildConfig.API_KEY).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final MoviesInterface movieService = retrofit.create(MoviesInterface.class);
        Call<MoviePageResults> call = movieService.sortMoviesBy(sortOrder);
        call.enqueue(new retrofit2.Callback<MoviePageResults>() {
            @Override
            public void onResponse(Call<MoviePageResults> call, Response<MoviePageResults> response) {
                //pageResults = response.body();
                //trailerList = response.body().getResults();
                trailerAdapter.refreshMyList(trailerList);
                if (trailerAdapter.getItemCount() > 0){
                    //connectionText.setVisibility(View.INVISIBLE);
                }else{
                    //connectionText.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<MoviePageResults> call, Throwable t) {
                trailerAdapter.clear();
                //connectionText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getReviews(String id) {
        reviewList.clear();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter(
                        "api_key", BuildConfig.API_KEY).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final MoviesInterface movieService = retrofit.create(MoviesInterface.class);
        Call<MovieReviewObject> call = movieService.getMovieReviews(id);
        call.enqueue(new retrofit2.Callback<MovieReviewObject>() {
            @Override
            public void onResponse(Call<MovieReviewObject> call, Response<MovieReviewObject> response) {
                //pageResults = response.body();
                reviewList = response.body().getResults();
                reviewAdapter.refreshMyList(reviewList);
                if (reviewAdapter.getItemCount() > 0){
                    //this isnt working for some reason
                    // also change hardcoded strings to strings.xml
                    //change toast messages to snackbars
                    MovieReviewObject mro = new MovieReviewObject("No Reviews", "There are no reviews at present please check back later");
                    reviewList.add(mro);
                    reviewAdapter.refreshMyList(reviewList);
                }
            }
            @Override
            public void onFailure(Call<MovieReviewObject> call, Throwable t) {
                reviewAdapter.clear();
                //connectionText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addFavourite(MovieObject movieObject, Bitmap bitmap){
        favourite = true;
        ContentValues values = new ContentValues();
        new ImageSaver(this).setFileName(movieObject.getId() + ".png").setDirectoryName("fav_movies").save(bitmap);
        values.put(MovieContract.MovieEntry.TITLE, movieObject.getTitle());
        values.put(MovieContract.MovieEntry.MOVIE_DATABASE_ID, movieObject.getId());
        values.put(MovieContract.MovieEntry.OVERVIEW, movieObject.getOverview());
        values.put(MovieContract.MovieEntry.RATING, movieObject.getRating());
        values.put(MovieContract.MovieEntry.RELEASE_DATE, movieObject.getRelease_dates());
        values.put(MovieContract.MovieEntry.POSTER_PATH, "NULL");
        values.put(MovieContract.MovieEntry.BACKDROP_PATH, "BACKDROP");
        try
        {
            movieDB.beginTransaction();
            movieDB.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            movieDB.setTransactionSuccessful();
        }
        catch (SQLException e) {
        }
        finally
        {
            movieDB.endTransaction();
        }
    }

    public void removeFavourite(String id){
        String selection = "movie_id=?";
        int movieId = movieObject.getId();
        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        Uri uri = MovieProvider.CONTENT_URI;
        getContentResolver().delete(uri, selection, selectionArgs);
        Toast.makeText(getBaseContext(), "Movie " + title + " was removed from favorites.", Toast.LENGTH_LONG).show();
    }

    public boolean exists(String id){
            String selectString = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.MOVIE_DATABASE_ID + " =?";
            Cursor cursor = movieDB.rawQuery(selectString, new String[] {id});
            boolean hasObject = false;
            if(cursor.moveToFirst()){
                hasObject = true;
                int count = 0;
                while(cursor.moveToNext()){
                    count++;
                }
            }
            cursor.close();
            return hasObject;
    }

    public void loadImage(MovieObject movieObject, final ImageView imageView){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String s = prefs.getString(SORT_ORDER,POPULAR);
        switch (s){
            case FAVOURITES :
                Uri input = new ImageSaver(context).
                        setFileName(movieObject.getId() + ".png").
                        setDirectoryName("fav_movies").
                        find();
                Picasso.with(this).load(input).placeholder(R.drawable.placeholder).into(imageView, new Callback() {
                    @Override public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        myBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }
                    @Override public void onError() {
                    }
                });
                break;
            default:
                Picasso.with(this).load(PathResolver.resolveImageURL(image)).placeholder(R.drawable.placeholder).into(imageView, new Callback() {
                    @Override public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        myBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }
                    @Override public void onError() {
                    }
                });
        }
    }

    public interface OnItemTouchListener {
        public void onCardClick(View view, int position);
        public void onCardLongClick(View view, int position);
    }

}
