package edu.mines.broomthompsondotadictionary;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Class: HeroesDataSource
 * Description: A DAO for the database, contains the Android helper for dealing with the Java Database and the Java Database itself
 *
 */
public class HeroesDataSource {
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] data = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_FOCUS, SQLiteHelper.COLUMN_ATTACK, SQLiteHelper.COLUMN_USE, SQLiteHelper.COLUMN_ROLE };

	public HeroesDataSource(Context ctx) {
		dbHelper = new SQLiteHelper(ctx);
	}
	
	public String getName() {
		return dbHelper.getDatabaseName();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Hero addHero(String[] attrs) {
		ContentValues vals = new ContentValues();
		
		vals.put(SQLiteHelper.COLUMN_NAME, attrs[0]);
		vals.put(SQLiteHelper.COLUMN_FOCUS, attrs[1]);
		vals.put(SQLiteHelper.COLUMN_ATTACK, attrs[2]);
		vals.put(SQLiteHelper.COLUMN_USE, attrs[3]);
		vals.put(SQLiteHelper.COLUMN_ROLE, attrs[4]);
		
		long heroId = database.insert(SQLiteHelper.TABLE_HEROES, null, vals);
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, SQLiteHelper.COLUMN_ID + 
				" = " + heroId, null, null, null, null);
		cursor.moveToFirst();
		Hero hero = cursorToHero(cursor);
		cursor.close();
		return hero;
	}
	
	public List<Hero> getAllHeroes() {
		List<Hero> heroes = new ArrayList<Hero>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hero hero = cursorToHero(cursor);
			heroes.add(hero);
			cursor.moveToNext();
		}
		cursor.close();
		
		return heroes;
	}
	
	public Hero getHeroByName(String name) {
		Log.d("dbget", "before query");
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, SQLiteHelper.COLUMN_NAME + " = '" + name + "'", null, null, null, null);
		cursor.moveToFirst();
		Hero hero = cursorToHero(cursor);
		cursor.close();
		return hero;
	}
	
	public List<Hero> getHeroByQuery(String[] attrs) {
		
		List<Hero> heroes = new ArrayList<Hero>();
		String query = "";
		for(int i = 0; i < attrs.length; i++) {
			if(attrs[i] != "") {
				switch(i) {
					case 0:
						query += SQLiteHelper.COLUMN_NAME + " = '" + attrs[i] + "' and ";
						break;
					case 1:
						query += SQLiteHelper.COLUMN_FOCUS + " = '" + attrs[i] + "' and ";
						break;
					case 2:
						query += SQLiteHelper.COLUMN_ATTACK + " = '" + attrs[i] + "' and ";
						break;
					case 3:
						query += SQLiteHelper.COLUMN_USE + " = '" + attrs[i] + "' and ";
						break;
					case 4:
						query += SQLiteHelper.COLUMN_ROLE + " = '" + attrs[i] + "' and ";
						break;
				}
			}
		}
		if(query != "") {
			query = query.substring(0, query.length() - 5);
		}
		Log.d("q", query);
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, query, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hero hero = cursorToHero(cursor);
			heroes.add(hero);
			cursor.moveToNext();
		}
		cursor.close();
		
		return heroes;
	}
	
	private Hero cursorToHero(Cursor cursor) {
		Hero hero = new Hero();		
		hero.setId(cursor.getLong(0));
		hero.setName(cursor.getString(1));
		hero.setFocus(cursor.getString(2));
		hero.setAttack(cursor.getString(3));
		hero.setUse(cursor.getString(4));
		hero.setRole(cursor.getString(5));
		return hero;
	}
	
	public void delete(Context ctx) {
		ctx.deleteDatabase(getName());
	}
}
