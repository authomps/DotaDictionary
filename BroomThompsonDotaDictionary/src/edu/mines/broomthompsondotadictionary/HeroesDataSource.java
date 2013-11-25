package edu.mines.broomthompsondotadictionary;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Alexander Broom
 * @author Austin Thompson
 * 
 * Class: HeroesDataSource
 * Description: A DAO for the database, contains the Android helper for 
 * using the Java Database, as well as the Java Database itself. Also contains
 * numerous functions for unique types of queries that are needed by the app.
 *
 */
public class HeroesDataSource {
	
	private SQLiteDatabase database; // The database itself
	private SQLiteHelper dbHelper; // An object that allows the database to be interfaced with
	
	// Stores the types of columns held in the database, used whenever the database is queried 
	// to indicate that we want all data associated with an entry.
	private String[] data = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_NAME, 
			SQLiteHelper.COLUMN_FOCUS, SQLiteHelper.COLUMN_ATTACK, 
			SQLiteHelper.COLUMN_USE, SQLiteHelper.COLUMN_ROLE, SQLiteHelper.COLUMN_PIC,
			SQLiteHelper.COLUMN_ABILITIES};

	/** 
	 * HeroesDataSource: Initializes the helper.
	 * 
	 * @param ctx: current context the DAO is created in
	 */ 
	public HeroesDataSource(Context ctx) {
		dbHelper = new SQLiteHelper(ctx);
	}
	
	/** 
	 * getName: gets the name of the database 
	 */ 
	public String getName() {
		return dbHelper.getDatabaseName();
	}

	/** 
	 * open: creates the database 
	 */ 
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/** 
	 * close: closes the database 
	 */ 
	public void close() {
		dbHelper.close();
	}
	
	/** 
	 * addHero: adds a hero to the database using an array of strings, each
	 * index indicating one of the attributes
	 * 
	 * @param attrs: Specifically ordered to define a Hero object and database entry
	 * FORMAT: [NAME, FOCUS, ATTACK, USE, ROLE, PICTURE]
	 */ 
	public void addHero(String[] attrs) {
		
		// Container for each of the values to be added to the new database entry.
		ContentValues vals = new ContentValues();
		
		// Key is the string indicating the column, value is the string contianing the info
		vals.put(SQLiteHelper.COLUMN_NAME, attrs[0]);
		vals.put(SQLiteHelper.COLUMN_FOCUS, attrs[1]);
		vals.put(SQLiteHelper.COLUMN_ATTACK, attrs[2]);
		vals.put(SQLiteHelper.COLUMN_USE, attrs[3]);
		vals.put(SQLiteHelper.COLUMN_ROLE, attrs[4]);
		vals.put(SQLiteHelper.COLUMN_PIC, attrs[5]);
		vals.put(SQLiteHelper.COLUMN_ABILITIES, attrs[6]);
		
		// Insert the entry, returns the id of the row in the database
		long heroId = database.insert(SQLiteHelper.TABLE_HEROES, null, vals);
		
		// Get a cursor pointing to the above id containing all data
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, SQLiteHelper.COLUMN_ID + 
				" = " + heroId, null, null, null, null);
		cursor.close();
	}
	
	/** 
	 * getAllHeroes: Gets every hero in the database, returns them as a list 
	 */ 
	public List<Hero> getAllHeroes() {
		List<Hero> heroes = new ArrayList<Hero>();	// Container for return value
		// Query has no criteria
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, null, null, null, null, null);
		cursor.moveToFirst();
		
		// Iterate through the resulting list and instantiate Hero objects
		while (!cursor.isAfterLast()) {
			Hero hero = cursorToHero(cursor);
			heroes.add(hero);
			cursor.moveToNext();
		}
		cursor.close();
		
		return heroes;
	}
	
	/** 
	 * getHeroByName: returns a Hero with the provided name
	 * 
	 *  @param name: A string containing the name of the hero being sought
	 */ 
	public Hero getHeroByName(String name) {
		// Query database
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, SQLiteHelper.COLUMN_NAME + " = '" + name + "'", null, null, null, null);
		cursor.moveToFirst();
		Hero hero = cursorToHero(cursor);
		cursor.close();
		return hero;
	}
	
	/** 
	 * getHeroByQuery: gets a List of heroes using the specially formatted attrs array of strings
	 * 
	 *  @param attrs: Values being sought as criteria for the list of heroes
	 *  FORMAT: [NAME, FOCUS, ATTACK, USE, ROLE]
	 */ 
	public List<Hero> getHeroByQuery(String[] attrs) {
		List<Hero> heroes = new ArrayList<Hero>();
		String query = ""; // Single string that will end up containing the full query from attrs
		// Iterate through the attrs array and add them to the query string according to which
		// index they are contained in.
		for(int i = 0; i < attrs.length; i++) {
			if(attrs[i] != "") {
				switch(i) {
					case 0:	// NAME
						query += SQLiteHelper.COLUMN_NAME + " = '" + attrs[i] + "' and ";
						break;
					case 1: // FOCUS
						query += SQLiteHelper.COLUMN_FOCUS + " = '" + attrs[i] + "' and ";
						break;
					case 2: // ATTACK
						query += SQLiteHelper.COLUMN_ATTACK + " = '" + attrs[i] + "' and ";
						break;
					case 3: // USE
						query += SQLiteHelper.COLUMN_USE + " = '" + attrs[i] + "' and ";
						break;
					case 4: // ROLE
						query += SQLiteHelper.COLUMN_ROLE + " = '" + attrs[i] + "' and ";
						break;
				}
			}
		}
		if(query != "") {
			// The last "and" needs to be removed from the final query string before it can be used
			query = query.substring(0, query.length() - 5);
		}
		// Use the query string to get the cursor
		Cursor cursor = database.query(SQLiteHelper.TABLE_HEROES, data, query, null, null, null, null);
		
		cursor.moveToFirst();
		// Get the list of heroes from the cursor
		while (!cursor.isAfterLast()) {
			Hero hero = cursorToHero(cursor);
			heroes.add(hero);
			cursor.moveToNext();
		}
		cursor.close();
		
		return heroes;
	}
	
	/** 
	 * cursorToHero: Uses the cursor to instantiate a hero object.
	 * 
	 *  @param cursor: self-explanatory
	 *  @return hero: The object created by the data in the cursor
	 */ 
	private Hero cursorToHero(Cursor cursor) {
		Hero hero = new Hero();		
		hero.setId(cursor.getLong(0));
		hero.setName(cursor.getString(1));
		hero.setFocus(cursor.getString(2));
		hero.setAttack(cursor.getString(3));
		hero.setUse(cursor.getString(4));
		hero.setRole(cursor.getString(5));
		hero.setPicture(cursor.getString(6));
		hero.setAbilities(cursor.getString(7));
		return hero;
	}
	
	/** 
	 * delete: Delete the database from the provided context. 
	 */ 
	public void delete(Context ctx) {
		ctx.deleteDatabase(getName());
	}
}
