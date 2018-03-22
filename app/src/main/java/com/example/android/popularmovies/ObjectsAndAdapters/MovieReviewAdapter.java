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

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MyViewHolder>{
    private List<MovieReviewObject> movieReviewList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView content;

        public MyViewHolder(View view){
            super(view);
            name =(TextView)view.findViewById(R.id.author);
            content = (TextView) view.findViewById(R.id.content);
        }
    }

    public MovieReviewAdapter(List<MovieReviewObject> movieReviewList, Context context){
        this.movieReviewList = movieReviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_holder, parent, false);
        return new MyViewHolder(itemView);
    }

    public void refreshMyList(List<MovieReviewObject> list){
        this.movieReviewList.clear();
        this.movieReviewList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void clear(){
        this.movieReviewList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MovieReviewObject movieReview = movieReviewList.get(position);
        holder.name.setText(movieReview.getAuthor());
        holder.content.setText(movieReview.getContent());
    }

    @Override
    public int getItemCount(){
        return movieReviewList.size();
    }
}
