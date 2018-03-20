package com.example.android.popularmovies.ObjectsAndAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.PathResolver;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.tools.ImageSaver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> implements touchHelperAdapter{
    private List<MovieObject> movieAdapterList;
    private Context context;
    private MainActivity.OnItemTouchListener onItemTouchListener;
    private boolean online;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageButton movieImage;
        public TextView movieTitle;
        public FrameLayout frame;

        public MyViewHolder(View view){
            super(view);
            movieImage =(ImageButton)view.findViewById(R.id.holder_image);
            movieTitle = (TextView) view.findViewById(R.id.title);
            frame = (FrameLayout) view.findViewById(R.id.item);
            movieImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardClick(v, getPosition());
                }
            });
        }
    }

    public MovieAdapter(List<MovieObject> movieList,Context context,MainActivity.OnItemTouchListener onItemTouchListener, boolean online){
        this.movieAdapterList = movieList;
        this.context = context;
        this.onItemTouchListener = onItemTouchListener;
        this.online = online;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_holder, parent, false);
        return new MyViewHolder(itemView);
    }

    public void refreshMyList(List<MovieObject> list , Boolean online){
        this.online = online;
        movieAdapterList.clear();
        movieAdapterList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear(){
        movieAdapterList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MovieObject movie = movieAdapterList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        if (online){
            Picasso.with(context).load(PathResolver.resolveImageURL(movie.getPoster()))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .into(holder.movieImage, new Callback() {
                        @Override public void onSuccess() {
                        }
                        @Override public void onError() {
                        }
                    });
        }else{
            Uri input = new ImageSaver(context).
                    setFileName(movie.getId() + ".png").
                    setDirectoryName("fav_movies").
                    find();
            Picasso.with(context).load(input)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .into(holder.movieImage, new Callback() {
                        @Override public void onSuccess() {
                        }
                        @Override public void onError() {
                        }
                    });
        }

    }

    @Override
    public int getItemCount(){
        return movieAdapterList.size();
    }
}
