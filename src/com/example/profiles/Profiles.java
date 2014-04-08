package com.example.profiles;

import java.util.ArrayList;

import android.util.Log;

public class Profiles {

	int profile_id;
	int provider;
	String profile_name;
	String provider_name;
	String base;
	ArrayList<String> addons;
	
	
	//Setter methods
	
	public void setProfileId(int profile_id){
		this.profile_id = profile_id;
	}
	
	public void setProvider(int provider){
		this.provider= provider;
	}
	
	public void setBasePack(String base){
		this.base = base;
	}
	
	public void setProfileName(String profile_name){
		this.profile_name = profile_name;
	}
	
	public void setProviderName(String provider_name){
		this.provider_name = provider_name;
	}
	
	public void setAddonPack(ArrayList<String> addonsStringList){
		this.addons = addonsStringList;
	}
	
	//Get methods
	
	public int getProfileId(){
		return this.profile_id;
	}
	
	public int getProvider(){
		return this.provider;
	}
	
	public String getBase(){
		return this.base;
	}
	
	public String getProfileName(){
		return this.profile_name;
	}
	
	public String getProviderName(){
		return this.provider_name;
	}
	
	
	
	public ArrayList<String> getAddonsString(){
		return addons;
	}
	
}
