package com.net.finditnow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
	
public class FINDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_NAME = "regions";
    private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DATABASE_TABLE_NAME + " (region_id INTEGER PRIMARY KEY, region_name TEXT, region_lat INTEGER, region_lon INTEGER)";


    public FINDatabase(Context context) {
        super(context, "FIN_LOCAL", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
