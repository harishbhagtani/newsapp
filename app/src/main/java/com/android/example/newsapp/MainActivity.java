package com.android.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {

    String requestURL;
    String query;
    public int currentPage;
    int pageSize;
    int numberOfResults;
    int numberOfPages;
    String apiKey;

    Button prevButton;
    Button nextButton;

    TextView statusText;
    TextView descriptionText;
    TextView creditTextView;
    TextView pageInfoText;

    RelativeLayout descriptionLayout;
    LinearLayout navigationBarLayout;

    ArrayList<NewsData> newsList = new ArrayList<>();
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        apiKey = "test";
        pageSize = 10;
        currentPage = 1;
        lv = (ListView) findViewById(R.id.list);
        descriptionLayout = (RelativeLayout) findViewById(R.id.relative_description_layout);
        navigationBarLayout = (LinearLayout) findViewById(R.id.linear_navigation_bar);
        creditTextView = (TextView) findViewById(R.id.text_view_credit);
        pageInfoText = (TextView) findViewById(R.id.text_view_page_info);
        prevButton = (Button) findViewById(R.id.button_prev);
        nextButton = (Button) findViewById(R.id.button_next);
        query = "";
        statusText = (TextView) findViewById(R.id.text_view_status);
        descriptionText = (TextView) findViewById(R.id.text_view_description);
        if (isNetworkAvailable()) {
            statusText.setText("Internet Connection Established");
            descriptionText.setText("Welcome");
        } else {
            statusText.setText("No Internet Connection Available");
            descriptionText.setText("Your Device is not connected to internet. You cannot search news without the proper internet connection.");
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onClickSearch(View v) {
        creditTextView.setText("The Guardian API");
        if (isNetworkAvailable()) {
            prevButton.setVisibility(View.GONE);
            EditText searchQuery = (EditText) findViewById(R.id.search);
            query = searchQuery.getText().toString();
            currentPage = 1;
            statusText.setText("Searching...");
            creditTextView.setVisibility(View.VISIBLE);
            navigationBarLayout.setVisibility(View.GONE);
            startSearch();
        } else {
            statusText.setText("No Internet Connection");
            descriptionText.setVisibility(View.VISIBLE);
            descriptionLayout.setVisibility(View.VISIBLE);
            descriptionText.setText("Please connect your device to internet to load news data.");
            creditTextView.setVisibility(View.VISIBLE);
            navigationBarLayout.setVisibility(View.GONE);
            lv.setVisibility(View.GONE);
        }
    }

    public void onClickNext(View v) {
        if (isNetworkAvailable()) {
            navigationBarLayout.setVisibility(View.GONE);
            creditTextView.setVisibility(View.VISIBLE);
            creditTextView.setText("Searching...");
            currentPage++;
            prevButton.setVisibility(View.VISIBLE);
            startSearch();
        } else {
            creditTextView.setText("The Guardian API");
            startSearch();
        }
    }

    public void onClickPrev(View view) {
        if (isNetworkAvailable()) {
            navigationBarLayout.setVisibility(View.GONE);
            creditTextView.setVisibility(View.VISIBLE);
            creditTextView.setText("Searching...");
            currentPage--;
            nextButton.setVisibility(View.VISIBLE);
            startSearch();
        } else {
            creditTextView.setText("The Guardian API");
            startSearch();
        }
    }


    public void startSearch() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        descriptionLayout.setVisibility(View.VISIBLE);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar_news_load);
        if (query.equals("")) {
            statusText.setText("No Query Entered");
            descriptionText.setVisibility(View.VISIBLE);
            descriptionText.setText("No query entered in the search box. Please enter appropriate search query and then press the search button.");
            lv.setVisibility(View.GONE);
            navigationBarLayout.setVisibility(View.GONE);
            creditTextView.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),
                    "Please enter a valid query",
                    Toast.LENGTH_LONG).show();
        } else {
            newsList.clear();
            pb.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            descriptionText.setVisibility(View.GONE);
            String managedQuery = query.replace(" ", "%20");
            requestURL = "http://content.guardianapis.com/search?order-by=newest&page=" + currentPage + "&page-size=" + pageSize + "&q=" + managedQuery + "&api-key=" + apiKey + "";
            new performSearch().execute();
        }
    }

    public class performSearch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpTaskHandler handler = new HttpTaskHandler();
            String JSONString = handler.performCallingHttpAction(requestURL);
            if (JSONString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(JSONString);
                    if (jsonObj.has("response")) {
                        JSONObject responseData = jsonObj.getJSONObject("response");
                        String newsDate;
                        String newsTitle;
                        String newsCategory;
                        String newsWebLink;

                        numberOfResults = responseData.getInt("total");
                        numberOfPages = responseData.getInt("pages");
                        JSONArray news = responseData.getJSONArray("results");
                        if (news.length() != 0) {
                            for (int i = 0; i < 10; i++) {
                                JSONObject newsData = news.getJSONObject(i);
                                if (newsData.has("sectionName")) {
                                    newsCategory = newsData.getString("sectionName");
                                } else {
                                    newsCategory = "Not Categorized";
                                }
                                if (newsData.has("webTitle")) {
                                    newsTitle = newsData.getString("webTitle");
                                } else {
                                    newsTitle = "NO Title";
                                }
                                if (newsData.has("webPublicationDate")) {
                                    newsDate = "Date : " + newsData.getString("webPublicationDate");
                                } else {
                                    newsDate = "Date Not Provided";
                                }
                                if (newsData.has("webUrl")) {
                                    newsWebLink = newsData.getString("webUrl");
                                } else {
                                    newsWebLink = "open-platform.theguardian.com/";
                                }
                                newsList.add(new NewsData(newsTitle, newsDate, newsWebLink, newsCategory, currentPage));
                            }
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "No Results Found",
                                        Toast.LENGTH_LONG).show();
                                descriptionText.setVisibility(View.VISIBLE);
                                descriptionText.setText("There are no results related to \"" + query + "\"\n Try searching for another query or try searching again with detailed name for the same query. This happens mainly when no items in the server matches your query.");

                            }
                        });
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            descriptionText.setVisibility(View.VISIBLE);
                            descriptionText.setText("There was some error in processing your query. Please try again in some time or try searching for any other book.");
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar_news_load);
                        pb.setVisibility(View.GONE);
                        descriptionText.setVisibility(View.VISIBLE);
                        descriptionLayout.setVisibility(View.VISIBLE);
                        navigationBarLayout.setVisibility(View.GONE);
                        creditTextView.setVisibility(View.VISIBLE);
                        statusText.setText("No Internet Connection");
                        descriptionText.setText("There is some problem with the network. Please check your internet connection. \n\nIf the problem persists, try connection to any other network.");
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isNetworkAvailable()) {
                descriptionLayout.setVisibility(View.GONE);
                creditTextView.setVisibility(View.GONE);
                navigationBarLayout.setVisibility(View.VISIBLE);
                if (numberOfResults == 1 || numberOfPages == 1) {
                    statusText.setText("Found Total " + numberOfResults + " Results ");
                    creditTextView.setVisibility(View.VISIBLE);
                    pageInfoText.setText("1");
                    navigationBarLayout.setVisibility(View.GONE);
                } else {
                    statusText.setText("Found Total " + numberOfResults + " Results");
                    pageInfoText.setText(currentPage + " / " + numberOfPages);
                }
                NewsAdapter adaptNews = new NewsAdapter(MainActivity.this, newsList);
                lv.setAdapter(adaptNews);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        NewsData data = newsList.get(i);
                        String sourceURL = data.getNewsLink();
                        Intent openBrowserForSourceNews = new Intent(Intent.ACTION_VIEW);
                        openBrowserForSourceNews.setData(Uri.parse(sourceURL));
                        openBrowserForSourceNews.setPackage("com.android.chrome");
                        startActivity(openBrowserForSourceNews);
                    }
                });
                ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar_news_load);
                pb.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                if (currentPage == numberOfPages && numberOfResults > 10) {
                    nextButton.setVisibility(View.GONE);
                }
                if (currentPage == 1) {
                    prevButton.setVisibility(View.GONE);
                }
                if (numberOfResults == 0) {
                    statusText.setText("No Results Found");
                    descriptionLayout.setVisibility(View.VISIBLE);
                    descriptionText.setVisibility(View.VISIBLE);
                    descriptionText.setText("No search result found for query " + query + ". Please search again with appropriate search query or check for any spelling mistake. ");
                    lv.setVisibility(View.GONE);
                    navigationBarLayout.setVisibility(View.GONE);
                    creditTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
