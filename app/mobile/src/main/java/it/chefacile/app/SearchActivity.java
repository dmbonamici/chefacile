package it.chefacile.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchBox.SearchListener;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
	Boolean isSearch;
	private SearchBox search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
		for(int x = 0; x < 10; x++){
			SearchResult option = new SearchResult("Ingredient " + Integer.toString(x), getResources().getDrawable(R.drawable.ic_history));
			search.addSearchable(option);
		}

		search.setSearchListener(new SearchListener(){

			@Override
			public void onSearchOpened() {
				//Use this to tint the screen
			}

			@Override
			public void onSearchClosed() {
				//Use this to un-tint the screen
			}

			@Override
			public void onSearchTermChanged(String term) {
				//React to the search term changing
				//Called after it has updated results
			}

			@Override
			public void onSearch(String searchTerm) {
				Toast.makeText(SearchActivity.this, searchTerm +" Searched", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResultClick(SearchResult result) {
				//React to a result being clicked
			}

			@Override
			public void onSearchCleared() {
				//Called when the clear button is clicked
			}
			
		});

		final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_ab);
		actionA.setSize(FloatingActionButton.SIZE_NORMAL);
		actionA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				 Snackbar.make(view, "Non disponibile", Snackbar.LENGTH_LONG)
				         .setAction("Action", null).show();

			}
		});

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1234 && resultCode == RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			search.populateEditText(matches.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/* Start new activies
	public void reveal(View v){
		startActivity(new Intent(this, newActivity.class));
	}*/

	
}
