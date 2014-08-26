package com.personalcontactmanager.provider;

import com.db.personalcontactmanager.databasemanager.DatabaseConstants;
import com.db.personalcontactmanager.databasemanager.DatabaseManager;

import android.content.ContentResolver;
import android.net.Uri;

public final class PersonalContactContract implements DatabaseConstants{
	
	/**
	 * The authority of the PersonalContactProvider
	 */
	public static final String AUTHORITY = "com.personalcontactmanager.provider";

	public static final String BASE_PATH = "contacts";

	/**
	 * The Uri for the top-level PersonalContactProvider
	 * authority
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY 
			+ "/" + BASE_PATH);

	/**
	 * The mime type of a directory of items.
	 */
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + 
			"/vnd.com.personalcontactmanager.provider.table";
	/**
	 * The mime type of a single item.
	 */
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + 
			"/vnd.com.personalcontactmanager.provider.table_item";

	/**
	 * A projection of all columns 
	 * in the items table.
	 */
	public static final String[] PROJECTION_ALL = { "_id", 
		"contact_name", "contact_number", 
		"contact_email", "photo_id" };

	/**
	 * The default sort order for 
	 * queries containing NAME fields.
	 */
	//public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
	
	public static final class Columns {
		public static String TABLE_ROW_ID = "_id";
		public static String TABLE_ROW_NAME  = "contact_name";
		public static String TABLE_ROW_PHONENUM = "contact_number";
		public static String TABLE_ROW_EMAIL = "contact_email";
		public static String TABLE_ROW_PHOTOID = "photo_id";
	}
}
