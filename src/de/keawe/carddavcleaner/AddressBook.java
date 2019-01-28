package de.keawe.carddavcleaner;

import java.io.File;

public class AddressBook {

	private String address;
	private String problem = "Not initialized";
	private String username = null;
	private String password = null;
	private boolean fixFieldSyntax = false;
	private boolean dropEmptyFields = false;
	private boolean dropEmptyContacts = false;

	public AddressBook(String address, String username, String password) {
		if (address == null) {
			problem = "Location of addressbook is null";
			return;
		}
		address=address.trim();
		if (address.isEmpty()) {
			problem = "Location of addressbook is empty";
			return;
		}
		if (!address.startsWith("file://")) {
			this.username = username;
			this.password = password;
		}
		this.address = address;
		problem = null;
	}
	
	public void commit() {
		// TODO Auto-generated method stub
		System.out.println("AddressBook.commit not implemented");
	}
	
	public void enableDropEmptyContacts() {
		dropEmptyContacts   = true;
	}

	public void enableDropEmptyFields() {
		dropEmptyFields  = true;
	}

	public void enableSytaxFixing() {
		fixFieldSyntax = true;
	}
	
	public MergeCandidate getMergeCandidate() {
		// TODO Auto-generated method stub
		System.out.println("AddressBook.getMergeCandidate not implemented");
		return null;
	}

	public void loadContacts(File backupPath) {
		// TODO Auto-generated method stub
		System.out.println("AddressBook.loadContacts not implemented");
	}

	public String problem() {
		return problem;
	}








}
