/**
 * @author Alexander Broom
 * @author Austin Thompson
 * 
 * Emulator:
 * Nexus 4 - API 17
 * Nexus 10 - API 18
 * 
 * Intermediate:
 * 		We created the large view for the tablet.
 * 
 * Description:
 * 		This app contains a list of heroes in Dota 2, and corresponding screens with more detailed information. The list of heroes and information is retrieved 
 * 		via http request from a published google doc spreadsheet. The information is then stored in a SQLite database and retrieved when the user selects a hero 
 * 		from the list, using the hero's name.  The filter fragment allows sorting of the names displayed in the names fragment.  Clicking on a name in the names
 * 		fragment shows a new Hero fragment.
 * 		The database will allow us to easily filter the displayed list of heroes. We also plan to add more information to the online document and better formatting 
 * 		for the detailed hero information page. 
 * 
 * Date: 11/11/13
 * 
 * Point Distribution:
 * 	We agree on a 50-50 point distribution.
 * 
 * Documentation:
 *  We did not cheat.  We did use and modify the following code for helping to develop the less important parts of our application.
 * 	http://www.vogella.com/articles/AndroidSQLite/article.html: His concept of a DAO was used by us, and much of his code was modified and added to for our project
 * 	
 * 	http://developer.android.com/training/basics/fragments/index.html: The fragments tutorial on Android formed the basic structure of our app: One fragment for browsing 
 *  a list, another for displaying more information when an element from that list is selected.
 *  
 *  http://android-holo-colors.com/: Used to create custom theme for application.
 *  
 *  http://stackoverflow.com/questions/12400113/resizing-imageview-to-fit-to-aspect-ratio: Used to rescale image
 * 	  
 */
package edu.mines.broomthompsondotadictionary;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import edu.mines.broomthompsondotadictionary.FilterFragment.OnFilterSelectedListener;
import edu.mines.broomthompsondotadictionary.NamesFragment.OnHeroSelectedListener;

/**
 * Class: MainActivity Description: The main container for the fragments in our
 * app. Performs http request and data parsing.
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnHeroSelectedListener, OnFilterSelectedListener {

	// static variables
	static String[] names;
	static HeroesDataSource source;
	static ArrayAdapter<Hero> adapter;
	static ArrayAdapter<Hero> other_adapter;

	// Options
	static boolean customView = true;
	static boolean themeDark = true;

	// Checks
	boolean succeeded;
	boolean inSplash;

	// names and filter fragments
	NamesFragment n_frag;
	FilterFragment f_frag;

	// local variables
	String url_name;
	String data;

	// Dialogs
	AlertDialog htmlEmptyDialog;

	// Setter for data
	private void setData(String data) {
		this.data = data;
	}

	/**
	 * Description: Creates the activity
	 * 
	 * @param: Bundle containing old instance if resumed
	 * @return: void
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);

		// Check if previous state is being restored
		if (savedInstanceState != null) {
			return;
		}

		// Set theme
		if (themeDark)
			setTheme(R.style.Theme_Dota);
		else
			setTheme(R.style.Theme_Dota_Light);

		// Hide soft input unless search view checked
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.splash);

		// Lock screen orientation
		if (findViewById(R.id.fragment_container) != null) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		// Set checks
		succeeded = true;
		inSplash = true;

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// get_adapter
				load_new_adapter();

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.e("async", "testing");

				setContentView(R.layout.activity_main);

				// create fragments
				n_frag = new NamesFragment();
				n_frag.setArguments(getIntent().getExtras());
				f_frag = new FilterFragment();
				f_frag.setArguments(getIntent().getExtras());

				// add fragments to activity
				FragmentManager frag_man = getSupportFragmentManager();
				FragmentTransaction frag_trans = frag_man.beginTransaction();
				frag_trans.add(R.id.fragment_container_top, f_frag);
				frag_trans.add(R.id.fragment_container_bottom, n_frag);
				frag_trans.commit();
				
				// Create options menu
				inSplash = false;
				invalidateOptionsMenu();
			}

		}.execute();
	}

	/**
	 * Description: Contains code for getting http and parsing data
	 * 
	 * @param: void
	 * @return: ArrayAdapter containing an array of Hero class that will
	 *          populate the ListFragment NamesFragment
	 */
	private void load_new_adapter() {
		if (adapter != null)
			return;

		// Initialize url and data
		url_name = getString(R.string.url);
		data = getString(R.string.empty_string);

		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(url_name);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.getInputStream();
			BufferedInputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			setData(readStream(in));
		} catch (IOException e) {
			Log.e(getString(R.string.app_name),
					"Failed to get stream form html");
			e.printStackTrace();
			succeeded = false;
		} catch (Exception e) {
			Log.e(getString(R.string.app_name), "Error with html");
			e.printStackTrace();
			succeeded = false;
		} finally {
			// close url connection
			urlConnection.disconnect();
		}

		Log.e("after html", "hi");

		if (data.equals("")) {
			// htmlEmptyDialog.show();
			if (adapter != null)
				return;
		}

		Log.e("after data check", "hi");

		// Initialize database source
		source = new HeroesDataSource(this);
		// Delete database if it already exists
		if (doesDatabaseExist(this, source.getName())) {
			source.delete(this);
		}
		// Create database
		source.open();

		Log.e("after source", "hi");

		if (!succeeded) {
			Toast toast = Toast.makeText(this,
					getResources().getString(R.string.bad_request),
					Toast.LENGTH_LONG * 10);
			toast.show();
			adapter = new ArrayAdapter<Hero>(this,
					android.R.layout.simple_list_item_1);
			// other_adapter = adapter;
			return;
		}

		Log.e("after succed", "hi");

		// Add data to database
		String[] heroes = data.split("\n");
		for (int i = 1; i < heroes.length; i++) {
			source.addHero(heroes[i].split(",", -1));
		}

		// Get list of heroes from database
		List<Hero> list = source.getAllHeroes();

		// Set adapter and other_adapter
		if (customView) {
			// Create CustomHeroAdapter for adapter
			adapter = new CustomHeroAdapter(this,
					android.R.layout.simple_list_item_1, list);
			other_adapter = new ArrayAdapter<Hero>(this,
					android.R.layout.simple_list_item_1, list);
		} else {
			// Create ArrayAdapter for adapter
			adapter = new ArrayAdapter<Hero>(this,
					android.R.layout.simple_list_item_1, list);
			other_adapter = new CustomHeroAdapter(this,
					android.R.layout.simple_list_item_1, list);
		}
	}

	/**
	 * Description: Reads input stream to string
	 * 
	 * @param: InputStream is: data recieved from http request
	 * @return: A string of the data recieved.
	 */
	private String readStream(InputStream is) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int i = is.read();
		while (i != -1) {
			bo.write(i);
			i = is.read();
		}
		return bo.toString();
	}

	/**
	 * Description: Create options menu
	 * 
	 * @param: Menu: the menu to be created
	 * @return: boolean.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Do not inflate the menu if in splash screen
		if (inSplash) return false;
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Description: Handles selection of option menu items
	 * 
	 * @param: MenuItem: the option that has been selected
	 * @return: boolean.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			// Create fragment manager
			FragmentManager manager = getSupportFragmentManager();
			if (manager.getBackStackEntryCount() == 1) {
				getActionBar().setDisplayHomeAsUpEnabled(false);
			}

			// Pop back stack, does nothing if not in hero fragment
			manager.popBackStack();
			return true;
		case R.id.menu_refresh:
			// load adapter
			adapter = null;

			 // Display loading
			 final ProgressDialog progressDialog = new ProgressDialog(this);
			 progressDialog.setTitle(getResources().getString(
			 R.string.progress_dialog_title));
			 progressDialog.setMessage(getResources().getString(
			 R.string.progress_dialog_message));
			 progressDialog.setCancelable(false);
			 progressDialog.show();
			
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					// get_adapter
					load_new_adapter();

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					f_frag.applyFilter();

					 // Dismiss progress dialog
					 progressDialog.dismiss();
				}
			}.execute();

			return true;
		case R.id.menu_custom:
			// Switch view
			customView = !customView;

			// Switch adapters
			ArrayAdapter<Hero> temp = adapter;
			adapter = other_adapter;
			other_adapter = temp;

			// Create new names fragment
			f_frag.applyFilter();
			f_frag.alternateFilter(); // TODO move to own list

			return true;
		case R.id.menu_theme:
			// switch themes
			themeDark = !themeDark;
			// load adapter
			adapter = null;

			finish();
			startActivity(getIntent());
			return true;
		case R.id.menu_help:
			
			AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this);
	 
				// set title
			helpDialogBuilder.setTitle("Help");
			
			helpDialogBuilder
			.setMessage(getResources().getString(R.string.help_contents))
			.setCancelable(false)
			.setPositiveButton("Dismiss",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			  });

			// create alert dialog
			AlertDialog helpDialog = helpDialogBuilder.create();

			// show it
			helpDialog.show();
		
			return true;
			
		case R.id.menu_about:
			
			AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(this);
	 
				// set title
			aboutDialogBuilder.setTitle("About");
			
			aboutDialogBuilder
			.setMessage(getResources().getString(R.string.about_contents))
			.setCancelable(false)
			.setPositiveButton("Dismiss",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			  });

			// create alert dialog
			AlertDialog aboutDialog = aboutDialogBuilder.create();

			// show it
			aboutDialog.show();
		
			return true;	
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// Create fragment manager
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() == 1) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}

		super.onBackPressed();
	}

	/**
	 * Description: Handles selected item in NameFragment
	 * 
	 * @param: Name: string containing name of hero that has been selected
	 * @return: void.
	 */
	@Override
	public void onHeroSelected(String name) {
		// Enable Up
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Hide soft input
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.findViewById(R.id.search)
				.getWindowToken(), 0);

		HeroFragment h_frag = new HeroFragment();
		Bundle args = new Bundle();
		args.putString(HeroFragment.ARG_ID, name);
		h_frag.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (findViewById(R.id.fragment_container) != null) {
			transaction.hide(f_frag);
			transaction.hide(n_frag);
		}

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and adds the transaction to the back stack so the user can
		// navigate back
		transaction.replace(R.id.fragment_container_overlay, h_frag);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	/**
	 * Description: Overridden function handling destruction of activity.
	 * Destroys the database
	 * 
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
	 * 
	 * @param: ContextWrapper: context to be checked for a database
	 * @param: String: database name
	 * @return: boolean: true if database exists, false otherwise.
	 */
	private static boolean doesDatabaseExist(ContextWrapper context,
			String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	/**
	 * Description: overridden function for when a filter is selected
	 * 
	 * @param: String[]: selected filter checkboxes
	 * @param: String: entered text in search box
	 * @return: void
	 */
	@Override
	public void onFilterSelected(String[] attrs, String search) {
		// Get list of heroes that match filter options
		List<Hero> list = source.getHeroByQuery(attrs);

		// Clear adapter
		adapter.clear();

		// If empty search, add all heroes, otherwise add heroes that match
		// search bar
		if (search.length() == 0) {
			for (Hero h : list)
				adapter.add(h);
		} else {
			for (Hero h : list) {
				if (search.length() <= h.getName().length()) {
					if (h.getName().substring(0, search.length())
							.toLowerCase(Locale.getDefault())
							.contains(search.toLowerCase(Locale.getDefault()))) {
						adapter.add(h);
					}
				}
			}
		}

		// Refresh the list
		n_frag.refreshList();
	}
}