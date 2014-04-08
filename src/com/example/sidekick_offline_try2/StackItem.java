package com.example.sidekick_offline_try2;

import java.util.ArrayList;

public class StackItem {
	
	public Integer rc;
	public Integer freq;
	public ArrayList<Integer> data;
	public int funcid;
	public StackItem(){
		rc=-1;
		
		data = new ArrayList<Integer>();
	}
	public void setrc(Integer rc){
		this.rc = rc;
	}
	public void setfuncid(Integer funcid){
		this.funcid = funcid;
	}
	public void setfreq(Integer freq){
		this.freq = freq;
	}
	public void setdata(String dat){
		for(String item : dat.split(",")){
			data.add(Integer.parseInt(item));
		}
	}
	
	public Integer retrc(){
		return rc;
	}
	public Integer retfreq(){
		return freq;
	}
	public Integer retfuncid(){
		return funcid;
	}
	public ArrayList<Integer> retdata(){
		return data;
	}
		
	
}
