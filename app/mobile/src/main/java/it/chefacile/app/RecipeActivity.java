package it.chefacile.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class RecipeActivity extends AppCompatActivity {

    private String recipeString;
    private String recipeListString;
    private String recipeTitle;
    private String recipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        this.recipeString = getIntent().getStringExtra("recipeId");
        this.recipeListString = getIntent().getStringExtra("recipesString");
        Log.d("RECIPEID FROM RECIPEACT", recipeString);

        Log.d("RECIPE STRING", recipeString);

        JSONObject object = null;
        try {
            object = (JSONObject) new JSONTokener(recipeString).nextValue();
            this.recipeTitle = object.getString("name");
            Log.d("title", recipeTitle);
            setTitle(recipeTitle);

            this.recipeImage = object.getJSONObject("images").get("hostedMediumUrl").toString();
            Log.d("IMAGE", recipeImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    /*public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = recipeURL;
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }*/

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        /*Intent setIntent = new Intent(RecipeActivity.this, ResultsActivity.class);
        setIntent.putExtra("mytext", recipeListString);
        startActivity(setIntent);*/
    }
}
