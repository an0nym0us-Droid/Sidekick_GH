package com.example.sidekick_offline_try2;

public class UserDetail {

	private String facebook_id;
	private String access_token;
	private String request_time;
	private String device_key;
	public UserDetail(String Fb_id,String aces_token,String rt, String dk)
	{
		this.facebook_id=Fb_id;
		this.access_token=aces_token;
		this.request_time=rt;
		this.device_key=dk;
		
		
	}
	public String retfacebook_id()
	{
		return this.facebook_id;
		
		
	}
	public String retaccess_token()
	{
		return this.access_token;
		
		
	}
	public void setaccess_token(String actoken)
	{
		access_token = actoken;
		
		
	}
	public String retreq_time()
	{
		return this.request_time;
		
		
	}
	public String retdevice_key()
	{
		return this.device_key;
		
		
	}
}
