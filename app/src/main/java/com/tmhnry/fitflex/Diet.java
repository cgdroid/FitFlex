package com.tmhnry.fitflex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tmhnry.fitflex.databinding.ActivityDietBinding;
import com.tmhnry.fitflex.fragment.DietCategories;
import com.tmhnry.fitflex.fragment.DietCategoryFoods;
import com.tmhnry.fitflex.fragment.Goal;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.Foods;
import com.tmhnry.fitflex.network.SearchClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Diet extends AppCompatActivity {
    private ActivityDietBinding b;
    private SearchClient client;
    private List<Foods.Model> foodModels;
    private List<ImageResult> imageResults;
    private boolean loading;
    private Dialog imageDialog;
    private String fragmentId;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityDietBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        loadFragment(DietCategories.FRAGMENT_ID);

        imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.dialog_image);
        imageDialog.setCancelable(false);

//        this.drawer = (DrawerLayout) findViewById(R.id.root_main_drawer);
//        ActionBarDrawerToggle actionBarDrawerToggle =
//                new ActionBarDrawerToggle(this,
//                        drawer,
//                        b.toolbar,
//                        0,
//                        0);
//        this.drawer.addDrawerListener(actionBarDrawerToggle);
//        this.drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//            public void onDrawerClosed(View view) {
//            }
//
//            public void onDrawerOpened(View view) {
//            }
//        });


//        foodModels = Foods.getFoods();
//
//        FoodsAdapter adapter = new FoodsAdapter(this, foodModels);
//
//        b.rvFoods.rv.setAdapter(adapter);
//        b.rvFoods.rv.setLayoutManager(new LinearLayoutManager(this));
//

    }

    @Override
    public void onBackPressed() {
        switch (fragmentId) {
            case DietCategories.FRAGMENT_ID:
                startActivity(new Intent(Diet.this, MainActivity.class));
                finish();
                return;
            case DietCategoryFoods.FRAGMENT_ID:
                loadFragment(DietCategories.FRAGMENT_ID);
                return;
            default:
        }
    }

    private void onImageSearch(String query) {
        if (Tools.isNetworkAvailable(this)) {
            client = new SearchClient();
            if (!query.equals("")) {
                client.getSearch(query, 1, this, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ImageView foodImage = imageDialog.findViewById(R.id.img_food);

                        try {
                            JSONArray imageJsonResults = null;
                            if (response != null) {
                                imageJsonResults = response.getJSONArray("items");
                                imageResults = ImageResult.fromJSONArray(imageJsonResults);
                            }
                            if (imageJsonResults != null && !(imageJsonResults.length() < 1)) {
                                Picasso.with(Diet.this).load(imageResults.get(0).getFullUrl()).into(foodImage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imageDialog.setCancelable(true);
                        imageDialog.findViewById(R.id.img_food).setVisibility(View.VISIBLE);
                        imageDialog.findViewById(R.id.txt_searching_image).setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                Toast.makeText(this, "Invalid query", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFragment(String fragmentId, int arg) {
        Fragment fragment;
        this.fragmentId = fragmentId;

        if (!fragmentId.equals(DietCategoryFoods.FRAGMENT_ID)) {
            return;
        }

        fragment = DietCategoryFoods.init(arg);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(b.frame.getId(), fragment)
                .commit();
    }

    public void loadFragment(String fragmentId) {
        Fragment fragment;
        this.fragmentId = fragmentId;

        if (fragmentId == Goal.FRAGMENT_ID) {
            fragment = Goal.newInstance("", "");
        } else if (fragmentId == DietCategories.FRAGMENT_ID) {
            fragment = DietCategories.newInstance();
        } else {
            fragment = DietCategories.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(b.frame.getId(), fragment)
                .commit();
    }

    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";

    private class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {

        protected void onPreExecute() {
        }


        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];

            // Http connection
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e(TAG, "Http connection ERROR " + e.toString());
            }


            try {
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
            } catch (IOException e) {
                Log.e(TAG, "Http getting response code ERROR " + e.toString());

            }

            Log.d(TAG, "Http response code =" + responseCode + " message=" + responseMessage);

            try {

                if (responseCode != null && responseCode == 200) {

                    // response OK

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();

                    conn.disconnect();

                    result = sb.toString();

                    Log.d(TAG, "result=" + result);

                    return result;

                } else {

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Are you online ? " + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result = errorMsg;
                    return result;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result) {

            Log.d(TAG, "AsyncTask - onPostExecute, result=" + result);


            // make TextView scrollable
//            mTextView.setMovementMethod(new ScrollingMovementMethod());

        }


    }

//    public void setupViews(){
//        // GUI init
//
//        // button onClick
//        b.btnClickMe.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//
//                // hide keyboard
//                InputMethodManager inputManager;
//                inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//                // remove spaces
//                String searchStringNoSpaces = searchString.replace(" ", "+");
//
//                String key = "AIzaSyAvV766EyL-K-bRUhJg1l6b0E_bwdfxwJ4";
//                String cx = "d93b30665dc1510d5";
//
//                String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
//                URL url = null;
//                try {
//                    url = new URL(urlString);
//                } catch (MalformedURLException e) {
//                    Log.e(TAG, "ERROR converting String to URL " + e.toString());
//                }
//                Log.d(TAG, "Url = " + urlString);
//
//
//                // start AsyncTask
//                GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
//                searchTask.execute(url);
//
//            }
//        });
//    }

}