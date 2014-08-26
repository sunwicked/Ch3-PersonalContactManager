package com.db.personalcontactmanager.databasemanager;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.db.personalcontactmanager.model.ContactModel;

public class DatabaseManager implements DatabaseConstants {

	private SQLiteDatabase db; // a reference to the database manager class.

	private Context context;

	public DatabaseManager(Context context) {
		this.context = context;

		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}

	// the beginnings our SQLiteOpenHelper class
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// the SQLite query string that will create our column database
			// table.
			String newTableQueryString = "create table " + TABLE_NAME + " ("
					+ TABLE_ROW_ID
					+ " integer primary key autoincrement not null,"
					+ TABLE_ROW_NAME + " text not null," + TABLE_ROW_PHONENUM
					+ " text not null," + TABLE_ROW_EMAIL + " text not null,"
					+ TABLE_ROW_PHOTOID + " BLOB" + ");";

			// execute the query string to the database.
			db.execSQL(newTableQueryString);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// LATER, WE WOULD SPECIFIY HOW TO UPGRADE THE DATABASE
			// FROM OLDER VERSIONS.
			String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
			db.execSQL(DROP_TABLE);
			onCreate(db);

		}

	}

	public SQLiteDatabase getDataBase() {
		return db;
	}

	public void addRow(ContactModel contactObj) {
		ContentValues values = prepareData(contactObj);
		// ask the database object to insert the new data
		try {
			db.insert(TABLE_NAME, null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString()); // prints the error message to
			// the
			// log
			e.printStackTrace(); // prints the stack trace to the log
		}
	}

	public long addRow(ContentValues values) {

		long id = -1;
		// ask the database object to insert the new data
		try {
			id = db.insert(TABLE_NAME, null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString()); // prints the error message to
			// the
			// log
			e.printStackTrace(); // prints the stack trace to the log
		}

		return id;
	}

	/**
	 * 
	 * @param contactObj
	 */
	public void addRowAlternative(ContactModel contactObj) {

		String insertStatment = "INSERT INTO " + TABLE_NAME + " ("
				+ TABLE_ROW_NAME + "," + TABLE_ROW_PHONENUM + ","
				+ TABLE_ROW_EMAIL + "," + TABLE_ROW_PHOTOID + ") " + " VALUES "
				+ "(?,?,?,?)";

		SQLiteStatement s = db.compileStatement(insertStatment);
		s.bindString(1, contactObj.getName());
		s.bindString(2, contactObj.getContactNo());
		s.bindString(3, contactObj.getEmail());
		if (contactObj.getPhoto() != null)
			s.bindBlob(4, contactObj.getPhoto());

		s.execute();
	}

	private ContentValues prepareData(ContactModel contactObj) {

		ContentValues values = new ContentValues();
		values.put(TABLE_ROW_NAME, contactObj.getName());
		values.put(TABLE_ROW_PHONENUM, contactObj.getContactNo());
		values.put(TABLE_ROW_EMAIL, contactObj.getEmail());
		values.put(TABLE_ROW_PHOTOID, contactObj.getPhoto());
		return values;
	}

	public ContactModel getRowAsObject(int rowID) {

		ContactModel rowContactObj = new ContactModel();
		Cursor cursor;

		try {

			cursor = db.query(TABLE_NAME, new String[] { TABLE_ROW_ID,
					TABLE_ROW_NAME, TABLE_ROW_PHONENUM, TABLE_ROW_EMAIL,
					TABLE_ROW_PHOTOID }, TABLE_ROW_ID + "=" + rowID, null,
					null, null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					prepareSendObject(rowContactObj, cursor);
				} while (cursor.moveToNext()); // try to move the cursor's
				// pointer forward one position.
			}
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		return rowContactObj;
	}

	/**
	 * 
	 * @param rowID
	 * @return
	 */
	public ContactModel getRowAsObjectAlternative(int rowID) {

		ContactModel rowContactObj = new ContactModel();
		Cursor cursor;

		try {

			// query to fetch all the columns and rows of the table
			String queryStatement = "SELECT * FROM " + TABLE_NAME + " WHERE "
					+ TABLE_ROW_ID + "=?";

			cursor = db.rawQuery(queryStatement,
					new String[] { String.valueOf(rowID) });
			cursor.moveToFirst();

			// if (!cursor.isAfterLast()) {
			rowContactObj = new ContactModel();
			rowContactObj.setId(cursor.getInt(0));
			prepareSendObject(rowContactObj, cursor);
			// }

		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		return rowContactObj;
	}

	public ArrayList<ContactModel> getAllData() {

		ArrayList<ContactModel> allRowsObj = new ArrayList<ContactModel>();
		Cursor cursor;
		ContactModel rowContactObj;

		String[] columns = new String[] { TABLE_ROW_ID, TABLE_ROW_NAME,
				TABLE_ROW_PHONENUM, TABLE_ROW_EMAIL, TABLE_ROW_PHOTOID };

		try {

			cursor = db
					.query(TABLE_NAME, columns, null, null, null, null, null);
			cursor.moveToFirst();

			if (!cursor.isAfterLast()) {
				do {
					rowContactObj = new ContactModel();
					rowContactObj.setId(cursor.getInt(0));
					prepareSendObject(rowContactObj, cursor);
					allRowsObj.add(rowContactObj);

				} while (cursor.moveToNext()); // try to move the cursor's
				// pointer forward one position.
			}
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		return allRowsObj;

	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<ContactModel> getAllDataAlternative() {

		ArrayList<ContactModel> allRowsObj = new ArrayList<ContactModel>();
		Cursor cursor;
		ContactModel rowContactObj;

		try {

			// query to fetch all the columns and rows of the table
			String queryStatement = "SELECT" + " * " + "FROM " + TABLE_NAME;

			
			cursor = db.rawQuery(queryStatement, null);

			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					rowContactObj = new ContactModel();
					rowContactObj.setId(cursor.getInt(0));
					prepareSendObject(rowContactObj, cursor);
					allRowsObj.add(rowContactObj);

				} while (cursor.moveToNext()); // try to move the cursor's
				// pointer forward one position.
			}
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return allRowsObj;
	}

	public Cursor getAllCursor(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Cursor cr = null;
		try {
			cr = db.query(TABLE_NAME, projection, selection, selectionArgs,
					null, null, sortOrder);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return cr;
	}

	private void prepareSendObject(ContactModel rowObj, Cursor cursor) {
		rowObj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_ROW_ID)));
		rowObj.setName(cursor.getString(cursor
				.getColumnIndexOrThrow(TABLE_ROW_NAME)));
		rowObj.setContactNo(cursor.getString(cursor
				.getColumnIndexOrThrow(TABLE_ROW_PHONENUM)));
		rowObj.setEmail(cursor.getString(cursor
				.getColumnIndexOrThrow(TABLE_ROW_EMAIL)));
		rowObj.setPhoto(cursor.getBlob(cursor
				.getColumnIndexOrThrow(TABLE_ROW_PHOTOID)));
	}

	public int deleteRow(int rowID) {

		int count = 0;

		// ask the database manager to delete the row of given id
		try {
			count = db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		return count;
	}

	public int deleteRow(String whereClause, String[] whereArgs) {

		int count = 0;

		// ask the database manager to delete the row of given id
		try {
			count = db.delete(TABLE_NAME, whereClause, whereArgs);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		return count;
	}

	/**
	 * 
	 * @param rowId
	 */
	public void deleteRowAlternative(int rowId) {

		String deleteStatement = "DELETE FROM " + TABLE_NAME + " WHERE "
				+ TABLE_ROW_ID + "=?";
		SQLiteStatement s = db.compileStatement(deleteStatement);
		s.bindLong(1, rowId);
		s.executeUpdateDelete();
	}

	public void updateRow(int rowId, ContactModel contactObj) {

		ContentValues values = prepareData(contactObj);

		String whereClause = TABLE_ROW_ID + "=?";
		String whereArgs[] = new String[] { String.valueOf(rowId) };

		db.update(TABLE_NAME, values, whereClause, whereArgs);

	}

	public int updateRow(ContentValues values, String whereClause,
			String[] whereArgs) {

		int count = 0;
		count = db.update(TABLE_NAME, values, whereClause, whereArgs);

		return count;
	}

	/**
	 * 
	 * @param rowId
	 * @param contactObj
	 */
	public void updateRowAlternative(int rowId, ContactModel contactObj) {

		String updateStatement = "UPDATE " + TABLE_NAME + " SET "
				+ TABLE_ROW_NAME + "=?," + TABLE_ROW_PHONENUM + "=?,"
				+ TABLE_ROW_EMAIL + "=?," + TABLE_ROW_PHOTOID + "=?"
				+ " WHERE " + TABLE_ROW_ID + "=?";

		SQLiteStatement s = db.compileStatement(updateStatement);
		s.bindString(1, contactObj.getName());
		s.bindString(2, contactObj.getContactNo());
		s.bindString(3, contactObj.getEmail());
		if (contactObj.getPhoto() != null)
			s.bindBlob(4, contactObj.getPhoto());
		s.bindLong(5, rowId);

		s.executeUpdateDelete();
	}

}
