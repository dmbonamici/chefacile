package it.chefacile.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

//libreria per implementare lo slide to delete non utilizzabile ora
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class ResultsActivity extends AppCompatActivity {
    private Context mContext;
    private MaterialListView mListView;
    private JSONArray retrievedRecipes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred);

        // Save a reference to the context
        mContext = this;
        Log.d("ON CREATE TEST", "ON CREATE TEST");
        String recipesString = getIntent().getStringExtra("mytext");

        try {
            JSONObject object = (JSONObject) new JSONTokener(recipesString).nextValue();
            retrievedRecipes = object.getJSONArray("matches");
            Log.d("JSONObject TEST", object.toString());
            //String recipeList = object.getString("requestId");
            //int likelihood = object.getInt("likelihood");
            //JSONArray photos = object.getJSONArray("photos");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Bind the MaterialListView to a variable
        mListView = (MaterialListView) findViewById(R.id.material_listview);
        // mListView.setItemAnimator(new SlideInLeftAnimator());
        //  mListView.getItemAnimator().setAddDuration(300);
        //  mListView.getItemAnimator().setRemoveDuration(300);

        // Fill the array withProvider mock content
        try {
            fillArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set the dismiss listener
        /* mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull Card card, int position) {
                // Show a toast
                Toast.makeText(mContext, "You have dismissed a " + card.getTag(), Toast.LENGTH_SHORT).show();
            }
        });*/

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Log.d("CARD_TYPE", "" + card.getTag());
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
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_welcome_card_layout)
                .setTitle("The best for you")
                .setTitleColor(Color.WHITE)
                .setDescription("Find what you like")
                .setDescriptionColor(Color.WHITE)
                .setSubtitle("Here you can find all the recipes that you have saved")
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .addAction(R.id.ok_button, new WelcomeButtonAction(this)
                        .setText("Okay!")
                        .setTextColor(Color.WHITE)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Toast.makeText(mContext, "Let's start!", Toast.LENGTH_SHORT).show();
                                card.dismiss();
                            }
                        }));


        return provider.endConfig().build();


    }

    private Card getRandomCard(final int position) throws JSONException {
        //String title = "Recipe number " + (position + 1);

        //String description = "Lorem ipsum dolor sit amet";

        String title = retrievedRecipes.getJSONObject(position).get("recipeName").toString();
        String rating = "Rating: " + retrievedRecipes.getJSONObject(position).get("rating").toString() + "/5";
        String imageURL = retrievedRecipes.getJSONObject(position).getJSONObject("imageUrlsBySize").get("90").toString();
        String totalTime = retrievedRecipes.getJSONObject(position).get("totalTimeInSeconds").toString();
        final String recipeId = retrievedRecipes.getJSONObject(position).get("id").toString();
        //String smallImageURL = retrievedRecipes.getJSONObject(position).get("smallImageUrls").toString();

             /*   final CardProvider provider1 = new Card.Builder(this)
                        .setTag("BIG_IMAGE_BUTTONS_CARD")
                        //.setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_image_with_buttons_card)
                        .setTitle(title)
                        .setTitleColor(Color.WHITE)
                        .setDescription(description)
                        .setDrawable("http://www.viaggiareusa.it/wp-content/uploads/2015/02/Roasted-thanksgiving-day-turkey-642x336.jpg")
                        .addAction(R.id.left_text_button, new TextViewAction(this)
                                .setText("Open")
                                .setTextResourceColor(R.color.black_button)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        //Log.d("ADDING", "CARD");
                                        Intent myIntent = new Intent(view.getContext(), RecipeActivity.class);
                                        startActivityForResult(myIntent, 0);
                                        //mListView.getAdapter().add(generateNewCard());
                                        Toast.makeText(mContext, "Open", Toast.LENGTH_SHORT).show();
                                    }
                                }))
                        .addAction(R.id.right_text_button, new TextViewAction(this)
                                .setText("right button")
                                .setTextResourceColor(R.color.accent_material_dark)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Toast.makeText(mContext, "You have pressed the right button", Toast.LENGTH_SHORT).show();
                                    }
                                }));

                return provider1.endConfig().build();*/

        final CardProvider provider = new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTON_CARD")
                .withProvider(new CardProvider<>())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle(title)
                .setDescription(rating)
                .setDrawable(imageURL)
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                        requestCreator.fit().centerCrop() ;
                    }
                })
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("Open")
                        .setTextResourceColor(R.color.black_button)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                //Log.d("ADDING", "CARD");
                                Intent myIntent = new Intent(view.getContext(), RecipeActivity.class);
                                startActivityForResult(myIntent, 0);
                                //mListView.getAdapter().add(generateNewCard());
                                Toast.makeText(mContext, "Open", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("right button")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Toast.makeText(mContext, "You have pressed the right button", Toast.LENGTH_SHORT).show();
                            }
                        }));

        return provider.endConfig().build();
    }




    /*private Card generateNewCard() {
        return new Card.Builder(this)
                .setTag("BASIC_IMAGE_BUTTONS_CARD")
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle("I'm new")
                .setDescription("I've been generated on runtime!")
                .setDrawable(R.drawable.chefacile)
                .endConfig()
                .build();
    }*/


}
