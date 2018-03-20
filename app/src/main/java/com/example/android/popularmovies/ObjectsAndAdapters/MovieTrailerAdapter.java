package com.example.android.popularmovies.ObjectsAndAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.MovieDetailActivity;
import com.example.android.popularmovies.R;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MyViewHolder> implements touchHelperAdapter{
    private List<MovieTrailerObject> movieTrailerAdapterList;
    private Context context;
    private MovieDetailActivity.OnItemTouchListener onItemTouchListener;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageButton play;
        public TextView movieTitle;

        public MyViewHolder(View view){
            super(view);
            play =(ImageButton)view.findViewById(R.id.play);
            movieTitle = (TextView) view.findViewById(R.id.name);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardClick(v, getPosition());
                }
            });
        }
    }

    public MovieTrailerAdapter(List<MovieTrailerObject> movieTrailerList,Context context,MovieDetailActivity.OnItemTouchListener onItemTouchListener){
        this.movieTrailerAdapterList = movieTrailerList;
        this.context = context;
        this.onItemTouchListener = onItemTouchListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_holder, parent, false);
        return new MyViewHolder(itemView);
    }

    public void refreshMyList(List<MovieTrailerObject> list){
        this.movieTrailerAdapterList.clear();
        this.movieTrailerAdapterList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void clear(){
        this.movieTrailerAdapterList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MovieTrailerObject movieTrailer = movieTrailerAdapterList.get(position);
        holder.movieTitle.setText(movieTrailer.getName());

    }

    @Override
    public int getItemCount(){
        return movieTrailerAdapterList.size();
    }
}
