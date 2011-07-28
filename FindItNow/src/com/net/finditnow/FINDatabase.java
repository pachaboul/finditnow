package com.net.finditnow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
	
public class FINDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String REGION_TABLE_CREATE = "CREATE TABLE regions (rid INTEGER PRIMARY KEY, name TEXT, full_name TEXT, latitude INTEGER, longitude INTEGER)";
    private static final String COLOR_TABLE_CREATE = "CREATE TABLE colors (rid INTEGER PRIMARY KEY, color1 TEXT, color2 TEXT)";
    private static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE categories (cat_id INTEGER PRIMARY KEY, name TEXT, full_name TEXT, parent INTEGER)";
    private static final String BUILDINGS_TABLE_CREATE = "CREATE TABLE buildings (bid INTEGER PRIMARY KEY, rid INTEGER, name TEXT, latitude INTEGER, longitude INTEGER)";
    private static final String FLOORS_TABLE_CREATE = "CREATE TABLE floors (fid INTEGER PRIMARY KEY, bid INTEGER, fnum INTEGER, name TEXT)";
    private static final String ITEMS_TABLE_CREATE = "CREATE VIRTUAL TABLE items USING fts3 (item_id, rid, latitude, longitude, special_info, fid, not_found_count, username, cat_id)";
    
    public FINDatabase(Context context) {
        super(context, "FIN_LOCAL", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REGION_TABLE_CREATE);
        db.execSQL(COLOR_TABLE_CREATE);
        db.execSQL(CATEGORIES_TABLE_CREATE);
        db.execSQL(BUILDINGS_TABLE_CREATE);
        db.execSQL(FLOORS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL("DROP TABLE items");
			db.execSQL(ITEMS_TABLE_CREATE);
		}
	}
}
