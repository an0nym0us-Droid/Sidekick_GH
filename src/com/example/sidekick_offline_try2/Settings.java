package com.example.sidekick_offline_try2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.profiles.Profiles;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;


public class Settings extends SherlockActivity {

	private ProfilePictureView profilePictureView;
	String actname = "Settings";
	ActionBar actionBar;
	int selectedProfileId;
	Profiles mProfile;
	TextView providerTV;
	TextView baseTV;
	TextView addonsTV;
	public ListDataSource datasource;
	private TextView userNameView;
	View fbdiv;
	private UiLifecycleHelper uiHelper;
	Spinner dthChooser;
	SharedPreferences myPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences remotePref;
	SharedPreferences.Editor remoteEditor;
    CheckBox pubperm;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	boolean askedpubperm;
	private static final List<String> WRITE_PERMISSIONS = Arrays.asList("publish_actions","publish_stream");
	private static final List<String> READ_PERMISSIONS = Arrays.asList("user_location", "user_birthday", "user_likes","user_hometown","email");
	
	//*** FROM WALKTHROUGH ACTIVITY *******************
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception){
    	ML.log(actname, "onSessionStateChangeCalled");
		ML.log(actname, "State :"+state.toString());
		if(session.isClosed()){
			askedpubperm = false;
			editor.putBoolean("askedpubperm", askedpubperm);
			askedpubperm = false;
			editor.putBoolean("askedpubperm", askedpubperm);
			
			editor.putString("fbid", "");
		    editor.putString("accesstoken", "");
		    editor.putBoolean("askedpubperm", false);
		    editor.putString("fbname", "");
		    ML.log(actname, "Logging out... generating new dk and storing");
		    ML.log(actname, "Prev dk was "+myPreferences.getInt("dk",1));
		    editor.putString("devicekey", getRandomNumber(16).toString());
		    editor.putInt("dk", myPreferences.getInt("dk", 1)+1);
		    editor.commit();
		    ML.log(actname, "Now dk is "+ myPreferences.getInt("dk", 1));
			ML.log(actname, "Session Closed");
			profilePictureView.setVisibility(View.GONE);
			userNameView.setVisibility(View.GONE);
			pubperm.setVisibility(View.GONE);
			fbdiv.setVisibility(View.GONE);
		}
		if(session!=null&&state.toString().equals("OPENING")){
			editor.putBoolean("loggingin", true);
			editor.commit();
		}
		
		if(session!=null&&state.isOpened()&&myPreferences.getBoolean("loggingin", true)||state.toString().equals("OPENED_TOKEN_UPDATED")){
			
			
			
			if(session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream")))
				pubperm.setChecked(true);
	        else
	        	pubperm.setChecked(false);
			if(!session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream"))&&!askedpubperm){
				ML.log(actname,"Requesting Permission ...");
				requestPublishPermissions(session);
				askedpubperm = true;
				editor.putBoolean("askedpubperm", askedpubperm);
				editor.commit();
				
			}
			else{	
				ML.log(actname, "Should send now");
				ML.log(actname, "Inside Else");
				ML.log(actname, "State :"+state.toString());
				if(myPreferences.getBoolean("loggingin", true)){
					ML.log(actname, "Loggin in and sending" );
				}
				if(state.toString().equals("OPENED_TOKEN_UPDATED")){
					ML.log(actname, "Updating the token and sending");
				}
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// TODO Auto-generated method stub
						if(user!=null){
							
							ML.log(actname,"Sending to MyServer with permissions : "+Session.getActiveSession().getPermissions());
							new DownloadFilesTask().execute(new String[] {setUserProperties(user),"create"});
							editor.putBoolean("loggingin", false);
							editor.commit();
							profilePictureView.setVisibility(View.VISIBLE);
							userNameView.setVisibility(View.VISIBLE);
							fbdiv.setVisibility(View.VISIBLE);
							pubperm.setVisibility(View.VISIBLE);
							userNameView.setText(myPreferences.getString("fbname", ""));
							profilePictureView.setProfileId(myPreferences.getString("fbid", ""));
						}
					}
				});//End of executeMeAsync
			}
		}
    }
    
    Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			// TODO Auto-generated method stub
			ML.log(actname, "StatusCallback : call");
			onSessionStateChange(session, state, exception);
		}
	};
	
	
	// ************************************************
	
	private void requestPublishPermissions(Session session) {
        if (session != null) {
        	
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, WRITE_PERMISSIONS)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(100);
            
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
	}
	
	public String setUserProperties(GraphUser user){
		Session session = Session.getActiveSession();
		int pubAction = 0;
		editor.putString("reqtime",DateUtil.formatTime(System.currentTimeMillis()));
		editor.putString("fbid", user.getId());
		editor.putString("accesstoken",session.getAccessToken());
		editor.putString("fbname", user.getName());

		editor.commit();
		if(session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream"))){
			pubAction = 1;
		}
		
		return serialize(pubAction);
	}
	
	public String serialize(int pubAction) 
    {
    	ML.log(actname, "serializing");
    	JSONObject json = new JSONObject();
    	JSONObject jsonBigger = new JSONObject();
    	try {
    		json.put("facebook_id", myPreferences.getString("fbid", ""));
    		json.put("access_token", myPreferences.getString("accesstoken", ""));
    		json.put("request_time", myPreferences.getString("reqtime", DateUtil.formatTime(System.currentTimeMillis())));
    		json.put("device_key", myPreferences.getString("devicekey", ""));
    		json.put("publish_actions", pubAction);
    		jsonBigger.put("fb_user", json);
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  jsonBigger.toString();	
   }
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.pref);
	    dthChooser = (Spinner)	findViewById(R.id.dthchooser);
	    providerTV = (TextView) findViewById(R.id.providerTV);
	    baseTV = (TextView) findViewById(R.id.baseTV);
	    addonsTV = (TextView) findViewById(R.id.addonsTV);
	    
	 
	    final ArrayList<Integer> dth_codes = new ArrayList<Integer>();
	    remotePref = getSharedPreferences("remotePref", 0);
		remoteEditor = remotePref.edit();
		mProfile = new Profiles();
		myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = myPreferences.edit();
		selectedProfileId = myPreferences.getInt("profile_id", 1);
		datasource = new ListDataSource(this,getResources().getString(R.string.domain));
		datasource.createDatabase();
        datasource.open();
		mProfile= datasource.getProfile(selectedProfileId);
		actionBar = getSupportActionBar();
	    actionBar.setTitle("  Settings");
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		
	    ArrayAdapter<String> dthOptions = new ArrayAdapter<String>(this, R.layout.spinner_actionbar_item,MainActivity.datasource.retDTHOptions(dth_codes));
        dthOptions.setDropDownViewResource(R.layout.spinner_actionbar_dropdownitem);
	    dthChooser.setAdapter(dthOptions);
        dthChooser.setSelection(remotePref.getInt("dth_code", 2353)-2353);

	   	dthChooser.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View v, int pos,
        			long id) {
        			remoteEditor.putInt("dth_code", dth_codes.get(pos));
        			remoteEditor.commit();
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {
        	}
    	 
        });
	   	
	   	
	   	providerTV.setText(mProfile.getProviderName());
	   	baseTV.setText(mProfile.getBase());
	   	StringBuilder addonBuilder = new StringBuilder();
	   	int i=1;
	   	for(String item : mProfile.getAddonsString()){
	   		addonBuilder.append(i+".");
	   		addonBuilder.append(item);
	   		addonBuilder.append("\n");
	   		i++;
	   	}
	   	
	   	addonsTV.setText(addonBuilder.toString());
	   	
	   	
//	    String[] sidekick_string = new String[] {"Feedback","Rate the app","About"};
//	    String[] customize_string = new String[] {"Customize Categories"};
//	    TextView feedback = (TextView) findViewById(R.id.feedback);
//	    TextView rateapp = (TextView) findViewById(R.id.rateapp);
//	    TextView abput = (TextView) findViewById(R.id.about);

//	   	ListView sidekick = (ListView) findViewById(R.id.listsettings);
//	    ListView customize = (ListView) findViewById(R.id.customize);
//	    ArrayAdapter<String> ad_sidekick = new ArrayAdapter<String>(this, R.layout.catitem, sidekick_string);
//	    ArrayAdapter<String> ad_customize = new ArrayAdapter<String>(this, R.layout.catitem, customize_string);
//	    customize.setAdapter(ad_customize);
//	    sidekick.setAdapter(ad_sidekick);
//	    customize.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int pos,
//					long id) {
//						if(pos==0){
//							startEditActivity();
//						}
//			}
//		});
	    
		
//	    sidekick.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int pos,
//					long id) {
//				if(pos==1){
//					AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
//					alert.setTitle("Rate the app");
//					final RatingBar rb = new RatingBar(Settings.this);
//					
//					rb.setNumStars(5);
//					rb.setStepSize((float) 0.1);
//					rb.setRating((float)(1.1));
//					alert.setView(rb);
//					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//
//							editor.putFloat("rating", rb.getRating());
//							editor.commit();
//						}
//					});
//					alert.show();
//				}
//				else{
//					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.1sidekick.com"));
//					startActivity(browserIntent);
//				}
//			}
//		});
	    LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = mInflater.inflate(R.layout.pref, null);
	   
		
		
		askedpubperm = myPreferences.getBoolean("askedpubperm", false);
	    profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
	    pubperm = (CheckBox) findViewById(R.id.share);
	    fbdiv = (View) findViewById(R.id.fbdiv);
	    pubperm.setOnClickListener(chkchanged);
	    userNameView = (TextView) findViewById(R.id.username);
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    LoginButton authButton = (LoginButton) findViewById(R.id.authButtonSettings);
        authButton.setReadPermissions(READ_PERMISSIONS);
	    ML.log(actname, "fb id :"+myPreferences.getString("fbid", ""));
    	ML.log(actname, "fb name : "+myPreferences.getString("fbname", ""));
	    if(userNameView == null){
	    	ML.log(actname, "Userview null hai !!!");
	    }
	    if(profilePictureView == null){
	    	ML.log(actname, "Profileview null hai !!!");
	    }
	   Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        Session.setActiveSession(session);
	        profilePictureView.setVisibility(View.VISIBLE);
			userNameView.setVisibility(View.VISIBLE);
			fbdiv.setVisibility(View.VISIBLE);
			pubperm.setVisibility(View.VISIBLE);
	        ML.log(actname, "setting username");
	        userNameView.setText(myPreferences.getString("fbname", ""));
	        ML.log(actname, "setting pic");
	        profilePictureView.setProfileId(myPreferences.getString("fbid", ""));
	        if(session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream")))
				pubperm.setChecked(true);
	        else
	        	pubperm.setChecked(false);
	        
	    }
	}
	
	
	public void sidekickSettings(View v){
		TextView tv = ((TextView)v);
		if(tv.getText().equals("Rate the app")){
			AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
			alert.setTitle("Rate the app");
			final RatingBar rb = new RatingBar(Settings.this);
			
			rb.setNumStars(5);
			rb.setStepSize((float) 0.1);
			rb.setRating((float)(1.1));
			alert.setView(rb);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					editor.putFloat("rating", rb.getRating());
					editor.commit();
				}
			});
			alert.show();
		}else{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.1sidekick.com"));
			startActivity(browserIntent);
		}
	}
	
	OnClickListener chkchanged = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Session session = Session.getActiveSession();
			if (session != null && session.isOpened()) {
				if(pubperm.isChecked()){
					Log.e("Sidekick_V3","Checking it ....");
					requestPublishPermissions(session);
					Log.e("Sidekick_V3","After Checking");

					askedpubperm = true;
					editor.putBoolean("askedpubperm", askedpubperm);
					editor.commit();
					if(session.getPermissions().containsAll(Arrays.asList("publish_actions","publish_stream"))){
						pubperm.setChecked(true);
						Log.e("Sidekick_V3","Updating FB DATA");

						new DownloadFilesTask().execute(new String[] {serialize(1),"update"});
					}
			        else
			        	pubperm.setChecked(false);
				}
				else{
					new Request(
							   session,
							    "/me/permissions/publish_stream",
							    null,
							    HttpMethod.DELETE,
							    new Request.Callback() {
							        public void onCompleted(Response response) {
							            /* handle the result */
							        	Log.e("Sidekick_V3",response.toString());
							        }
							    }
					).executeAsync();
					Log.e("Sidekick_V3","Unchecking it ..updating");

					new DownloadFilesTask().execute(new String[] {serialize(0),"update"});
				}
			}
		}
	};
	
	public void editProfile(View v){
		Intent intent = new Intent(this,SetupWizard.class);
		startActivity(intent);
		finish();
	}
	
	public void startEditActivity(){
		Intent intent = new Intent(this,FinEditCat.class);
		startActivity(intent);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	   
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	}
	@Override
	public void onResume() {
	    super.onResume();
	    Log.i("awol", "onResume");
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    Log.i("awol", "onSavedInstance");
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    Log.i("awol", "Pause");
	    uiHelper.onPause();
	}

	public void onBackPressed() {
		finish();
    }
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
//				MainActivity.datasource.updateOrder(MainActivity.categories);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.i("awol", "onDestroy");
	    uiHelper.onDestroy();
	}
	
	
	public static BigInteger getRandomNumber(final int digCount){
	    return getRandomNumber(digCount, new Random());
	}
	public static BigInteger getRandomNumber(final int digCount, Random rnd){
	    final char[] ch = new char[digCount];
	    for(int i = 0; i < digCount; i++){
	        ch[i] =
	            (char) ('0' + (i == 0 ? rnd.nextInt(9) + 1 : rnd.nextInt(10)));
	    }
	    return new BigInteger(new String(ch));
	}
	
	 private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {
	       
			@Override
			protected Void doInBackground(String... params) {
				ML.log(actname, "doInBackground");
	        	String jsonString = params[0];
	        	
	        	StringBuilder builder = new StringBuilder();
	        	HttpClient httpclient = new DefaultHttpClient();
	     	    HttpPost httppost = new HttpPost(getResources().getString(R.string.domain)+"fb_user/"+params[1]);
	     	    Log.e("Sidekick_V3","URL for Settings: "+getResources().getString(R.string.domain)+"fb_user/"+params[1]);
	     	    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
	     	   	postParams.add(new BasicNameValuePair("fb_user_json", jsonString));
	     	   ML.log(actname,"String to send : "+jsonString);
	     	       try{
	     	    	   	UrlEncodedFormEntity urlentity = new UrlEncodedFormEntity(postParams);
	     	    	    urlentity.setContentEncoding(HTTP.UTF_8);
	     	    	    httppost.setEntity(urlentity);
	     	    	//  httppost.getParams().setParameter("fb_user_json", gson);
	   		    	HttpResponse response = httpclient.execute(httppost);
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
			    		Log.e("err", "Failed to download file");
	   		    }catch (ClientProtocolException e){
	   		      e.printStackTrace();
	   		    }catch (IOException e){
	   		      e.printStackTrace();
	   		    }
	     	      ML.log(actname,"Response : "+builder.toString());
				return null;
				
			}
	    }
}
