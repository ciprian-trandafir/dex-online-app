package com.dexonline.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dexonline.R;
import com.dexonline.adapter.DefinitionAdapter;
import com.dexonline.classes.Definition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Search extends Fragment {
    private EditText searchInput;
    private Dialog loadingDialog;
    private final List<Definition> definitionList = new ArrayList<>();
    private DefinitionAdapter definitionAdapter;

    public Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = buildLoadingDialog(getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDefinitions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        definitionAdapter = new DefinitionAdapter(definitionList, getActivity());
        recyclerView.setAdapter(definitionAdapter);

        searchInput = view.findViewById(R.id.searchInput);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWord(searchInput.getText().toString().trim());
                View view_ = requireActivity().getCurrentFocus();
                if (view_ != null) {
                    InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view_.getWindowToken(), 0);
                }

                return true;
            }
            return false;
        });
    }

    private void searchWord(String search) {
        loadingDialog.show();
        String url = "https://dexonline.ro/definitie/" + search + "/json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray arrayDefinitions = new JSONArray(response.getString("definitions"));
                if (arrayDefinitions.length() == 0) {
                    Toast.makeText(getActivity(), "nimic", Toast.LENGTH_LONG).show();
                } else {
                    definitionList.clear();
                    for (int i = 0; i < arrayDefinitions.length(); i++) {
                        JSONObject definition = arrayDefinitions.getJSONObject(i);
                        definitionList.add(new Definition(
                                definition.getString("id"),
                                definition.getString("htmlRep"),
                                definition.getString("userNick"),
                                definition.getString("sourceName"),
                                definition.getString("createDate"),
                                definition.getString("modDate")
                                ));
                    }
                    definitionAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    Gson gson = new Gson();
                    String json = sharedPrefs.getString("searchHistory", "");
                    Type type = new TypeToken<List<com.dexonline.classes.Search>>(){}.getType();
                    List<com.dexonline.classes.Search> searchList = gson.fromJson(json, type);

                    if (searchList == null) {
                        searchList = new ArrayList<>();
                    }

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                    Date date = new Date(System.currentTimeMillis());
                    searchList.add(new com.dexonline.classes.Search(response.getString("word"), formatter.format(date)));

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    String listOthersWordOfDay_ = gson.toJson(searchList);
                    editor.putString("searchHistory", listOthersWordOfDay_);
                    editor.apply();
                }
                loadingDialog.hide();
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
        }, error -> Log.d("ERROR REQUEST", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("InflateParams")
    private Dialog buildLoadingDialog(Context context){
        Dialog dialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setView(LayoutInflater.from(context).inflate(R.layout.loading, null, false));
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
}