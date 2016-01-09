package it.chefacile.app;

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

public class RecipeActivity extends AppCompatActivity {

    private String recipeId;
    private String appKey = "90b990034f035755978a14d3bc8a72ec";
    private String appId = "c098d0fb";
    private String recipeTitle;
    private String recipeGET;
    private JSONObject recipeJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        this.recipeId = getIntent().getStringExtra("recipeId");
        this.recipeGET = "http://api.yummly.com/v1/api/recipe/"+ recipeId +"?_app_id="+ this.appId +"&_app_key=" + this.appKey;
        try {
            recipeJSON = (JSONObject) new JSONTokener(recipeGET).nextValue();
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

        Log.d("id", recipeId);
        Log.d("recipeget", recipeGET);

    }
}
