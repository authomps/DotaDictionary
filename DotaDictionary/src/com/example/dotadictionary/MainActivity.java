package com.example.dotadictionary;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dotadictionary.NamesFragment.OnHeroSelectedListener;

public class MainActivity extends FragmentActivity implements OnHeroSelectedListener {

	String url_name;
	String html;
	
	// Setter for html
	private void setHtml(String o) {
		html = o;
	}
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize url and html
		url_name = "https://docs.google.com/spreadsheet/pub?key=0ApYAGkG5V1-NdEdXLTJMdjR2MEdKRUJ1MWs0alRkRVE&single=true&gid=0&output=csv";
//		url_name = "http://www.wikipedia.org/"; // TODO place url string in R
		html = ""; // TODO maybe create empty string in R
		
		// Check if previous state is being restored
		if (savedInstanceState != null) {
            return;
        }
		
		// Grab html code
		URL url;
		final HttpURLConnection urlConnection;
		// TODO clean up this code
		try {
			url = new URL(url_name);
			urlConnection = (HttpURLConnection) url.openConnection();
			// Start new thread to download html
			try {
				Thread trd = new Thread(new Runnable() {
				  @Override
				  public void run(){
						try {
							urlConnection.getInputStream();
						    BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
						    setHtml(readStream(in));
						} catch (IOException e) {
							// TODO Auto-generated catch block
						    setHtml("IO error thread");
						}
				  }
				});
				trd.start();
				// Wait for thread to finish
				// TODO Output something to user?
			    try {
			        trd.join();
			    } catch (InterruptedException e) {
					Toast.makeText(MainActivity.this, "trd_join", Toast.LENGTH_LONG).show();
			    	e.printStackTrace();
			    }
			} catch (Exception e) {
				Toast.makeText(this, "IOstream", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			Toast.makeText(this, "MalformedURL", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "IO", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO output errors with try catches
		Toast.makeText(MainActivity.this, html, Toast.LENGTH_LONG).show();
		Log.d("MainActivity",html); // TODO remove
		
		// TODO comment this
		NamesFragment n_frag = new NamesFragment();
		n_frag.setArguments(getIntent().getExtras());
		
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, n_frag).commit();
	}

	// Reads input stream to string
	private String readStream(InputStream is) {
	    try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			// TODO output some error
			return "IO_readStream";
		}

	}

	// Create options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_quit:
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// Handles selected item in List //TODO better comment
	@Override
	public void onHeroSelected(int position) {
		// The user selected the headline of an article from the HeadlinesFragment

        HeroFragment newFragment = new HeroFragment();
        Bundle args = new Bundle();
        args.putInt(HeroFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();	
	}
}