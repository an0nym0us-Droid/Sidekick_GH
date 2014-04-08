package com.example.sidekick_offline_try2;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.view.MenuItem;
import com.example.sidekick_offline_try2.SliderContainer.OnTimeChangeListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;


public class MainActivity extends SlidingActivity implements OnTouchListener{

	private boolean isExpanded;
	public int run =0;
	 final public static int DISPLAY_LISTINGS=0;
	 final public static int LOADING_LISTINGS=1;
	 final public static int FAILED_TO_CONNECT=2;
	 final public static int NO_CONNECTION=3;
	 final public static int INCOMPLETE_LISTINGS=0;
	 final public static int ABSENT_LISTINGS=1;
	 final public static int WITH_LOADING=0;
	 final public static int WITHOUT_LOADING=1;

	ActionBar actionBar;
//	boolean sendingLock=true;
	public static boolean deleted; 
	public static int lastRowPressed = 0;
	SlidingMenu sm ;
	private DisplayMetrics metrics;	
	private int panelWidth;
	private ListView catList;
	private ImageButton logoButton;
	public int EDIT_LIST=12;
	Map<Integer,Integer> stateMap;
	
	/*
	 * CATEGORY VARIABLES
	 * ************************************************
	 */
	public  static ArrayList<Category> categories;
	public  static ArrayList<Category> defaultcategories;
	private ArrayAdapter<String> catAdapter;
	public static int currCatSelected;
    public static int currCatIdSelected;
    public Category presentCategory;
	
	/*
	 * ************************************************
	 */
	
	ArrayAdapter<String> spinnerAdapter;
	public ChannelListAdapter chanAdapter;
	
	private ArrayList<ListItems> chNameId;
	private ArrayList<ShowsList> showListItems;
	private ArrayList<UpdateListingObject> updateListingObject;
	public static ListDataSource datasource;
	ExpandableListView chanList;
	String[] week = new String[] {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	String staging = "staging";
    protected Calendar mInitialTime, minTime, maxTime;
    protected int mLayoutID;
    public ExistingRemote mRemote;
    protected SliderContainer mContainer;
    protected int minuteInterval;
    public  String currQueryTime;
    
    LinearLayout listingLoading;
    FrameLayout listingFrameLayout;
    ImageView listingFailed;
    SharedPreferences tuneins;
    int curr=-1;
    public static double factor;
    SharedPreferences.Editor tuneinEditor;
    SharedPreferences remotePref;
    SharedPreferences.Editor remoteEditor;
    Boolean isInternetPresent;
	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	ArrayList<String> catListContent ;
//	public static ArrayList<String> chanListContent;
	Calendar currentTime;
	public static ScheduleClient scheduleClient;
	Calendar currCal;
	String lastTimeSlot;
	String LastupdatedforRecommendations="0000-01-01 00:00:00";
	SharedPreferences myPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> spinnerContent;
    ImageButton sett;
	int nohours=2;	// number of hours from listings update
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layer_stack);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		currCatIdSelected=2;
		currCatSelected=1;
		
		Log.e("Sidekick_V3","currCat: "+currCatSelected);
		Log.e("Sidekick_V3","currCatID: "+currCatIdSelected);
		
		
		stateMap = new HashMap<Integer, Integer>();
		
		/**
		 * INITIALIZATIONS : BEGIN
		 */
		
		isInternetPresent = false;
        showListItems = new ArrayList<ShowsList>();
		
		
		/**
		 * INITIALIZATIONS : END
		 */
		
		/**
		 * Detecting DPI of the device. This is done to scale the showlogos to fit 
		 * the desired size
		 * 
		 * DPI ADJUSTMENT : BEGIN
		 */
		int density = getResources().getDisplayMetrics().densityDpi;
		switch(density){
		case DisplayMetrics.DENSITY_TV:
		case DisplayMetrics.DENSITY_HIGH:
			factor = 1.5;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			factor=1.0;
			break;
		case DisplayMetrics.DENSITY_LOW:
			factor = 0.75;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			factor = 2.0;
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			factor = 3.0;
			break;
		}
		
		/**
		 * DPI ADJUSTMENT : END
		 */
		
		/**
		 * Settings for action bar and sliding menu.
		 * 
		 * ACTION BAR AND SLIDE MENU SETTINGS : BEGIN
		 */
			setBehindContentView(R.layout.sidemenu_layout);
			sm = getSlidingMenu();
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			sm.setTouchmodeMarginThreshold(48);
			
			
			actionBar = getSupportActionBar();
			actionBar.setTitle("\tAll Channels");
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowCustomEnabled(true);
		/**
		 * ACTION BAR AND SLIDING MENU : END
		 */
			
		
		
			
		datasource = new ListDataSource(this,getResources().getString(R.string.domain));
		datasource.createDatabase();
        datasource.open();
        listingLoading = (LinearLayout) findViewById(R.id.listingLoading);
        listingFrameLayout = (FrameLayout) findViewById(R.id.listingFrameLayout);
        listingFailed = (ImageView) findViewById(R.id.listingFailed);
        
        sett = (ImageButton) findViewById(R.id.settings);
        sett.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,Settings.class);
				startActivity(intent);
			}
		});
        
        
        try{
        	mRemote = new ExistingRemote(this.getApplicationContext());
		}
		catch(Exception e){
			mRemote = null;
		}
        
        
		myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = myPreferences.edit();
		if(myPreferences.getBoolean("firsttime", true)){
			editor.putBoolean("firsttime", false);
			editor.commit();
		}
		tuneins = getSharedPreferences(staging, 0);
		tuneinEditor = tuneins.edit();
		remotePref = getSharedPreferences("remotePref", 0);
		remoteEditor = remotePref.edit();
		registerReceivers();
//		isInternetPresent = ConnectionDetector.isInternetPresent;
		Log.e("Sidekick_V4","internet : "+isInternetPresent);
		new GetProfileBuildUp().execute(DateUtil.formatTime(System.currentTimeMillis()),myPreferences.getString("devicekey", ""));
	 
	    
	    
		String prev = tuneins.getString("lid,time,endtime,duration", "0,0,0,0");
		String[] items= prev.split(",");
		Integer prev_lid = Integer.parseInt(items[0]);
		Long prev_time = Long.parseLong(items[1]);
		Long end_time = Long.parseLong(items[2]);
		Long prev_duration = Long.parseLong(items[3]);
		if(prev_lid!=0)
			datasource.addToStagingTable(prev_lid,(int) Math.min(end_time-prev_time,System.currentTimeMillis()-prev_time)/(1000), DateUtil.formatTime(end_time), (int) (prev_duration/1000)/60);

		tuneinEditor.remove("lid,time,endtime,duration");
		tuneinEditor.commit();
		//Metrics for getting the width
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//Width of the sliding panel
		panelWidth = (int) ((metrics.widthPixels)*0.85);
		
		
        
        spinnerContent = new ArrayList<String>();
        
        //<Categories> ArrayList for handling categories
        categories = new ArrayList<Category>();
        categories = datasource.getCategories(0);
        defaultcategories = datasource.getCategories(1);
        int i=1;
        for(;i<categories.size();i++){
        	if(categories.get(i).retVisibility()==0)
        		continue;
        	else
        		break;
        }
		actionBar.setTitle("\t"+categories.get(i).retCategoryName());
		
		presentCategory = categories.get(currCatSelected);
        catListContent = new ArrayList<String>();
        
        fill_catListContent();
      //<Categories> Get the list
        catList =  (ListView) findViewById(R.id.sidemenu_layout_catlist);
        //<Categories> Adapter for this list
        catAdapter = new MyAdapter(this,R.layout.catitem,catListContent);
        catList.setAdapter(catAdapter);
        catList.setOnItemClickListener(mOnItemClickListener);
//        catList.setSelection(1);
        catAdapter.notifyDataSetChanged();
        // resets last updated time for recommended search
        SharedPreferences settings1 = getSharedPreferences("Recommendations", 0);
		Log.i("shared preferences",settings1.getString("LastUpdated", ""));
		
        if(settings1.getString("LastUpdated", "")==null)
        {
        SharedPreferences settings = getSharedPreferences("Recommendations", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("LastUpdated","0000-01-01 00:00:00");
		editor.commit();
        }
        final Calendar calendar = Calendar.getInstance();
		minTime = Calendar.getInstance();
    	minTime.add(Calendar.HOUR, 0);
        maxTime = calendar;
        maxTime.add(Calendar.HOUR, 72);
        mInitialTime = Calendar.getInstance(calendar.getTimeZone());
        currQueryTime = DateUtil.formatTime(mInitialTime.getTimeInMillis());
        currCal = Calendar.getInstance();
        currCal.setTimeInMillis(mInitialTime.getTimeInMillis());
        lastTimeSlot = retTimeSlot(currCal);
        updateListingObject = datasource.retUpdListings(categories.get(currCatSelected).returnAsIntArray(),lastTimeSlot);
        Log.d("Spinner","Setting innitially.. "+DateUtil.formatTime(calendar.getTimeInMillis()));
        minuteInterval = 5;
        
        if (minuteInterval>1) {
        	int minutes = mInitialTime.get(Calendar.MINUTE);
        	//For rounding off to nearest interval
    		int diff = ((minutes+minuteInterval/2)/minuteInterval)*minuteInterval - minutes;
    		mInitialTime.add(Calendar.MINUTE, diff);
        }
        
        
        /**	<ListItems> ChannelListing
         * 	chanListContent : It is for filling in the text. Since it ArrayList<String>
         * 	chanAdapter : Adapter for chanList
         * 	updatePos(arg2) : updates the variable in ChannelListAdapter to currently selected list item. 
         * 	getView() is called for each item, after calling notifyDataSetChanged.
         */
        chNameId = new ArrayList<ListItems>();
        chanAdapter = new ChannelListAdapter(this,showListItems);
        chanList = (ExpandableListView) findViewById(R.id.lv1);
		
        
        /**
         * INITIAL FILL
         */
//			initialFill();
        
		chanList.setAdapter(chanAdapter);
		driverForRemote(105);

		chanList.setOnGroupClickListener(new OnGroupClickListener(){
			@Override
			public boolean onGroupClick(ExpandableListView par, View v,
					int pos, long id) {
				ShowsList sl = showListItems.get(pos);
				sl = datasource.retListItems(sl.getChannelId(), currQueryTime);
				if(sl.st_time.equals("")){
					return true;
				}
				if(pos==curr){
					if(!chanList.collapseGroup(pos))
						chanList.expandGroup(pos);
					return true;
				}
				
				Date st_time = sl.getStartTime();
				Date end_time = sl.getEndTime();
				Long currtime = System.currentTimeMillis();
				Long duration;
				if(end_time.getTime()>currtime&&st_time.getTime()<currtime){
					duration = end_time.getTime()-st_time.getTime();
					Log.i("Tuneins","show is airing.. updating preference");
					updateTuneInPreference(end_time,sl.getListingId(),currtime,duration);
					if(mRemote!=null)
						mRemote.test(remotePref.getInt("dth_code", 2353),chidToDthId(sl.getChannelId()));
					Log.e("MyRemote","After the test()");
				}
				else
					Log.e("Tuneins","Not adding");
				chanList.expandGroup(pos);
				if(curr!=-1)
					chanList.collapseGroup(curr);
				
				curr = pos;
				return true;
			}
		});
		
		//Timewheel Data 
		Log.d("Sidekick_V3","mContainer=(SliderContainer) this.findViewById....");
        mContainer = (SliderContainer) this.findViewById(R.id.dateSliderContainer);
		Log.d("Sidekick_V3","mContainer.setOnTimeChangeListener");
        mContainer.setOnTimeChangeListener(onTimeChangeListener);
		Log.d("Sidekick_V3","mContainer.setMinuteInterval");
        mContainer.setMinuteInterval(minuteInterval);
		Log.d("Sidekick_V3","mContainer.setTime");
        mContainer.setTime(mInitialTime.getTimeInMillis());
        Calendar myCal = Calendar.getInstance();
        Log.d("Spinner","After.. "+ DateUtil.formatTime(mInitialTime.getTimeInMillis()));
		Log.d("Sidekick_V3","mContainer.setMinimumTime");
        if (minTime!=null) mContainer.setMinTime(minTime.getTimeInMillis());
		Log.d("Sidekick_V3","mContainer.setMaximumTime");
        mContainer.setMaxTime(maxTime);
        updateSpinnerContent();
		setSpinner();
        
//        for(Category cat : categories){
//        	Log.e("Sidekick_V3","{"+cat.retOrder()+"|"+cat.retId()+"}");
//        }
		
		Log.e("Sidekick_V3",String.valueOf(remotePref.getInt("dth_code", 2353)));
//		Log.e("Sidekick_V3",chidToDthId(sl.getChannelId());
		
//		initialFill();
	}
	
	/**
	 * ON CREATE() FINISHED
	 */
	
	public void setMainScreen(int layoutState){
		switch(layoutState){
		case DISPLAY_LISTINGS:
			chanList.setVisibility(View.VISIBLE);
			listingFrameLayout.setVisibility(View.GONE);
			break;
		case LOADING_LISTINGS:
			chanList.setVisibility(View.GONE);
			listingFrameLayout.setVisibility(View.VISIBLE);
			listingLoading.setVisibility(View.VISIBLE);
			listingFailed.setVisibility(View.GONE);
			break;
		case FAILED_TO_CONNECT:
			chanList.setVisibility(View.GONE);
			listingFrameLayout.setVisibility(View.VISIBLE);
			listingLoading.setVisibility(View.GONE);
			listingFailed.setVisibility(View.VISIBLE);
			listingFailed.setImageResource(R.drawable.connectionerror);
			break;
		case NO_CONNECTION:
			chanList.setVisibility(View.GONE);
			listingFrameLayout.setVisibility(View.VISIBLE);
			listingLoading.setVisibility(View.GONE);
			listingFailed.setVisibility(View.VISIBLE);
			listingFailed.setImageResource(R.drawable.noconnection);
			break;
		}
	}
	/**
	 * On app launch, fill the channel listings with those present in database.
	 * If no listings are present, then check the internet. If internet is not present
	 * display "No connection" by calling setMainScreen(NO_CONNECTION). If internet was
	 * present, call sendAndCheck(). 
	 * Register receivers for detecting when the network switches on or off.
	 * Connection detector object to check whether network is on or not.
	 * @throws ParseException 
	 */
//	public void initialFill(){
//	
//		Log.e("Sidekick_V3","InitialFill has been called");
//		//GETTING THE SHOWLIST ITEMS
//		showListItems = datasource.retShowListItems(presentCategory.returnAsIntArray(), currQueryTime);
//		
//		/*
//		 * SHOWING THE SHOWLIST ITEMS IF PRESENT IN DB 
//		 * ELSE GETTING SHOWLIST ITEMS FROM NET IF CONNECTED
//		 */
//		Log.e("Sidekick_V3","Intial Fill size check : "+showListItems.size()+"|"+isInternetPresent);
//		if(showListItems.size()==0){
//			if(isInternetPresent){
//		        Log.v("Sidekick_V3","Check&Send called from initialFill line 458");
////		        if(sendingLock)
//				checkAndSend();
//			}else{
//				setMainScreen(NO_CONNECTION);
//			}
//		}else{
//			Log.e("Sidekick_V3","Size is not Zero (initial fill)");
//
//			setMainScreen(DISPLAY_LISTINGS);
//			chanAdapter.reload(showListItems);
//			chanAdapter.notifyDataSetChanged();
//		}
//		
//	}
	
	public void updateTuneInPreference(Date et,Integer listing_id,Long currtime,Long duration){
		String prev = tuneins.getString("lid,time,endtime,duration", "0,0,0,0");
		String[] items= prev.split(",");
		Integer prev_lid = Integer.parseInt(items[0]);
		Long prev_time = Long.parseLong(items[1]);
		Long end_time = Long.parseLong(items[2]);
		Long prev_duration = Long.parseLong(items[3]);
		if(prev_lid==0){
			Log.i("Tuneins","first one.. Adding .. "+listing_id+","+currtime+","+et.getTime()+","+(duration/1000)/60);
			tuneinEditor.putString("lid,time,endtime,duration",listing_id+","+currtime+","+et.getTime()+","+duration);
			tuneinEditor.commit();
		}
		else{
			Log.i("Tuneins","SENDING  :::: "+prev_lid+","+(int) Math.min(end_time-prev_time,currtime-prev_time)/(1000)+","+(prev_duration/1000)/60);
			datasource.addToStagingTable(prev_lid,(int) Math.min(end_time-prev_time,currtime-prev_time)/(1000), DateUtil.formatTime(end_time),(int) (prev_duration/1000)/60);
			tuneinEditor.putString("lid,time,endtime,duration",listing_id+","+currtime+","+et.getTime()+","+duration);
			Log.i("Tuneins","ADDING NOW :::  "+listing_id+","+currtime+","+et.getTime()+","+(duration/1000)/60);
			tuneinEditor.commit();
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				toggle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void driverForRemote(int chid){
		// Values being sent to ExistingRemote Class
		int dth_code = remotePref.getInt("dth_code", 2355);
		String dth_chancode = chidToDthId(chid);
		Log.e("EntirelyNewLog",dth_code+" | "+dth_chancode);
		// Now inside Existing Remote
		StringBuilder build = new StringBuilder(dth_chancode);
		build.reverse();
		final ArrayList<StackItem> st = MainActivity.datasource.retIRItem(dth_code,dth_chancode);
		int t = Integer.parseInt(build.toString());
		Log.e("EntirelyNewLog","SIZE : "+st.size());
     
		int temp =t;
		while(temp>0){
			int dig = temp%10;
			temp=temp/10;
			for(StackItem item : st){
				 if(dig!=((item.retfuncid()-5)%10))
					 continue;
				 else
					 Log.e("EntirelyNewLog","Sending digit "+((item.retfuncid()-5)%10));
				 StringBuilder data =  new StringBuilder();
				 for(int m=0;m<item.data.size();m++){
						data.append(item.data.get(m)+",");
				 }
				 
				 Log.e("EntirelyNewLog",data.toString());
			}
		}
		
		
	}
	
	public void setSpinner(){
		LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View spinnerView = inflater.inflate(R.layout.spinner_actionbar, null);
	    Spinner spinner = (Spinner) spinnerView.findViewById(R.id.spinner_actionbar_headSpinner);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_actionbar_item,spinnerContent);
	     adapter.setDropDownViewResource(R.layout.spinner_actionbar_dropdownitem);	    
	    spinner.setAdapter(adapter);
	    LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
	    layoutParams.gravity = Gravity.RIGHT; 
	    layoutParams.bottomMargin = 4;
	    layoutParams.rightMargin = 2;
	    getSupportActionBar().setCustomView(spinnerView, layoutParams); 
	    
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	int init=0;
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View v, int pos,
        			long id) {
        			if(init==0){
        				init=1;
        				return;
        			}
        			//((TextView) parent.getChildAt(0)).setTextColor(Color.argb(	255, 245, 245, 220));
        			//((TextView) parent.getChildAt(0)).setPadding(3, 0, 0, 0);
        				mContainer.setTime(System.currentTimeMillis()+pos*24*60*60*1000);
                		ML.log("New World", pos+" CALLED");

        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {
        		ML.log("New World", "NOTHING SELECTED CALLED");
        	}
    	 
        });
	    
	    
	}
	
	public String chidToDthId(int chid){
		String dth_chancode = datasource.retDTHid(chid, remotePref.getInt("dth_code", 2353));
		if(dth_chancode.equals(""))
			Toast.makeText(this, "Please setup the remote first.", Toast.LENGTH_SHORT).show();
		else{
			Log.e("MyRemote",dth_chancode);
			return dth_chancode;
		}
		return null;
	}
	
	public String retTimeSlot(Calendar cal){
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTimeInMillis(cal.getTimeInMillis());
        if(cal.get(Calendar.AM_PM)==0){
        	tempCal.set(Calendar.HOUR_OF_DAY, 0);
        	tempCal.set(Calendar.MINUTE, 0);
        	tempCal.set(Calendar.SECOND, 0);
        	return DateUtil.formatTime(tempCal);
        }
        else{
        	tempCal.set(Calendar.HOUR_OF_DAY, 12);
        	tempCal.set(Calendar.MINUTE, 0);
        	tempCal.set(Calendar.SECOND, 0);
        	
        	return DateUtil.formatTime(tempCal);
        }
	}
	
	
	
	public void updateSpinnerContent(){
		spinnerContent.add("Today");
		spinnerContent.add("Tomorrow");
		Calendar myCal = Calendar.getInstance();
		myCal.setTimeInMillis(System.currentTimeMillis()+2*24*60*60*1000);
		spinnerContent.add(week[myCal.get(Calendar.DAY_OF_WEEK)-1]);
		myCal.setTimeInMillis(myCal.getTimeInMillis()+24*60*60*1000);
		spinnerContent.add(week[myCal.get(Calendar.DAY_OF_WEEK)-1]); 
	}
	
	
// *************  BROADCAST RECEIVER FOR CHECKING INTERNET CONNECTION  ***********************
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	ConnectivityManager cm =
        	        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        	 
        	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        	isInternetPresent = activeNetwork != null &&
        	                      activeNetwork.isConnectedOrConnecting();
        	ArrayList<ApiCall> apiCalls = datasource.getFromApiQueue();
        	Log.e("Sidekick_V4","API QUEUE SIZE: "+apiCalls.size());
        	if(apiCalls.size()!=0){
        		for(ApiCall call : apiCalls){
        			new ApiQueueCall().execute(call);
        		}
        	}
        	else{
        		Log.e("Sidekick_V4","API QUEUE WAS EMPTY");
        	}
        	try {
				mContainer.setTime(DateUtil.formatTime(currQueryTime).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            if(currentNetworkInfo.isConnectedOrConnecting()){
//            	isInternetPresent = true;
//            	Log.e("Sidekick_V4","Internet is present !!!");
////            	if(sendingLock)
////            	checkAndSend();
//            	ArrayList<ApiCall> apiCalls = datasource.getFromApiQueue();
//            	Log.e("Sidekick_V4","API QUEUE SIZE: "+apiCalls.size());
//            	if(apiCalls.size()!=0){
//            		for(ApiCall call : apiCalls){
//            			new ApiQueueCall().execute(call);
//            		}
//            	}
//            	else{
//            		Log.e("Sidekick_V4","API QUEUE WAS EMPTY");
//            	}
//            }
//            else {
//            	isInternetPresent = false;
//            }
        }
    };
    
    // For registering the above Broadcast Receiver
    private void registerReceivers() {    
        registerReceiver(mConnReceiver, 
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

  
    
    // This function is the heart of sending request to the server.
    // It first checks whether the last updated time for a particular id and timeslot is 
    // at least 2hrs old then the current system time. Then only it proceeds to send those ids to server to
    // get fresh listings.
    private boolean checkAndSend(int showLoading){
    	
//		Category cat = categories.get(currCatSelected);
//		sendingLock=false;
    	Log.e("Sidekick_V4","Check and Send called with loading status : "+showLoading);
		String req_time = DateUtil.formatTime(System.currentTimeMillis());
		
		StringBuilder builderId = new StringBuilder();
		StringBuilder builderUpd = new StringBuilder();
    	String delimiter = "";
    	
    	ArrayList<Integer> presentChannels = presentCategory.returnAsIntArray();
    	
		for(UpdateListingObject updateObject : updateListingObject){
			if(presentChannels.contains(updateObject.retsidekickid())){
				Date calLastUpdate = null;
				Date calCurrTime = null;
				try {
					calLastUpdate = DateUtil.formatTime(updateObject.retlastupd());
					calCurrTime = DateUtil.formatTime(req_time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				Log.e("Sidekick_V4","Difference was : "+DateUtil.formatTime(calCurrTime.getTime())+"-"+DateUtil.formatTime(calLastUpdate.getTime()));
				// Checking if time difference is greater than 2hrs
				if((calCurrTime.getTime()-calLastUpdate.getTime())>(nohours*60*60*1000)){
					builderId.append(delimiter);
					builderId.append(updateObject.retsidekickid());
					builderUpd.append(delimiter);
					builderUpd.append(updateObject.retlastupd());
					delimiter=",";
				}
			}
		}
		
		String idList = builderId.toString();
		String updList = builderUpd.toString();

		//If idlist is empty then we dont send any request to server
		if(!idList.equals("")){
			Log.e("Sidekick_V4","Ids sent : "+idList);
			if(showLoading==WITH_LOADING)
				setMainScreen(LOADING_LISTINGS);
			new LoadJsonTask().execute(idList,updList,currQueryTime,req_time);
			return true;
		}
		else{
			Log.e("Sidekick_V4", "NO REQUEST SENT TO SERVER");
			return false;
//			sendingLock=true;
		}
    }
    
    
	/**
	 * Listener for when timewheel is scrolled.
	 */
    private OnTimeChangeListener onTimeChangeListener = new OnTimeChangeListener() {
		public void onTimeChange(Calendar time) {
			SimpleDateFormat sdf;
			Date tempDate= new Date(time.getTimeInMillis());
			StringBuilder finalText = new StringBuilder();
			
			sdf = new SimpleDateFormat("c");
			finalText.append("\t"+sdf.format(tempDate)+", ");
			
			sdf = new SimpleDateFormat("d MMM");
			finalText.append(sdf.format(tempDate)+", ");
			
			sdf = new SimpleDateFormat("h:mm a");
			finalText.append(sdf.format(tempDate));
			getSupportActionBar().setSubtitle(finalText.toString());
		}

		/**
		 * Function querying for the listings of the time indicated by the timewheel. If we 
		 * go into another slot, we request the server
		 */
		@Override
		public void onUpCalled(Calendar time){
			Log.e("Sidekick_V4","Entered onUp with time: "+DateUtil.formatTime(time));
			Integer state;
			currQueryTime = DateUtil.formatTime(time);
			currCal.setTimeInMillis(time.getTimeInMillis());
			
			if(!retTimeSlot(currCal).equals(lastTimeSlot)){
				Log.i("Sidekick_V4","TimeSlot Changed. "+lastTimeSlot+" --> "+ retTimeSlot(currCal));
				lastTimeSlot = retTimeSlot(currCal);
				updateListingObject = datasource.retUpdListings(presentCategory.returnAsIntArray(),retTimeSlot(currCal));
			}
			
			
			showListItems = datasource.retShowListItems(presentCategory.returnAsIntArray(), currQueryTime);
			
			Integer key = getHashCode(currCatIdSelected, retTimeSlot(currCal));
			
			if(!stateMap.containsKey(key)){
				Log.d("Sidekick_V4","Key wasnt present...");
				if(showListItems.size()!=0){
					state=DISPLAY_LISTINGS;
					setMainScreen(state);
					chanAdapter.reload(showListItems);
					chanAdapter.notifyDataSetChanged();
					if(presentCategory.returnAsIntArray().size()!=showListItems.size()){
						Log.d("Sidekick_V4","Incomplete Listings. Check & Send.");
						checkAndSend(WITHOUT_LOADING);
					}
				}else{
					if(isInternetPresent){
						if(checkAndSend(WITH_LOADING))
							state=LOADING_LISTINGS;
						else
							state=FAILED_TO_CONNECT;
					}else{
						state=NO_CONNECTION;
					}
					Log.v("Sidekick_V4","State is set to : "+state);
					setMainScreen(state);
					stateMap.put(key, state);
				}
			}else{
				Log.d("Sidekick_V4","Key WAS present...");
				state = stateMap.get(key);
				switch(state){
				case DISPLAY_LISTINGS:
					Log.v("Sidekick_V4","Display Listings State");
					setMainScreen(DISPLAY_LISTINGS);
					chanAdapter.reload(showListItems);
					chanAdapter.notifyDataSetChanged();
					if(presentCategory.returnAsIntArray().size()!=showListItems.size()){
						Log.e("Sidekick_V4","Incomplete Listings. Check & Send.");
						checkAndSend(WITHOUT_LOADING);
					}
					break;
				case NO_CONNECTION:
				case FAILED_TO_CONNECT:
					Log.v("Sidekick_V4","No connection or error State");
					if(isInternetPresent){
						if(checkAndSend(WITH_LOADING))
							state=LOADING_LISTINGS;
						else
							state=FAILED_TO_CONNECT;
					}else{
						state=NO_CONNECTION;
					}
					Log.v("Sidekick_V4","State is set to : "+state);
					setMainScreen(state);
					stateMap.put(key, state);
					break;
				case LOADING_LISTINGS:
					Log.v("Sidekick_V4","Loading Listings state");
					setMainScreen(LOADING_LISTINGS);
				}
			}
			chanList.collapseGroup(curr);
			curr=-1;
		}
    };
	
    
    
    public void updatePresentCategory(int currCat,int currCatId,boolean canToggle){
    	if(curr!=-1)
    		chanList.collapseGroup(curr);
    	if(canToggle)
    		toggle();
    	currCatSelected = currCat;
    	currCatIdSelected = currCatId;
    	presentCategory= categories.get(currCat);
    	actionBar.setTitle("\t"+presentCategory.retCategoryName());
    	updateListingObject = datasource.retUpdListings(presentCategory.returnAsIntArray(),retTimeSlot(currCal));
        try {
			mContainer.setTime(DateUtil.formatTime(currQueryTime).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		catAdapter.notifyDataSetChanged();
    }
//    public void manualMethodOnClick(int position,boolean canToggle){
//    	
//    	
//		
//        Log.e("Sidekick_V4","Manual Method entered currcat: "+position);
//        
//		// Following code is for selecting correct category from categories.
//		
//		
//		
//		actionBar.setTitle("\t"+temp_cat.retCategoryName());
////		Log.e("Sidekick_V3","Category Clicked Orignally : "+categories.get(position).retCategoryName()+"|"+categories.get(position).retOrder()+"|"+categories.get(position).retId());
////		Log.e("Sidekick_V3","Category Clicked Temp : "+temp_cat.retCategoryName()+"|"+temp_cat.retOrder()+"|"+temp_cat.retId());
//		presentCategory = temp_cat;
////		Toast.makeText(this,"Id:"+ presentCategory.retId()+"|"+categories.get(position).retId(), Toast.LENGTH_SHORT).show();
////		Toast.makeText(this,"Order :"+ presentCategory.retOrder()+"|"+categories.get(position).retOrder(), Toast.LENGTH_SHORT).show();
//		currCatSelected = temp_cat.retOrder()-1;
//		currCatIdSelected = temp_cat.retId();
//		Log.e("Sidekick_V4","Manual Method exit currcat: "+currCatSelected);
//        updateListingObject = datasource.retUpdListings(presentCategory.returnAsIntArray(),retTimeSlot(currCal));
//        
//        try {
//			mContainer.setTime(DateUtil.formatTime(currQueryTime).getTime());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		catAdapter.notifyDataSetChanged();
//		
//    }
//    
	/**
	 * onListItemClick for the category list.
	 */
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
	
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			int k=0,index=-1;
			Category temp_cat = new Category();
			for(Category item : categories){ 
				index++;
				if(item.retVisibility()==1){
					if(k==position){
						temp_cat = item;
						break;
					}
					else
						k++;
				}
			}
			updatePresentCategory(index, temp_cat.retId(), true);
		}
	};
    
	/**
	 * Onclick function for Edit Button
	 * @param v View of the button.
	 */
	public void watch(View v){
		Intent intent = new Intent(this,RemRecWatch.class);
		intent.putExtra("activity", 2);
		startActivity(intent);
	}

	public void rem(View v){
		Intent intent = new Intent(this,RemRecWatch.class);
		intent.putExtra("activity", 0);
		startActivity(intent);
	}
	public void rec(View v){
		Intent intent = new Intent(this,RemRecWatch.class);
		intent.putExtra("activity", 1);
		startActivity(intent);
	}
	
	public void startSettingActivity(View v){
		Intent intent = new Intent(this,Settings.class);
		startActivity(intent);
	}

	/**
	 * Function for filling catListContent String from categories variable.
	 */
	public void fill_catListContent(){
		catListContent.clear();
		 for(Category item : categories){
			 if(item.retVisibility()==1)
				 catListContent.add(item.retCategoryName());
		 }
	}
	
	
	public void onRestart(){
		super.onRestart();
		// On restart fill the categories variable from database. So that it contains all the changes made.
		// TODO : I dont think there is any need for this anymore. categories variable should have updated automatically.
		categories = datasource.getCategories(0);
		fill_catListContent();
		//Notify the adapter to change the refresh the list view.
		catAdapter.notifyDataSetChanged();
		catList.invalidateViews();
//			mContainer.setTime(DateUtil.formatTime(currQueryTime).getTime());
			mContainer.setMinTime(System.currentTimeMillis());
		int flag=0,firstVisible=-1;
		for(Category item: categories){
			if(item.retCategoryName().equals("All Channels"))
				continue;
			if(item.retVisibility()==1){
				firstVisible = firstVisible==-1 ? (item.retOrder()-1) : firstVisible;
				if(item.retId()==currCatIdSelected){
					flag=1;
					currCatSelected = item.retOrder()-1;
					break;
					
				}
			}
		}
		if(flag==0){
			Log.e("Sidekick_V3","Setting firstVisible , currCat to : "+firstVisible);
			currCatSelected=firstVisible;
			currCatIdSelected = categories.get(currCatSelected).retId();
		}
		
		Log.e("Sidekick_V4","onResume currCat: "+currCatSelected);
		Log.e("Sidekick_V4","onResume currCatId: "+currCatIdSelected);

		updatePresentCategory(currCatSelected, currCatIdSelected, false);
		Log.e("lifecycle","onRestartCalled");
	}
	
	public void onDestroy(){
		super.onDestroy();
//		datasource.close();
		unregisterReceiver(mConnReceiver);
		Log.e("lifecycle","onDestroyCalled");
	}
	
	public void onPause(){
		super.onPause();
		Log.e("lifecycle","onPauseCalled");
	}
	
	public void onResume(){
		super.onResume();
//		if(deleted){
//			Toast.makeText(this, "Performing Click", Toast.LENGTH_SHORT).show();
//			manualMethodOnClick(currCatSelected,false);
//			deleted = false;
//		}
	
		Log.e("lifecycle","onResumeCalled");
	}
		
	public void retryLoading(View v){
		Toast.makeText(this, "RETRYING...", Toast.LENGTH_SHORT).show();
		if(isInternetPresent){
//			if(sendingLock)
			checkAndSend(WITH_LOADING);
		}
		else
			setMainScreen(NO_CONNECTION);
	}
	
	
//********************************   SERVER COMMUNICATION  ***********************************************************
	  //***************************API QUEUE HANDLING *********************************
	
	public class GetProfileBuildUp extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... params) {
			Log.e("Sidekick_V3","Getting profile perc");
			 	StringBuilder builder = new StringBuilder();
			    StringBuilder url = new StringBuilder();
			    //Building the url using the parameters. Check trello Dev Spech for format.
			    url.append(getResources().getString(R.string.domain)+"user_inputs?");
			    url.append("device_key="+params[1]);
			    url.append("&request_time="+params[0]);
			    String finURL = url.toString();
			    String sp = " ";
			    String per = "%20";
			    //Replacing spaces with %20. Spaces are characterised as invalid character in HTTP get query.
			    finURL = finURL.replaceAll("["+Pattern.quote(sp)+"]", per);
			    ML.log("MAIN",url.toString());
			    HttpParams httpParameters = new BasicHttpParams();
			 // Set the timeout in milliseconds until a connection is established.
			 // The default value is zero, that means the timeout is not used. 
			 int timeoutConnection = 3000;
			 HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			 // Set the default socket timeout (SO_TIMEOUT) 
			 // in milliseconds which is the timeout for waiting for data.
			 int timeoutSocket = 5000;
			 HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		     HttpClient client = new DefaultHttpClient(httpParameters);
		     HttpGet httpGet = new HttpGet(finURL);
			 try{
			    	HttpResponse response = client.execute(httpGet);
			    	StatusLine statusLine = response.getStatusLine();
			    	int statusCode = statusLine.getStatusCode();
			    	if (statusCode == 200) {
			    		HttpEntity entity = response.getEntity();
			    		InputStream content = entity.getContent();
			    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			    		String line;
			    		while ((line = reader.readLine()) != null) 
			    			builder.append(line);
			    	}
			    	else
			    		ML.log("MAIN", "Failed to download file");
			    }catch (ClientProtocolException e){
			      e.printStackTrace();
			    }catch(SocketTimeoutException e1){
			      System.out.println(e1);
			    }catch (IOException e){
			      e.printStackTrace();
			    }
			return builder.toString();
		}
		
		@Override
		protected void onPostExecute(String result){
			Log.e("Sidekick_V3","ProfilePercent Response: "+result);
			try {
				JSONObject mainJson = new JSONObject(result);
				Integer count = mainJson.getInt("count");
				Log.e("Sidekick_V3","Count : "+count.toString());
				if(count!=null){
					editor.putInt("profile_count", count);
					editor.commit();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	
	
	
	private class ApiQueueCall extends AsyncTask<ApiCall,String,String>{

		@Override
		protected String doInBackground(ApiCall... params) {
			readApiResponse(params[0]);
			return null;
		}
		
	}
	
	public String readApiResponse(ApiCall call){
		int statusCode = 0;
		JSONObject apiData = null;
		JSONObject metadata = new JSONObject();
		try {
			apiData = new JSONObject(call.api_data);
			metadata.put("request_time", DateUtil.formatTime(System.currentTimeMillis()));
			metadata.put("updated_time", DateUtil.formatTime(System.currentTimeMillis()));
			metadata.put("device_key", myPreferences.getString("devicekey", "0"));
			metadata.put("request_id", call.request_id);
			apiData.put("metadata",metadata);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String uri = call.url;
 	    Log.e("Sidekick_V3","API DATA : "+apiData.toString());

//    	StringBuilder builder = new StringBuilder();
//    	HttpClient httpclient = new DefaultHttpClient();
// 	    HttpPost httppost = new HttpPost(uri);
// 	    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
// 	   	postParams.add(new BasicNameValuePair("json_data", apiData.toString()));
// 	       try{
// 	    	   	UrlEncodedFormEntity urlentity = new UrlEncodedFormEntity(postParams);
// 	    	    urlentity.setContentEncoding(HTTP.UTF_8);
// 	    	    httppost.setEntity(urlentity);
// 	    	//  httppost.getParams().setParameter("fb_user_json", gson);
//		    	HttpResponse response = httpclient.execute(httppost);
//		    	StatusLine statusLine = response.getStatusLine();
//		    	statusCode = statusLine.getStatusCode();
//	    	if (statusCode == 200) {
//	    		HttpEntity entity = response.getEntity();
//	    		InputStream content = entity.getContent();
//	    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//	    		String line;
//	    		while ((line = reader.readLine()) != null) 
//	    			builder.append(line);
//	    	}
//	    	else
//	    		Log.e("err", "Failed to download file");
////				JSONObject json = new JSONObject(builder.toString());
////				if(json.getString("status").equals("success")){
////					editor.putString("devicekey", json.getString("device_key"));
////					editor.commit();
////				}
//				
//		    }catch (ClientProtocolException e){
//		      e.printStackTrace();
//		    }catch (IOException e){
//		      e.printStackTrace();
//		    }
// 	       if(statusCode!=200)
// 	    	   return null;
// 	       return builder.toString();
 	    return null;
	}
	
	
	//**************************************************************************************
	
	
	/**
	   * Async Task for getting response from the 
	   * server and storing it in the database.
	   * @param	param[0] idlist : List of ids to be sent to server for getting the latest listings.
	   * @param param[1] updtime : Time the listings were last updated.
	   * @param param[2] listing_time : Time for which listings are demanded.
	   * @param param[3] req_time_sent : System time, when the request was sent.
	   */
	  private class LoadJsonTask extends AsyncTask<String, Void, Integer> {
		  
		  Integer beforeCatId;
		  String beforeTimeSlot;
	      protected void onPreExecute (){
	    	  beforeCatId = Integer.valueOf(presentCategory.retId());
	    	  beforeTimeSlot = new String(retTimeSlot(currCal));
	    	  Log.e("Sidekick_V4","beforeCategory: "+beforeCatId);
	    	  Log.e("Sidekick_V4","beforeTimeSlot: "+beforeTimeSlot);
	      }
	      
	      protected void onPostExecute(Integer message){
	    	  int key = getHashCode(beforeCatId, beforeTimeSlot);
	    	  stateMap.put(key, message);
	    	  
	    	  try {
				mContainer.setTime(DateUtil.formatTime(currQueryTime).getTime());
	    	  } catch (ParseException e) {
				e.printStackTrace();
	    	  }	    
	      }
	      
	      protected Integer doInBackground(String... params) {
	    	  return getMyJSON(params[0],params[1],params[2],params[3]);
	      }
	  }
	  
	  
	  /**
	   * Function for getting the JSON data from the server.
	   * @param idlist	:	Same as above.
	   * @param updtime	:	Same as above.
	   * @param listing_time	:	Same as above.
	   * @param req_time_sent	:	Same as above.
	   * @return	: None. (ArrayList<String> returned is placeholder. Because if return type is void,
	   * onPostExecute() function of AsyncTask is not called. This matter needs further investigation,
	   */
	  public Integer getMyJSON(String idListSent,String updTimeSent,String listingTimeSent,String reqTimeSent){
		  String readResponse = readResponse(idListSent,updTimeSent,listingTimeSent,reqTimeSent);
		  String reqTimes[] = new String[2];
		  Integer message;
		  reqTimes[0] = reqTimeSent;
		  if(readResponse==null){
			  Log.e("Sidekick_V3","Response was null");
			  if(isInternetPresent)
				  message = FAILED_TO_CONNECT;
			  else
				  message = NO_CONNECTION;
			  return null;
		  }
		  else{
			  Log.e("Sidekick_V3","Response was NOT null: "+readResponse);
			  message=DISPLAY_LISTINGS;

		  }
		  
		  int listingIdReceived;
		  String stTimeReceived,endTimeReceived,titleReceived,progSynopsisReceived,epiSynopsisReceived,progIdReceived,imageURLReceived,retelecastReceived,youtubeReceived;
		  String metaTimeslot,metaIdList,metaNoUpdIds;
		  String metaReqTime=null;
		  
		    try {
		    	//Our JSON Object contains two fields namely 'Listings' and 'listings_metadata'
		    	JSONObject mainResponse = new JSONObject(readResponse);
		    	//Here we get the Listings fields which itself is an array.
		    	JSONArray arrayListings =mainResponse.getJSONArray("Listings");
		    	
		    	//Here we get listings_metadata field.
		    	JSONObject metaData = mainResponse.getJSONObject("listings_metadata");
		    	
		    	//Following lines store and display the listings_metadata content for debugging purposes.
		    	metaTimeslot = metaData.getString("timeslot");
		    	metaTimeslot+=":00";
	    		metaIdList = metaData.getString("requested_sidekick_ids");
	    		metaReqTime = metaData.getString("request_time");
	    		reqTimes[1] = metaReqTime;
	    		metaNoUpdIds = metaData.getString("no_updates");
	    		if(arrayListings.length()==0)
	    			message=FAILED_TO_CONNECT;
		    	//Now we get listings of each channel one by one using this loop.
		    	for(int j=0;j<arrayListings.length();j++){
		    		
		    		JSONArray arrayChannelListings =arrayListings.getJSONArray(j);	// jth channel taken from the listings.
		    		JSONObject chanListing=null;
		    		Integer chanIdReceived=1;
		    		
		    		if(arrayChannelListings.length()==0){
		    			 continue;
		    		}
		    		
		    		chanListing = arrayChannelListings.getJSONObject(0);
		    		chanIdReceived = chanListing.getInt("sidekick_id");
		    		//We take out the channel id from the first listing of the channel. It remains same for all listings of the same channel.
		    		
		    		//We delete the listings already existing with same chanId and Timeslot
		    		datasource.refreshListings(chanIdReceived,metaTimeslot);
		    		
		    		// Getting listings of the jth channel.
		    		for (int i = 0; i < arrayChannelListings.length(); i++) {
		    			
		    			JSONObject jsonObject = arrayChannelListings.getJSONObject(i);
		    			stTimeReceived = jsonObject.getString("start_time");
		    			endTimeReceived = jsonObject.getString("end_time");
		    			retelecastReceived="";
		    			if(jsonObject.getJSONArray("retelecast").length()!=0)
		    			retelecastReceived = jsonObject.getJSONArray("retelecast").getString(0);
		    			if(retelecastReceived!=null){
		    				retelecastReceived = retelecastReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
			    			retelecastReceived = retelecastReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			}
		    			youtubeReceived="";
		    			if(!jsonObject.isNull("youtube_link"))
		    				youtubeReceived=jsonObject.getString("youtube_link");
		    			
		    			
		    			// Formatting start and end time. Removing T and Z from it
		    			stTimeReceived = stTimeReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
		    			stTimeReceived = stTimeReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			endTimeReceived = endTimeReceived.replaceFirst("["+Pattern.quote("T")+"]", " ");
		    			endTimeReceived = endTimeReceived.replaceFirst("["+Pattern.quote("Z")+"]", "");
		    			
		    			imageURLReceived = jsonObject.getString("image_url");
		    			titleReceived = jsonObject.getString("title");
		    			progSynopsisReceived = jsonObject.getString("synopsis");
		    			epiSynopsisReceived = jsonObject.getString("episode_synopsis");
		    			listingIdReceived = jsonObject.getInt("id");
		    			progIdReceived = jsonObject.getString("program_id");
		    			
		    			//Adding the above extracted details into our channels_listings table
		    			datasource.addChannelListing(listingIdReceived,chanIdReceived, stTimeReceived, endTimeReceived, titleReceived, progSynopsisReceived,epiSynopsisReceived, imageURLReceived,progIdReceived,metaTimeslot,retelecastReceived,youtubeReceived);
		    			Log.v("Sidekick_V3", "Incoming Data from Server");
		    		}
		    	}
//		    	if(arrayListings.length()!=0)
		    	datasource.addUpdListing(extractIds(metaIdList),metaTimeslot,metaReqTime);
		    	updateListingObject = datasource.retUpdListings(presentCategory.returnAsIntArray(),retTimeSlot(currCal));
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return message;
	  }
	  
	  /**
	   * Function for reading the response from server.
	   * @param idlist	:	Same as above
	   * @param lastupd	:	Same as above
	   * @param listing_time	:	Same as above
	   * @param req_time_sent	:	Same as above
	   * @return	String containing all of the JSON data. It is used for making JSON objects in previous function.
	   */
	  public String readResponse(String idListSent,String updTimeSent,String listingTimeSent,String reqTimeSent) {
		    StringBuilder builder = new StringBuilder();
		    StringBuilder tempURL = new StringBuilder();
		    
		    //Building the url using the parameters. Check trello Dev Spech for format.
		    tempURL.append(getResources().getString(R.string.domain)+"listings/channel_timeslot_listings?");
		    tempURL.append("sidekick_ids="+idListSent);
		    tempURL.append("&last_updated="+updTimeSent);
		    tempURL.append("&listings_time="+listingTimeSent);
		    tempURL.append("&request_time="+reqTimeSent);
		    String finalURL = tempURL.toString();
		    Log.i("Sidekick_V4", finalURL);
		    String sp = " ";
		    String per = "%20";
		    //Replacing spaces with %20. Spaces are characterised as invalid character in HTTP get query.
		    finalURL = finalURL.replaceAll("["+Pattern.quote(sp)+"]", per);
		    int statusCode=0;;
		    HttpClient client = new DefaultHttpClient();
		    HttpGet httpGet = new HttpGet(finalURL);
		    try{
		    	HttpResponse response = client.execute(httpGet);
		    	StatusLine statusLine = response.getStatusLine();
		    	statusCode = statusLine.getStatusCode();
		    	if (statusCode == 200) {
		    		HttpEntity entity = response.getEntity();
		    		InputStream content = entity.getContent();
		    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    		String line;
		    		while ((line = reader.readLine()) != null) 
		    			builder.append(line);
		    		
		    	}
		    	else
		    		Log.e("err", "Failed to download file");
		    }catch (ClientProtocolException e){
		      e.printStackTrace();
		    }catch (IOException e){
		      e.printStackTrace();
		    }catch(Exception e){
		    	Log.e("err", "Failed to download file");
		    }
		    
		    
		    if(statusCode!=200)
		    	return null;
		    return builder.toString();
	  }
		  
	  /**
	   * Extracting ids from the comma separated string obtained from the server.
	   * @param req_ids : String containing comma separated values.
	   * @return ArrayList<Integer>  containing ids.
	   */
	  public ArrayList<Integer>	extractIds(String req_ids){
		  ArrayList<Integer> temp = new ArrayList<Integer>();
		  for(String single_channel : req_ids.split(",")){
				temp.add(Integer.parseInt(single_channel));
			}
		return temp;
	  }
	   

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void startEditActivity(View v){
		Log.e("Sidekick_V4","Before even starting currCat: "+currCatSelected);

		Intent intent = new Intent(this,FinEditCat.class);
		startActivity(intent);
	}
	
	public int getHashCode(int catId,String timeSlot){
		int hashCode = 17;
		hashCode = 31*hashCode + catId;
		hashCode = 31*hashCode + timeSlot.hashCode();
		return hashCode;
	}
	
}
