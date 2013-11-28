package edu.mines.broomthompsondotadictionary;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author Alexander Broom
 * @author Austin Thompson
 * 
 * Class: FilterFragment 
 * Description: The top fragment of the main screen, displays the checkboxes for filtering the list 
 * of heroes and the text edit for search by name.
 * 
 */
public class FilterFragment extends Fragment {
	OnFilterSelectedListener mCallback;

	// Containers for each category of check boxes.
	// Separated because only one can be checked in each row
	ArrayList<CheckBox> focusBoxes;
	ArrayList<CheckBox> attackBoxes;
	ArrayList<CheckBox> easeBoxes;
	ArrayList<CheckBox> roleBoxes;
	
	// Array that will contain strings to look for in each column of 
	// the database, one index for each column.
	// FORMAT: [FOCUS, ATTACK, EASE OF USE, ROLE ]
	String[] query;
	
	// Search EditText
	EditText search;
	
	// saved previous filter options
	String[] prev_query;
	String prev_search;
	

	/** 
	 * OnFilterSelectedListener: An interface which requires the activity using this 
	 * fragment to implement the function necessary to pass information back into the
	 * activity.  
	 */ 

	public interface OnFilterSelectedListener {
		/**
		 * Called by fragment to check which filters have been selected, builds
		 * a SQL queyr as a result.
		 */
		public void onFilterSelected(String[] attrs, String search);
	}

	/** 
	 * OnCreate: Creates the fragment.
	 * 
	 * @param savedInstanceState: A Bundle that allows the fragment to be restored 
	 * rather than rebuilt. 
	 */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Initialize the query to all null values
		query = new String[] { "", "", "", "", "" };
		super.onCreate(savedInstanceState);
		
		// initialize previous filter options
		prev_query = new String[] { "", "", "", "", "" };
		prev_search = "";
	}

	/** 
	 * OnCreateView: Initializes member variables, loads defined xml,
	 * sets listeners for the check boxes.
	 * 
	 * @param inflater: The object to inflate the view in the container.
	 * @param container: The container the view is inflated into
	 * @param savedInstanceState: A Bundle that allows the fragment to be restored 
	 * rather than rebuilt. 
	 */ 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Initialize containers of check boxes
		focusBoxes = new ArrayList<CheckBox>();
		attackBoxes = new ArrayList<CheckBox>();
		easeBoxes = new ArrayList<CheckBox>();
		roleBoxes = new ArrayList<CheckBox>();

		// inflate xml into container
		View v = inflater.inflate(R.layout.filter_frag, container, false);

		// Link check boxes to xml and add them to the appropriate ArrayList
		CheckBox focus_str = (CheckBox) v.findViewById(R.id.focus_str);
		focusBoxes.add(focus_str);
		CheckBox focus_int = (CheckBox) v.findViewById(R.id.focus_int);
		focusBoxes.add(focus_int);
		CheckBox focus_agi = (CheckBox) v.findViewById(R.id.focus_agi);
		focusBoxes.add(focus_agi);

		CheckBox attack_melee = (CheckBox) v.findViewById(R.id.attack_melee);
		attackBoxes.add(attack_melee);
		CheckBox attack_rng = (CheckBox) v.findViewById(R.id.attack_ranged);
		attackBoxes.add(attack_rng);

		CheckBox ease_easy = (CheckBox) v.findViewById(R.id.ease_easy);
		easeBoxes.add(ease_easy);
		CheckBox ease_med = (CheckBox) v.findViewById(R.id.ease_med);
		easeBoxes.add(ease_med);
		CheckBox ease_hard = (CheckBox) v.findViewById(R.id.ease_hard);
		easeBoxes.add(ease_hard);

		CheckBox role_carry = (CheckBox) v.findViewById(R.id.role_carry);
		roleBoxes.add(role_carry);
		CheckBox role_support = (CheckBox) v.findViewById(R.id.role_support);
		roleBoxes.add(role_support);
		CheckBox role_ganker = (CheckBox) v.findViewById(R.id.role_ganker);
		roleBoxes.add(role_ganker);
		
		// Create search field, text must be accessed by the listeners on the 
		// check boxes, so it is final.
		search = (EditText) v.findViewById(R.id.search);

		// Iterate through focus CheckBoxes adding appropriate listeners
		for (CheckBox box : focusBoxes) {
			box.setOnClickListener(new CheckBoxClickListener(focusBoxes));
		}

		// Iterate through attack CheckBoxes adding appropriate listeners
		for (CheckBox box : attackBoxes) {
			box.setOnClickListener(new CheckBoxClickListener(attackBoxes));
		}

		// Iterate through ease of use CheckBoxes adding appropriate listeners
		for (CheckBox box : easeBoxes) {
			box.setOnClickListener(new CheckBoxClickListener(easeBoxes));
		}

		// Iterate through role CheckBoxes adding appropriate listeners
		for (CheckBox box : roleBoxes) {
			box.setOnClickListener(new CheckBoxClickListener(roleBoxes));
		}
		
		// Add listener to search EditText, listens whenever text is changed
		search.addTextChangedListener(new TextWatcher() {          
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
        		prev_query = new String[] { "", "", "", "", "" };
        		// Use callback for query whenever a key is pressed
        		applyFilter();
            }                       
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            	// Do nothing
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
		
		// Add listener to clear search button
		Button clearSearch = (Button) v.findViewById(R.id.clear_search);
		clearSearch.setOnClickListener(new OnClickListener() {
			// Clear text on click
			@Override
			public void onClick(View v) {
				search.setText("");
			}
		});
		// Add listener to change button image, provides user feedback
		clearSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					v.setBackgroundResource(R.drawable.ic_clear_highlighted);
				else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
					v.setBackgroundResource(R.drawable.ic_clear);
				return false;
			}
			
		});
		
		// set text color for search
		if (MainActivity.themeDark) search.setTextColor(getResources().getColor(android.R.color.white));
		else search.setTextColor(getResources().getColor(android.R.color.black));

		return v;
	}
	
	/**
	 * CheckBoxClickListener: custom OnClickListener
	 * 
	 * sets other boxes in group to false
	 * applies the filter
	 *
	 */
	private class CheckBoxClickListener implements OnClickListener {
		ArrayList<CheckBox> checkBoxGroup;
		
		public CheckBoxClickListener(ArrayList<CheckBox> checkBoxGroup) {
			this.checkBoxGroup = checkBoxGroup;
		}
		
		@Override
		public void onClick(View v) {
			prev_query = new String[] { "", "", "", "", "" };
			
			CheckBox selected_box = (CheckBox) v;
			for (CheckBox cbox : checkBoxGroup) {
				if (cbox.getId() != selected_box.getId()) {
					cbox.setChecked(false);
				}
			}
			checkBoxes();
			applyFilter();
		}
	}
	
	/** 
	 * checkBoxes: Helper function that iterates through all check boxes and builds a 
	 * query according to which are checked.
	 * 
	 */ 
	public void checkBoxes() {
		// Start with an empty query
		query = new String[] { "", "", "", "", "" };
		
		// Iterate through the check boxes on the first row
		for (CheckBox box : focusBoxes) {
			// If a box is checked, use the value of its string as the query to the database.
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				// The first index of the query array is for the focus category
				query[1] = text_getter.getText().toString();
			}
		}
		// The above is repeated for each category (attack, ease and role)
		for (CheckBox box : attackBoxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[2] = text_getter.getText().toString();
			}
		}
		for (CheckBox box : easeBoxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[3] = text_getter.getText().toString();
			}
		}
		for (CheckBox box : roleBoxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[4] = text_getter.getText().toString();
			}
		}
	}
	
	/** 
	 * onAttach: When this fragment is attached to the Main Activity, this function
	 * ensures that the appropriate methods have been implemented.
	 * 
	 * @param activity: The activity that is using this fragment 
	 */ 
	public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallback = (OnFilterSelectedListener) activity;
    }
	
	/**
	 * applyFilter: applies the filter to the list
	 * 
	 * @param void
	 * @return void
	 */
	public void applyFilter() {
		mCallback.onFilterSelected(query, search.getText().toString());
	}
	
	/**
	 * clearFilter: applies clear filter to the list
	 * 
	 * @param void
	 * @return void
	 */
	public void alternateFilter() {
		// Prepare current filter options for saving
		String[] current_query = query;
		String current_search = search.getText().toString();
		
		// Load previous filter options
		query = prev_query;
		search.setText(prev_search);
		search.setSelection(prev_search.length());
		
		// Check / uncheck focus boxes
		for (CheckBox box : focusBoxes) {
			Log.d("box name",box.getText().toString());
			if (box.getText().toString().equals(query[1])) box.setChecked(true);
			else box.setChecked(false);
		}
		for (CheckBox box : attackBoxes) {
			if (box.getText().toString().equals(query[2])) box.setChecked(true);
			else box.setChecked(false);
		}
		for (CheckBox box : easeBoxes) {
			if (box.getText().toString().equals(query[3])) box.setChecked(true);
			else box.setChecked(false);
		}
		for (CheckBox box : roleBoxes) {
			if (box.getText().toString().equals(query[4])) box.setChecked(true);
			else box.setChecked(false);
		}
		
		// Save filter options
		prev_query = current_query;
		prev_search = current_search;
	}
}