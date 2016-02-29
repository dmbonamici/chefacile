package it.chefacile.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DatabaseHelper chefacileDb;
    private Context mContext;
    private MaterialListView mListView;
    private EditText editText;
    private TextView responseView;
    private ImageView iv;
    private ProgressBar progressBar;
    private ImageButton TutorialButton;
    private ImageButton FilterButton;
    private Button AddButton;
    private Button Show;
    private String ingredients = ",";
    private ArrayAdapter<String> adapter;
    private String currentIngredient = "";
    private String currentImageUrl = "";
    private String responseJSON = "";
    private String singleIngredient;
    private int clicks = 0;
    private AlertDialog alert;
    private int ranking = 1;
    private String urlFindByIngredient = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?ingredients=";
    private String urlIngredientDetais = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/parseIngredients";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        chefacileDb = new DatabaseHelper(this);
        FilterButton = (ImageButton) findViewById(R.id.buttonfilter);
        TutorialButton = (ImageButton) findViewById(R.id.button);
        AddButton = (Button) findViewById(R.id.button2);
        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Show = (Button) findViewById(R.id.buttonShow);


        mListView = (MaterialListView) findViewById(R.id.material_listview);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);


        TutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IntroScreenActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });



        Show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Cursor res = chefacileDb.getAllDataIngredients();

                if (res.getCount() == 0){
                    showMessage("Error","Nothing found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("INGREDIENT: " +res.getString(0)+ "\n");
                    buffer.append("COUNT: " +res.getString(1)+ "\n\n");

                }

                showMessage("Data", buffer.toString());
            }
        });



        iv = (ImageView) findViewById(R.id.imageView);
        iv.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                clicks++;
                Log.d("CLICKS", String.valueOf(clicks));
                if(clicks == 15){
                    Log.d("IMAGE SHOWN", "mai vero");
                    setBackground(iv);
                }
            }
        });


        final CharSequence[] items = {"Maximize used ingredients", "Minimize missing ingredients"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter mode");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].toString().trim().equals("Maximize used ingredients")){
                    ranking = 1;
                }
                else{
                    ranking = 2;
                }
                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                Log.d("ranking", String.valueOf(ranking));
            }
        });
        alert = builder.create();


        FilterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.show();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String input;
                String s1 = editText.getText().toString().substring(0,1).toUpperCase();
                String s2 = editText.getText().toString().substring(1);
                input = s1+s2;
                Log.d("INPUT: ", input);


                if (chefacileDb.findIngredient(input))
                    chefacileDb.updateCount(input);


                if(editText.getText().toString().equals("")){
                    ingredients += editText.getText().toString().trim() + "";
                    editText.getText().clear();

                }
                else {
                    ingredients += editText.getText().toString().replaceAll(" ","+").trim().toLowerCase() + ",";
                    singleIngredient = editText.getText().toString().trim().toLowerCase();
                    currentIngredient = singleIngredient;
                    new RetrieveIngredientTask().execute();

                    //adapter.add(singleIngredient.substring(0,1).toUpperCase() + singleIngredient.substring(1));
                }
                //responseView.setText(ingredients);

                //ingredients += editText.getText().toString().trim() + ",";

            }
        });

      /*  try {
            fillArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final FloatingActionButton actionABC = (FloatingActionButton) findViewById(R.id.action_abc);
        actionABC.bringToFront();
        actionABC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RetrieveFeedTask().execute();

            }

            // Snackbar.make(view, "Non disponibile, mangia l'aria", Snackbar.LENGTH_LONG)
            //         .setAction("Action", null).show();

        });

    }


    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d("Tect", responseView.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String ingredient = responseView.getText().toString();

            // Do some validation here about String ingredient

            try {

                URL urlSpoo = new URL(urlFindByIngredient + ingredients + "&number=20&ranking=" + String.valueOf(ranking));
                HttpURLConnection urlConnection = (HttpURLConnection) urlSpoo.openConnection();
                //TODO: Changing key values
                urlConnection.setRequestProperty("KEY","KEY");


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

            if (response == null) {
                response = "THERE WAS AN ERROR";
                Snackbar.make(responseView, "Network connectivity unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            } else if (response.toString().trim().equals("[]") || response.toString().trim().equals("")) {
                Snackbar.make(responseView, "No recipes for these ingredients", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                // responseView.setText(response);
                Intent myIntent1 = new Intent(MainActivity.this, ResultsActivity.class);
                myIntent1.putExtra("mytext", response);
                startActivity(myIntent1);
                responseView.setText(null);
                ingredients = "";
            }
            //  check this.exception
            //  do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }


    }

    class RetrieveIngredientTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d("Tect", responseView.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String ingredient = responseView.getText().toString();

            // Do some validation here about String ingredient

            try {
                URL urlIngredientRetriver = new URL(urlIngredientDetais);
                HttpURLConnection urlConnection = (HttpURLConnection) urlIngredientRetriver.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                //TODO: Changing key values
                urlConnection.setRequestProperty("KEY","KEY");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("ingredientList", currentIngredient)
                        .appendQueryParameter("servings", "1");
                String query = builder.build().getEncodedQuery();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

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
                    Log.d("DISCONNECT", "");
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                response = "THERE WAS AN ERROR";
                Snackbar.make(responseView, "Network connectivity unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                // responseView.setText(response);
                   /* Intent myIntent1 = new Intent(MainActivity.this, ResultsActivity.class);
                    myIntent1.putExtra("mytext", response);
                    startActivity(myIntent1);
                    responseView.setText(null);
                    ingredients = "";*/
                responseJSON = response;
                Log.d("RESPONSE", response);
                Log.d("RESPONSEJSON", responseJSON);
                JSONArray object;
                try {
                    object = (JSONArray) new JSONTokener(responseJSON).nextValue();
                    //currentImageUrl = object.get(0).toString();
                    currentImageUrl = object.getJSONObject(0).get("image").toString();
                    Log.d("IMAGEURL", currentImageUrl);
                    if((object.getJSONObject(0).has("image"))) {
                        Log.d("IF","NOT Image");
                        currentImageUrl = object.getJSONObject(0).get("image").toString();
                    }
                    else{
                        currentImageUrl = "https://spoonacular.com/cdn/ingredients_100x100/appe.jpg1";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fillArray(singleIngredient.substring(0,1).toUpperCase() + singleIngredient.substring(1));
                    currentImageUrl = "https://spoonacular.com/cdn/ingredients_100x100/appe.jpg1";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editText.getText().clear();
                Log.d("INGREDIENTS ,", ingredients);
            }
        }
    }
    private void fillArray(String ingredient) throws JSONException {
        List<Card> cards = new ArrayList<>();
        cards.add(generateNewCard(ingredient));
        mListView.getAdapter().addAll(cards);
    }

    private Card generateNewCard(final String ingredient) {
        mListView.smoothScrollToPosition(0);
        Log.d("IMGURL", currentImageUrl );

        if(!chefacileDb.findIngredient(ingredient.trim().replaceAll(",",""))) {

            CardProvider provider = new Card.Builder(this)
                    .setTag("BASIC_IMAGE_BUTTON_CARD")
                    //.setDismissible()
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.card_layout)
                    .setTitle(ingredient)
                    .setDrawable(currentImageUrl)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {

                                    Toast.makeText(mContext, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                                    ingredients = ingredients.replaceAll("," + ingredient.trim().toLowerCase() + ",", ",");
                                    Log.d("ingredients_card", ingredients);
                                    card.setDismissible(true);
                                    card.dismiss();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(this)
                            .setText("Save")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {

                                    boolean isInserted = chefacileDb.insertDataIngredient(ingredient);

                                    if (isInserted == true)
                                        Toast.makeText(mContext, "Ingredient is added to favorited", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(mContext, "Ingredient NOT ADDED!", Toast.LENGTH_LONG).show();
                                    //card.dismiss();
                                }
                            }));

            return provider.endConfig().build();
        }

        else {
            CardProvider provider = new Card.Builder(this)
                    .setTag("BASIC_IMAGE_BUTTON_CARD")
                    //.setDismissible()
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.card_layout)
                    .setTitle(ingredient)
                    .setDrawable(currentImageUrl)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {

                                    Toast.makeText(mContext, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                                    ingredients = ingredients.replaceAll("," + ingredient.trim().toLowerCase() + ",", ",");
                                    Log.d("ingredients_card", ingredients);
                                    card.setDismissible(true);
                                    card.dismiss();
                                }
                            }));

            return provider.endConfig().build();
        }

    }

    private void setBackground(ImageView iv){
        iv.setImageResource(R.drawable.egg);
    }
}