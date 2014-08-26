package com.db.personalcontactmanager;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.db.personalcontactmanager.databasemanager.DatabaseManager;
import com.db.personalcontactmanager.model.ContactModel;

public class AddNewContactActivity extends Activity implements OnClickListener {

	public final static int PICK_PHOTO_FROM_GALLERY = 1001;
	public final static int CAPTURE_PHOTO_FROM_CAMERA = 1002;
	public final static int PICK_CONTACT = 1003;

	private TextView contactName, contactPhone, contactEmail, contactPhoto;
	private Button doneButton, pickPhotoBtn;
	private ImageView capturedImg;
	ImageButton getContact;

	private Bitmap imageBitmap;
	private byte[] blob;

	private boolean photoPicked = false;
	private int reqType;
	private int rowId;

	private final static String TAG = "addnewcontactactivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reqType = getIntent().getIntExtra(ContactsMainActivity.REQ_TYPE,
				ContactsMainActivity.CONTACT_ADD_REQ_CODE);

		setContentView(R.layout.add_contact);
		bindViews();
		setListeners();

		if (reqType == ContactsMainActivity.CONTACT_UPDATE_REQ_CODE) {

			rowId = getIntent().getIntExtra(ContactsMainActivity.ITEM_POSITION,
					1);
			initialize(rowId);
		}
	}

	private void initialize(int position) {

		DatabaseManager dm = new DatabaseManager(this);
		ContactModel contactObj = dm.getRowAsObjectAlternative(position);

		contactName.setText(contactObj.getName());
		contactPhone.setText(contactObj.getContactNo());
		contactEmail.setText(contactObj.getEmail());

		setImage(contactObj.getPhoto(), capturedImg);
	}

	private void setImage(byte[] blob, ImageView img) {

		if (blob != null) {
			Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
			img.setImageBitmap(bmp);
		}
	}

	/**
	 * 
	 * @brief: bindViews
	 * 
	 * @return void
	 * 
	 * @detail: Method to bind the xml views
	 */
	private void bindViews() {

		contactName = (TextView) findViewById(R.id.contactName);
		contactPhone = (TextView) findViewById(R.id.contactPhone);
		contactEmail = (TextView) findViewById(R.id.contactEmail);
		contactPhoto = (TextView) findViewById(R.id.contactPhoto);
		doneButton = (Button) findViewById(R.id.doneBtn);
		pickPhotoBtn = (Button) findViewById(R.id.pickPhotoBtn);
		capturedImg = (ImageView) findViewById(R.id.capturedImg);
		getContact = (ImageButton) findViewById(R.id.getContact);
	}

	/**
	 * 
	 * @brief: setListeners
	 * 
	 * @return void
	 * 
	 * @detail: Method to set listeners
	 */
	private void setListeners() {
		doneButton.setOnClickListener(this);
		pickPhotoBtn.setOnClickListener(this);
		getContact.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.doneBtn:
			prepareSendData();
			break;
		case R.id.pickPhotoBtn:
			pickPhoto();
			break;
		case R.id.getContact:
			pickContact();
			break;

		}
	}

	public void pickContact() {
		try {
			Intent cIntent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(cIntent, PICK_CONTACT);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Exception while picking contact");
		}
	}

	/**
	 * 
	 * @brief: pickPhoto
	 * 
	 * @return void
	 * 
	 * @detail:
	 */
	private void pickPhoto() {

		final CharSequence[] items = { "Capture Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddNewContactActivity.this);
		builder.setTitle("Pick Photo");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (items[item].equals("Capture Photo")) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, CAPTURE_PHOTO_FROM_CAMERA);
				} else if (items[item].equals("Choose from Gallery")) {

					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select Photo"),
							PICK_PHOTO_FROM_GALLERY);

				} else if (items[item].equals("Cancel")) {

					dialog.dismiss();
					photoPicked = false;
				}
			}

		});
		builder.show();
	}

	/**
	 * 
	 * @brief: prepareSendData
	 * 
	 * @return void
	 * 
	 * @detail:
	 */
	private void prepareSendData() {

		if (TextUtils.isEmpty(contactName.getText().toString())
				|| TextUtils.isEmpty(contactPhone.getText().toString())) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					AddNewContactActivity.this);
			alertDialogBuilder.setTitle("Empty fields");
			alertDialogBuilder.setMessage("Please fill phone number and name")
					.setCancelable(true);
			alertDialogBuilder.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
			alertDialogBuilder.show();
		} else {

			ContactModel contact = new ContactModel();
			contact.setName(contactName.getText().toString());
			contact.setContactNo(contactPhone.getText().toString());
			contact.setEmail(contactEmail.getText().toString());

			if (photoPicked) {
				contact.setPhoto(blob);
			} else {
				contact.setPhoto(null);
			}

			DatabaseManager dm = new DatabaseManager(this);

			if (reqType == ContactsMainActivity.CONTACT_UPDATE_REQ_CODE) {
				dm.updateRowAlternative(rowId, contact);
			} else {
				dm.addRowAlternative(contact);
			}

			setResult(RESULT_OK);
			finish();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String path;
		Uri uri = data.getData();

		if (requestCode == PICK_PHOTO_FROM_GALLERY) {

			if (resultCode == RESULT_OK) {

				if (uri != null) {
					path = getImagePath(data.getData());
					imageBitmap = getScaledBitmap(path, 256);
					capturedImg.setImageBitmap(imageBitmap);
					blob = getBlob();
					photoPicked = true;
				} else {
					Toast.makeText(this, "Could not load image",
							Toast.LENGTH_LONG).show();
				}
			} else if (resultCode == RESULT_CANCELED) {

				photoPicked = false;
			}
		} else if (requestCode == CAPTURE_PHOTO_FROM_CAMERA) {

			if (resultCode == RESULT_OK) {
				if (uri != null) {
					path = getImagePath(data.getData());
					imageBitmap = getScaledBitmap(path, 256);
					capturedImg.setImageBitmap(imageBitmap);
					blob = getBlob();

					photoPicked = true;
				} else {
					Toast.makeText(this, "Could not load image",
							Toast.LENGTH_LONG).show();
				}
			} else if (resultCode == RESULT_CANCELED) {

				photoPicked = false;
			}
		} else if (requestCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK)

			{
				Uri contactData = data.getData();
				Cursor c = getContentResolver().query(contactData, null, null,
						null, null);
				if (c.moveToFirst()) {
					String id = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

					String hasPhone = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					if (hasPhone.equalsIgnoreCase("1")) {
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);
						phones.moveToFirst();
						contactPhone.setText(phones.getString(phones
								.getColumnIndex("data1")));

						contactName
								.setText(phones.getString(phones
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

					}

				}
			}

		}

	}

	/**
	 * 
	 * @brief: getBlob
	 * 
	 * @return byte[]
	 * 
	 * @detail: Method to get image in Blob format (byte array format)
	 */
	private byte[] getBlob() {

		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);

		byte[] byteArray = boas.toByteArray();

		return byteArray;
	}

	/**
	 * 
	 * @brief: getImagePath
	 * 
	 * @return String
	 * 
	 * @detail: Method to get the image path from the Uri
	 */
	private String getImagePath(Uri uri) {

		String[] projection = new String[] { MediaColumns.DATA };
		Cursor cursor = this.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		String path = cursor.getString(column_index);

		return path;
	}

	/**
	 * 
	 * @brief: getScaledBitmap
	 * 
	 * @return Bitmap
	 * 
	 * @detail: Method to create Scalled bitmap
	 */
	private Bitmap getScaledBitmap(String path, int maxSize) {

		Bitmap bmp = null;
		int width, height, inSampleSize;

		Options op = new Options();
		op.inJustDecodeBounds = true;
		op.inPurgeable = true;

		BitmapFactory.decodeFile(path, op);

		width = op.outWidth;
		height = op.outHeight;

		if (width == -1) {

			return null;
		}

		int max = Math.max(width, height);
		inSampleSize = 1;

		while (max > maxSize) {
			inSampleSize *= 2;
			max /= 2;
		}

		op.inJustDecodeBounds = false;
		op.inSampleSize = inSampleSize;

		bmp = BitmapFactory.decodeFile(path, op);

		return bmp;
	}

}
