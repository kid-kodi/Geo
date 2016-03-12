package com.aqs.db.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aqs.db.models.GeoPoint;
import com.aqs.db.models.Pacerelle;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "db";

	// Table Names
	private static final String TABLE_PACERELLE = "pacerelles";
	private static final String TABLE_GEOPOINT = "geopoints";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// NOTES Table - column nmaes
	private static final String KEY_PACERELLE_CODE = "code";
	private static final String KEY_PACERELLE_DESC = "description";
	private static final String KEY_PACERELLE_GEONUM = "geo_num";
	private static final String KEY_PACERELLE_PHOTO = "photo";

	// GEOPOINTS Table - column names
	private static final String KEY_GEOPOINT_PACERELLE_ID = "pacerelle_id";
	private static final String KEY_GEOPOINT_LAT = "latitude";
	private static final String KEY_GEOPOINT_LON = "longitude";


	// Table Create Statements
	// Pacerelle table create statement
	private static final String CREATE_TABLE_PACERELLE = "CREATE TABLE "
			+ TABLE_PACERELLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PACERELLE_PHOTO
			+ " TEXT,"+ KEY_PACERELLE_CODE
			+ " TEXT," + KEY_PACERELLE_DESC + " INTEGER," + KEY_PACERELLE_GEONUM + " INTEGER,"
			+ KEY_CREATED_AT + " DATETIME" + ")";

	// GeoPoint table create statement
	private static final String CREATE_TABLE_GEOPOINT = "CREATE TABLE " + TABLE_GEOPOINT
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GEOPOINT_PACERELLE_ID + " INTEGER,"
			+ KEY_GEOPOINT_LAT + " TEXT," + KEY_GEOPOINT_LON + " TEXT,"
			+ KEY_CREATED_AT + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_PACERELLE);
		db.execSQL(CREATE_TABLE_GEOPOINT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACERELLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOPOINT);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a todo
	 */
	public long createPacerelle(Pacerelle pacerelle) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PACERELLE_CODE, pacerelle.getCode());
		values.put(KEY_PACERELLE_PHOTO, pacerelle.getPhoto());
		values.put(KEY_PACERELLE_DESC, pacerelle.getDescription());
		values.put(KEY_PACERELLE_GEONUM, 0 + "");
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long pacerelle_id = db.insert(TABLE_PACERELLE, null, values);

		return pacerelle_id;
	}

	/*
	 * get single todo
	 */
	public Pacerelle getPacerelle(long pacerelle_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PACERELLE + " WHERE "
				+ KEY_ID + " = " + pacerelle_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Pacerelle td = new Pacerelle();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		td.setCode((c.getString(c.getColumnIndex(KEY_PACERELLE_CODE))));
		td.setPhoto((c.getString(c.getColumnIndex(KEY_PACERELLE_PHOTO))));
		td.setDescription((c.getString(c.getColumnIndex(KEY_PACERELLE_DESC))));
		td.setGeoNum(getPacerelleGeoCount((int) pacerelle_id));
		td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

		return td;
	}

	/**
	 * getting all todos
	 * */
	public ArrayList<Pacerelle> getAllPacerelles() {
		ArrayList<Pacerelle> pacerelles = new ArrayList<Pacerelle>();
		String selectQuery = "SELECT  * FROM " + TABLE_PACERELLE;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Pacerelle td = new Pacerelle();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setCode((c.getString(c.getColumnIndex(KEY_PACERELLE_CODE))));
				td.setPhoto((c.getString(c.getColumnIndex(KEY_PACERELLE_PHOTO))));
				td.setDescription((c.getString(c.getColumnIndex(KEY_PACERELLE_DESC))));
				td.setGeoNum(getPacerelleGeoCount(c.getInt((c.getColumnIndex(KEY_ID)))));
				td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				// adding to todo list
				pacerelles.add(td);
			} while (c.moveToNext());
		}

		return pacerelles;
	}

	/*
	 * getting todo count
	 */
	public int getPacerelleCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PACERELLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	public int getPacerelleGeoCount(int pacerelle_id) {
		String countQuery = "SELECT  * FROM " + TABLE_GEOPOINT + " WHERE "
				+ KEY_GEOPOINT_PACERELLE_ID + " = " + pacerelle_id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	public ArrayList<GeoPoint> getPacerelleAllGeo(int pacerelle_id) {
		ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
		String countQuery = "SELECT  * FROM " + TABLE_GEOPOINT+ " WHERE "
				+ KEY_GEOPOINT_PACERELLE_ID + " = " + pacerelle_id;;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(countQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				GeoPoint t = new GeoPoint();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setPacerelle_id(c.getInt((c.getColumnIndex(KEY_GEOPOINT_PACERELLE_ID))));
				t.setLatitude(c.getDouble(c.getColumnIndex(KEY_GEOPOINT_LAT)));
				t.setLongitude(c.getDouble(c.getColumnIndex(KEY_GEOPOINT_LON)));

				// adding to tags list
				geoPoints.add(t);
			} while (c.moveToNext());
		}
		return geoPoints;
	}

	/*
	 * Updating a todo
	 */
	public int updatePacerelle(Pacerelle pacerelle) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PACERELLE_CODE, pacerelle.getCode());
		values.put(KEY_PACERELLE_PHOTO, pacerelle.getPhoto());
		values.put(KEY_PACERELLE_DESC, pacerelle.getDescription());

		// updating row
		return db.update(TABLE_PACERELLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(pacerelle.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deletePacerelle(long pacerelle_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PACERELLE, KEY_ID + " = ?",
				new String[] { String.valueOf(pacerelle_id) });
	}

	// ------------------------ "tags" table methods ----------------//

	/*
	 * Creating tag
	 */
	public long createGeoPoint(GeoPoint geoPoint) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GEOPOINT_PACERELLE_ID, geoPoint.getPacerelle_id());
		values.put(KEY_GEOPOINT_LAT, geoPoint.getLatitude());
		values.put(KEY_GEOPOINT_LON, geoPoint.getLongitude());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long geoPoint_id = db.insert(TABLE_GEOPOINT, null, values);

		return geoPoint_id;
	}

	/**
	 * getting all tags
	 * */
	public List<GeoPoint> getAllGeoPoints() {
		List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
		String selectQuery = "SELECT  * FROM " + TABLE_GEOPOINT;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				GeoPoint t = new GeoPoint();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setPacerelle_id(c.getInt((c.getColumnIndex(KEY_GEOPOINT_PACERELLE_ID))));
				t.setLatitude(c.getDouble(c.getColumnIndex(KEY_GEOPOINT_LAT)));
				t.setLongitude(c.getDouble(c.getColumnIndex(KEY_GEOPOINT_LON)));

				// adding to tags list
				geoPoints.add(t);
			} while (c.moveToNext());
		}
		return geoPoints;
	}

	/*
	 * Updating a tag
	 */
	public int updateGeoPoint(GeoPoint geoPoint) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GEOPOINT_PACERELLE_ID, geoPoint.getPacerelle_id());
		values.put(KEY_GEOPOINT_LAT, geoPoint.getLatitude());
		values.put(KEY_GEOPOINT_LON, geoPoint.getLongitude());

		// updating row
		return db.update(TABLE_GEOPOINT, values, KEY_ID + " = ?",
				new String[] { String.valueOf(geoPoint.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deleteGeo(long geo_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GEOPOINT, KEY_ID + " = ?",
				new String[] { String.valueOf(geo_id) });
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
