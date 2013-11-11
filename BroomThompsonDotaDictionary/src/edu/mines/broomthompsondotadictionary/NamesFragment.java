package edu.mines.broomthompsondotadictionary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Alexander Broom
 * @author Austin Thompson
 * 
 * Class: NamesFragment
 * Description: List of the hero names, when selected, goes to the HeroFragment
 *
 */
public class NamesFragment extends ListFragment {
    OnHeroSelectedListener mCallback;

    // The container Activity must implement this interface so the fragment can deliver messages
    /** 
     * OnHeroSelectedListener: Called by MainActivity when it has been selected.
     * Lets the activity know which hero has been selected.
     */
    public interface OnHeroSelectedListener {
        public void onHeroSelected(String name);
    }

	/** 
	 * onCreate: Creates the fragment
	 * 
	 *  @param savedInstanceState: the Bundle that can be used to recreate the fragment
	 *  instead of recreate.
	 */ 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setListAdapter(MainActivity.adapter);
    }
    
    /**
     * onActivityCreated: Turns off overscroll
     * 
     * @param savedInstanceState: the Bundle that can be used to recreate the fragment
     * 
     * Removes overscroll, since overscroll color cannot be changed and
     * Holo Blue does not match app's color scheme
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getListView().setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

	/** 
	 * refreshList: refreshes list of heroes displayed
	 * 
	 *  sets the ListFragment's List adapter to the static adapter in MainActivity
	 */ 
    public void refreshList() {
    	setListAdapter(MainActivity.adapter);
    }
    

	/** 
	 * onStart: overriden onStart function
	 * calls super.onStart()
	 */ 
    @Override
    public void onStart() {
        super.onStart();
    }

	/** 
	 * onAttach: makes sure container activity has implemented the callback interface
	 * Throws an exception if it has not been implemented
	 */ 
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            mCallback = (OnHeroSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeroSelectedListener");
        }
    }

	/** 
	 * onListItemClick: Notifies the parent activity of selected item
	 * parent receives the text of selected list item
	 */ 
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	TextView view = (TextView) v;
        mCallback.onHeroSelected(view.getText().toString());
    }
}