package edu.mines.broomthompsondotadictionary;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Alexander Broom
 * @author Austin Thompson
 * 
 *         Class: HeroFragment
 * 
 *         Description: The fragment containing detailed information about each
 *         hero: Will eventually contain a picture and other miscellaneous
 *         information.
 * 
 */
public class HeroFragment extends Fragment {
	// Indicator of the name of the value passed in a savedInstance
	final static String ARG_ID = "id";
	// The name of the hero being displayed
	String mCurrentName = "";

	/**
	 * onCreate: Creates the fragment
	 * 
	 * @param savedInstanceState
	 *            : the Bundle that can be used to recreate the fragment instead
	 *            of recreate.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * onCreateView: Creates the view of the fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentName = savedInstanceState.getString(ARG_ID);
		}

		return inflater.inflate(R.layout.hero_view, container, false);
	}

	/**
	 * onStart: Handles start up of the app
	 */
	@Override
	public void onStart() {
		super.onStart();
		Bundle args = getArguments();

		// Create fragment if there are args, otherwise use previous instance of
		// fragment
		if (args != null) {
			updateHeroView(args.getString(ARG_ID));
		} else if (mCurrentName != "") {
			updateHeroView(mCurrentName);
		}
	}

	/**
	 * updateHeroView: Sets data in fragment for each hero
	 */
	public void updateHeroView(String name) {
		// Get hero
		final Hero hero = MainActivity.source.getHeroByName(name);
		// Variable for image to be loaded
		Bitmap img;

		// Executor Service to create new thread to download image from url
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<Bitmap> result = es.submit(new Callable<Bitmap>() {
			public Bitmap call() throws Exception {
				URL url = new URL(hero.getPicture());
				Bitmap image = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				return image;
			}
		});

		// Try to download image
		try {
			img = result.get();
			ImageView portrait = (ImageView) getActivity().findViewById(
					R.id.portrait);
			portrait.setImageBitmap(img);

			// Get the width of the screen
			WindowManager wm = (WindowManager) getActivity().getSystemService(
					Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int intendedWidth = size.x;

			// Get the downloaded image dimensions
			int originalWidth = img.getWidth();
			int originalHeight = img.getHeight();

			if (getActivity().findViewById(R.id.fragment_container) != null) {
				// Calculate the new dimensions
				float scale = (float) intendedWidth / originalWidth;
				int newHeight = (int) Math.round(originalHeight * scale);
				// Resize the portrait
				portrait.getLayoutParams().width = intendedWidth;
				portrait.getLayoutParams().height = newHeight;
			} else {

				portrait.getLayoutParams().width = originalWidth * 2;
				portrait.getLayoutParams().height = originalHeight * 2;
			}
		} catch (Exception e) {
			// If exception, make log statement and print stack
			Log.e(getString(R.string.app_name), "Failed to load portrait");
			e.printStackTrace();
			// Continue to create rest of fragment, whether image loads or not
		}

		// Set the name text view
		TextView name_view = (TextView) getActivity().findViewById(R.id.name);
		if (hero.getName().length() > 13
				&& getActivity().findViewById(R.id.fragment_container) != null) {
			name_view.setTextSize(24);
		}
		name_view.setText(hero.getName());

		// Display hero's ease of use and role
		TextView use_view = (TextView) getActivity().findViewById(R.id.role_use_hero);
		use_view.setText(hero.getUse() + " " + hero.getRole());
		mCurrentName = name;

		// Determine which icon to display depending on hero's focus
		ImageView focus_view = (ImageView) getActivity().findViewById(
				R.id.focus);
		String focus = hero.getFocus();
		if (focus.equals("Intelligence")) {
			focus_view.setImageResource(R.drawable.icon_intelligence);
		} else if (focus.equals("Agility")) {
			focus_view.setImageResource(R.drawable.icon_agility);
		} else {
			focus_view.setImageResource(R.drawable.icon_strength);
		}

		// Determine which icon to display depending on hero's attack type
		ImageView attack_view = (ImageView) getActivity().findViewById(
				R.id.attack);
		if (hero.getAttack().equals("Melee")) {
			attack_view.setImageResource(R.drawable.icon_melee);
		} else {
			attack_view.setImageResource(R.drawable.icon_ranged);
		}

		// Display abilities
		LinearLayout abilities_ll = (LinearLayout) getActivity().findViewById(
				R.id.abilities);
		abilities_ll.removeAllViews();
		abilities_ll.setOverScrollMode(View.OVER_SCROLL_NEVER);
		String[] abilities = hero.getAbilities().split(";");
		for (int i = 0; i < abilities.length - 2; i = i + 3) {
			// Create Row
			LinearLayout row_ll = new LinearLayout(getActivity());
			row_ll.setOrientation(LinearLayout.HORIZONTAL);

			// Download image
			Bitmap ability_img;
			final String ability_img_url = abilities[i];
			ExecutorService ability_es = Executors.newSingleThreadExecutor();
			Future<Bitmap> ability_result = ability_es
					.submit(new Callable<Bitmap>() {
						public Bitmap call() throws Exception {
							URL url = new URL(ability_img_url);
							Bitmap image = BitmapFactory.decodeStream(url
									.openConnection().getInputStream());
							return image;
						}
					});

			try {
				ability_img = ability_result.get();
			} catch (Exception e) {
				// If exception, make log statement and print stack
				ability_img = BitmapFactory.decodeResource(getActivity()
						.getResources(), R.drawable.img_portrait_unknown);
				Log.e("HeroFragment", "Failed to load ability image");
				e.printStackTrace();
			}

			// Set image
			ImageView ability_image = new ImageView(getActivity());
			ability_image.setImageBitmap(ability_img);
			ability_image.setPadding(10, 25, 0, 0);
			row_ll.addView(ability_image);

			// Create Vertical segment
			LinearLayout vert_ll = new LinearLayout(getActivity());
			vert_ll.setOrientation(LinearLayout.VERTICAL);

			// Set name
			TextView ability_name = new TextView(getActivity());
			ability_name.setText(abilities[i + 1]);
			ability_name.setTextSize(25);
			ability_name.setPadding(25, 0, 0, 0);

			// Set Line
			View ability_line = new View(getActivity());
			ability_line.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 1));
			ability_line.setBackgroundColor(getActivity().getResources()
					.getColor(R.color.red));

			// Set desc
			TextView ability_desc = new TextView(getActivity());
			ability_desc.setText(abilities[i + 2]);
			ability_desc.setPadding(75, 0, 75, 0);

			// Add name and desc to vertical segment
			vert_ll.addView(ability_name);
			vert_ll.addView(ability_line);
			vert_ll.addView(ability_desc);

			// Add vertical segment to row
			row_ll.addView(vert_ll);

			// Add row to abilities
			abilities_ll.addView(row_ll);
		}
	}

	/**
	 * onSaveInstanceState: saves the current article selection in case we need
	 * to recreate the fragment
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ARG_ID, mCurrentName);
	}
}
