package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import java.io.InputStream;

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
    private LinearLayout layout;

    public LoadImageTask(LinearLayout layout) {
        this.layout = layout;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        try {
            InputStream input = new java.net.URL(url).openStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            Drawable drawable = new BitmapDrawable(layout.getResources(), result);
            layout.setBackground(drawable);
        }
    }
}
