package edu.mines.broomthompsondotadictionary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Class: NamesFragment
 * Description: List of the hero names, when selected, goes to the HeroFragment
 *
 */
public class NamesFragment extends ListFragment {
    OnHeroSelectedListener mCallback;

    // The container Activity must implement this interface so the fragment can deliver messages
    public interface OnHeroSelectedListener {
        /** Called by MainActivity when it has been selected, lets the activity know which hero has been selected. */
        public void onHeroSelected(String name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setListAdapter(MainActivity.adapter);
    }
    
    public void refreshList() {
    	setListAdapter(MainActivity.adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeroSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeroSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
    	TextView view = (TextView) v;
        mCallback.onHeroSelected(view.getText().toString());
    }
}