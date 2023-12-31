package com.dexonline.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dexonline.R;
import com.dexonline.classes.Definition;
import com.dexonline.classes.Helper;
import com.dexonline.classes.WordOfDay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppLoading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
            switch (preferences.getString("theme", "default")) {
                case "default":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_loading);

        if (Helper.isNetwork(this)) {
            getWordOfDay();
        } else {
            Dialog dialogNoNetwork = buildNoInternetDialog();
            dialogNoNetwork.show();
        }
    }

    public void onBackPressed() { }

    public void getWordOfDay() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://dexonline.ro/cuvantul-zilei/json", null, response -> {
            try {
                JSONObject requestedRecord = response.getJSONObject("requested").getJSONObject("record");
                WordOfDay wordOfDay = new WordOfDay(
                        requestedRecord.getString("year"),
                        response.getString("day"),
                        response.getString("month"),
                        requestedRecord.getString("word"),
                        requestedRecord.getString("reason"),
                        requestedRecord.getString("image")
                );
                Definition definition = new Definition(
                        requestedRecord.getJSONObject("definition").getString("id"),
                        requestedRecord.getJSONObject("definition").getString("htmlRep"),
                        requestedRecord.getJSONObject("definition").getString("userNick"),
                        requestedRecord.getJSONObject("definition").getString("sourceName"),
                        requestedRecord.getJSONObject("definition").getString("createDate"),
                        requestedRecord.getJSONObject("definition").getString("modDate"),
                        false
                );

                List<WordOfDay> listOthersWordOfDay = new ArrayList<>();
                JSONArray jsonRecordOthersRecord = new JSONArray(response.getJSONObject("others").getString("record"));
                for (int i = 0; i < jsonRecordOthersRecord.length(); i++) {
                    JSONObject wordOfDayObject = jsonRecordOthersRecord.getJSONObject(i);
                    listOthersWordOfDay.add(new WordOfDay(
                            wordOfDayObject.getString("year"),
                            "", "",
                            wordOfDayObject.getString("word"),
                            wordOfDayObject.getString("reason"),
                            wordOfDayObject.getString("image")
                    ));
                }

                SharedPreferences sharedPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();

                String listOthersWordOfDay_ = gson.toJson(listOthersWordOfDay);
                editor.putString("listOthersWordOfDay", listOthersWordOfDay_);  //save previous Word Of Day

                String definition_ = gson.toJson(definition);
                editor.putString("WordOfDayDefinition", definition_); //save current Word Of Day definition

                String wordOfDay_ = gson.toJson(wordOfDay);
                editor.putString("WordOfDay", wordOfDay_); //save current Word Of Day

                editor.putBoolean("anotherActivity", false);

                editor.apply();

                new Handler().postDelayed(() -> {
                    finishAffinity();
                    startActivity(new Intent(AppLoading.this, HomePage.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }, 1000);
            } catch (Exception e) {
                Helper.writeError(new com.dexonline.classes.Error("try-catch", e.toString(), "getWordOfDay()"), this);
            }
        }, error -> Helper.writeError(new com.dexonline.classes.Error("request", error.toString(), "getWordOfDay()"), this));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public Dialog buildNoInternetDialog() {
        SharedPreferences sharedPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("listOthersWordOfDay", "");
        Type type = new TypeToken<List<WordOfDay>>(){}.getType();
        List<WordOfDay> listOthersWordOfDay = gson.fromJson(json, type);
        if (listOthersWordOfDay == null) {
            listOthersWordOfDay = new ArrayList<>();
        }

        Dialog dialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Eroare de conexiune")
                .setMessage("Nu se poate comunica cu serverul.\nVerifică dacă ai conexiune la internet.")
                .setNegativeButton("IEȘIRE", (dialogg, which) -> finishAffinity())
                .setCancelable(false);
        if (listOthersWordOfDay.size() == 0) {
            builder.setPositiveButton("ÎNCEARCĂ DIN NOU", (dialogg, which) -> {
                dialogg.dismiss();
                finishAffinity();
                startActivity(new Intent(this, AppLoading.class));
            });
        } else {
            builder.setPositiveButton("CONTINUĂ", (dialogg, which) -> {
                dialogg.dismiss();
                finishAffinity();
                startActivity(new Intent(this, HomePage.class));
            });
        }

        dialog = builder.create();

        return dialog;
    }
}
