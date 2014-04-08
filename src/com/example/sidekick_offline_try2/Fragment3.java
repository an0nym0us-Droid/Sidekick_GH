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
 
public class Fragment3 extends SherlockFragment {
 
	ArrayList<String> chanListContent;
	RemRecWatchAdapter chanAdapter;
	ArrayList<ShowsList> showNameDesc;
	ListView watchList;
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
        // Get the view from fragmenttab3.xml
        View view = inflater.inflate(R.layout.fragmenttab3, container, false);
        watchList = (ListView) view.findViewById(R.id.watchlv);
        TextView noWatch = (TextView) view.findViewById(R.id.noWatch);
        showNameDesc = MainActivity.datasource.retListItemsForWatchlist();
        if(showNameDesc.size()==0){
        	watchList.setVisibility(View.GONE);
	    	noWatch.setVisibility(View.VISIBLE);
        }else{
        	watchList.setVisibility(View.VISIBLE);
	    	noWatch.setVisibility(View.GONE);
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
        watchList.setAdapter(chanAdapter);
        chanAdapter.notifyDataSetChanged();

        return view;
    }
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	ML.log("Frag", "oncreate fragment CALLED");
		showNameDesc = new ArrayList<ShowsList>();
		chanListContent = new ArrayList<String>();
		showNameDesc = MainActivity.datasource.retListItemsForWatchlist();
        chanAdapter = new RemRecWatchAdapter(getSherlockActivity(),showNameDesc);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
 
}