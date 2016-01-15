package it.chefacile.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

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

            this.recipeImage = object.getJSONArray("images").getJSONObject(0).get("hostedLargeUrl").toString();
            Log.d("IMAGE", recipeImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ImageView toolbarImage = (ImageView) findViewById(R.id.image_stretch_detail);
        picassoLoader(this, toolbarImage, recipeImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
    }
    public void picassoLoader(Context context, ImageView imageView, String recipeImage){
        Log.d("PICASSO", "loading image");
        Picasso.with(context)
                .load(recipeImage)
                //.resize(30,30)
                .placeholder(R.color.colorPrimary)
                .error(R.color.colorPrimaryDark)
                .into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, ResultsActivity.class);
                Log.d("RECIPES", recipeListString);
                intent.putExtra("mytext", recipeListString);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

}
