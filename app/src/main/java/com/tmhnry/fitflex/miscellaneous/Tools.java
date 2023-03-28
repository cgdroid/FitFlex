package com.tmhnry.fitflex.miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;

public class Tools {

    private static final String ENTITIES_DIR = "entities";

    public static String[] getDatasets(Context context) {
        String[] datasets = new String[0];
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(ENTITIES_DIR);
            datasets = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                InputStream is = assetManager.open(ENTITIES_DIR + "/" + files[i]);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
                datasets[i] = json.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datasets;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static Double toDouble(String value) {
        return toDouble(value, new Double(0));
    }

    public static Double toDouble(String value, Double base) {
        Double d = base;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void hideSystemBars(Activity activity) {
//        Window window = activity.getWindow();
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        WindowInsetsControllerCompat windowInsetsControllerCompat = ViewCompat.getWindowInsetsController(window.getDecorView());
//        if (windowInsetsControllerCompat == null) {
//            return;
//        }
//        windowInsetsControllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
//        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
    }

}
