package com.personalcontactmanager.provider;

import com.db.personalcontactmanager.databasemanager.DatabaseManager;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author Vikash Kumar Karn
 *
 */
public class PersonalContactProvider extends ContentProvider {

	// Integer constants used with UriMatcher
	private static final int CONTACTS_TABLE = 1;
	private static final int CONTACTS_TABLE_ITEM = 2;

	private static final UriMatcher mmURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mmURIMatcher.addURI(PersonalContactContract.AUTHORITY, 
				PersonalContactContract.BASE_PATH, CONTACTS_TABLE);
		mmURIMatcher.addURI(PersonalContactContract.AUTHORITY, 
				PersonalContactContract.BASE_PATH +  "/#", CONTACTS_TABLE_ITEM);
	}

	private DatabaseManager dbm;

	@Override
	public boolean onCreate() {
		// there should always be minimum operations inside
		// the onCreate as it runs on the main thread
		dbm = new DatabaseManager(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		int uriType = mmURIMatcher.match(uri);

		switch(uriType) {
		case CONTACTS_TABLE:
			break;
		case CONTACTS_TABLE_ITEM:
			if (TextUtils.isEmpty(selection)) {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment();
			} else {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment() + 
						" and " + selection;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		Cursor cr = dbm.getAllCursor(projection, selection, selectionArgs, sortOrder);

		return cr;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		int uriType = mmURIMatcher.match(uri);

		switch(uriType) {
		case CONTACTS_TABLE:
			break;
		case CONTACTS_TABLE_ITEM:
			if (TextUtils.isEmpty(selection)) {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment();
			} else {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment() + 
						" and " + selection;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		int count = dbm.deleteRow(selection, selectionArgs);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int uriType = mmURIMatcher.match(uri);
		long id;
		
		switch(uriType) {
		case CONTACTS_TABLE:
			id = dbm.addRow(values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		Uri ur = ContentUris.withAppendedId(uri, id);
		return ur;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int uriType = mmURIMatcher.match(uri);

		switch(uriType) {
		case CONTACTS_TABLE:
			break;
		case CONTACTS_TABLE_ITEM:
			if (TextUtils.isEmpty(selection)) {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment();
			} else {
				selection = PersonalContactContract.Columns.TABLE_ROW_ID + "=" + uri.getLastPathSegment() + 
						" and " + selection;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		int count = dbm.updateRow(values, selection, selectionArgs);
		
		return count;
	}

}
