package edu.mines.broomthompsondotadictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class: SQLiteOpenHelper
 * Description: The helper class for the Java database in the DAO 
 *
 * @author Alex Broom, Austin Thompson
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	
	// The constants for each of the column names in the database.
	public static final String TABLE_HEROES = "heroes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_FOCUS = "focus";
	public static final String COLUMN_ATTACK = "attack";
	public static final String COLUMN_USE = "use";
	public static final String COLUMN_ROLE = "role";
	public static final String COLUMN_PIC = "picture";
	
	
	private static final String DATABASE_NAME = "heroes.db";
	private static final int DATABASE_VERSION = 1;
	
	// String used in creation of the database.
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_HEROES + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, " 
			+ COLUMN_FOCUS	+ " text not null, " 
			+ COLUMN_ATTACK + " text not null, " 
			+ COLUMN_USE + " text not null, " 
			+ COLUMN_ROLE + " text not null, "
			+ COLUMN_PIC + " text not null);";

	/** 
	 * SQLiteHelper: creates the object 
	 */ 
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEROES);
		onCreate(db);
	}
}
