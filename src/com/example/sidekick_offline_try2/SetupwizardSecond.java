package com.example.sidekick_offline_try2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupClickListener;
 
public class SetupwizardSecond extends Activity {
 
    TryingExpandable listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
	ListDataSource datasource;

    HashMap<String, List<String>> listDataChild;
    ArrayList<Channels> channels;
    HashMap<String,String> map;
    int curr=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_wizard_second);
        Intent intent = getIntent();
        datasource = new ListDataSource(this,getResources().getString(R.string.domain));
		datasource.createDatabase();
        datasource.open();
        map = (HashMap<String,String>)intent.getSerializableExtra("categoriesMap");
        ArrayList<Integer> ar = (ArrayList<Integer>)intent.getSerializableExtra("channelsListInteger");
        int dth_code = intent.getIntExtra("selectedProvider", 2353);
        channels = datasource.retChannelItems(ar,dth_code);
        ((TextView)findViewById(R.id.setupSecondChoose)).setTypeface(Typeface.createFromAsset(this.getAssets(),"Roboto-Italic.ttf"));
        expListView = (ExpandableListView) findViewById(R.id.mListView);
        Log.e("Sidekick_V4",""+channels.size());
        listAdapter = new TryingExpandable(this,map,channels);
        
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        expListView.setOnGroupClickListener(new OnGroupClickListener(){
			@Override
			public boolean onGroupClick(ExpandableListView par, View v,
					int pos, long id) {
				if(pos==curr){
					if(!expListView.collapseGroup(pos))
						expListView.expandGroup(pos);
					return true;
				}
				expListView.expandGroup(pos);
				if(curr!=-1)
					expListView.collapseGroup(curr);
				curr = pos;
				return true;
			}
		});
    }
    
    public void makeChannels(View v){
    	for(String item : TryingExpandable.selectedCategoryNames)
    		Log.d("Sidekick_V5","Selected Category : "+item);
    	for(Integer item : TryingExpandable.selectedChannelIds)
    		Log.d("Sidekick_V5","Selected Channels : "+item);
    	
    	ArrayList<Category> finalCategories = datasource.getCategories(0);
    	
    	
    	for(Category category : finalCategories){
    		if(category.retId()==1)
    			continue;
    		if(TryingExpandable.selectedCategoryNames.contains(category.retCategoryName())){
    			category.setVisibility(1);
    		}
    		else{
    			category.setVisibility(0);
    		}
    		
    		
    		for(int i=0;i<category.channel_list.size();i++){
    			if(!TryingExpandable.selectedChannelIds.contains(category.channel_list.get(i)))
    				category.channel_list.set(i, -1);
    		}
    	}
    	
    	datasource.updateOrder(finalCategories);
    	datasource.close();
    	finish();
    	Intent intent = new Intent(this,MainActivity.class);
    	startActivity(intent);
    	
    }
}