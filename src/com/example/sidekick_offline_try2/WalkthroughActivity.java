package com.example.sidekick_offline_try2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.SessionTracker;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;



public class WalkthroughActivity extends Activity {

    private static final int MAX_VIEWS = 3;
    public String actname = "WalkthroughActivity";
    static final int[] rids = {R.id.imageView1,R.id.imageView2,R.id.imageView3};
    SharedPreferences myPreferences;
    SharedPreferences.Editor editor;
    ViewPager mViewPager;
    TextView urset;
    RelativeLayout walkParent;
    LinearLayout fbButtons;
    ImageButton fblogin;
    UserDetail fbUser;
    private UiLifecycleHelper uiHelper;
    public final WalkthroughActivity wk = this;
    Session mCurrentSession;
    String uid;
    SessionTracker mSessionTracker;
    public boolean askedpubperm;
    private static final List<String> WRITE_PERMISSIONS = Arrays.asList("publish_actions","publish_stream");
    private static final List<String> READ_PERMISSIONS = Arrays.asList("user_location", "user_birthday", "user_likes","user_hometown","email");
    // ************ ON SESSION STATE CHANGED *******************
    
   /* private void fbLogin(View v){
    	 mSessionTracker = new SessionTracker(getBaseContext(), new StatusCallback() {

    	        @Override
    	        public void call(Session session, SessionState state, Exception exception) {
    	        }
    	    }, null, false);

    	    String applicationId = getString(R.string.app_id);
    	    mCurrentSession = mSessionTracker.getSession();

    	    if (mCurrentSession == null || mCurrentSession.getState().isClosed()) {
    	        mSessionTracker.setSession(null);
    	        Session session = new Session.Builder(getBaseContext()).setApplicationId(applicationId).build();
    	        Session.setActiveSession(session);
    	        mCurrentSession = session;
    	    }

    	    if (!mCurrentSession.isOpened()) {
    	        Session.OpenRequest openRequest = null;
    	        openRequest = new Session.OpenRequest(SignUpChoices.this);

    	        if (openRequest != null) {
    	            openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
    	            openRequest.setPermissions(Arrays.asList("user_birthday", "email", "user_location"));
    	            openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

    	            mCurrentSession.openForRead(openRequest);
    	        }
    	    }else {
    	        Request.executeMeRequestAsync(mCurrentSession, new Request.GraphUserCallback() {
    	              @Override
    	              public void onCompleted(GraphUser user, Response response) {
    	                  Log.w("myConsultant", user.getId() + " " + user.getName() + " " + user.getInnerJSONObject());
    	              }
    	            });
    	    }
    }*/
    private void onSessionStateChange(Session session, SessionState state, Exception exception){
    	ML.log(actname, "onSessionStateChangeCalled");
		ML.log(actname, "State :"+state.toString());
		if(session.isClosed()){
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
			
		}
		if(session!=null&&state.toString().equals("OPENING")){
			editor.putBoolean("loggingin", true);
			editor.commit();
		}
		else if(session!=null&&state.isOpened()&&myPreferences.getBoolean("loggingin", true)||state.toString().equals("OPENED_TOKEN_UPDATED")){
			if(!session.getPermissions().containsAll(WRITE_PERMISSIONS)&&!askedpubperm){
				ML.log(actname,"Requesting Permission ...");
				requestPublishPermissions(session);
				askedpubperm = true;
				editor.putBoolean("askedpubperm", askedpubperm);
				editor.commit();
			}
			else{	
				ML.log(actname, "Inside Else");
				ML.log(actname, "State :"+state.toString());
				ML.log(actname, "Should send now");
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
							ML.log(actname, "Sending dk "+String.valueOf(myPreferences.getInt("dk", 1)));
							new DownloadFilesTask().execute(new String[] {setUserProperties(user),"update"});
							editor.putBoolean("loggingin", false);
							editor.commit();
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
    // *********************************************************
    
	
	
	public String setUserProperties(GraphUser user){
		Session session = Session.getActiveSession();
		int pubAction = 0;
		editor.putString("reqtime",DateUtil.formatTime(System.currentTimeMillis()));
		editor.putString("fbid", user.getId());
		editor.putString("accesstoken",session.getAccessToken());
		editor.putString("fbname", user.getName());
		editor.commit();
		if(session.getPermissions().containsAll(WRITE_PERMISSIONS)){
			pubAction = 1;
		}
		return serialize(pubAction);
	}
	public String serialize(int pubAction){
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
			e.printStackTrace();
		}
		return  jsonBigger.toString();	
   }
	
	 private void requestPublishPermissions(Session session) {
	        if (session != null) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, WRITE_PERMISSIONS)
	                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
	            	    .setRequestCode(100);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	        }
	 }
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough_activity);
        fbButtons = (LinearLayout) findViewById(R.id.rel);
        walkParent = (RelativeLayout) findViewById(R.id.walkParent);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = myPreferences.edit();
		 Typeface tf = Typeface.createFromAsset(WalkthroughActivity.this.getAssets(),"Roboto-Thin.ttf");
		askedpubperm = myPreferences.getBoolean("askedpubperm", false);
		urset = (TextView) findViewById(R.id.urset);
		urset.setTypeface(tf);
		TextView plzlogin = (TextView) findViewById(R.id.plzlogin);
		plzlogin.setTypeface(Typeface.createFromAsset(WalkthroughActivity.this.getAssets(),"RobotoCondensed-Bold.ttf"));
        mViewPager = (ViewPager) findViewById(R.id.walkthroughPager);
        mViewPager.setAdapter(new WalkthroughPagerAdapter());
        mViewPager.setOnPageChangeListener(new WalkthroughPageChangeListener());
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(READ_PERMISSIONS);
        ML.log(actname, "onCreate()");
        uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	   
    }
    
   
    //On pressing skip, start MainActivity.
    public void skip(View v){
//    	CheckBox ch = (CheckBox) findViewById(R.id.checkBox1);
//    	//Set the preference so that walkthrough won't be shown again.
//    	if(ch.isChecked()){
//            editor.putBoolean("wScreenKey", true);
//            editor.commit(); 
//    	}
    	finish();
    	Intent intent = new Intent();
    	if(myPreferences.getBoolean("firsttime", true))
    		intent.setClass(wk, SetupWizard.class);
    	else
        	intent.setClass(wk, MainActivity.class);

    	startActivity(intent);
    }
    
    //Setting appropriate screens.
    class WalkthroughPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_VIEWS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            Log.e("walkthrough", "instantiateItem(" + position + ");");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Typeface tf = Typeface.createFromAsset(WalkthroughActivity.this.getAssets(),"Roboto-BoldItalic.ttf");
            View imageViewContainer = inflater.inflate(R.layout.walkthrough_single_view, null);
            ImageView imageView = (ImageView) imageViewContainer.findViewById(R.id.walkthruSingleitem);
//            ((TextView) imageViewContainer.findViewById(R.id.screen1Item1)).setTypeface(tf);
//            ((TextView) imageViewContainer.findViewById(R.id.screen1Item2)).setTypeface(tf);
//            ((TextView) imageViewContainer.findViewById(R.id.screen1Item3)).setTypeface(tf);
//            ImageView imageView = (ImageView) imageViewContainer.findViewById(R.id.image_view);
//
            switch(position) {
            case 0:
                imageView.setBackgroundResource(R.drawable.newbg);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.newbgsc2);
                break;
            case 2:
                imageViewContainer.setVisibility(View.GONE);
                break;
            }

            ((ViewPager) container).addView(imageViewContainer, 0);
            return imageViewContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }
    }

	
    public void OnFinished(){
    	ML.log(actname, "FINISHED");
    }
    
    //PageChangeListener. Called when walkthrough page are changed. 
    class WalkthroughPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
              @Override
        public void onPageSelected(int position) {

            	  
            	  if(position==2){
                  	fbButtons.setVisibility(View.VISIBLE);
                  	walkParent.setBackgroundResource(R.drawable.setup_background);
            	  }
                  else{
                  	fbButtons.setVisibility(View.GONE);
                  	walkParent.setBackgroundColor(0x000000);
                  }
        	//Setting the indicator according to page selected.
        	for(int i=0;i<3;i++){
        		ImageView iv = (ImageView) findViewById(rids[i]);
        		if(i==position)
        			iv.setImageResource(R.drawable.bulb_lit);
        		else
        			iv.setImageResource(R.drawable.bulb_unlit);
        	}
        	
//        	//Runs the MainActivity when we are on the last view.
//            switch(position) {
//
//            case MAX_VIEWS - 1:
//            	CheckBox ch = (CheckBox) findViewById(R.id.checkBox1);
//            	//Set the preference so that walkthrough won't be shown again.
//            	if(ch.isChecked()){
//                    editor.putBoolean("wScreenKey", true);
//                    editor.commit(); 
//            	}
//            	finish();
//            	Intent intent = new Intent();
//            	intent.setClass(wk, MainActivity.class);
//            	startActivity(intent);
//                break;
//            }
        }
    }
    
    private class DownloadFilesTask extends AsyncTask<String, Integer, String> {
       
		@Override
		protected String doInBackground(String... params) {
			ML.log(actname, "doInBackground");
        	String jsonString = params[0];
        	String uri = getResources().getString(R.string.domain)+"fb_user/create";
        	StringBuilder builder = new StringBuilder();
        	HttpClient httpclient = new DefaultHttpClient();
     	    HttpPost httppost = new HttpPost(uri);
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
					JSONObject json = new JSONObject(builder.toString());
					if(json.getString("status").equals("success")){
						ML.log(actname, "Updating device key");
						editor.putString("devicekey", json.getString("device_key"));
						editor.commit();
					}
					
   		    }catch (ClientProtocolException e){
   		      e.printStackTrace();
   		    }catch (IOException e){
   		      e.printStackTrace();
   		    }catch (JSONException e) {
				e.printStackTrace();
			}
     	      ML.log(actname,"Response : "+builder.toString());
			return null;
		}
		
		protected void onPostExecute(String r) {
			// TODO Auto-generated method stub
			
		}
    }
   
    
    //************** Additions for UIHelper
    @Override
	public void onResume() {
	    super.onResume();
	 // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    
	    Session session = Session.getActiveSession();
	   

	    if(session!=null&session.isOpened()&&myPreferences.getBoolean("askedpubperm", false)){
//	    	Toast.makeText(this, "Launching Main", Toast.LENGTH_SHORT).show();
	    	finish();
	    	Intent intent = new Intent();
	    	if(myPreferences.getBoolean("firsttime", true))
	    		intent.setClass(wk, SetupWizard.class);
	    	else
	        	intent.setClass(wk, MainActivity.class);

	    	startActivity(intent);
	    }
	    	
	    	
	    ML.log(actname, "onResume");
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    ML.log(actname, "onActivityResult");
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    ML.log(actname, "onPause");
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    ML.log(actname, "onDestroy");
	    
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    ML.log(actname, "onSaveInstanceState");
	    uiHelper.onSaveInstanceState(outState);
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
	
    
}