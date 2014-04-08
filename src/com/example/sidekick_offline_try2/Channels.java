package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.util.Log;


// Class representing a category. It's objects contain the data derived from 
// userCategories Table.

public class Channels {
	
	private String category_name;
	private String channel_name;
	private int id;
	
	public void setCategoryName(String category_name){
		this.category_name=category_name;
	}
	
	public String retCategoryName(){
		return category_name;
	}
	
	public void setChannelName(String channel_name){
		this.channel_name=channel_name;
	}
	
	public String retChannelName(){
		return channel_name;
	}
	
	public void setId(int channel_id){
		this.id=channel_id;
	}
	
	public int retId(){
		return id;
	}
	
}
