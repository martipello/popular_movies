package com.example.android.popularmovies.ObjectsAndAdapters;

public interface touchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}

