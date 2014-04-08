package com.example.sidekick_offline_try2;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
 

public class Fragment2 extends SherlockFragment {

	ArrayList<String> chanListContent;
	RemRecWatchAdapter chanAdapter;
	ArrayList<ShowsList> showNameDesc;
	ProgressBar percBar;
	ListView recomList;
	SharedPreferences myPreferences;
	SharedPreferences.Editor editor;
	LinearLayout profileBuildUp;
    @Override
    public SherlockFragmentActivity getSherlockActivity() {
        return super.getSherlockActivity();
    }
 
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Get the view from fragmenttab2.xml
    	ML.log("Frag", "CREATE VIEW CALLED");
        View view = inflater.inflate(R.layout.fragmenttab2, container, false);
        recomList = (ListView) view.findViewById(R.id.recolv1);
        profileBuildUp = (LinearLayout) view.findViewById(R.id.profilebuildup);
        percBar = (ProgressBar) view.findViewById(R.id.profilePercBar);
        TextView noRecom = (TextView) view.findViewById(R.id.noRecom);
        percBar.setMax(25);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
	    editor = myPreferences.edit(); 
	    Integer count = myPreferences.getInt("profile_count", 0);
	    TextView percProfile = (TextView) view.findViewById(R.id.profilePerc);
	    percProfile.setText(String.valueOf(count*4)+"%");
	    percBar.setProgress(count);
	    TextView hide = (TextView) view.findViewById(R.id.hideButton);
	    hide.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"Roboto-Italic.ttf"));

	    hide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				profileBuildUp.setVisibility(View.GONE);
		    	editor.putBoolean("showprofile", false);
		    	editor.commit();
			}
		});
	    if(myPreferences.getBoolean("showprofile", true))
	    	profileBuildUp.setVisibility(View.VISIBLE);
	    
	    
	    showNameDesc = MainActivity.datasource.retListItemsForRecom();
	    if(showNameDesc.size()==0){
	    	recomList.setVisibility(View.GONE);
	    	noRecom.setVisibility(View.VISIBLE);
	    }else{
	    	recomList.setVisibility(View.VISIBLE);
	    	noRecom.setVisibility(View.GONE);
	    }
        Comparator<ShowsList> comp = new Comparator<ShowsList>(){
        	public int compare(ShowsList ob1,ShowsList ob2){
        		Date c1= ob1.getStartTime();
        		Date c2 = ob2.getStartTime();
        		return (int)(c1.getTime()-c2.getTime());
        	}
        };
        Collections.sort(showNameDesc, comp);
        
        chanAdapter.reload(showNameDesc);
        recomList.setAdapter(chanAdapter);
        chanAdapter.notifyDataSetChanged();
        return view;
    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	ML.log("Frag", "oncreate fragment CALLED");
		showNameDesc = new ArrayList<ShowsList>();
		chanListContent = new ArrayList<String>();
	    showNameDesc = MainActivity.datasource.retListItemsForRecom();
        chanAdapter = new RemRecWatchAdapter(getSherlockActivity(),showNameDesc);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
 
}