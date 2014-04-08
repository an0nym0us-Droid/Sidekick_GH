package com.example.sidekick_offline_try2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profiles.Profiles;

public class SetupWizard extends Activity {

	TextView welcome;
	TextView message;
	TextView providerTV;
	TextView basePackTV;
	TextView addonsTV;
	ListView addonList;
	AddonAdapter addonAdapter;
	Button done;
	Spinner providers;
	Spinner basePackages;
	ListDataSource datasource;
	ArrayList<Integer> dth_codes;
	ArrayList<String> totalAddons;
	ArrayList<String> selectedAddons;
	boolean firstTime;
	String selectedProvider;
	String selectedBasePackage;
	String loadingDataDialogString = "Creating your profile";
	Integer selectedProviderInteger;
	Integer selectedProfile;
	Profiles mProfile;
	SharedPreferences myPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences remotePref;
    SharedPreferences.Editor remoteEditor;
	ProgressDialog loadingDataDialog;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setup_wizard);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		dth_codes = new ArrayList<Integer>();
		totalAddons = new ArrayList<String>();
		selectedAddons = new ArrayList<String>();
		
		remotePref = getSharedPreferences("remotePref", 0);
		remoteEditor = remotePref.edit();
	    done = (Button) findViewById(R.id.done);
	    providers = (Spinner) findViewById(R.id.providerSpinner);
	    basePackages = (Spinner) findViewById(R.id.basePackageSpinner);
	    welcome = (TextView) findViewById(R.id.welcome);
	    message = (TextView) findViewById(R.id.message);
	    providerTV = (TextView) findViewById(R.id.provider);
	    basePackTV = (TextView) findViewById(R.id.basepackage);
	    addonsTV = (TextView) findViewById(R.id.addons);

	    addonList = (ListView) findViewById(R.id.addonList);
	    
	    
	    addonList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if(AddonAdapter.selectedAddons.contains(AddonAdapter.totalAddons.get(pos))){
					AddonAdapter.selectedAddons.remove(AddonAdapter.totalAddons.get(pos));
				}
				else{
					AddonAdapter.selectedAddons.add(AddonAdapter.totalAddons.get(pos));
				}
				addonAdapter.notifyDataSetChanged();
			}
		});
	    
	    welcome.setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Thin.ttf"));
	    message.setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Regular.ttf"));
	    providerTV.setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Regular.ttf"));
	    basePackTV.setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Regular.ttf"));
	    addonsTV.setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Regular.ttf"));
//	    addonitem.setTypeface(face);
	    
	    basePackages.setEnabled(false);
	    datasource = new ListDataSource(this,getResources().getString(R.string.domain));
		datasource.createDatabase();
        datasource.open();
        
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = myPreferences.edit();
        firstTime = myPreferences.getBoolean("firsttime", true);
        selectedProfile = myPreferences.getInt("selected_profile", 1);
        
        if(!firstTime){
        	mProfile= datasource.getProfile(selectedProfile);
        	selectedAddons = mProfile.getAddonsString();
        	loadingDataDialogString = "Updating your profile";
        }
	    fill_providers();
	    Log.e("Sidekick_V3","selectedProvider: "+selectedProvider);
	    Log.e("setup",selectedProvider+"|"+firstTime);
	    totalAddons = datasource.retAddonPackageOptions(selectedProvider);
	    addonAdapter = new AddonAdapter(SetupWizard.this,totalAddons,selectedAddons);
	    addonList.setAdapter(addonAdapter);
	}
	
	public void fill_providers(){
		String[] providerData = datasource.retDTHOptions(dth_codes);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,providerData);
        adapter.setDropDownViewResource(R.layout.spinner_actionbar_dropdownitem);
		providers.setAdapter(adapter);
		providers.setOnItemSelectedListener(providerOnItemSelectedListener);
		if(firstTime){
			selectedProvider = providerData[0];
			selectedProviderInteger = dth_codes.get(0);
		}
		else{
			selectedProvider = mProfile.getProviderName();
			for(int i=0;i<providerData.length;i++){
				if(providerData[i].equals(selectedProvider)){
					Log.e("Sidekick_V3","val of i : "+i);
					providers.setSelection(i);
					selectedProviderInteger = dth_codes.get(i);
					break;
				}
			}
		}
	}
	
	OnItemSelectedListener providerOnItemSelectedListener = new OnItemSelectedListener() {
		protected int init=1;
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView tv = (TextView)arg1;
			Log.e("Setup","init:"+init);
			if(init==1&&!firstTime){
				init=0;
				fill_basePackages();
				basePackages.setEnabled(true);
				return;
			}
			selectedProvider = tv.getText().toString();
			selectedProviderInteger = dth_codes.get(arg2);
			Log.e("Setup","init:"+init+"|"+firstTime+"|"+selectedProvider);
			totalAddons.clear();
			totalAddons = datasource.retAddonPackageOptions(selectedProvider);
			selectedAddons.clear();
			addonAdapter.refreshTotalAddons(totalAddons);
			addonAdapter.notifyDataSetChanged();
			basePackages.setEnabled(true);
			fill_basePackages();
			remoteEditor.putInt("dth_code", dth_codes.get(arg2));
			remoteEditor.commit();
			
			
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
	public void fill_basePackages(){
		ArrayList<String> basepackageData = datasource.retBasePackageOptions(selectedProvider);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,basepackageData);
		adapter.setDropDownViewResource(R.layout.spinner_actionbar_dropdownitem);
		basePackages.setAdapter(adapter);
		basePackages.setOnItemSelectedListener(baseOnItemSelectedListener);
        if(firstTime){
        	selectedBasePackage = basepackageData.get(0);
        }
        else{
        	selectedBasePackage = mProfile.getBase();
        	for(int i=0;i<basepackageData.size();i++){
        		if(basepackageData.get(i).equals(selectedBasePackage)){
        			basePackages.setSelection(i);
        			break;
        		}
        	}
        }
	}
	
	OnItemSelectedListener baseOnItemSelectedListener = new OnItemSelectedListener() {
		protected int init = 1;
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView tv = (TextView)arg1;
			if(init==1&&!firstTime){
				Toast.makeText(SetupWizard.this, "Initial Call base:", Toast.LENGTH_SHORT).show();
				init=0;
				return;
			}
			selectedBasePackage= tv.getText().toString();
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
//	public void addons(){
//		
//		ArrayList<String> addonData = datasource.retAddonPackageOptions(selectedProvider);
//		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
//                this);
//        builderSingle.setIcon(R.drawable.ic_launcher);
//        builderSingle.setTitle("Select Addon");
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this,android.R.layout.select_dialog_singlechoice,addonData);
//        builderSingle.setNegativeButton("cancel",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        builderSingle.setAdapter(arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    	if(!addons.contains(arrayAdapter.getItem(which))){
//                    		addons.add(arrayAdapter.getItem(which));
//                            addonList.setText(addonList.getText()+"\n"+arrayAdapter.getItem(which));
//                    	}
//                         
////                         addonAdapter.add(arrayAdapter.getItem(which));
////                         addonAdapter.notifyDataSetChanged();
//                         dialog.dismiss();
//                    }
//                });
//        builderSingle.show();
//	}
	
	public void deleteAddon(View v){
		
	}
	
	public void makeChannels(View v){
		new LoadDBData().execute("Placeholder");
	}
	
	
	private class LoadDBData extends AsyncTask<String,String,String>{

		HashMap<String,String> map;
		ArrayList<Channels> finalChannels;
		ArrayList<Integer> channelList;
		@Override
		 protected void onPreExecute() {
			 loadingDataDialog = ProgressDialog.show(SetupWizard.this, "Please wait...", loadingDataDialogString);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			for(String item:AddonAdapter.selectedAddons){
				Log.e("ADDONS :",item);
			}
			
			channelList = datasource.retChannelList(selectedProvider,selectedBasePackage, AddonAdapter.selectedAddons);
			finalChannels = datasource.retChannelItems(channelList,selectedProviderInteger);
			Log.e("CHANNELITEMS :",String.valueOf(finalChannels.size()));
			StringBuilder allChannels = new StringBuilder();
			allChannels.append("[");
			String delimiter = "";
			map = new HashMap<String,String>();
			for(Channels item : finalChannels){
				allChannels.append(delimiter);
				allChannels.append(String.valueOf(item.retId()));
				delimiter=", ";
				String cat = item.retCategoryName();
				String id = String.valueOf(item.retId());
				if(map.get(cat) == null)
					map.put(cat,"["+id);
				else
					map.put(cat,map.get(cat)+", "+id);
			}
			allChannels.append("]");
			for(Map.Entry<String,String> entry : map.entrySet()){
				map.put(entry.getKey(), entry.getValue()+"]");
			}
			for(Map.Entry<String,String> entry : map.entrySet()){
				Log.e("DECEMBER",entry.getKey()+":"+entry.getValue());
			}
			datasource.fillUserCategories(allChannels.toString(),map);
			
			
			if(firstTime)
				datasource.createProfile("AUTO_GENERATED_PROGRAM",selectedProviderInteger,selectedBasePackage,AddonAdapter.selectedAddons);
			else
				datasource.updateProfile(selectedProfile,"AUTO_GENERATED_PROGRAM",selectedProviderInteger,selectedBasePackage,AddonAdapter.selectedAddons);

		
			return null;
		}
	
		 @Override
		 protected void onPostExecute(String data) {
			 loadingDataDialog.dismiss();
			 datasource.close();
			 SetupWizard.this.finish();
			 Intent intent = new Intent();
			 if(firstTime)
				 intent.setClass(SetupWizard.this, SetupwizardSecond.class);
			 else
				 intent.setClass(SetupWizard.this, MainActivity.class);

		     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     intent.putExtra("categoriesMap", map);
		     intent.putExtra("channelsListInteger", channelList);
		     intent.putExtra("selectedProvider", selectedProviderInteger);
		     startActivity(intent);
		 }
		
	}
	
}
