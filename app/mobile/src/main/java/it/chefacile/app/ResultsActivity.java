package it.chefacile.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//librerie per implementare cards
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.card.action.WelcomeButtonAction;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//libreria per implementare lo slide to delete non utilizzabile ora
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
public class ResultsActivity extends AppCompatActivity {
    private Context mContext;
    private MaterialListView mListView;
    private JSONArray retrievedRecipes = null;
    private String recipeId;
    private String recipesString;
    private TextView responseView;
    private String searchedIngredients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred);
        // Save a reference to the context
        mContext = this;
        Log.d("ON CREATE TEST", "ON CREATE TEST");

        this.recipesString = getIntent().getStringExtra("mytext");
        this.searchedIngredients = getIntent().getStringExtra("searchedIngredients");
        Log.d("RECIPESTRING", recipesString);
        Log.d("SEARCHED INGR RESULT", searchedIngredients);
        try {
            //retrievedRecipes = (JSONArray) new JSONTokener(recipesString).nextValue();
            JSONObject JSONob = (JSONObject) new JSONTokener(recipesString).nextValue();
            Log.d("JSONOB MOMENT", JSONob.toString());
            retrievedRecipes = JSONob.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Bind the MaterialListView to a variable
        mListView = (MaterialListView) findViewById(R.id.material_listview);

        try {
            fillArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Log.d("CARD_TYPE", "" + card.getTag());
                if (card.getTag().equals("WELCOME_CARD"));
                else {
                    try {
                        recipeId = retrievedRecipes.getJSONObject(position - 1).get("id").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new RetrieveFeedTask().execute();
                }
            }
            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Log.d("LONG_CLICK", "" + card.getTag());
            }
        });
    }
    private void fillArray() throws JSONException {
        List<Card> cards = new ArrayList<>();
        cards.add(getWelcomeCard());
        for (int i = 0; i < this.retrievedRecipes.length(); i++) {
            cards.add(getRandomCard(i));
        }
        mListView.getAdapter().addAll(cards);
    }
    private Card getWelcomeCard() {
        final CardProvider provider = new Card.Builder(this)
                .setTag("WELCOME_CARD")
                //.setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.welcome_card)
                .setTitle("The best for you")
                .setTitleColor(Color.WHITE)
                .setDescription("Find what you like")
                .setDescriptionColor(Color.WHITE)
                .setSubtitle("Here you can find all the recipes that you can cook")
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        return provider.endConfig().build();
    }
    private Card getRandomCard(final int position) throws JSONException {
        //String title = "Recipe number " + (position + 1);
        //String description = "Lorem ipsum dolor sit amet";

        String title = retrievedRecipes.getJSONObject(position).get("title").toString();
        String rating = "Likes: " + retrievedRecipes.getJSONObject(position).get("likes").toString();
        String missing = "✘ " + retrievedRecipes.getJSONObject(position).get("missedIngredientCount").toString();
        String used = "✓ " + retrievedRecipes.getJSONObject(position).get("usedIngredientCount").toString();
        String imageURL;
        //JSONObject img = retrievedRecipes.getJSONObject(position).getJSONObject("imageUrlsBySize");
        if(!retrievedRecipes.getJSONObject(position).has("image")){
            imageURL = "http://i.imgur.com/exqW1px.png";

        }
        else {
            imageURL = retrievedRecipes.getJSONObject(position).get("image").toString();
        }
        //String totalTime = retrievedRecipes.getJSONObject(position).get("readyInMinutes").toString();
        recipeId = retrievedRecipes.getJSONObject(position).get("id").toString();

        final CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.result_card_layout)
                .setTitle(title)
                .setDescription(missing + "   " + used)
               // .setDescription(missing)
                .setDrawable(imageURL)
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                        requestCreator.fit().centerCrop() ;
                    }
                });

        return provider.endConfig().build();
    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Toast.makeText(mContext, "Loading..", Toast.LENGTH_SHORT).show();

        }

        protected String doInBackground(Void... urls) {
            String idrecipe = recipeId;
            // Do some validation here about String ingredient

            try {
                URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/"+ idrecipe +"/information");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //TODO: Changing key values
                urlConnection.setRequestProperty("X-Mashape-Key", "KEY");
                Log.d("URLCONNECTION", url.toString());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
                Snackbar.make(responseView, "Network connectivity unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                Log.i("INFO", response);
                // responseView.setText(response);

                Intent myIntent1 = new Intent(ResultsActivity.this, RecipeActivity.class);
                myIntent1.putExtra("recipeId", response);
                myIntent1.putExtra("recipesString", recipesString);
                myIntent1.putExtra("searchedIngredients", searchedIngredients);
                startActivity(myIntent1);
            }


        }
    }

    @Override
    public void onBackPressed()
    {
      Log.d("PREMUTO RESULT", "SI");
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();



    }
}