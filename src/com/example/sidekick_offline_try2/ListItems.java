package com.example.sidekick_offline_try2;


public class ListItems {

	// This class represents a item to be shown in ChannelListing.
	String channel_name;
	Integer channel_id;
	
	public void setChannelName(String channel_name){
		this.channel_name=channel_name;
	}
	
	public void setChannelId(Integer channel_id){
		this.channel_id=channel_id;
	}
	
	public String getChannelName(){
		return channel_name;
	}
	
	public Integer getChannelId(){
		return channel_id;
	}
	
	
	
}
