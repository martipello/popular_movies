package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  {

    public String filter;
    private final static String POPULAR = "popular";
    private final static String TOP_RATED = "top_rated";
    private final static String FAVOURITES = "favourites";
    public static final String EXTRA_IMAGE = "EXTRA_IMAGE";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public MovieAdapter movieAdapter;
    MoviePageResults pageResults;
    public boolean sortByPopular;
    public boolean sortByRated;
    public boolean sortByFav;
    private static ItemTouchHelper mItemTouchHelper;
    private StaggeredGridLayoutManager staggeredGridLayoutManagerVertical;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static ArrayList<MovieObject> movieList = new ArrayList<>();
    public RecyclerView recyclerView;
    TextView connectionText;
    private SQLiteDatabase movieDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        connectionText = findViewById(R.id.no_connection);
        connectionText.setVisibility(View.INVISIBLE);
        int columns = getResources().getInteger(R.integer.columns);
        movieList.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean("RAN_ONCE",false)){
            editor.putBoolean("RAN_ONCE",true);
            editor.putBoolean(TOP_RATED, true);
            editor.putBoolean(POPULAR, false);
            editor.putBoolean(FAVOURITES, false);
            editor.apply();
        }
        sortByRated = prefs.getBoolean(TOP_RATED, false);
        sortByPopular = prefs.getBoolean(POPULAR, false);
        sortByFav = prefs.getBoolean(FAVOURITES, false);
        staggeredGridLayoutManagerVertical = new StaggeredGridLayoutManager(columns,StaggeredGridLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.list_view);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        movieDB = databaseHelper.getReadableDatabase();
        OnItemTouchListener itemTouchListener = new MainActivity.OnItemTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
                showDetailActivity(position);
            }
            @Override
            public void onCardLongClick(View view, int position) {
            }
        };
        if (sortByRated){
            movieAdapter = new MovieAdapter(movieList,MainActivity.this,itemTouchListener,true);
            sortMoviesBy(TOP_RATED);
        }
        else if (sortByPopular){
            movieAdapter = new MovieAdapter(movieList,MainActivity.this,itemTouchListener,true);
            sortMoviesBy(POPULAR);
        }
        else if (sortByFav){
            movieAdapter = new MovieAdapter(movieList,MainActivity.this,itemTouchListener,false);
            sortMoviesBy(FAVOURITES);
        }
        ItemTouchHelper.Callback callback =
                new touchHelperCallback(movieAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(staggeredGridLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sortByRated)
                    sortMoviesBy(TOP_RATED);
                if (sortByPopular)
                    sortMoviesBy(POPULAR);
                if (sortByFav)
                    sortMoviesBy(FAVOURITES);
            }
        });

    }

    public void showDetailActivity(int position){
        Intent myIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        myIntent.putExtra("movie", new MovieObject(
                movieList.get(position).getId(),
                movieList.get(position).getTitle(),
                movieList.get(position).getOverview(),
                movieList.get(position).getPoster(),
                movieList.get(position).getRating(),
                movieList.get(position).getRelease_dates()));
        MainActivity.this.startActivity(myIntent);
    }

    public void sortMoviesBy(String sortOrder) {
        movieList.clear();
        if(!sortByFav){
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
            call.enqueue(new Callback<MoviePageResults>() {
                @Override
                public void onResponse(Call<MoviePageResults> call, Response<MoviePageResults> response) {
                    pageResults = response.body();
                    movieList = response.body().getResults();
                    movieAdapter.refreshMyList(movieList,true);
                    if (movieAdapter.getItemCount() > 0){
                        connectionText.setVisibility(View.INVISIBLE);
                    }else{
                        connectionText.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<MoviePageResults> call, Throwable t) {
                    movieAdapter.clear();
                    connectionText.setVisibility(View.VISIBLE);
                }
            });
        }else{
            System.out.println("fav sort method");

            //Cursor cursor = getFavourites();
            //new WordFetchTask().execute();

            //String URL = "content://com.example.android.popularmovies.MovieProvider/movie";
            //Uri movie = Uri.parse(URL);
            Cursor c = getFavourites();
            if (c.moveToFirst()) {
                do{
                    MovieObject movieObject = new MovieObject(c.getInt(c.getColumnIndex( MovieProvider.MOVIE_DATABASE_ID)),
                            c.getString(c.getColumnIndex( MovieProvider.TITLE)),
                            c.getString(c.getColumnIndex( MovieProvider.OVERVIEW)),
                            c.getString(c.getColumnIndex( MovieProvider.POSTER_PATH)),
                            c.getDouble(c.getColumnIndex( MovieProvider.RATING)),
                            c.getString(c.getColumnIndex( MovieProvider.BACKDROP_PATH)));
                    movieList.add(movieObject);
                    System.out.println("while looping");
                } while (c.moveToNext());
                System.out.println("while finished");
                movieAdapter.refreshMyList(movieList,false);
                if (movieAdapter.getItemCount() > 0){
                    connectionText.setVisibility(View.INVISIBLE);
                }else{
                    connectionText.setVisibility(View.VISIBLE);
                }
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public Cursor getFavourites(){
        return movieDB.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.MOVIE_DATABASE_ID);
    }

    public interface OnItemTouchListener {
        public void onCardClick(View view, int position);
        public void onCardLongClick(View view, int position);
    }

    private void filterDialog() {
        int indexChecked = -1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(FAVOURITES , false))
            indexChecked = 2;
        if (prefs.getBoolean(TOP_RATED , false))
            indexChecked = 1;
        if (prefs.getBoolean(POPULAR , false))
            indexChecked = 0;

        new MaterialDialog.Builder(this)
                .title(R.string.sort_by)
                .content(R.string.filter_content)
                .items(R.array.spinner_options)
                .itemsCallbackSingleChoice(indexChecked, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(dialog.getContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        switch (which) {
                            case -1: {
                            }
                            case 0: {
                                editor.putBoolean(FAVOURITES, false);
                                editor.putBoolean(TOP_RATED, false);
                                editor.putBoolean(POPULAR, true);
                                editor.apply();
                                sortByFav = prefs.getBoolean(FAVOURITES,false);
                                sortByRated = prefs.getBoolean(TOP_RATED, false);
                                sortByPopular = prefs.getBoolean(POPULAR, false);
                                sortMoviesBy(POPULAR);
                                return true;
                            }
                            case 1: {
                                editor.putBoolean(FAVOURITES, false);
                                editor.putBoolean(TOP_RATED, true);
                                editor.putBoolean(POPULAR, false);
                                editor.apply();
                                sortByFav = prefs.getBoolean(FAVOURITES,false);
                                sortByRated = prefs.getBoolean(TOP_RATED, false);
                                sortByPopular = prefs.getBoolean(POPULAR, false);
                                sortMoviesBy(TOP_RATED);
                                return true;
                            }
                            case 2: {
                                editor.putBoolean(FAVOURITES, true);
                                editor.putBoolean(TOP_RATED, false);
                                editor.putBoolean(POPULAR, false);
                                editor.apply();
                                sortByFav = prefs.getBoolean(FAVOURITES,false);
                                sortByRated = prefs.getBoolean(TOP_RATED, false);
                                sortByPopular = prefs.getBoolean(POPULAR, false);
                                sortMoviesBy(FAVOURITES);
                                return true;
                            }
                        }
                        return true;
                    }
                })
                .positiveText(R.string.apply)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_filter, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                return true;
            }
            case R.id.filter: {
                //showEditDialog();
                filterDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
