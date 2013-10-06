package edu.mines.broomthompsondotadictionary;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.dotadictionary.R;

/**
 * Class: FilterFragment Description: List of the hero names, when selected,
 * goes to the HeroFragment
 * 
 */
public class FilterFragment extends Fragment {
	OnFilterSelectedListener mCallback;

	ArrayList<CheckBox> focus_boxes;
	ArrayList<CheckBox> attack_boxes;
	ArrayList<CheckBox> ease_boxes;
	ArrayList<CheckBox> role_boxes;

	Button ranged, melee;
	Button easy, medium, hard;
	CheckBox box;
	String[] query;

	// The container Activity must implement this interface so the fragment can
	// deliver messages
	public interface OnFilterSelectedListener {
		/**
		 * Called by fragment to check which filters have been selected, builds
		 * a SQL queyr as a result.
		 */
		public void onFilterSelected(String[] attrs);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		query = new String[] { "", "", "", "", "" };
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		focus_boxes = new ArrayList<CheckBox>();
		attack_boxes = new ArrayList<CheckBox>();
		ease_boxes = new ArrayList<CheckBox>();
		role_boxes = new ArrayList<CheckBox>();

		View v = inflater.inflate(R.layout.filter_frag, container, false);

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

		for (CheckBox box : focus_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					for (CheckBox cbox : focus_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
					}
					Button text_getter = (Button) v;
					checkBoxes();
					mCallback.onFilterSelected(query);
				}
			});
		}
		
		for (CheckBox box : attack_boxes) {
			box.setOnClickListener(new CheckBox.OnClickListener() {
				public void onClick(View v) {
					CheckBox selected_box = (CheckBox) v;
					for (CheckBox cbox : attack_boxes) {
						if (cbox.getId() != selected_box.getId()) {
							cbox.setChecked(false);
						}
						
					}
					Button text_getter = (Button) v;
					checkBoxes();
					mCallback.onFilterSelected(query);
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
					Button text_getter = (Button) v;
					checkBoxes();
					Log.d("q", query[3]);
					mCallback.onFilterSelected(query);
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
					Button text_getter = (Button) v;
					checkBoxes();
					mCallback.onFilterSelected(query);
				}
			});
		}

		return v;
	}
	
	public void checkBoxes() {
		query = new String[] { "", "", "", "", "" };
		for (CheckBox box : focus_boxes) {
			if(box.isChecked()) {
				Button text_getter = (Button) box;
				query[1] = text_getter.getText().toString();
			}
		}
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
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFilterSelectedListener");
        }
    }

	@Override
	public void onStart() {
		super.onStart();

	}
}