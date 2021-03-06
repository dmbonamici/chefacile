package it.chefacile.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.getbase.floatingactionbutton.FloatingActionButton.*;


public class MainActivity extends AppCompatActivity {

    private DatabaseHelper chefacileDb;
    private Context mContext;
    private MaterialListView mListView;
    private EditText editText;
    private TextView responseView;
    private MaterialAnimatedSwitch materialAnimatedSwitch;
    private ImageView iv;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private ImageButton TutorialButton;
    //private ImageButton FilterButton;
    private ImageButton AddButton;
    private ImageButton Show;
    private ImageButton Show2;
    private String ingredients = ",";
    private ArrayAdapter<String> adapter;
    private String currentIngredient = "";
    private String currentImageUrl = "";
    private String responseJSON = "";
    private String singleIngredient;
    private boolean[] cuisineBool = new boolean[24];
    private final String[] cuisineItems = {"african", "chinese", "japanese", "korean", "vietnamese", "thai", "indian", "british",
            "irish", "french", "italian", "mexican", "spanish", "middle Eastern", "jewish",
            "american", "cajun", "southern", "greek", "german", "nordic", "eastern European", "caribbean", "latin American"};
    private final String[] intolItems = {"Dairy", "Egg", "Gluten", "Peanut", "Sesame", "Seafood", "Shellfish", "Soy", "Sulfite", "Tree Nut", "Wheat"};
    private int checkedDietBool = 0;
    private boolean[] intolBool = new boolean[11];
    private final String[] dietItems = {"None", "Pescetarian", "Lacto Vegetarian", "Ovo Vegeterian", "Vegan", "Paleo", "Primal", "Vegetarian"};
    private String cuisineString = ",";
    private String dietString = ",";
    private String intolString = ",";
    private int clicks = 0;
    private ImageButton buttoncuisine;
    private ImageButton buttondiet;
    private ImageButton buttonintol;
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private int ranking = 1;
    private String urlFindByIngredient = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?ingredients=";
    private String urlIngredientDetais = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/parseIngredients";
    private String urlSearchComplex = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex";
    private Map<String, Integer> mapIngredients;
    private List<String> listIngredientsPREF;
    private String[] sugg;
    private String[] suggOccurrences;
    private List<String> searchedIngredients = new ArrayList<String>();




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this;
        chefacileDb = new DatabaseHelper(this);
        // FilterButton = (ImageButton) findViewById(R.id.buttonfilter);
        TutorialButton = (ImageButton) findViewById(R.id.button);
      //  AddButton = (ImageButton) findViewById(R.id.button2);
        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //Show = (ImageButton) findViewById(R.id.buttonShow);
        //Show2 = (ImageButton) findViewById(R.id.buttonShow2);
        materialAnimatedSwitch = (MaterialAnimatedSwitch) findViewById(R.id.pin);
        //buttoncuisine = (ImageButton) findViewById(R.id.btn_cuisine);
        //buttondiet = (ImageButton) findViewById(R.id.btn_diet);
        //buttonintol = (ImageButton) findViewById(R.id.btn_intoll);
        //Mostra = (Button) findViewById(R.id.btn_mostra);

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(1000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(5); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);



        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.logo));

        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setPosition(com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.POSITION_RIGHT_CENTER
                )
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView dietIcon = new ImageView(this);
        dietIcon.setImageDrawable(getResources().getDrawable(R.drawable.diet));
        SubActionButton button1 = itemBuilder.setContentView(dietIcon).build();
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSingleChoiceDialogDiet(v);
            }
        });

        ImageView intolIcon = new ImageView(this);
        intolIcon.setImageDrawable(getResources().getDrawable(R.drawable.intoll));
        SubActionButton button2 = itemBuilder.setContentView(intolIcon).build();
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMultiChoiceDialogIntol(v);
            }
        });

        ImageView cuisineIcon = new ImageView(this);
        cuisineIcon.setImageDrawable(getResources().getDrawable(R.drawable.cook12));
        SubActionButton button3 = itemBuilder.setContentView(cuisineIcon).build();
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMultiChoiceDialogCuisine(v);
            }
        });

        ImageView favouriteIcon = new ImageView(this);
        favouriteIcon.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
        SubActionButton button4 = itemBuilder.setContentView(favouriteIcon).build();
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSimpleListDialogFav(v);
            }
        });

        ImageView wandIcon = new ImageView(this);
        wandIcon.setImageDrawable(getResources().getDrawable(R.drawable.wand));
        SubActionButton button5 = itemBuilder.setContentView(wandIcon).build();
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sugg = getSuggestion();
                suggOccurrences = getCount();
                showSimpleListDialogSuggestions(v);
            }
        });

        final FloatingActionButton actionABC = (FloatingActionButton) findViewById(R.id.action_abc);

        actionABC.bringToFront();
        actionABC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredients.length() < 2) {
                    Snackbar.make(responseView, "Insert at least one ingredient", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    new RetrieveFeedTask().execute();
                }
            }

            // Snackbar.make(view, "Non disponibile, mangia l'aria", Snackbar.LENGTH_LONG)
            //         .setAction("Action", null).show();

        });






        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .setStartAngle(90)
                .setEndAngle(270)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .addSubActionView(button5)
                .attachTo(actionButton)
                .build();

        startDatabase(chefacileDb);


        for (int j = 0; j < cuisineItems.length; j++) {
            cuisineItems[j] = cuisineItems[j].substring(0, 1).toUpperCase() + cuisineItems[j].substring(1);
        }
        Arrays.sort(cuisineItems);

        for (int i = 0; i < 24; i++) {
            cuisineBool[i] = false;
        }

        Arrays.sort(intolItems);

        for (int i = 0; i < 11; i++) {
            intolBool[i] = false;
        }

        mListView = (MaterialListView) findViewById(R.id.material_listview);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        TutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IntroScreenActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });


        iv = (ImageView) findViewById(R.id.imageView);
        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clicks++;
                Log.d("CLICKS", String.valueOf(clicks));
                if (clicks == 15) {
                    Log.d("IMAGE SHOWN", "mai vero");
                    setBackground(iv);
                }
            }
        });

        materialAnimatedSwitch.setOnCheckedChangeListener(
                new MaterialAnimatedSwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isChecked) {
                        if (isChecked == true) {
                            ranking = 2;
                            Toast.makeText(getApplicationContext(), "Minimize missing ingredients", Toast.LENGTH_SHORT).show();
                        } else {
                            ranking = 1;
                            Toast.makeText(getApplicationContext(), "Maximize used ingredients", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Ranking", String.valueOf(ranking));
                    }
                });


        final CharSequence[] items = {"Maximize used ingredients", "Minimize missing ingredients"};



        editText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (!(editText.getText().toString().trim().equals(""))) {

                                String input;
                                String s1 = editText.getText().toString().substring(0, 1).toUpperCase();
                                String s2 = editText.getText().toString().substring(1);
                                input = s1 + s2;

                                Log.d("INPUT: ", input);
                                searchedIngredients.add(input);
                                Log.d("SEARCHED INGR LIST",searchedIngredients.toString());


                                if (!chefacileDb.findIngredientPREF(input)) {
                                    if (chefacileDb.findIngredient(input)) {
                                        chefacileDb.updateCount(input);
                                        mapIngredients = chefacileDb.getDataInMapIngredient();
                                        Map<String, Integer> map2;
                                        map2 = sortByValue(mapIngredients);
                                        Log.d("MAPPACOUNT: ", map2.toString());

                                    } else {
                                        if (chefacileDb.occursExceeded()) {
                                            //chefacileDb.deleteMinimum(input);
                                            // chefacileDb.insertDataIngredient(input);
                                            mapIngredients = chefacileDb.getDataInMapIngredient();
                                            Map<String, Integer> map3;
                                            map3 = sortByValue(mapIngredients);
                                            Log.d("MAPPAINGREDIENTE: ", map3.toString());

                                        } else {
                                            chefacileDb.insertDataIngredient(input);
                                            mapIngredients = chefacileDb.getDataInMapIngredient();
                                            Map<String, Integer> map3;
                                            map3 = sortByValue(mapIngredients);
                                            Log.d("MAPPAINGREDIENTE: ", map3.toString());
                                        }
                                    }
                                }
                            }

                            if (editText.getText().toString().trim().equals("")) {
                               ingredients += editText.getText().toString().trim() + "";
                                editText.getText().clear();

                            } else {
                                ingredients += editText.getText().toString().replaceAll(" ", "+").trim().toLowerCase() + ",";
                                singleIngredient = editText.getText().toString().trim().toLowerCase();
                                currentIngredient = singleIngredient;
                                new RetrieveIngredientTask().execute();

                                //adapter.add(singleIngredient.substring(0,1).toUpperCase() + singleIngredient.substring(1));
                            }

                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                            in.hideSoftInputFromWindow(editText
                                            .getApplicationWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                            actionABC.startAnimation(animation);

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });



        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d("Tect", responseView.getText().toString());
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Consulting the chefs", true);
            //progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String ingredient = responseView.getText().toString();

            // Do some validation here about String ingredient

            try {

                // URL urlSpoo = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?cuisine=american&includeIngredients=apples%2Cflour%2Csugar%2Cchicken&limitLicense=false&number=5&offset=0&query=apples%2Cflour%2Csugar&ranking=1");
                URL urlSpoo = new URL(urlSearchComplex + "?cuisine=" + cuisineString + "&diet=" + dietString + "&includeIngredients=" + ingredients + "&intolerances=" + intolString + "&limitLicense=true" + "&query=" + "recipe" + "&number=20&ranking=" + String.valueOf(ranking));
                Log.d("URL SPOO", urlSearchComplex + "?cuisine=" + cuisineString + "&diet=" + dietString + "&includeIngredients=" + ingredients + "&intolerances=" + intolString + "&limitLicense=true" + "&query=" + "recipe" + "&number=20&ranking=" + String.valueOf(ranking));
                HttpURLConnection urlConnection = (HttpURLConnection) urlSpoo.openConnection();
                //TODO: Changing key values
                urlConnection.setRequestProperty("X-Mashape-Key", "KEY");

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
                progressDialog.dismiss();
                //progressBar.setVisibility(View.GONE);
            } else if (response.toString().trim().equals("[]") || response.toString().trim().equals("")) {
                showErrorMessage();
                progressDialog.dismiss();

            } else if (!response.toString().contains("usedIngredientCount")) {
                showErrorMessage();
                progressDialog.dismiss();

            } else {
                progressDialog.dismiss();
                //progressBar.setVisibility(View.GONE);
                // responseView.setText(response);
                Intent myIntent1 = new Intent(MainActivity.this, ResultsActivity.class);
                myIntent1.putExtra("mytext", response);
                myIntent1.putExtra("searchedIngredients", searchedIngredients.toString());
                startActivity(myIntent1);
                responseView.setText(null);
                ingredients = "";

            }

        }




    }

    class RetrieveIngredientTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d("Tect", responseView.getText().toString());
            String s = SentenceGenerator.generateTip();
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading", s, true);
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
                urlConnection.setRequestProperty("X-Mashape-Key", "KEY");
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
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
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
                    currentImageUrl = "https://spoonacular.com/cdn/ingredients_100x100/appe.jpg1";
                    Log.d("IMAGEURL", currentImageUrl);
                    if ((object.getJSONObject(0).has("image"))) {
                        Log.d("IF", "NOT Image");
                        currentImageUrl = object.getJSONObject(0).get("image").toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fillArray(singleIngredient.substring(0, 1).toUpperCase() + singleIngredient.substring(1));
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
        Log.d("IMGURL", currentImageUrl);
        //Log.d("VERIFICA:",deleteCommas(",,,Ciao,"));

        if (!chefacileDb.findIngredientPREF(ingredient.trim().replaceAll(",", ""))) {

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
                                    searchedIngredients.remove(ingredient);
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
                                    final TextView tv = (TextView) findViewById(R.id.right_text_button);
                                    boolean finalControl = true;

                                    chefacileDb.insertDataIngredientPREF(ingredient);

                                    if (chefacileDb.getNumberIngredients() > 3) {
                                        chefacileDb.deleteDataIngredient(ingredient);
                                        mapIngredients = chefacileDb.getDataInMapIngredient();
                                        mapIngredients = sortByValue(mapIngredients);
                                        Log.d("MapVerifica:", mapIngredients.toString());

                                    } else if(chefacileDb.getNumberIngredients() == 3) {
                                        String n = addRandomIngredient();

                                        if (!n.equals(ingredient)) {

                                            chefacileDb.deleteDataIngredient(ingredient);
                                            chefacileDb.insertDataIngredient(n);
                                            mapIngredients = chefacileDb.getDataInMapIngredient();
                                            mapIngredients = sortByValue(mapIngredients);
                                            Log.d("MapVerificaNOWhile:", mapIngredients.toString());

                                        } else {

                                            while (n.equals(ingredient) || chefacileDb.findIngredient(n)) {
                                                n = addRandomIngredient();
                                            }

                                            chefacileDb.deleteDataIngredient(ingredient);
                                            chefacileDb.insertDataIngredient(n);
                                            mapIngredients = chefacileDb.getDataInMapIngredient();
                                            mapIngredients = sortByValue(mapIngredients);
                                            Log.d("MapVerificaWhile:", mapIngredients.toString());

                                        }
                                    }

                                    tv.setText("Unsave");
                                    checkButtonSave(tv, finalControl, ingredient);
                                }
                            }));

            return provider.endConfig().build();


        } else {
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
                            .setText("Unsave")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    final TextView tv = (TextView) findViewById(R.id.right_text_button);
                                    boolean finalControl = false;

                                    chefacileDb.deleteDataIngredientPREF(ingredient);
                                    chefacileDb.insertDataIngredient(ingredient);
                                    tv.setText("Save");

                                    checkButtonSave(tv, finalControl, ingredient);
                                }
                            }));

            return provider.endConfig().build();


        }
    }

    private void setBackground(ImageView iv) {
        iv.setImageResource(R.drawable.egg);
    }

    /*private void checkFieldsForEmptyValues() {
        ImageButton b = (ImageButton) findViewById(R.id.button2);

        String s1 = editText.getText().toString();

        if (s1.length() > 0) {
            b.setImageResource(R.drawable.addplusg);
            b.setEnabled(true);
        } else {
            b.setImageResource(R.drawable.addpluslg);
            b.setEnabled(false);
        }

    }*/


    private void showMultiChoiceDialogCuisine(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Select cuisines");

        builder.setMultiChoiceItems(cuisineItems, cuisineBool, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                //Toast.makeText(getApplicationContext(),"You clicked "+ cuisineItems[i]+" "+b,Toast.LENGTH_SHORT).show();
                cuisineBool[i] = b;
                if (b) {
                    String s = cuisineItems[i].trim().replaceAll(" ", "+");
                    cuisineString += s + ",";
                    Log.d("CUISINE STRING", cuisineString);
                } else {
                    cuisineString = cuisineString.replaceAll(cuisineItems[i] + ",", ",");
                }
            }
        });

        builder.setPositiveButton("Okay", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void showMultiChoiceDialogIntol(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Select intolerances");

        builder.setMultiChoiceItems(intolItems, intolBool, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                //Toast.makeText(getApplicationContext(),"You clicked "+ cuisineItems[i]+" "+b,Toast.LENGTH_SHORT).show();
                intolBool[i] = b;
                if (b) {
                    String s = intolItems[i].trim().replaceAll(" ", "+");
                    intolString += s + ",";
                    Log.d("Intol STRING", intolString);
                } else {
                    intolString = intolString.replaceAll(intolItems[i] + ",", ",");
                }
            }
        });

        builder.setPositiveButton("Okay", null);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void showSingleChoiceDialogDiet(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Select diet");

        builder.setSingleChoiceItems(dietItems, checkedDietBool, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0)
                    dietString = "";

                checkedDietBool = i;
                dietString = dietItems[i].trim().replaceAll(" ", "+");
                Log.d("Diet", dietString);
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }




    private void showErrorMessage() {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Error");

       // builder.setMessage("No recipe has been found "+"\n"+"with these parameters");

        if((ingredients.length()!= 0 && !occursOnlyCommas(ingredients)) && (!dietString.replaceAll(",","").equals("None") && !dietString.replaceAll(",","").equals("")) && (intolString.length()!=0 && !occursOnlyCommas(intolString))) {

            builder.setMessage( "No recipe has been found with... \n\n" +
                    "Ingredients: " + ingredients.substring( 1, ingredients.length() - 1 ).toString() + "\n" +
                    "Diet: " + dietString.toString().replaceAll(",","") + "\n" +
                    "Intol: " + intolString.substring( 1, intolString.length() - 1 ).toString() + "\n\n" );
        }

        else if((ingredients.length() == 0 || occursOnlyCommas(ingredients)) && (!dietString.replaceAll(",","").equals("None") && !dietString.replaceAll(",","").equals("")) && (!occursOnlyCommas(intolString) && intolString.length()!=0)) {

            builder.setMessage( "No recipe has been found with... \n\n" +
                    "Diet: " + dietString.toString().replaceAll(",","") + "\n" +
                    "Intol: " + intolString.substring( 1, intolString.length() - 1 ).toString() + "\n\n" );
        }



        else if((ingredients.length()!= 0 && ingredients.length()!=1 && !occursOnlyCommas(ingredients)) && (!intolString.replaceAll(",","").equals("None") && !intolString.replaceAll(",","").equals("")) && (dietString.length()==0 || occursOnlyCommas(dietString))) {


            builder.setMessage( "No recipe has been found with... \n\n" +
                    "Ingredients: " + ingredients.substring( 1, ingredients.length() - 1 ).toString() + "\n" +
                    "Intolerances: " + intolString.substring( 1, intolString.length() - 1 ).toString().replaceAll("\\+", " ") + "\n\n" );
        }

        else if((ingredients.length()!= 0 && ingredients.length()!=1 && !occursOnlyCommas(ingredients)) && (!dietString.replaceAll(",","").equals("None") && !dietString.replaceAll(",","").equals("")) && (intolString.length()==0 || occursOnlyCommas(intolString))) {

            builder.setMessage( "No recipe has been found with... \n\n" +
                    "Ingredients: " + ingredients.substring( 1, ingredients.length() - 1 ).toString() + "\n" +
                    "Diet: " + dietString.toString().replaceAll(",","").replaceAll("\\+", " ") + "\n\n");
        }

        else{
            builder.setMessage( "No recipe has been found with... \n\n" +
                    "Ingredients: " + ingredients.substring(1, ingredients.length() -1).toString().replaceAll("\\+", " ") + "\n\n");
        }

        builder.setPositiveButton("Okay", null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showSimpleListDialogFav(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Favourite ingredients");


        final String[] items = chefacileDb.getDataInListIngredientPREF().toArray(new String[chefacileDb.getDataInListIngredientPREF().size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currentIngredient = items[i];
                singleIngredient = currentIngredient;
                new RetrieveIngredientTask().execute();
                ingredients += singleIngredient.toLowerCase().trim() + ",";
                searchedIngredients.add(singleIngredient);
                Log.d("SEARCHED ING", searchedIngredients.toString());
                ingredients = ingredients.replaceAll(" ", "+");
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSimpleListDialogSuggestions(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Maybe you already have");
        if (sugg != null || sugg.length != 0) {
            String[] res = new String[sugg.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = sugg[i];
            }

            builder.setItems(res, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    currentIngredient = sugg[i];
                    singleIngredient = currentIngredient;
                    chefacileDb.updateCount(singleIngredient);
                    new RetrieveIngredientTask().execute();
                    ingredients += singleIngredient.toLowerCase().trim() + ",";
                    searchedIngredients.add(singleIngredient);
                    Log.d("SEARCHED ING",searchedIngredients.toString());
                    ingredients = ingredients.replaceAll(" ", "+");


                }
            });
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public Map<String, Integer> sortByValue(Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return -(o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void checkButtonSave(final TextView tv, boolean checked, final String ingredient) {
        boolean control = checked;
        if (control) {
            listIngredientsPREF = chefacileDb.getDataInListIngredientPREF();
           /* if (chefacileDb.findIngredient(ingredient))
                chefacileDb.decrementedId(ingredient);*/

            if (chefacileDb.getNumberIngredients() > 3) {
                chefacileDb.deleteDataIngredient(ingredient);
                mapIngredients = chefacileDb.getDataInMapIngredient();
                mapIngredients = sortByValue(mapIngredients);
                Log.d("MapVerifica:", mapIngredients.toString());

            } else if(chefacileDb.getNumberIngredients() == 3) {
                String n = addRandomIngredient();

                if (!n.equals(ingredient)) {

                    chefacileDb.deleteDataIngredient(ingredient);
                    chefacileDb.insertDataIngredient(n);
                    mapIngredients = chefacileDb.getDataInMapIngredient();
                    mapIngredients = sortByValue(mapIngredients);
                    Log.d("MapVerificaNOWhile:", mapIngredients.toString());

                } else {

                    while (n.equals(ingredient) || chefacileDb.findIngredient(n)) {
                        n = addRandomIngredient();
                    }

                    chefacileDb.deleteDataIngredient(ingredient);
                    chefacileDb.insertDataIngredient(n);
                    mapIngredients = chefacileDb.getDataInMapIngredient();
                    mapIngredients = sortByValue(mapIngredients);
                    Log.d("MapVerificaWhile:", mapIngredients.toString());

                }
            }

            Log.d("LISTA PREFERITI: ", listIngredientsPREF.toString());
            Log.d("DOPO ELIM PER PREF:", mapIngredients.toString());
            tv.setText("Unsave");
            Toast.makeText(mContext, "Ingredient added to favourites", Toast.LENGTH_LONG).show();
            control = false;
            final boolean finalControl = control;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chefacileDb.deleteDataIngredientPREF(ingredient);
                    chefacileDb.insertDataIngredient(ingredient);
                    tv.setText("Save");
                    checkButtonSave(tv, finalControl, ingredient);
                }
            });

        } else {
            tv.setText("Save");
            Toast.makeText(mContext, "Ingredient removed from favourites", Toast.LENGTH_LONG).show();
            control = true;
            final boolean finalControl = control;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chefacileDb.insertDataIngredientPREF(ingredient);
                    chefacileDb.deleteDataIngredient(ingredient);
                    tv.setText("Unsave");
                    checkButtonSave(tv, finalControl, ingredient);
                }
            });
        }
    }

    public String[] getSuggestion() {
        int i = 0;
        Map<String, Integer> map;
        map = chefacileDb.getDataInMapIngredient();
        map = sortByValue(map);
        Log.d("MAPPA METODO:", map.toString());

        String[] array = new String[map.size()];

        if (map != null || !map.isEmpty()) {

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                array[i] = entry.getKey();
                i++;

            }

            String[] res = new String[3];

            for (int j = 0; j < 3; j++) {
                res[j] = array[j];
            }

            return res;

        } else return null;

    }

    public String[] getCount() {
        int i = 0;
        Map<String, Integer> map;
        map = chefacileDb.getDataInMapIngredient();
        map = sortByValue(map);
        Log.d("MAPPA METODO:", map.toString());

        String[] array = new String[map.size()];

        if (map != null || !map.isEmpty()) {

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                array[i] = String.valueOf(entry.getValue());
                i++;

            }

            String[] res = new String[3];

            for (int j = 0; j < 3; j++) {
                res[j] = array[j];
            }

            return res;

        } else return null;

    }


    public String addRandomIngredient() {
        int n = (int) (Math.random() * 15);
        switch (n) {
            case 0:
                return "Chicken";
            case 1:
                return "Apple";
            case 2:
                return "Milk";
            case 3:
                return "Tomato";
            case 4:
                return "Potato";
            case 5:
                return "Pepperoni";
            case 6:
                return "Wine";
            case 7:
                return "Orange";
            case 8:
                return "Lemon";
            case 9:
                return "Mozzarella";
            case 10:
                return "Bacon";
            case 11:
                return "Chili";
            case 12:
                return "Bbq";
            case 13:
                return "Ketchup";
            case 14:
                return "Mint";
            case 15:
                return "Pepper";
            default:
                return "Water";
        }
    }

    public void startDatabase(DatabaseHelper db) {
        Cursor res = chefacileDb.getAllDataIngredients();

        if (res.getCount() == 0) {

            db.insertDataIngredient( "Pasta" );
            db.insertDataIngredient( "Eggs" );
            db.insertDataIngredient( "Oil" );

            if (db.findIngredientPREF( "Pasta" )) {
                String n1 = addRandomIngredient();
                db.deleteDataIngredient( "Pasta" );
                db.insertDataIngredient( n1 );
            }

            if (db.findIngredientPREF( "Eggs" )) {
                String n2 = addRandomIngredient();
                db.deleteDataIngredient( "Eggs" );
                db.insertDataIngredient( n2 );
            }

            if (db.findIngredientPREF( "Oil" )) {
                String n3 = addRandomIngredient();
                db.deleteDataIngredient( "Oil" );
                db.insertDataIngredient( n3 );
            }

            mapIngredients = db.getDataInMapIngredient();
        }
    }


    public boolean occursOnlyCommas(String s) {
        char[] charArray = s.toCharArray();
        boolean b = true;

        for (char c : charArray) {
            if (c == ',') {
                b = true;

            } else return false;

        }
        return b;
    }


    public String deleteCommas(String s) {
        char[] charArray = s.toCharArray();
        boolean b = true;
        String test="";

        for (char c : charArray) {
            if (c == ',') {
                c = ' ';
            } else
                test= String.copyValueOf(charArray);
                return test;

        }
        test = String.copyValueOf(charArray);
        return test;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.logo).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }


}

