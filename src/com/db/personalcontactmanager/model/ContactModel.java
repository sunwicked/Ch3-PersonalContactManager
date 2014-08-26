package com.db.personalcontactmanager.model;

public class ContactModel {

	private int id;
	private String name, contactNo, email;

	private byte[] byteArray;
	
	public byte[] getPhoto() {
		return byteArray;
	}

	public void setPhoto(byte[] array) {
		byteArray = array;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
