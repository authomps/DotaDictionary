package edu.mines.broomthompsondotadictionary;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomHeroAdapter extends ArrayAdapter<Hero> {
	Context context;
	static List<Hero> heroes;
	Map<String, Bitmap> portraits;
	
	private class HeroRow {
	    ImageView portrait;
	    TextView name;
	    TextView desc;
	}
	 
    public CustomHeroAdapter(Context context, int resourceId, List<Hero> heroes) {
        super(context, resourceId, heroes);
        this.context = context;
        CustomHeroAdapter.heroes = heroes;
        portraits = new HashMap<String, Bitmap>();

        for (final Hero hero : heroes) {
	        Bitmap img;
	        ExecutorService es = Executors.newSingleThreadExecutor();
	        Future<Bitmap> result = es.submit(new Callable<Bitmap>() {
				public Bitmap call() throws Exception {
					URL url = new URL(hero.getPicture());
					Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					return image;
				}
			});
	        
	        try {
	        	img = result.get();
	        } catch (Exception e) {
				// If exception, make log statement and print stack
	        	img = BitmapFactory.decodeResource(context.getResources(),R.drawable.img_portrait_unknown);
				Log.e("CustomHeroAdapter", "Failed to load portrait");
				e.printStackTrace();	        	
	        }

	        portraits.put(hero.getName(), img);
        }
        
        
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        HeroRow heroRow = null;
        final Hero hero = getItem(position);
 
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item, null);
            heroRow = new HeroRow();
            heroRow.name = (TextView) convertView.findViewById(R.id.row_name);
            heroRow.portrait = (ImageView) convertView.findViewById(R.id.row_portrait);
            heroRow.desc = (TextView) convertView.findViewById(R.id.row_desc);
            convertView.setTag(heroRow);
        } else
            heroRow = (HeroRow) convertView.getTag();
 
        heroRow.name.setText(hero.getName());
        heroRow.portrait.setImageBitmap(portraits.get(hero.getName()));
        heroRow.desc.setText(hero.getUse() + " " + hero.getRole());
        
        return convertView;
    }
}
