package com.example.sidekick_offline_try2;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
 
public class Fragment1 extends SherlockFragment {
 
	ListView remlv;
	ArrayList<String> chanListContent;
	RemRecWatchAdapter chanAdapter;
	ArrayList<ShowsList> showNameDesc;
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
        // Get the view from fragmenttab1.xml
    	View view = inflater.inflate(R.layout.fragmenttab1, container, false);
        ML.log("Frag", "CREATE VIEW CALLED");
        remlv = (ListView) view.findViewById(R.id.remlv);
        TextView noRem = (TextView) view.findViewById(R.id.noReminder);
        showNameDesc = MainActivity.datasource.retListItemsForReminders();
        if(showNameDesc.size()==0){
        	remlv.setVisibility(View.GONE);
	    	noRem.setVisibility(View.VISIBLE);
        }else{
        	remlv.setVisibility(View.VISIBLE);
	    	noRem.setVisibility(View.GONE);
        }
        if(showNameDesc!=null){
            Comparator<ShowsList> comp = new Comparator<ShowsList>(){
            	public int compare(ShowsList ob1,ShowsList ob2){
            		Date c1= ob1.getStartTime();
            		Date c2 = ob2.getStartTime();
            		return (int)(c1.getTime()-c2.getTime());
            	}
            };
            Collections.sort(showNameDesc, comp);
            
            chanAdapter.reload(showNameDesc);
        }
        remlv.setAdapter(chanAdapter);
        return view;
    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	ML.log("Frag", "oncreate fragment CALLED");
		showNameDesc = new ArrayList<ShowsList>();
		chanListContent = new ArrayList<String>();
        showNameDesc = MainActivity.datasource.retListItemsForReminders();

        chanAdapter = new RemRecWatchAdapter(getSherlockActivity(),showNameDesc);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
 
}