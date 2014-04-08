package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.util.Log;


// Class representing a category. It's objects contain the data derived from 
// userCategories Table.

public class Category {
	
	private String category_name;
	private int category_id;
	public ArrayList<Integer> channel_list;
	private int order;
	private int is_visible;
	
	public Category(){
		channel_list = new ArrayList<Integer>();
	}
	
	public void setCategoryName(String category_name){
		this.category_name=category_name;
	}
	
	public String retCategoryName(){
		return category_name;
	}
	
	
	public void setWholeChannelList(String clist){
		String temp = clist.substring(1, clist.length()-1);
		Log.i("temporary",temp);
		if(clist.equals("[]"))
		return;
		else{
			Log.e("Ientered", "I hav entered");
		for(String single_channel : temp.split(", ")){
			channel_list.add(Integer.parseInt(single_channel));
		}
		}
	}
	
	public void setOrder(int order){
		this.order=order;
	}
	
	public int retOrder(){
		return order;
	}
	
	public void setId(int category_id){
		this.category_id=category_id;
	}
	
	public int retId(){
		return category_id;
	}
	
	public void setVisibility(int is_visible){
		this.is_visible= is_visible;
	}
	
	public int retVisibility(){
		return this.is_visible;
	}
	
	public void insertChannel(int pos,int chid){
		channel_list.add(pos, chid);
	}
	
	public void deleteChannel(int pos){
		channel_list.remove(pos);
	}
	public String retWholeChannelList(){
		return channel_list.toString();
	}
	
	public ArrayList<Integer> returnTotalAsIntArray(){
		return channel_list;
	}
	
	public ArrayList<Integer> returnAsIntArray(){
		ArrayList<Integer> tempChannelList = new ArrayList<Integer>();
		for(Integer item : channel_list){
			if(item!=-1)
				tempChannelList.add(item);
		}
		return tempChannelList;
	}
	
}
