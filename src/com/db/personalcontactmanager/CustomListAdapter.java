package com.db.personalcontactmanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.db.personalcontactmanager.databasemanager.DatabaseManager;
import com.db.personalcontactmanager.model.ContactModel;

public class CustomListAdapter extends BaseAdapter {
	DatabaseManager dm;
	ArrayList<ContactModel> contactModelList;
	LayoutInflater inflater;
	Context _context;

	public CustomListAdapter(Context context) {

		contactModelList = new ArrayList<ContactModel>();
		_context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dm = new DatabaseManager(_context);
		contactModelList = dm.getAllDataAlternative();

	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		contactModelList = dm.getAllDataAlternative();

	}

	public void delRow(int delPosition) {
		// TODO Auto-generated method stub

		dm.deleteRowAlternative(contactModelList.get(delPosition).getId());
		contactModelList.remove(delPosition);

	}


	@Override
	public int getCount() {
		return contactModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_list_row, null);
			vHolder = new ViewHolder();

			vHolder.contact_name = (TextView) convertView
					.findViewById(R.id.contact_name);
			vHolder.contact_phone = (TextView) convertView
					.findViewById(R.id.contact_phone);
			vHolder.contact_email = (TextView) convertView
					.findViewById(R.id.contact_email);
			vHolder.contact_photo = (ImageView) convertView
					.findViewById(R.id.contact_photo);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		ContactModel contactObj = contactModelList.get(position);

		vHolder.contact_name.setText(contactObj.getName());
		vHolder.contact_phone.setText(contactObj.getContactNo());
		vHolder.contact_email.setText(contactObj.getEmail());
		setImage(contactObj.getPhoto(), vHolder.contact_photo);

		return convertView;
	}

	class ViewHolder {
		ImageView contact_photo;
		TextView contact_name, contact_phone, contact_email;
	}

	/**
	 * 
	 * @brief: setImage
	 *
	 * @return void
	 *
	 * @detail: returns the Image in the Blob format (byte array format)
	 */
	private void setImage(byte[] blob, ImageView img) {

		if(blob != null) {
			Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
			img.setImageBitmap(bmp);
		}
	}
}
