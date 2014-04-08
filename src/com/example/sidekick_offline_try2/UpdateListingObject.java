package com.example.sidekick_offline_try2;



// Represents data of get_listings table

public class UpdateListingObject {
	
	private int sidekick_id;
	private String timeslot;
	private String updated_time;

	@Override
	public String toString(){
		String temp = new String();
		temp += this.retsidekickid()+" | ";
		temp += this.rettimeslot()+" | ";
		temp += this.retlastupd();
		return temp;
	}
	public UpdateListingObject(){
		timeslot="";
		updated_time="";
	}
	public void setsidekickid(int id){
		sidekick_id = id;
	}
	
	public int retsidekickid(){
		return sidekick_id;
	}
	
	public void settimeslot(String timeslot){
		this.timeslot = timeslot;
	}
	
	public String rettimeslot(){
		return timeslot;
	}
	
	public void setlastupd(String updated_time){
		this.updated_time = updated_time;
	}
	
	public String retlastupd(){
		return updated_time;
	}
	
}
