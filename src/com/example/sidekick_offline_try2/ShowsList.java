package com.example.sidekick_offline_try2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShowsList {

	// This class represents a item to be shown in ChannelListing.
	public String st_time;
	public String end_time;
	public long timeQueried;
	String show_name;
	String description;
	String episode_description;
	Integer channel_id;
	String image_url;
	String program_id;
	int listing_id;
	public String retelecast;
	String youtube;
	String[] week = new String[] {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

	public ShowsList(){
		st_time="";
		end_time="";
		show_name="";
		description="";
		episode_description="";

		image_url="";
		program_id="";
		listing_id=-1;
	}
	
	public ShowsList(ShowsList show){
		this.st_time=show.st_time;
		this.end_time=show.end_time;
		timeQueried=show.getTimeQueried();
		show_name=show.getShowName();
		description=show.getDescription();
		episode_description=show.getEpisodeDescription();
		channel_id=show.getChannelId();
		image_url=show.getImageURL();
		program_id=show.getProgramId();
		listing_id=show.getListingId();
	}
	
	@Override
	public String toString(){
		return String.valueOf(this.getListingId());
	}
	public void setDescription(String description){
		this.description=description;
	}
	
	public void setEpisodeDescription(String episode_description){
		this.episode_description=episode_description;
	}
	
	public Date getEndTime(){
		if(end_time.equals("")){
			return null;
		}
		else{
		try {
			return DateUtil.formatTime(this.end_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	}
	
	
	
	public Date getStartTime(){
		if(st_time.equals("")){
			return null;
		}
		try {
			
			return DateUtil.formatTime(this.st_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	public boolean showRem(){
		if(st_time.equals(""))
			return false;
		Date date=null;
		try {
			date = DateUtil.formatTime(st_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(date.getTime()<System.currentTimeMillis())
			return false;
		else
			return true;
	}
	public String getDOW(){
		String day = "";
		ML.log("ShowsListImp","Hello");
		if(st_time.equals("")){
			ML.log("ShowsListImp", "REturning Empty");
			return "";
			
		}
		Date date=null;
		try {
			date = DateUtil.formatTime(st_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		Calendar show_cal = Calendar.getInstance();
		show_cal.setTimeInMillis(date.getTime());
		Calendar present_cal = Calendar.getInstance();
		int i;
		
		for(i=0;i<4;i++){
			present_cal.setTimeInMillis(System.currentTimeMillis()+i*24*60*60*1000);
			if(present_cal.get(Calendar.DATE)==show_cal.get(Calendar.DATE))
				break;
		}
		
		ML.log("ShowsListImp","i ki value : " + String.valueOf(i));

		switch(i){
		case 0:
			day="Today";
			break;
		case 1:
			day="Tomorrow";
			break;
		case 2:
		case 3:
			day=week[present_cal.get(Calendar.DAY_OF_WEEK)-1];
		}
		
		ML.log("ShowsListImp",day);
		return day;
	}
	
	public String getRepeatDOW(){
		String day = "";
		ML.log("ShowsListImp","Hello");
		if(retelecast.equals("")){
			ML.log("ShowsListImp", "REturning Empty");
			return "";
			
		}
		Date date=null;
		try {
			date = DateUtil.formatTime(retelecast);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM");
		Calendar show_cal = Calendar.getInstance();
		show_cal.setTimeInMillis(date.getTime());
		Calendar present_cal = Calendar.getInstance();
		int i;
		
		for(i=0;i<4;i++){
			present_cal.setTimeInMillis(System.currentTimeMillis()+i*24*60*60*1000);
			if(present_cal.get(Calendar.DATE)==show_cal.get(Calendar.DATE))
				break;
		}
		
		ML.log("ShowsListImp","i ki value : " + String.valueOf(i));

		switch(i){
		case 0:
			day="Today";
			break;
		case 1:
			day="Tomorrow";
			break;
		case 2:
		case 3:
			day=week[present_cal.get(Calendar.DAY_OF_WEEK)-1];
		default:
			day = sdf.format(date);
		}
		
		ML.log("ShowsListImp",day);
		return day;
	}
	
	
	public void setTiming(String st_time,String end_time){
		this.st_time=st_time;
		this.end_time=end_time;
	}
	public void setShowName(String show_name){
		this.show_name=show_name;
	}
	
	public void setRetelecast(String retelecast){
		this.retelecast=retelecast;
	}
	
	
	public void setYoutube(String youtube){
		this.youtube=youtube;
	}
	
	public void setChannelId(Integer channel_id){
		this.channel_id=channel_id;
	}
	public void setListingId(int listing_id){
		this.listing_id=listing_id;
	}
	
	public void setImageURL(String image_url){
		this.image_url=image_url;
	}
	public void setProgramId(String program_id){
		this.program_id=program_id;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getRetelecast(){
		return retelecast;
	}
	
	public String getYoutube(){
		return youtube;
	}
	
	public String getEpisodeDescription(){
		return episode_description;
	}
	
	public long getTimeQueried(){
		return timeQueried;
	}
	
	public String getTiming() throws ParseException{
		if(st_time.equals("")||end_time.equals("")){
			return ""	;
		}
		ML.log("Timing", getDOW());
		String start = String.format("%tR",DateUtil.formatTime(st_time));
		String end = String.format("%tR", DateUtil.formatTime(end_time));
		return start+" - "+end;
	}
	public String getRepeatTiming() throws ParseException{
		if(retelecast.equals("")){
			return ""	;
		}
		ML.log("Timing", getDOW());
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		
		String start = sdf.format(DateUtil.formatTime(retelecast));
		return start;
	}
	public String getShowName(){
		return show_name;
	}
	public String getProgramId(){
		return program_id;
	}
	public String getImageURL(){
		return image_url;
	}
	public Integer getChannelId(){
		return channel_id;
	}
	public Integer getListingId(){
		return listing_id;
	}
	
	
	
}
