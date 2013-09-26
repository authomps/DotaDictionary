package com.example.dotadictionary;

import com.example.dotadictionary.NamesFragment.OnHeroSelectedListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements OnHeroSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check if previous state is being restored
		if (savedInstanceState != null) {
            return;
        }
		
		NamesFragment n_frag = new NamesFragment();
		n_frag.setArguments(getIntent().getExtras());
		
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, n_frag).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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