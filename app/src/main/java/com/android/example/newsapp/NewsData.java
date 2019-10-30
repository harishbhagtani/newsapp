package com.android.example.newsapp;

/**
 * Created by harishbhagtani on 25/07/17.
 */

public class NewsData {

    private String newsSource;
    private String newsTitle;
    private String newsDescription;
    private String newsTime;
    private String newsLink;
    private String newsImageLink;
    private String newsCategory;

    private int newsInPage;

    public NewsData(String newsTitle, String newsTime, String newsLink, String newsCategory,int newsInPage) {
        this.newsTitle = newsTitle;
        this.newsTime = newsTime;
        this.newsLink = newsLink;
        this.newsCategory = newsCategory;
        this.newsInPage = newsInPage;
    }

    //getters


    public String getNewsSource() {
        return newsSource;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public String getNewsImageLink() {
        return newsImageLink;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public int getNewsInPage() {
        return newsInPage;
    }
}
