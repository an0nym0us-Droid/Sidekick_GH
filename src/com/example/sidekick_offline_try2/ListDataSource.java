package com.example.sidekick_offline_try2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.profiles.Profiles;

public class ListDataSource {

  /**database : database object to handle queries.
   * dbHelper: instance of MySQLiteHelper.
   */
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  String domain;
  Context mContext;
  ArrayList<ShowsList> showNameDesc;
  public ListDataSource(Context context,String domain) {
	//context.deleteDatabase("genres.db");
	  mContext = context;
	  this.domain = domain;
    dbHelper = new MySQLiteHelper(context);
   showNameDesc = new ArrayList<ShowsList>();
  }

  public ListDataSource createDatabase() throws SQLException 
  {
      try {
          dbHelper.createDataBase();
      } 
      catch (IOException mIOException) 
      {
          Log.e("tag", mIOException.toString() + "  UnableToCreateDatabase");
          throw new Error("UnableToCreateDatabase");
      }
      return this;
  }
  public ListDataSource open() throws SQLException {
	  try 
      {
          dbHelper.openDataBase();
          database = dbHelper.getWritableDatabase();
      } 
      catch (SQLException mSQLException) 
      {
          Log.e("tag", "open >>"+ mSQLException.toString());
          throw mSQLException;
      }
      return this;
  }

  public void close() {
    dbHelper.close();
  }
 
  /**This function returns a list of Categories.
   * Each item of this list is object of class Category
   * and contains category name and list of channel ids that belong to this category.
   */
  public ArrayList<Category> getCategories(int type) {
	  String table_name;
	  if(type==0)
		  table_name = "userCategories";
	  else
		  table_name = "userDefaultCategories";
    ArrayList<Category> categories = new ArrayList<Category>();
    Cursor cursor = database.rawQuery("SELECT * FROM "+table_name,new String[] {});
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Category cat = cursorToitem(cursor);
      categories.add(cat);
      cursor.moveToNext();
    }
    Comparator<Category> comp = new Comparator<Category>(){
    	public int compare(Category ob1,Category ob2){
    		int c1= ob1.retOrder();
    		int c2 = ob2.retOrder();
    		return c1-c2;
    	}
    };
    Collections.sort(categories, comp);
    cursor.close();
    return categories;
  }
  
  /**
   * Converting cursor to Category. Setting appropriate fields of category object.
   */
  private Category cursorToitem(Cursor cursor) {
		Category category = new Category();
		category.setId(cursor.getInt(0));
		
	    category.setCategoryName(cursor.getString(1));
	    Log.i("Setting CATNAME : ", cursor.getString(1));
	    category.setOrder(cursor.getInt(2));
	    category.setWholeChannelList(cursor.getString(3));
	    category.setVisibility(cursor.getInt(4));
	    return category;
  }
  
  
  public void fillUserCategories(String allChannels,HashMap<String,String> map){
	  database.execSQL("DELETE FROM userCategories");
	  database.execSQL("DELETE FROM userDefaultCategories");
	  database.execSQL("DELETE FROM sqlite_sequence WHERE name='userCategories'");
	  database.execSQL("DELETE FROM sqlite_sequence WHERE name='userDefaultCategories'");
	  database.execSQL("INSERT INTO userCategories VALUES(null,'All Channels',1,'"+allChannels+"',1,'nodata');");
	  database.execSQL("INSERT INTO userDefaultCategories VALUES(null,'All Channels',1,'"+allChannels+"',1,'nodata');");

	  int i=2;
	  for(Map.Entry<String,String> entry : map.entrySet()){
		  database.execSQL("INSERT INTO userCategories VALUES(null,'"+entry.getKey()+"',"+i+",'"+entry.getValue()+"',1,'nodata');");
		  database.execSQL("INSERT INTO userDefaultCategories VALUES(null,'"+entry.getKey()+"',"+i+",'"+entry.getValue()+"',1,'nodata');");
		  i++;
	  }
  }
  
  public void addNewCategory(Category category){
	  database.execSQL("INSERT INTO userCategories VALUES(null,'"+category.retCategoryName()+"',"+category.retOrder()+",'"+category.retWholeChannelList()+"',"+category.retVisibility()+",'nodata');");
  }
  public void deleteCategory(Category category){
	  database.delete("userCategories", "category_id=?", new String[]{String.valueOf(category.retId())});
  }
  public void addNewReminder(Integer listing_id,String st_time){
	  ContentValues val = new ContentValues();
	  val.put("reminder", "true");
	  String whereClause = "listing_id=?";
	  database.update("channels_listings", val, whereClause, new String[]{listing_id.toString()});
	  //val.clear();
	  //val.put("listing_id", listing_id);
	  //val.put("start_time",st_time);
	  //database.insert("staging_rem",null,val);
  } 
  public void addNewTunein(Integer listing_id,String st_time){
	  ContentValues val = new ContentValues();
	  val.put("tunein", "true");
	  String whereClause = "listing_id=?";
	  database.update("channels_listings", val, whereClause, new String[]{listing_id.toString()});
	  //val.clear();
	  //val.put("listing_id", listing_id);
	  //val.put("start_time",st_time);
	  //database.insert("staging_rem",null,val);
  }
  public void addNewWatchList(String program_id){
	  Cursor cur = database.query("watchlist", null, "program_id=?", new String[]{program_id}, null, null, null);
	  if(cur.getCount()!=0){
		  return;
	  }
	  ContentValues val = new ContentValues();
	  val.put("program_id",program_id);
	  database.insert("watchlist", null, val);
	  JSONObject json = new JSONObject();
		  try {
			json.put("program_id", program_id);
		  } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	  addToAPIQueue(json.toString(), domain+"update_user/watchlists/add");
	  val.clear();
	  val.put("watch_list", "true");
	  String whereClause = "program_id=?";
	  database.update("channels_listings", val, whereClause, new String[]{program_id});
	 // JSONObject json = new JSONObject();
	//  try {
	//	json.put("program_id", program_id);
	//} catch (JSONException e) {
		// TODO Auto-generated catch block
	//	e.printStackTrace();
	//}
	 // addToAPIQueue(json.toString());
  }
  
  public void remWatchList(String program_id){
	  	Log.v("REMINDER","REMOVING : "+program_id);
	  	database.delete("watchlist", "program_id=?", new String[]{program_id});
	  	ContentValues val = new ContentValues();
	  	val.put("watch_list", "false");
		  String whereClause = "program_id=?";
		  database.update("channels_listings", val, whereClause, new String[]{program_id});
		  JSONObject json = new JSONObject();
		  try {
			json.put("program_id", program_id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  addToAPIQueue(json.toString(), domain+"update_user/watchlists/remove");
		  
  }
  
  public ArrayList<StackItem> retIRItem(Integer dth_code,String dth_chancode){
	  ArrayList<StackItem> st = new ArrayList<StackItem>();
	  StringBuilder whereClause = new StringBuilder();
	  for(int i=0;i<dth_chancode.length();i++){
		  if(!whereClause.toString().equals(""))
			  whereClause.append(" OR ");
		  int func_id = (int)dth_chancode.charAt(i)-48;
		  if(func_id==0){
			  func_id=15;
		  }else{
			  func_id+=5;
		  }
		  Log.e("MyRemtote","Setting whereClause : "+(func_id));
		  whereClause.append("func_id="+func_id);
	  }
	  
	  	
	  	Cursor cur = database.rawQuery("SELECT rc,freq,data,func_id FROM keymaps WHERE ("+whereClause.toString()+") AND (remote_id="+dth_code+")", null);
  		Log.e("SQL STRING","SELECT rc,freq,data,func_id FROM keymaps WHERE ("+whereClause.toString()+") AND (remote_id="+dth_code+")");

	  	if(cur.getCount()==0){
	  		Log.e("SQL STRING","SELECT rc,freq,data,func_id FROM keymaps WHERE ("+whereClause.toString()+") AND (remote_id="+dth_code+")");
//	  		Toast.makeText(mContext, "EMPTY", Toast.LENGTH_SHORT).show();
	  		return st;
	  	}
	  	cur.moveToFirst();
	  	 while (!cur.isAfterLast()) {
	  		StackItem item = new StackItem();
	  		item.setrc(cur.getInt(0));
	  		item.setfreq(cur.getInt(1));
	  		item.setdata(cur.getString(2));
	  		item.setfuncid(cur.getInt(3));
	  		st.add(item);
	        cur.moveToNext();
	  	 }
	  return st;
  }
  
  public void remReminder(Integer listing_id){
	  	Log.v("REMINDER","REMOVING : "+listing_id);
	  	ContentValues val = new ContentValues();
	  	val.put("reminder", "false");
	  	String whereClause = "listing_id=?";
	  	database.update("channels_listings", val, whereClause, new String[]{listing_id.toString()});
	  	//database.delete("staging_rem", listing_id=?, new String[]{});
  }
  public void remTunein(Integer listing_id){
	  	Log.v("TUNEIN","REMOVING : "+listing_id);
	  	ContentValues val = new ContentValues();
	  	val.put("tunein", "false");
	  	String whereClause = "listing_id=?";
	  	database.update("channels_listings", val, whereClause, new String[]{listing_id.toString()});
	  	//database.delete("staging_rem", listing_id=?, new String[]{});
}
  public boolean isPrograminWL(String program_id){
	  	Cursor cur;
	  	if(program_id==null)
	  		return false;
		cur = database.rawQuery("SELECT * FROM watchlist where program_id=?",new String[] {program_id});
		if(cur.getCount()==0)
			return false;
		else
			return true;
  }
  public boolean isProgramRem(Integer listing_id){
	  	Cursor cur;
	  	if(listing_id==null)
	  		return false;
		cur = database.rawQuery("SELECT * FROM channels_listings where listing_id=? AND reminder=?",new String[] {listing_id.toString(),"true"});
		if(cur.getCount()==0)
			return false;
		else
			return true;
  }
  public boolean isProgramTunedin(Integer listing_id){
	  	Cursor cur;
	  	if(listing_id==null)
	  		return false;
		cur = database.rawQuery("SELECT * FROM channels_listings where listing_id=? AND tunein=?",new String[] {listing_id.toString(),"true"});
		if(cur.getCount()==0)
			return false;
		else
			return true;
  }

  /**
   * Updates the database with new data.
   * @param categories Contains new data to be written.
   */
  	public void updateOrder(ArrayList<Category> categories){
	Log.i("UPDATING....", categories.get(6).returnAsIntArray().toString());

  		for(Category item : categories){
  			ContentValues val = new ContentValues();
  			val.put("category_sequence",item.retOrder());
  			val.put("channels", item.retWholeChannelList());
  			val.put("isvisible", item.retVisibility());
  			val.put("category_name", item.retCategoryName());
  			String whereClause = "category_id="+item.retId();
  			Log.i("Updating for category id :",item.retId()+"|"+item.retWholeChannelList());
  			database.update("userCategories", val, whereClause, null);
  		}
  	}
  
  	
  	/**
  	 * HANDLING PROFILER : BEGIN
  	 */
  	
  	public void createProfile(String profileName,Integer selectedProvider,String basePack, ArrayList<String> addonPacks){
  		
  		ArrayList<Integer> addons = getIntegerList(selectedProvider, addonPacks);
  		StringBuilder addonsString = new StringBuilder();
  		String delimiter="";
  		for(Integer item : addons){
  			addonsString.append(delimiter);
  			addonsString.append(item);
  			delimiter=",";
  		}
  		database.execSQL("INSERT INTO profiles(profile_id,provider,base_pack,addon_packs) VALUES(null,"+selectedProvider+",'"+basePack+"','"+addonsString.toString()+"');");
  	}
  	
  	public void updateProfile(Integer profileId,String profileName,Integer selectedProvider,String basePack, ArrayList<String> addonPacks){
  		ArrayList<Integer> addons = getIntegerList(selectedProvider, addonPacks);
  		StringBuilder addonsString = new StringBuilder();
  		String delimiter="";
  		for(Integer item : addons){
  			addonsString.append(delimiter);
  			addonsString.append(item);
  			delimiter=",";
  		}
  		ContentValues val = new ContentValues();
  		val.put("profile_name", profileName);
  		val.put("provider", selectedProvider);
  		val.put("base_pack", basePack);
  		val.put("addon_packs", addonsString.toString());
  		
  		database.update("profiles", val, "profile_id=?", new String[]{String.valueOf(profileId)});
  	} 
  	
  	
  	
  	public Profiles getProfile(int profile_id){
  		
  		Cursor cursor = database.query("profiles", new String[]{"profile_id","profile_name","provider","base_pack","addon_packs"}, "profile_id=?", new String[]{String.valueOf(profile_id)}, null, null, null);
  		
  		if(cursor.getCount()==0){
  			return null;
  		}
  		else{
  			Profiles mProfile = new Profiles();
  			ArrayList<Integer> addonsIntegerList = new ArrayList<Integer>();
  			cursor.moveToFirst();
  			mProfile.setProfileId(cursor.getInt(0));
  			mProfile.setProfileName(cursor.getString(1));
  			mProfile.setProvider(cursor.getInt(2));
  			Cursor tempCursor = database.query("dth_name", new String[]{"name"}, "dth_code=?", new String[]{String.valueOf(cursor.getInt(2))}, null, null, null);
  			tempCursor.moveToFirst();
  			mProfile.setProviderName(tempCursor.getString(0));
  			tempCursor.close();
  			mProfile.setBasePack(cursor.getString(3));
  			String addons = cursor.getString(4);
  			if(!(addons==null||addons.equals(""))){
  				for(String single_addon : addons.split(","))
  					addonsIntegerList.add(Integer.parseInt(single_addon));
  				
  			}
  			mProfile.setAddonPack(getStringList(addonsIntegerList));
  			cursor.close();
  			return mProfile;
  		}
  		
  	}
  	
  	public ArrayList<String> getStringList(ArrayList<Integer> addonsIntegerList){
  		ArrayList<String> addonsStringList = new ArrayList<String>();
  		Cursor cursor;
  		for(Integer addonId : addonsIntegerList){
  			cursor = null;
  			cursor = database.query("profiler_addons", new String[]{"addon_name"}, "addons_id=?", new String[]{String.valueOf(addonId)}, null, null, null);
  			cursor.moveToFirst();
  			addonsStringList.add(cursor.getString(0));
  		}
  		return addonsStringList;
  	}
  	
  	public ArrayList<Integer> getIntegerList(Integer provider,ArrayList<String> addonsStringList){
  		ArrayList<Integer> addonsIntegerList = new ArrayList<Integer>();
  		Cursor cursor;
  		for(String addonName : addonsStringList){
  			Log.e("ADDONS", "provider:" + provider+"|"+"addon:"+addonName);
  			cursor = null;
  			cursor = database.query("profiler_addons", new String[]{"addons_id"}, "provider_id=? AND addon_name=?", new String[]{String.valueOf(provider),addonName}, null, null, null);
  			cursor.moveToFirst();
  			Log.e("January",addonName);
  			addonsIntegerList.add(cursor.getInt(0));
  		}
  		return addonsIntegerList;
  	}
//  	
  	/**
  	 * HANDLING PROFILER : END
  	 */
  	
  	
  	/**
  	 * HANDLING DTH OPTIONS : BEGIN
  	 */
  	
  	//retDTHOptions handles providers options
  	
  	public ArrayList<String> retBasePackageOptions(String provider){
  		ArrayList<String> packageNames = new ArrayList<String>();
  		Cursor cursor = database.query("packages", new String[]{"Package"}, "Provider=? AND Type=?", new String[]{provider,"Base"}, null,null,null);
  		if(cursor.moveToFirst()){
  			do{
  				packageNames.add(cursor.getString(0));
  			}while(cursor.moveToNext());
  		}
  		return packageNames;
  	
  	}
  	
  	public ArrayList<String> retAddonPackageOptions(String provider){
  		ArrayList<String> packageNames = new ArrayList<String>();
  		Cursor cursor = database.query("packages", new String[]{"Package"}, "Provider=? AND Type=?", new String[]{provider,"Addon"}, null,null,null);
  		if(cursor.moveToFirst()){
  			do{
  				packageNames.add(cursor.getString(0));
  			}while(cursor.moveToNext());
  		}
  		return packageNames;
  	}
  	
  	public ArrayList<Integer> retChannelList(String provider,String basePack, ArrayList<String> addonPack){
  		ArrayList<Integer> channelList = new ArrayList<Integer>();
  		
  		Cursor cursor = database.query("packages", new String[]{"Channels"}, "Provider=? AND Type=? AND Package=?", new String[]{provider,"Base",basePack}, null,null,null);

  		if(cursor.moveToFirst()){
  			int cnt1 = 0;
  			String channelsString = cursor.getString(0);
  			channelsString = channelsString.substring(0, channelsString.length()-1);
  			for(String chanItem : channelsString.split(",")){
  				if(!channelList.contains(Integer.parseInt(chanItem)))
  				channelList.add(Integer.parseInt(chanItem));
  				cnt1++;
  			}
  			Log.e("BaseSize:",String.valueOf(cnt1));
  			
  		}
  		Log.e("TOTALCHANNELLISTSIZE:",String.valueOf(channelList.size()));

  		if(addonPack.size()==0)
  			return channelList;
  		
  		for(String addOn : addonPack){
  			cursor = null;
  			cursor = database.query("packages", new String[]{"Channels"}, "Provider=? AND Type=? AND Package=?", new String[]{provider,"Addon",addOn}, null,null,null);
  			if(cursor.moveToFirst()){
  				int cnt=0;
  	  			String channelsString = cursor.getString(0);
  	  			channelsString = channelsString.substring(0, channelsString.length()-1);
  	  			for(String chanItem : channelsString.split(",")){
  	  				cnt++;
  	  				if(!channelList.contains(Integer.parseInt(chanItem)))
  	  				channelList.add(Integer.parseInt(chanItem));
  	  			}
  	  			Log.e("AddonSize:",String.valueOf(cnt));
  	  		}
  		}
  		
  		return channelList;
  	}
  	
  	public ArrayList<Channels> retChannelItems(ArrayList<Integer> channels,Integer dth_code){
  		
  		ArrayList<Channels> finalChannels = new ArrayList<Channels>();
  		Cursor cursor = null;
  		for(Integer item : channels){
  			if(retDTHid(item, dth_code).equals("")){
  				continue;
  			}
  			cursor = null;
  			cursor = database.query("channels", new String[]{"sidekick_id","channel_name","category"}, "sidekick_id=?", new String[]{item.toString()}, null,null,null);
  			if(cursor.moveToFirst()){
  				Channels ch = new Channels();
  				ch.setId(cursor.getInt(0));
  				ch.setChannelName(cursor.getString(1));
  				ch.setCategoryName(cursor.getString(2));
  				finalChannels.add(ch);
  			}
  		}
  		cursor.close();
  		return finalChannels;
  	}
  	
  	public void hideIntialCategories(ArrayList<String> catNames){
  		ContentValues val = new ContentValues();
  		val.put("isvisible", 0);
  		for(String item : catNames){
  	  		database.update("userCategories", val, "category_name=?", new String[]{item});
  	  		database.update("userDefaultCategories", val, "category_name=?", new String[]{item});

  		}
  	}
  	
  	/**
  	 * HANDLING DTH OPTIONS : END
  	 */
  	
  
  
  
  /**This function returns a instance of ListItems. 
   * It queries the database on the basis of chid and st_time
   * and returns an ListItems object. This object contains several fields
   * such as chid,chname,timing,showname,desc.
   * @param ch_id channel id sent for querying
   * @param st_time time for which shows are returned
   * @param li	ListItem that corresponfs to this show. We set channel id and name in it.
   */
  
  public ArrayList<String> getChannel_NameList(ArrayList<Integer> channelIDList){
	  ArrayList<String> chanNameList = new ArrayList<String>();
	  String temp;
	  try{
	  Cursor cursor_shows;
	  
	  Log.i("list data source", channelIDList.size()+"");
	  for(Integer ch_id : channelIDList)
	  {
		cursor_shows = database.rawQuery("SELECT channel_name FROM channels where sidekick_id=?",new String[] {ch_id.toString()});
		cursor_shows.moveToFirst();
		temp=cursor_shows.getString(0);
		Log.i("in for",temp);  
		chanNameList.add(temp);
	}
	}
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  Log.i("in for",chanNameList.size()+"");
	  return chanNameList;
  }
  
  public String retDTHid(Integer chid,Integer dthcode){
	  Cursor cursor;
	  cursor  = database.rawQuery("SELECT dth_chancode FROM sidekick_dth where dthcode=? AND sidekick_id=?",new String[] {dthcode.toString(),chid.toString()});
	  
	  if(cursor.getCount()==0)
		  return "";
	  else{
		  cursor.moveToFirst();
		  return cursor.getString(0);
	  }
  }
  
  public String[] retDTHOptions(ArrayList<Integer> dth_codes){
	  Cursor cursor;
	  cursor = database.rawQuery("SELECT dth_code,name FROM dth_name",null);
	  String[] dthOptions = new String[cursor.getCount()];
	  int i=0;
	  while(cursor.moveToNext()){
		  dth_codes.add(cursor.getInt(0));
		  dthOptions[i] = cursor.getString(1);
		  i++;
	  }
	  return dthOptions;
  }
  
  public ShowsList retListItems(Integer ch_id,String st_time){
	  
	  Cursor cursor_shows;
	  ShowsList sl = new ShowsList();
	  cursor_shows = database.rawQuery("SELECT listing_id,start_time,end_time,title,synopsis,episode_synopsis,image_url,program_id,retelecast,youtube FROM channels_listings where sidekick_id=? AND start_time<= datetime('"+st_time+"') AND end_time>= datetime('"+st_time+"')",new String[] {ch_id.toString()});
	  if(cursor_shows.getCount()==0){
		  sl.setChannelId(ch_id);
		  cursor_shows.close();
		//  Log.v("I m returning", "with NOTHING");
		  return sl;
	  }
	  cursor_shows.moveToFirst();
	  sl.setChannelId(ch_id);
	  sl.setListingId(cursor_shows.getInt(0));
	  sl.setTiming(cursor_shows.getString(1), cursor_shows.getString(2));
	  sl.setShowName(cursor_shows.getString(3));
	  sl.setDescription(cursor_shows.getString(4));
	  sl.setEpisodeDescription(cursor_shows.getString(5));
	  sl.setImageURL(cursor_shows.getString(6));
	  sl.setProgramId(cursor_shows.getString(7));
	  sl.setRetelecast(cursor_shows.getString(8));
	  sl.setYoutube(cursor_shows.getString(9));
	 /// Log.v("I m returning", sl.getShowName());
	  cursor_shows.close();
	  
	  return sl;
  }
  
  
  public ArrayList<ShowsList> retShowListItems(ArrayList<Integer> channelIds,String st_time){
	ArrayList<ShowsList> showListItems = new ArrayList<ShowsList>();
	Cursor cursor;
	for(Integer oneChannelId : channelIds){
		cursor = null;
		cursor = database.rawQuery("SELECT listing_id,start_time,end_time,title,synopsis,episode_synopsis,image_url,program_id,retelecast,youtube FROM channels_listings where sidekick_id=? AND start_time<= datetime('"+st_time+"') AND end_time>= datetime('"+st_time+"')",new String[] {oneChannelId.toString()});
		if(cursor.getCount()==0)
			continue;
		else{
			cursor.moveToFirst();
			ShowsList showItem = new ShowsList();
			 showItem.setChannelId(oneChannelId);
			 showItem.setListingId(cursor.getInt(0));
			 showItem.setTiming(cursor.getString(1), cursor.getString(2));
			 showItem.setShowName(cursor.getString(3));
			 showItem.setDescription(cursor.getString(4));
			 showItem.setEpisodeDescription(cursor.getString(5));
			 showItem.setImageURL(cursor.getString(6));
			 showItem.setProgramId(cursor.getString(7));
			 showItem.setRetelecast(cursor.getString(8));
			 showItem.setYoutube(cursor.getString(9));
			 showListItems.add(showItem);
		}
	}
	
	return showListItems;
  }
  
  
  public String returnChannelName(int ch_id)
  {
	  Cursor cursor_shows;
	  cursor_shows = database.rawQuery("SELECT channel_name FROM channels where sidekick_id=?",new String[] {ch_id+""});
		cursor_shows.moveToFirst();
		  return cursor_shows.getString(0);
  }
  
  /**
   * This function returns the contents of get_listing table. It contains
   * sidekick_id, timeslot and last updated time for that particular id and timeslot.
   * @param chids	Channel ids for which data is to be returned in combination with timeslot.
   * @param timeslot Timeslot for which data is to be returned in combination with channel id.
   * @return ArrayList containing objects of class UpdateListingObject.
   */
  public ArrayList<UpdateListingObject> retUpdListings(ArrayList<Integer> chids,String timeslot){
	  ArrayList<UpdateListingObject> updListingObject = new ArrayList<UpdateListingObject>();
	  Log.d("database","gettingUpdListings");
	  for(Integer chid: chids){
		  Cursor cursor_shows = database.rawQuery("SELECT * FROM get_listings WHERE sidekick_id=? AND timeslot=?",new String[] {chid.toString(),timeslot});
		  UpdateListingObject ch = new UpdateListingObject();
		  ch.setsidekickid(chid);
		  
		  if(cursor_shows.getCount()==0){
			  Log.d("database","count was 0");
			  ch.setlastupd("0000-01-01 00:00:00");
			  ch.settimeslot(timeslot);
			  database.execSQL("INSERT INTO get_listings VALUES("+chid+",'"+timeslot+"','"+ch.retlastupd()+"');");
		  }
		  else{
			  Log.d("database","count wasnt zero...");

			  cursor_shows.moveToNext();
			  Log.i("database", cursor_shows.getString(1) + " | " + cursor_shows.getString(2));

			  ch.setlastupd(cursor_shows.getString(2));
			  ch.settimeslot(cursor_shows.getString(1));
		  }
		  
		  Log.i("adding CHREQUEST", ch.rettimeslot());

		  updListingObject.add(ch);
	  }
	return updListingObject;
  }
  
  /**
   * Function for adding listings to the table channels_listings. This is called when we receive data
   * from the server. Following parameters correspond to the fields of the table. The meaning of them
   * is clear from the name.
   * @param chid
   * @param start_time
   * @param end_time
   * @param title
   * @param synopsis
   * @param timeslot
   */
  	public void addChannelListing(int listing_id,int chid,String start_time, String end_time,String title, String synopsis,String episode_synopsis, String image_url,String program_id,String timeslot,String retelecast,String youtube){
  		Cursor cur = database.rawQuery("SELECT * FROM channels_listings WHERE listing_id=?", new String[] {String.valueOf(listing_id)});
  		ContentValues val = new ContentValues();
  		String watch_list;
  		Cursor w = database.rawQuery("SELECT * FROM watchlist WHERE program_id=?", new String[]{program_id});
  		if(w.getCount()==0){
  			watch_list="false";
  		}
  		else
  			watch_list="true";
  		w.close();
  		if(cur.getCount()!=0){
  			val.put("timeslot", timeslot);
  			val.put("watch_list",watch_list);
		    String whereClause = "listing_id="+listing_id;
		    database.update("channels_listings", val, whereClause, null);
  		}
  		else{
  			val.put("listing_id", listing_id);
  			val.put("sidekick_id",chid);
  			val.put("start_time", start_time);
  			val.put("end_time", end_time);
  			val.put("title", title);
  			val.put("synopsis", synopsis);
  			val.put("episode_synopsis", episode_synopsis);
  			val.put("retelecast",retelecast);
  			val.put("youtube", youtube);
  			val.put("image_url", image_url);
  			val.put("timeslot", timeslot);
			val.put("recommended", "false");
			val.put("program_id", program_id);
			val.put("watch_list",watch_list);
		database.insert("channels_listings", null, val);
  		}
  		
  		
  	}
  	
  
  	/**
  	 * Function for adding data to get_listing table.
  	 * @param chids
  	 * @param timeslot
  	 * @param last_upd
  	 */
  	public void addUpdListing(ArrayList<Integer> chids, String timeslot, String updated_time){
  		Cursor cursor = database.rawQuery("SELECT * FROM get_listings", null);
  		Log.e("database","addingUpdListings");
  		if(cursor.getCount()==0){
  			Log.e("database","..there were none");
  			for(Integer chid : chids)
  				database.execSQL("INSERT INTO get_listings VALUES("+chid+",'"+timeslot+"','"+updated_time+"');");
  			cursor.close();
  		}
  		else{
  			Log.e("database","updating...");
  			for(Integer chid : chids){
  				ContentValues val = new ContentValues();
  				val.put("timeslot", timeslot);
  				val.put("updated_time", updated_time);
  				String whereClause = "sidekick_id="+chid+" AND timeslot='"+timeslot+"'";
  				Log.e("database",chid+" | "+timeslot+" | "+updated_time);
  				database.update("get_listings", val, whereClause, null);
  			}
  		}
  	}
  
  	/**
  	 * Function for deleting the existing listings of a particular channel and timeslot
  	 * because new listings for them are about to be entered. Again the meaning of parameters is clear 
  	 * from their names.
  	 * @param chid
  	 * @param timeslot
  	 */
  	public void refreshListings(Integer chid, String timeslot){
  		Cursor cur = database.rawQuery("SELECT * FROM channels_listings WHERE sidekick_id=? AND timeslot=?", new String[] {chid.toString(),timeslot});
  		if(cur.getCount()!=0){
  			database.execSQL("DELETE FROM channels_listings WHERE sidekick_id="+chid+" AND timeslot='"+timeslot+"';");
  		}
  	}
  	
  	public void addToStagingTable(Integer listing_id,Integer time,String end_time,Integer duration){
  		Log.e("MyRemote","Adding to Staging Table called");
  		Cursor cur = database.rawQuery("SELECT time_watched FROM staging_table WHERE listing_id=?",new String[]{String.valueOf(listing_id)});
  		ContentValues val = new ContentValues();
  		if(cur.getCount()==0){
  			val.put("listing_id", listing_id);
  			val.put("time_watched",time);
  			val.put("duration", duration);
  			val.put("end_time", end_time);
  			database.insert("staging_table", null, val);
  		}
  		else{
  			cur.moveToFirst();
  			val.put("time_watched", cur.getInt(0)+time);
		    String whereClause = "listing_id="+listing_id;
		    database.update("staging_table", val, whereClause, null);
  		}
  	}
  	
  	/* ********** FOR REMINDERS ***************************** */
  	
  	public ArrayList<ShowsList> retListItemsForReminders(){
  	  ArrayList<ShowsList> showNameDesc = new ArrayList<ShowsList>();
  	  Cursor cursor_shows;
  	  cursor_shows = database.rawQuery("SELECT listing_id,sidekick_id,start_time,end_time,title,synopsis,episode_synopsis,image_url,program_id FROM channels_listings WHERE end_time > datetime('"+DateUtil.formatTime(System.currentTimeMillis())+"')AND reminder=?",new String[] {"true"});
  	  cursor_shows.moveToFirst();
  	  while (!cursor_shows.isAfterLast()) {
  		ShowsList sl = makeShowsListCursor(cursor_shows);
        showNameDesc.add(sl);
        cursor_shows.moveToNext();
        ML.log("ListDataSource", "Adding "+sl.getListingId());
      }
  	  
  	  return showNameDesc;
 }
/* ***************** For Watchlist ******************** */
  	public ArrayList<ShowsList> retListItemsForWatchlist(){
    	  ArrayList<ShowsList> showNameDesc = new ArrayList<ShowsList>();
    	  Cursor cursor_shows;
    	  cursor_shows = database.rawQuery("SELECT listing_id,sidekick_id,start_time,end_time,title,synopsis,episode_synopsis,image_url,program_id FROM channels_listings where end_time > datetime('"+DateUtil.formatTime(System.currentTimeMillis())+"') AND watch_list=?",new String[] {"true"});
    	  cursor_shows.moveToFirst();
    	  while (!cursor_shows.isAfterLast()) {
    		ShowsList sl = makeShowsListCursor(cursor_shows);
          showNameDesc.add(sl);
          cursor_shows.moveToNext();
          ML.log("ListDataSource", "Adding "+sl.getListingId());
        }
    	  
    	  return showNameDesc;
   }
  	
/* *****************************   For recommendations   ***********************************************/
  	
  	/**
  	 * Used by adapter for Recommendation.
  	 * @param listing_id
  	 * @param ch_id
  	 * @return
  	 */
  	public ArrayList<ShowsList> retListItemsForRecom(){
  	  ArrayList<ShowsList> showNameDesc = new ArrayList<ShowsList>();
  	  Cursor cursor_shows;
  	  cursor_shows = database.rawQuery("SELECT listing_id,sidekick_id,start_time,end_time,title,synopsis,episode_synopsis,image_url,program_id FROM channels_listings where recommended=?",new String[] {"true"});
  	  cursor_shows.moveToFirst();
  	  while (!cursor_shows.isAfterLast()) {
  		ShowsList sl = makeShowsListCursor(cursor_shows);
        showNameDesc.add(sl);
        cursor_shows.moveToNext();
        ML.log("ListDataSource", "Adding "+sl.getListingId());
      }
  	  
  	  return showNameDesc;
   }
  	
  	public ShowsList makeShowsListCursor(Cursor cursor_shows) {
  		ShowsList sl = new ShowsList();
  		sl.setListingId(cursor_shows.getInt(0));
  		sl.setChannelId(cursor_shows.getInt(1));
  		sl.setTiming(cursor_shows.getString(2), cursor_shows.getString(3));
  		sl.setShowName(cursor_shows.getString(4));
  		sl.setDescription(cursor_shows.getString(5));
  		sl.setEpisodeDescription(cursor_shows.getString(6));
  		sl.setImageURL(cursor_shows.getString(7));
  		sl.setProgramId(cursor_shows.getString(8));
		return sl;
	}

  	public void addToAPIQueue(String jsonString,String URL){
  			Log.e("APIcall : ", "apicalled");
			ContentValues val = new ContentValues();
			val.put("api_data", jsonString);
			val.put("created_time", DateUtil.formatTime(System.currentTimeMillis()));
  			val.put("url",URL);
			database.insert("api_queue", null, val);
  	}
  	
  	public ArrayList<ApiCall> getFromApiQueue(){
  		ArrayList<ApiCall> apiCalls = new ArrayList<ApiCall>();
  		Cursor cursor = database.rawQuery("SELECT * FROM api_queue LIMIT 40", null);
  		while(cursor.moveToNext()){
  			ApiCall temp = new ApiCall();
  			temp.request_id = cursor.getInt(0);
  			temp.created_time = cursor.getString(1);
  			temp.api_data=cursor.getString(2);
  			temp.url=cursor.getString(3);
  			apiCalls.add(temp);
  		}
  		
  		return apiCalls;
  	}
  	
  	
  	public void transformTuneins(){
			Log.e("MyTuneIns:","Inside transfform");

  		String currTime = DateUtil.formatTime(System.currentTimeMillis());
  		if(database.rawQuery("SELECT * FROM staging_table",new String[] {}).getCount()==0){
  			Log.e("MyTuneIns:","Empty");
  			return;
  		}
  		else
  			database.execSQL("DELETE FROM staging_table WHERE datetime(end_time) < datetime('"+currTime+"') AND time_watched <= 1");

  		while(true){
  		 Cursor cur = database.rawQuery("SELECT * FROM staging_table WHERE datetime(end_time) < datetime('"+currTime+"') LIMIT 2",new String[] {});
 		 if(cur.getCount()<2)
 			 break;
 		 else{
 			 
 			   JSONArray ids = new JSONArray();
 			   JSONArray time_watched = new JSONArray();
 			   JSONObject jsonTuneins = new JSONObject(); 
 			   JSONObject json = new JSONObject();
 			 	cur.moveToFirst();
		 		while(!cur.isAfterLast()){
		 			Log.e("MyTuneIns:",String.valueOf(cur.getInt(0)));
		 		 	ids.put(cur.getInt(0));
		 			time_watched.put(cur.getInt(1));
		 			cur.moveToNext();
		 		} 
		 		try {
		 		json.put("listing_id", ids);
		 		json.put("view_time_mins",time_watched);
				jsonTuneins.put("tuneins",json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	  			database.execSQL("DELETE FROM staging_table WHERE listing_id IN (SELECT listing_id FROM staging_table WHERE datetime(end_time) < datetime('"+currTime+"') LIMIT 2)" );
	  			Log.e("MyTuneIns:",jsonTuneins.toString());
	  			String URL=domain+"update_user/tuneins";
	  			addToAPIQueue(jsonTuneins.toString(),URL);
 		 }
  		}
 		 
  	}
  	
	public void setListingasRecommended(int listing_id,int chid,String start_time, String end_time,String title, String synopsis,String episode_synopsis, String image_url,String program_id,String timeslot,String retelecast,String youtube,String type)
  	{
		
		if(type.equals("recommend")){
			type="recommended";
		}else{
			type="watch_list";
		}
		
		String watch_list;
		Cursor w = database.rawQuery("SELECT * FROM watchlist WHERE program_id=?", new String[]{program_id});
  		if(w.getCount()==0)
  			watch_list="false";
  		else
  			watch_list="true";
  		 
  		Cursor cursor_shows = database.rawQuery("SELECT * FROM channels_listings WHERE listing_id=?",new String[] {String.valueOf(listing_id)});
  		 if(cursor_shows.getCount()!=0){
  		    ContentValues val = new ContentValues();
			val.put(type,"true");
			String whereClause = "listing_id=?";
			database.update("channels_listings", val, whereClause, new String[] {String.valueOf(listing_id)});
			cursor_shows.close();
  		 }
  		 else{
  			Log.i("IF", "count=0");
//  			 String reco="true";
  			 ML.log("ListDataSource", "adding as true");
  			ContentValues val = new ContentValues();
  			val.put("listing_id", listing_id);
  			val.put("sidekick_id",chid);
  			val.put("start_time", start_time);
  			val.put("end_time", end_time);
  			val.put("title", title);
  			val.put("synopsis", synopsis);
  			val.put("episode_synopsis", episode_synopsis);
  			val.put("retelecast",retelecast);
  			val.put("youtube", youtube);
  			val.put("image_url", image_url);
  			val.put("timeslot", timeslot);
			val.put("program_id", program_id);
  			val.put(type, "true");
  			if(type.equals("recommended")){
  				val.put("watch_list", watch_list);
  			}
//  			val.put("recommended", reco);
//  			val.put("watch_list", watch_list);
  			val.put("reminder", "false");
  			database.insert("channels_listings", null, val);
  			cursor_shows.close();
  		}
  	}
  	
  	
} 