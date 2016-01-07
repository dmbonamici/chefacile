package it.chefacile.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SearchActivity extends AppCompatActivity {

	EditText emailText;
	TextView responseView;
	ProgressBar progressBar;

	String API_ID = "c098d0fb";
	String API_KEYS = "90b990034f035755978a14d3bc8a72ec";
	String URLProva = "http://api.yummly.com/v1/api/recipes?_app_id=" + API_ID + "&_app_key=" + API_KEYS + "&allowedIngredient[]=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		responseView = (TextView) findViewById(R.id.responseView);
		emailText = (EditText) findViewById(R.id.emailText);
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
			String email = emailText.getText().toString();
			// Do some validation here

			try {
				URL url = new URL(URLProva + email);
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
			}
			progressBar.setVisibility(View.GONE);
			Log.i("INFO", response);
			responseView.setText(response);
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