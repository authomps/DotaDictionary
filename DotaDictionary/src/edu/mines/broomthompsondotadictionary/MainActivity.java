/**
 * @author Austin Thompson, Alex Broom
 * Description:
 * 		This app contains a list of heroes in Dota 2, and corresponding screens with more detailed information. The list of heroes and information is retrieved 
 * 		via http request from a published google doc spreadsheet. The information is then stored in a SQLite database and retrieved when the user selects a hero 
 * 		from the list, using the hero's name.
 * 		The database will allow us to easily filter the displayed list of heroes. We also plan to add more information to the online document and better formatting 
 * 		for the detailed hero information page. 
 * 
 * Date: 10/01/13
 * Documentation: 
 * 	http://www.vogella.com/articles/AndroidSQLite/article.html: His concept of a DAO was used by us, and much of his code was modified and added to for our project
 * 	
 * 	http://developer.android.com/training/basics/fragments/index.html: The fragments tutorial on Android formed the basic structure of our app: One fragment for browsing 
 *  a list, another for displaying more information when an element from that list is selected.
 * 	  
 */
package edu.mines.broomthompsondotadictionary;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.dotadictionary.R;

import edu.mines.broomthompsondotadictionary.FilterFragment.OnFilterSelectedListener;
import edu.mines.broomthompsondotadictionary.NamesFragment.OnHeroSelectedListener;

/**
 * Class: MainActivity
 * Description: The main container for the fragments in our app.
 * 		Performs http request and data parsing.
 *
 */
public class MainActivity extends FragmentActivity implements
		OnHeroSelectedListener, OnFilterSelectedListener {

	String url_name;
	String data;
	static String[] names;
	static HeroesDataSource source;
	static ArrayAdapter<Hero> adapter;
	NamesFragment n_frag;
	FilterFragment f_frag;

	// Setter for data
	private void setData(String data) {
		this.data = data;
	}

	/**
	 * Description: Creates the activity 
	 * @param: Bundle containing old instance if resumed
	 * @return: void
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Check if previous state is being restored
		if (savedInstanceState != null) {
			return;
		}

		adapter = refresh();

		// TODO comment this
		n_frag = new NamesFragment();
		n_frag.setArguments(getIntent().getExtras());
		
		f_frag = new FilterFragment();
		f_frag.setArguments(getIntent().getExtras());

		FragmentManager frag_man = getSupportFragmentManager();
		FragmentTransaction frag_trans = frag_man.beginTransaction();
		frag_trans.add(R.id.fragment_container_bottom, f_frag);
		frag_trans.add(R.id.fragment_container_top, n_frag).commit();
	}

	/**
	 * Description: Contains code for getting http and parsing data 
	 * @param: void
	 * @return: ArrayAdapter containing an array of Hero class that will populate the ListFragment NamesFragment 
	 */
	private ArrayAdapter<Hero> refresh() {
		// Initialize url and data
		url_name = "https://docs.google.com/spreadsheet/pub?key=0ApYAGkG5V1-NdEdXLTJMdjR2MEdKRUJ1MWs0alRkRVE&single=true&gid=2&output=csv";
		data = ""; // TODO maybe create empty string in R
		// Grab data
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
					public void run() {
						try {
							urlConnection.getInputStream();
							BufferedInputStream in = new BufferedInputStream(
									urlConnection.getInputStream());
							setData(readStream(in));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							setData("IO error thread");
						}
					}
				});
				trd.start();
				// Wait for thread to finish
				// TODO Output something to user?
				try {
					trd.join();
				} catch (InterruptedException e) {
					Toast.makeText(MainActivity.this, "trd_join",
							Toast.LENGTH_LONG).show();
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
		source = new HeroesDataSource(this);
		if (doesDatabaseExist(this, source.getName())) {
			source.delete(this);
		}

		source.open();

		List<Hero> list = source.getAllHeroes();
		Log.d("db", "1");
		ArrayAdapter<Hero> adapt = new ArrayAdapter<Hero>(this,
				android.R.layout.simple_list_item_1, list);
		String[] heroes = data.split("\n");
		String[] test;
		for (int i = 1; i < heroes.length; i++) {
			// Init database
			test = heroes[i].split(",", -1);
			Log.d("length", Integer.toString(test.length));
			for (int j = 0; j < test.length; j++) {
				Log.d("split", test[j]);
			}
			Hero hero = source.addHero(test);
			adapt.add(hero);
		}
		return adapt;
	}

	/**
	 * Description: Reads input stream to string 
	 * @param: InputStream is: data recieved from http request
	 * @return: A string of the data recieved.
	 */
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

	/**
	 * Description: Create options menu 
	 * @param: Menu: the menu to be created
	 * @return: boolean.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Description: Handles selection of option menu items
	 * @param: MenuItem: the option that has been selected
	 * @return: boolean.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_quit:
			finish();
			return true;
		case R.id.menu_refresh:
			adapter = refresh();
			n_frag = new NamesFragment();
			FragmentManager frag_man = getSupportFragmentManager();
			FragmentTransaction frag_trans = frag_man.beginTransaction();
			frag_trans.replace(R.id.fragment_container_bottom, n_frag).commit();
//			NamesFragment n_frag = new NamesFragment();
//			n_frag.setArguments(getIntent().getExtras());
//			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, n_frag);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Description: Handles selected item in NameFragment
	 * @param: Name: string containing name of hero that has been selected
	 * @return: void.
	 */
	@Override
	public void onHeroSelected(String name) {
		HeroFragment h_frag = new HeroFragment();
		Bundle args = new Bundle();
		args.putString(HeroFragment.ARG_ID, name);
		h_frag.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.hide(f_frag);
		transaction.hide(n_frag);

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can navigate
		// back
		transaction.replace(R.id.fragment_container_overlay, h_frag);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	/**
	 * Description: Overrided function handling destruction of activity. Destorys database
	 * @param: void
	 * @return: void.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (doesDatabaseExist(this, source.getName())) {
			source.delete(this);
		}
	}

	/**
	 * Description: Helper function for determining if a database exists
	 * @param: ContextWrapper: context to be checked for a database
	 * @return: boolean: true if database exists, false otherwise.
	 */
	private static boolean doesDatabaseExist(ContextWrapper context,
			String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}
	
	@Override
	public void onBackPressed() {
		FragmentManager frag_man = getSupportFragmentManager();
		frag_man.popBackStack();
	}

	@Override
	public void onFilterSelected(String[] attrs, String search) {
		// TODO Auto-generated method stub
		List<Hero> list = source.getHeroByQuery(attrs);
		adapter.clear();
		for(Hero h : list) {
			if(search.length() <= h.getName().length()) {
				if(h.getName().substring(0, search.length()).toLowerCase(Locale.getDefault()).contains(search.toLowerCase(Locale.getDefault()))) {
					adapter.add(h);
				}				
			}
		}
		
		n_frag.refreshList();
	}
}