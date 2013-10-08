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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dotadictionary.R;

/**
 * Class: HeroFragment Description: The fragment containing detailed information
 * about each hero: Will eventually contain a picture and other miscellaneous
 * information.
 * 
 */
public class HeroFragment extends Fragment {
	final static String ARG_ID = "id";
	String mCurrentName = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.names_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			mCurrentName = savedInstanceState.getString(ARG_ID);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.hero_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			updateArticleView(args.getString(ARG_ID));
		} else if (mCurrentName != null) {
			// Set article based on saved instance state defined during
			// onCreateView
			updateArticleView(mCurrentName);
		}
	}

	public void updateArticleView(String name) {

		final Hero hero = MainActivity.source.getHeroByName(name);
		Bitmap img;

		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<Bitmap> result = es.submit(new Callable<Bitmap>() {
			public Bitmap call() throws Exception {
				URL url = new URL(hero.getPicture());
				Bitmap image = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				return image;
			}
		});

		try {
			img = result.get();
			ImageView portrait = (ImageView) getActivity().findViewById(
					R.id.portrait);
//			portrait.setScaleType(ScaleType.FIT_XY);
			portrait.setImageBitmap(img);
			// Gets the width you want it to be
//			int intendedWidth = portrait.getWidth();
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int intendedWidth = size.x;
			// Gets the downloaded image dimensions
			int originalWidth = img.getWidth();
			int originalHeight = img.getHeight();

			// Calculates the new dimensions
			float scale = (float) intendedWidth / originalWidth;
			int newHeight = (int) Math.round(originalHeight * scale);
			
			// Resizes portrait. Change "FrameLayout" to whatever layout
			// portrait is located in.
//			portrait.setLayoutParams(new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.WRAP_CONTENT,
//					LinearLayout.LayoutParams.WRAP_CONTENT));
			portrait.getLayoutParams().width = intendedWidth;
			portrait.getLayoutParams().height = newHeight;			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("portrait", "error getting portrait");
		}

		TextView name_view = (TextView) getActivity().findViewById(R.id.name);
		if(hero.getName().length() > 13) {
			name_view.setTextSize(24);
		}
		name_view.setText(hero.getName());
		
		
		ImageView focus_view = (ImageView) getActivity().findViewById(R.id.focus);
		String focus = hero.getFocus();
		if(focus.equals("Intelligence")) {
			focus_view.setImageResource(R.drawable.icon_intelligence);
		} else if (focus.equals("Agility")) {
			focus_view.setImageResource(R.drawable.icon_agility);
		} else {
			focus_view.setImageResource(R.drawable.icon_strength);
		}
		
		ImageView attack_view = (ImageView) getActivity().findViewById(R.id.attack);
		Log.d("test", hero.getAttack());
		if(hero.getAttack().equals("Melee")) {
			attack_view.setImageResource(R.drawable.icon_melee);
		}
		else {
			attack_view.setImageResource(R.drawable.icon_ranged);
		}
		
		TextView use_view = (TextView) getActivity().findViewById(R.id.role_use);
		use_view.setText(hero.getUse() + " " + hero.getRole());
		mCurrentName = name;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_quit:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putString(ARG_ID, mCurrentName);
	}
}
