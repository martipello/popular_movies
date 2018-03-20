package com.example.android.popularmovies.tools;

import android.content.Context;
import android.util.AttributeSet;

public class AspectRatioImageButton extends android.support.v7.widget.AppCompatImageButton {

    public AspectRatioImageButton(Context context) {
        super(context);
    }

    public AspectRatioImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}