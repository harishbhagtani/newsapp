package com.android.example.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by harishbhagtani on 27/07/17.
 */

public class NewsAdapter extends ArrayAdapter<NewsData> {
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(Activity context, ArrayList<NewsData> bookDetailsInfo) {
        super(context, 0, bookDetailsInfo);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View newsInfoView = convertView;
        if (newsInfoView == null) {
            newsInfoView = LayoutInflater.from(getContext()).inflate(R.layout.news_item_layout, parent, false);
        }
        final NewsData currentNewsInfo = getItem(position);
        int newsNumber = ((currentNewsInfo.getNewsInPage() - 1) * 10) + 1+position;
        final TextView setNewsTitle = (TextView) newsInfoView.findViewById(R.id.text_view_news_title);
        final String newsTitle = String.valueOf(currentNewsInfo.getNewsTitle());
        setNewsTitle.setText(newsTitle);
        TextView setNewsNumber = (TextView) newsInfoView.findViewById(R.id.text_view_counter);
        setNewsNumber.setText(String.valueOf(newsNumber));
        TextView setCategory = (TextView) newsInfoView.findViewById(R.id.text_view_news_category);
        setCategory.setText(currentNewsInfo.getNewsCategory());
        TextView setDate = (TextView)newsInfoView.findViewById(R.id.text_view_news_date);
        setDate.setText(String.valueOf(currentNewsInfo.getNewsTime()));
        return newsInfoView;
    }
}
