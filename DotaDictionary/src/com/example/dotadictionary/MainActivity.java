package com.example.dotadictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.Toast;

import com.example.dotadictionary.NamesFragment.OnHeroSelectedListener;

public class MainActivity extends FragmentActivity implements OnHeroSelectedListener {
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		url = "https://docs.google.com/spreadsheet/pub?key=0ApYAGkG5V1-NdEdXLTJMdjR2MEdKRUJ1MWs0alRkRVE&single=true&gid=0&output=html";
		
		// Check if previous state is being restored
		if (savedInstanceState != null) {
            return;
        }
		
		try {
			getHtml();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public void getHtml() throws ClientProtocolException, IOException
	{
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpContext localContext = new BasicHttpContext();
	    HttpGet httpGet = new HttpGet(url);
	    HttpResponse response = httpClient.execute(httpGet, localContext);
	    String result = "";

	    BufferedReader reader = new BufferedReader(
	        new InputStreamReader(
	          response.getEntity().getContent()
	        )
	      );

	    String line = null;
	    while ((line = reader.readLine()) != null){
	      result += line + "\n";
	      Toast.makeText(this, line.toString(), Toast.LENGTH_LONG).show();

	    }

	}
}