package com.tmhnry.fitflex.network;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchClient {
    private static final String API_BASE_URL = "https://www.googleapis.com/customsearch/v1?";
    private static final String API_KEY = "AIzaSyCDg8ySJyxaAdF_Y2NuBkSPgtW57VHZMU0";
    private static final String CX_KEY = "69f79d37bd22858d7";
    private AsyncHttpClient client;

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public SearchClient() {
        this.client = new AsyncHttpClient();
    }

    public void getSearch(final String query, int startPage, Context context, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("q=" + URLEncoder.encode(query, "utf-8") + "&start=" + startPage
                    + "&cx=" + CX_KEY + "&searchType=image" + "&key=" + API_KEY);
            client.get(url, handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(context, "Search not found", Toast.LENGTH_SHORT).show();
        }
    }
}
