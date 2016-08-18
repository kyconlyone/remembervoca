package com.ihateflyingbugs.hsmd.data;

public class Publish {

	private int _id;
	private String title;
	
	private boolean isCheck;
	
	public Publish(int _id, String title)
	{
		this._id = _id;
		this.title = title;
	}
	
	public void setChecked(boolean isCheck){ this.isCheck = isCheck;}
	public boolean isChecked(){return isCheck;}
	
	public int get_id()
	{
		
		return _id;
	}
	
	public String getTitle()
	{
		title = title.replace("(", "\n(");
		
		return title;
	}
}
