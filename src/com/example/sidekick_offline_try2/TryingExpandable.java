package com.example.sidekick_offline_try2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class TryingExpandable extends BaseExpandableListAdapter {
 
    private Context _context;
	private LayoutInflater mInflater;
	ArrayList<String> checkList;
	ArrayList<Channels> totalChannels;
	public static ArrayList<Integer> selectedChannelIds;

    public static ArrayList<String> totalCategoryNames;
    public static ArrayList<String> selectedCategoryNames;// header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
 
    HashMap<String,String> map;
    
    public TryingExpandable(Context context,HashMap<String,String> map,ArrayList<Channels> channels) {
        this._context = context;
        mInflater = LayoutInflater.from(this._context);
        checkList = new ArrayList<String>();
        this.totalChannels = channels;
        selectedChannelIds = new ArrayList<Integer>();
        for(Channels item : totalChannels)
        	selectedChannelIds.add(item.retId());
//        this._listDataHeader = listDataHeader;
//        this._listDataChild = listChildData;
        totalCategoryNames = new ArrayList<String>();
        selectedCategoryNames = new ArrayList<String>();
        this.map = map;
        for(Map.Entry<String,String> entry : map.entrySet()){
        	totalCategoryNames.add(entry.getKey());
        	selectedCategoryNames.add(entry.getKey());
        }
        
        
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return null;
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View v;
    	if(convertView == null) {
            v = infalInflater.inflate(R.layout.setup_second_item, null);
        } else {
            v = convertView;
        }
    	
        GridView gridView = (GridView) v.findViewById(R.id.setupGrid);
        
        final ArrayList<Channels> particularChannels = new ArrayList<Channels>();
        final String cat = totalCategoryNames.get(groupPosition);
        for(Channels ch : totalChannels){
        	if(ch.retCategoryName().equals(cat)){
        		particularChannels.add(ch);
        	}
        }
        
        Log.e("Sidekick_V4","Size is : "+particularChannels.size()); 
        
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, numbers);
        SetupwizardGridAdapter adapter = new SetupwizardGridAdapter(_context, particularChannels);
		gridView.setAdapter(adapter);
 
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
			   
				LinearLayout l = (LinearLayout) v.findViewById(R.id.setupGridOuterboundary);
				ImageView im = (ImageView) 	v.findViewById(R.id.setup_grid_check);
				if(im.getVisibility()==View.VISIBLE){
					im.setVisibility(View.GONE);
					selectedChannelIds.remove((Integer)particularChannels.get(position).retId());
					int flag =0;
					for(Channels item : particularChannels){
						if(selectedChannelIds.contains(item.retId())){
							flag=1;
							break;
						}
					}
					if(flag==0){
						selectedCategoryNames.remove(cat);
					}
					l.setBackgroundColor(0x00000000);
				}else{
					im.setVisibility(View.VISIBLE);
					if(!selectedCategoryNames.contains(cat))
						selectedCategoryNames.add(cat);
					selectedChannelIds.add(particularChannels.get(position).retId());
					l.setBackgroundColor(0xAAED9A00);
				}
				notifyDataSetChanged();
			}
		});
		
		final int spacingDp = 10;
	    final int colWidthDp = 50;
	    final int rowHeightDp = 20;

	    // convert the dp values to pixels
	    final float COL_WIDTH = _context.getResources().getDisplayMetrics().density * colWidthDp;
	    final float ROW_HEIGHT = _context.getResources().getDisplayMetrics().density * rowHeightDp;
	    final float SPACING = _context.getResources().getDisplayMetrics().density * spacingDp;;

	    // calculate the column and row counts based on your display
	    final int colCount = 0;
	    final int rowCount = (int)Math.ceil((particularChannels.size() + 0d) / colCount);

	    // calculate the height for the current grid
	    final int GRID_HEIGHT = Math.round(rowCount * (ROW_HEIGHT + SPACING));

	    // set the height of the current grid
	    gridView.getLayoutParams().height = GRID_HEIGHT;
 
		    
       
        return v;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.totalCategoryNames.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.totalCategoryNames.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	View v;
        if(convertView == null) {
            v = mInflater.inflate(R.layout.setup_second_group, null);
        } else {
            v = convertView;
        }
 
        final String headerTitle = (String) getGroup(groupPosition);
        final CheckBox ch = (CheckBox) v.findViewById(R.id.setCh);
        
        if(selectedCategoryNames.contains(headerTitle))
        	ch.setChecked(true);
        else
        	ch.setChecked(false);
        
        TextView lblListHeader = (TextView) v
                .findViewById(R.id.lblListHeader);
        
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        
       
        		
        ch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!ch.isChecked()){
					selectedCategoryNames.remove(headerTitle);
					for(Channels item : totalChannels){
						if(item.retCategoryName().equals(headerTitle)){
							selectedChannelIds.remove((Integer)item.retId());
						}
					}
				}
				else{
					selectedCategoryNames.add(headerTitle);
					for(Channels item : totalChannels){
						if(item.retCategoryName().equals(headerTitle)){
							if(!selectedChannelIds.contains(item.retId()))
							selectedChannelIds.add(item.retId());
						}
					}
				}
				notifyDataSetChanged();
			}
		});
        
       
 
        return v;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}