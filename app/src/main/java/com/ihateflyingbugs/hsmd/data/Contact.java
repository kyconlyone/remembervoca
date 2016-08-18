package com.ihateflyingbugs.hsmd.data;

public class Contact {
	String name;
	String number;
	
	boolean isSelect= false;
	
	public Contact(String name, String number){
		this.name = name;
		this.number = number;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getNumber(){
		return this.number;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	
	

}
