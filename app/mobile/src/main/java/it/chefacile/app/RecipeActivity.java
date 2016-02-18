package it.chefacile.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.card.action.WelcomeButtonAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private String recipeString;
    private String recipeListString;
    private String recipeTitle;
    private String recipeImage;
    private String totalTime;
    private String[] recipeIngredients;
    private String stringIngredients;
    private String recipeServings;
    private String recipeURL;
    private String proc;

    private Context mContext;
    private MaterialListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mContext = this;


        this.recipeString = getIntent().getStringExtra("recipeId");
        this.recipeListString = getIntent().getStringExtra("recipesString");
        Log.d("RECIPEID FROM RECIPEACT", recipeString);

        Log.d("RECIPE STRING", recipeString);

        mListView = (MaterialListView) findViewById(R.id.material_listview1);
        // mListView.setItemAnimator(new SlideInLeftAnimator());
        //  mListView.getItemAnimator().setAddDuration(300);
        //  mListView.getItemAnimator().setRemoveDuration(300);
        // Fill the array withProvider mock content

        JSONObject object = null;
        try {
            object = (JSONObject) new JSONTokener(recipeString).nextValue();
            this.recipeTitle = object.getString("title");
            Log.d("title", recipeTitle);
            setTitle(recipeTitle);

            this.recipeImage = object.get("image").toString();
            //retrievedRecipes.getJSONObject(position).get("image").toString();
            Log.d("IMAGE", recipeImage);

            this.totalTime = object.getString("readyInMinutes");
            Log.d("TIME", object.getString("readyInMinutes"));


            this.recipeServings = object.getString("servings");
            Log.d("SERVINGS", String.valueOf(recipeServings));



            this.recipeIngredients = new String[object.getJSONArray("extendedIngredients").length()];
            JSONObject ingre;
            String ingredi;
            for(int i = 0; i < object.getJSONArray("extendedIngredients").length(); i++){
                ingre = (JSONObject) object.getJSONArray("extendedIngredients").get(i);
                ingredi = ingre.getString("originalString");
                this.recipeIngredients[i] = ingredi;
                Log.d("ING", recipeIngredients[i]);
            }
           stringIngredients = java.util.Arrays.toString(recipeIngredients);

            stringIngredients = stringIngredients.replaceAll(",","\n");
            stringIngredients = stringIngredients.replaceAll("\\[","");
            stringIngredients = stringIngredients.replaceAll("]","");


            this.recipeURL = object.getString("spoonacularSourceUrl");
            Log.d("URL", recipeURL);


            (new ParseURL()).execute(new String[]{recipeURL});



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
                /*Snackbar.make(view, "Cooking tools..very soon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent myIntent = new Intent(RecipeActivity.this, TimerActivity.class);
                myIntent.putExtra("recipeId", recipeString);
                myIntent.putExtra("recipesString", recipeListString);
                startActivity(myIntent);

            }

        });
    }

    private void fillArray(String proc) throws JSONException {
        List<Card> cards = new ArrayList<>();
        cards.add(generateServingCard());
        cards.add(generateTimeCard());
        cards.add(generateIngredientCard());
        cards.add(generateProcedureCard(proc));
        mListView.getAdapter().addAll(cards);
    }

    private Card generateServingCard() {
        Log.d("Servings", this.recipeServings);
        CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                //.setDismissible()
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.recipe_card_layout)
                .setTitle("Servings: " + recipeServings);


        return provider.endConfig().build();
    }

    private Card generateTimeCard() {
        Log.d("Servings", this.recipeServings);
        CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                //.setDismissible()
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.recipe_card_layout)
                .setTitle("Time: " + totalTime + " minutes");


        return provider.endConfig().build();
    }

    private Card generateIngredientCard() {
        Log.d("Servings", this.recipeServings);
        CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                //.setDismissible()
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.recipe_card_layout)
                .setTitle("Ingredients: \n\n" + stringIngredients);


        return provider.endConfig().build();
    }

    private Card generateProcedureCard(String proc) {
        Log.d("Servings", this.recipeServings);
        CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                //.setDismissible()
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.recipe_card_layout)
                .setTitle(proc);


        return provider.endConfig().build();
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

     private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            String clean = null;
            try {
                Log.d("JSwa", "Connecting to ["+strings[0]+"]");
                Document doc  = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to ["+strings[0]+"]");
                //Instructions to deselect dome div
                //doc.select("div").remove();
                // Get document (HTML page) title
                String title = doc.title();
                Log.d("JSwA", "Title ["+title+"]");
                Elements instructions = doc.select("div.recipeInstructions");
                instructions.html();

                buffer.append(instructions);
               // buffer.append("Title: " + title + "\r\n");
                Log.d("BEFORE", instructions.toString());
                clean = Jsoup.clean(instructions.html(), Whitelist.simpleText());
                Log.d("AFTER", clean);

                // Get meta info
//                Elements metaElems = doc.select("meta");
//                buffer.append("META DATA\r\n");
//                for (Element metaElem : metaElems) {
//                    String name = metaElem.attr("name");
//                    String content = metaElem.attr("content");
//                    buffer.append("name ["+name+"] - content ["+content+"] \r\n");
//                }


            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return "Procedure: \n\n" + clean.replaceAll("\\.","\\.\n\n");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {


            super.onPostExecute(s);
            Log.d("INSTRUCTIONS", s);
            proc = s;
            try {
                fillArray(proc);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
