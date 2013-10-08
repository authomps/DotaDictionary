package edu.mines.broomthompsondotadictionary;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.dotadictionary.R;

/**
 * Class: FilterFragment 
 * Description: The top fragment of the main screen, displays the checkboxes for filtering the list 
 * of heroes and the text edit for search by name.
 * 
 * @author Alex Broom, Austin Thompson
 */
public class FilterFragment extends Fragment {
	OnFilterSelectedListener mCallback;

	// Containers for each category of check boxes.
	// Separated because only one can be checked in each row
	ArrayList<CheckBox> focus_boxes;
	ArrayList<CheckBox> attack_boxes;
	ArrayList<CheckBox> ease_boxes;
	ArrayList<CheckBox> role_boxes;
	
	// Array that will contain strings to look for in each column of 
	// the database, one index for each column.
	// FORMAT: [FOCUS, ATTACK, EASE OF USE, ROLE ]
	String[] query;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Initialize containers of check boxes
		focus_boxes = new ArrayList<CheckBox>();
		attack_boxes = new ArrayList<CheckBox>();
		ease_boxes = new ArrayList<CheckBox>();
		role_boxes = new ArrayList<CheckBox>();

		// inflate xml into container
		View v = inflater.inflate(R.layout.filter_frag, container, false);

		// Link check boxes to xml and add them to the appropriate ArrayList
		CheckBox focus_str = (CheckBox) v.findViewById(R.id.focus_str);
		focus_boxes.add(focus_str);
		CheckBox focus_int = (CheckBox) v.findViewById(R.id.focus_int);
		focus_boxes.add(focus_int);
		CheckBox focus_agi = (CheckBox) v.findViewById(R.id.focus_agi);
		focus_boxes.add(focus_agi);

		CheckBox attack_melee = (CheckBox) v.findViewById(R.id.attack_melee);
		attack_boxes.add(attack_melee);
		CheckBox attack_rng = (CheckBox) v.findViewById(R.id.attack_ranged);
		attack_boxes.add(attack_rng);

		CheckBox ease_easy = (CheckBox) v.findViewById(R.id.ease_easy);
		ease_boxes.add(ease_easy);
		CheckBox ease_med = (CheckBox) v.findViewById(R.id.ease_med);
		ease_boxes.add(ease_med);
		CheckBox ease_hard = (CheckBox) v.findViewById(R.id.ease_hard);
		ease_boxes.add(ease_hard);

		CheckBox role_carry = (CheckBox) v.findViewById(R.id.role_carry);
		role_boxes.add(role_carry);
		CheckBox role_support = (CheckBox) v.findViewById(R.id.role_support);
		role_boxes.add(role_support);
		CheckBox role_ganker = (CheckBox) v.findViewById(R.id.role_ganker);
		role_boxes.add(role_ganker);
		
		// Create search field, text must be accessed by the listeners on the 
		// check boxes, so it is final.
		final EditText search = (EditText) v.findViewById(R.id.search);

		// Iterate through the first row of boxes and add appropriate listeners
		for (CheckBox box : focus_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
			
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					// Checks to see if any other box is checked in that row, if so, uncheck it
					for (CheckBox cbox : focus_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
					}
					// Check all other boxes and build query
					checkBoxes();
					// Use callback to access database with query
					mCallback.onFilterSelected(query, search.getText().toString());
				}
			});
		}
		
		// The above is repeated for the other 3 array lists of checkboxes
		for (CheckBox box : attack_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					for (CheckBox cbox : attack_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
						
					}
					checkBoxes();
					mCallback.onFilterSelected(query, search.getText().toString());
				}
			});
		}
		
		for (CheckBox box : ease_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					for (CheckBox cbox : ease_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
					}
					checkBoxes();
					Log.d("q", query[3]);
					mCallback.onFilterSelected(query, search.getText().toString());
				}
			});
		}
		
		for (CheckBox box : role_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					for (CheckBox cbox : role_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
					}
					checkBoxes();
					mCallback.onFilterSelected(query, search.getText().toString());
				}
			});
		}
		
		// Queries are also made whenever the user types a letter into the search field,
		// these listeners allow for that.
		search.addTextChangedListener(new TextWatcher() {          
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            		// Use callback for query whenever a key is pressed
            		mCallback.onFilterSelected(query, search.getText().toString());

            }                       
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub                          
            }                       
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub                          

            }
        });

		return v;
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
		for (CheckBox box : focus_boxes) {
			// If a box is checked, use the value of its string as
			// the query to the database.
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				// The first index of the query array is for the focus category
				query[1] = text_getter.getText().toString();
			}
		}
		// The above is repeated for each category (attack, ease and role)
		for (CheckBox box : attack_boxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[2] = text_getter.getText().toString();
			}
		}
		for (CheckBox box : ease_boxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[3] = text_getter.getText().toString();
			}
		}
		for (CheckBox box : role_boxes) {
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

        try {
            mCallback = (OnFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFilterSelectedListener");
        }
    }
}