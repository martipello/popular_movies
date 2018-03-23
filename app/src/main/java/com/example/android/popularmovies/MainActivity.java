package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieAdapter;
import com.example.android.popularmovies.ObjectsAndAdapters.MovieObject;

import java.io.IOException;
import java.util.ArrayList;

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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public String filter;
    private final static String SORT_ORDER = "SORT_ORDER";
    private String SORT_PREF = "";
    private final static String POPULAR = "popular";
    private final static String TOP_RATED = "top_rated";
    private final static String FAVOURITES = "favourites";
    public static final String EXTRA_IMAGE = "EXTRA_IMAGE";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String TAG = "Cursor";
    private static final int LOADER = 44;
    public MovieAdapter movieAdapter;
    MoviePageResults pageResults;
    private static ItemTouchHelper mItemTouchHelper;
    private StaggeredGridLayoutManager staggeredGridLayoutManagerVertical;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static ArrayList<MovieObject> movieList = new ArrayList<>();
    public RecyclerView recyclerView;
    TextView connectionText;
    private SQLiteDatabase movieDB;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        connectionText = findViewById(R.id.no_connection);
        connectionText.setVisibility(View.INVISIBLE);
        int columns = getResources().getInteger(R.integer.columns);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean("RAN_ONCE",false)){
            editor.putBoolean("RAN_ONCE",true);
            editor.putString(SORT_ORDER, TOP_RATED);
            editor.apply();
        }
        SORT_PREF = prefs.getString(SORT_ORDER,TOP_RATED);
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
        movieAdapter = new MovieAdapter(movieList,MainActivity.this,itemTouchListener,false);
        sortMoviesBy(SORT_PREF);
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
                sortMoviesBy(SORT_PREF);
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
        //if (sortByFav)
        //    myIntent.putExtra("column" , cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
        MainActivity.this.startActivity(myIntent);
    }

    public void sortMoviesBy(String sortOrder) {
        switch(sortOrder){
            case FAVOURITES :
                getSupportLoaderManager().initLoader(LOADER, null, this);
                break;
                default:
                    movieList.clear();
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
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                //Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                Uri uri = MovieProvider.CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.TITLE;
                return new CursorLoader(this,
                        uri,
                        null,
                        null,
                        null,
                        sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        ArrayList<MovieObject> databaseMovieList = new ArrayList<>();
        if (data != null && data.getCount() > 0){
            cursor = data;
        }
        data.moveToFirst();
        if (data.moveToFirst()) {
            do{
                MovieObject movieObject = new MovieObject(
                        data.getInt(data.getColumnIndex( MovieProvider.MOVIE_DATABASE_ID)),
                        data.getString(data.getColumnIndex( MovieProvider.TITLE)),
                        data.getString(data.getColumnIndex( MovieProvider.OVERVIEW)),
                        data.getString(data.getColumnIndex( MovieProvider.POSTER_PATH)),
                        data.getDouble(data.getColumnIndex( MovieProvider.RATING)),
                        data.getString(data.getColumnIndex( MovieProvider.BACKDROP_PATH)));
                databaseMovieList.add(movieObject);
            } while (data.moveToNext());
        }
        data.moveToFirst();
        movieAdapter.refreshMyList(databaseMovieList,false);
        if (movieAdapter.getItemCount() > 0){
            connectionText.setVisibility(View.INVISIBLE);
        }else{
            connectionText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    public interface OnItemTouchListener {
        public void onCardClick(View view, int position);
        public void onCardLongClick(View view, int position);
    }

    private void filterDialog() {
        int indexChecked = -1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (SORT_PREF){
            case TOP_RATED:
                indexChecked = 1;
                break;
            case POPULAR:
                indexChecked = 0;
                break;
            case FAVOURITES:
                indexChecked = 2;
                break;
        }
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
                                editor.putString(SORT_ORDER, POPULAR);
                                editor.apply();
                                SORT_PREF = prefs.getString(SORT_ORDER,POPULAR);
                                sortMoviesBy(SORT_PREF);
                                return true;
                            }
                            case 1: {
                                editor.putString(SORT_ORDER, TOP_RATED);
                                editor.apply();
                                SORT_PREF = prefs.getString(SORT_ORDER , POPULAR);
                                sortMoviesBy(SORT_PREF);
                                return true;
                            }
                            case 2: {
                                editor.putString(SORT_ORDER, FAVOURITES);
                                editor.apply();
                                SORT_PREF = prefs.getString(SORT_ORDER , POPULAR);
                                sortMoviesBy(SORT_PREF);
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
                filterDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
