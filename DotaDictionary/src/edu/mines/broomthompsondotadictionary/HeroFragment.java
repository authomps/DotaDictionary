package edu.mines.broomthompsondotadictionary;

import com.example.dotadictionary.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Class: HeroFragment
 * Description: The fragment containing detailed information about each hero: Will eventually
 * contain a picture and other miscellaneous information.
 *
 */
public class HeroFragment extends Fragment {
    final static String ARG_ID = "id";
    String mCurrentName = "";

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
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getString(ARG_ID));
        } else if (mCurrentName != null) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentName);
        }
    }

    public void updateArticleView(String name) {
    	TextView name_view = (TextView) getActivity().findViewById(R.id.name);
        Hero hero = MainActivity.source.getHeroByName(name);
        name_view.setText(hero.getName());
        TextView focus_view = (TextView) getActivity().findViewById(R.id.focus);
        focus_view.setText(hero.getFocus());
        TextView attack_view = (TextView) getActivity().findViewById(R.id.attack);
        attack_view.setText(hero.getAttack());
        TextView use_view = (TextView) getActivity().findViewById(R.id.use);
        use_view.setText(hero.getUse());
        TextView role_view = (TextView) getActivity().findViewById(R.id.role);
        role_view.setText(hero.getRole());
        mCurrentName = name;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(ARG_ID, mCurrentName);
    }
}
