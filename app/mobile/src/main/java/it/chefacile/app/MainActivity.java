package it.chefacile.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView responseView;
    ProgressBar progressBar;

    String API_ID = "c098d0fb";
    String API_KEYS = "90b990034f035755978a14d3bc8a72ec";
    String URLProva = "http://api.yummly.com/v1/api/recipes?_app_id=" + API_ID + "&_app_key=" + API_KEYS + "&allowedIngredient[]=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final FloatingActionButton actionABC = (FloatingActionButton) findViewById(R.id.action_abc);
        actionABC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        new RetrieveFeedTask().execute();

            }

            // Snackbar.make(view, "Non disponibile, mangia l'aria", Snackbar.LENGTH_LONG)
            //         .setAction("Action", null).show();

        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            String ingredient = editText.getText().toString();
            // Do some validation here about String ingredient

            try {
                URL url = new URL(URLProva + ingredient);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
                Snackbar.make(responseView, "Network connectivity unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            }
            else {
                progressBar.setVisibility(View.GONE);
                Log.i("INFO", response);
               // responseView.setText(response);

                Intent myIntent1 = new Intent(MainActivity.this, ResultsActivity.class);
                myIntent1.putExtra("mytext", response);
                startActivity(myIntent1);
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



}