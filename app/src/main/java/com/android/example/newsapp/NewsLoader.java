package com.android.example.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.net.URL;
import java.util.List;

/**
 * Created by harishbhagtani on 28/07/17.
 */

public class NewsLoader extends AsyncTaskLoader<String> {

    private String getRequestUrl;
    public NewsLoader(Context context,String requestURL) {
        super(context);
        getRequestUrl = requestURL;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        String url = getRequestUrl;
        HttpTaskHandler handler = new HttpTaskHandler();
        String JSONString = handler.performCallingHttpAction(url);
        return JSONString;
    }
}
