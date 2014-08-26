package com.db.personalcontactmanager.databasemanager;

public interface DatabaseConstants {
	
	String DB_NAME = "contact"; // the name of our database
	int DB_VERSION = 1; // the version of the database

	String TABLE_NAME = "contact_table";// table name

	// the names for our database columns
	String TABLE_ROW_ID = "_id";
	String TABLE_ROW_NAME = "contact_name";
	String TABLE_ROW_PHONENUM = "contact_number";
	String TABLE_ROW_EMAIL = "contact_email";
	String TABLE_ROW_PHOTOID = "photo_id";
}
