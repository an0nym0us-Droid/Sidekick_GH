package com.example.sidekick_offline_try2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

//package com.example.sidekick_offline_try2;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.app.ListActivity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.ToggleButton;
//
//import com.mobeta.android.dslv.DragSortController;
//import com.mobeta.android.dslv.DragSortListView;
//
//public class WarpDSLV extends ListActivity {
//
//    private ArrayAdapter<String> adapter;
//    private String[] array;
//    private MAdapter mMyAdapter;
//    private ArrayList<String> list;
//    public ArrayList<Category> tempCategories;
//    
//    private DragSortListView.DropListener onDrop =
//        new DragSortListView.DropListener() {
//            @Override
//            public void drop(int from, int to) {
//            	 
//            	Log.e("OLDcatSize", to+""+from);
//            	adapter.notifyDataSetChanged();
//            	String item=adapter.getItem(from);
//	                // move the button
//	                if(from < tempCategories.size()) {
//                       Log.e("OLDcatSize", String.valueOf(tempCategories.size()));
//
//	                    Category itemRemoved = tempCategories.remove(from+1);
//	                    adapter.remove(item);
//                       Log.e("itemRemoved", String.valueOf(itemRemoved.retCategoryName()));
//
//	                   Log.e("from", String.valueOf(from));
//	                  Log.e("to", String.valueOf(to));
//	                    if(to <= tempCategories.size()) {
//	                        tempCategories.add(to+1, itemRemoved);
//	                        adapter.insert(item, to);
//	                        // save our item's orders
//	                        assignorders();
//	                        
//	                        // tell our adapter/listview to reload
//	                        list= new ArrayList<String>();
//	                        for(Category tem : tempCategories)
//	                      	list.add(tem.retCategoryName());
//	                        adapter.notifyDataSetChanged();
//	                        mMyAdapter.reloadButtons();
// 	                        mMyAdapter.notifyDataSetChanged();
//	                        
//	                    }
//	                }
//                  }
//              };
//                
//                
//            
//        
//        public void assignorders(){
//		    	int i=1;
//		    	for(Category item :tempCategories){
//		    		item.setOrder(i);
//		    		i++;
//		    	}
//		    	
//		    }
//        public void done(View v){
//    		MainActivity.datasource.updateOrder(tempCategories);
//    		finish();
//    	}
//        public void cancel(View v){
//    		finish();
//    	}
//        public void addNewList(View v){
//		    	
//	    	 Intent  intent = new Intent(this,EditChannelActivity.class);
//			 intent.putExtra("catName", "a" );
//			 intent.putExtra("newlyCreated", true);
//			 intent.putExtra("catPos", tempCategories.size()+1 );
//		     startActivity(intent);
//		     
//	    }

//    private DragSortListView.RemoveListener onRemove = 
//        new DragSortListView.RemoveListener() {
//            @Override
//            public void remove(int which) {
//                adapter.remove(adapter.getItem(which));
//            }
//        };
//
//   
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.warp_main);
//        tempCategories = MainActivity.categories;
//        final DragSortListView lv = (DragSortListView) getListView(); 
//        lv.setDropListener(onDrop);
//        lv.setRemoveListener(onRemove);
//        list = new ArrayList<String>();
//        for(Category item : tempCategories)
//        {
//        	if(item.retVisibility()==1&&item.retId()!=1)
//        list.add(item.retCategoryName());
//        }
//        mMyAdapter = new MAdapter(this,tempCategories);
//        //list = new ArrayList<String>(Arrays.asList(array));
//        
//        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_right, R.id.text, list);
//        setListAdapter(mMyAdapter);
//     
//    }
//    	
//    
//   public void editButtonClicked(View v)
//    {
//           Log.i("clicked","image"+v.getId()); 	
//    }
//    }
//
//
//********************* NEW BEGINNING ***********************************//


public class FinEditCat extends SherlockActivity{
	
	DragSortListView listView;
	MAdapter adapter;
	ActionBar actionBar;
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener(){
	    @Override
	    public void drop(int from, int to){
	        if (from != to){
	        	
	            Category temp = MainActivity.categories.remove(from+1);
	            MainActivity.categories.add(to+1, temp);
	            
	            adapter.notifyDataSetChanged();
	            assignorders();
	            adapter.show();
//	            StringBuilder build = new StringBuilder();
//	            for(Category item : MainActivity.categories)
//	            	build.append(item.retCategoryName()+"\n");
//	            Toast.makeText(FinEditCat.this, build.toString(), Toast.LENGTH_SHORT).show();
	        }
	    }
	};

	public void assignorders(){
	    	int i=1;
	    	for(Category item : MainActivity.categories){
	    		item.setOrder(i);
	    		i++;
	    	}
	}
	
	public void done(View v){
		MainActivity.datasource.updateOrder(MainActivity.categories);
		finish();
	}
	
	public void backAction(View v){
		MainActivity.datasource.updateOrder(MainActivity.categories);
		finish();
	}
	
	public void onBackPressed() {
		Log.e("Sidekick_V3","BackPressed Before currCat: "+MainActivity.currCatSelected);
		MainActivity.datasource.updateOrder(MainActivity.categories);
		Log.e("Sidekick_V3","BackPressed After currCat: "+MainActivity.currCatSelected);

		finish();
    }
	
	public void addCategory(View v) {
		 Intent  intent = new Intent(FinEditCat.this,EditChannelActivity.class);
		 intent.putExtra("catName", "New Category");
		 intent.putExtra("catPos", MainActivity.categories.size());
		 intent.putExtra("isNewlyCreated", true);
	     this.startActivity(intent);
    }
	
	public void onRestart(){
		super.onRestart();
//		Toast.makeText(this, "restarted", Toast.LENGTH_SHORT).show();
		adapter.notifyDataSetChanged();
	}
	
	public void onStart(){
		super.onStart();
//		Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
		adapter.notifyDataSetChanged();
	}
	
	public void onResume(){
		super.onResume();
//		Toast.makeText(this, "resumed", Toast.LENGTH_SHORT).show();
		adapter.notifyDataSetChanged();
	}
	
	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener(){
	    @Override
	    public void remove(int which){
	    	MainActivity.categories.remove(which);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.fin_editcat);
	  listView = (DragSortListView) findViewById(R.id.dslvid);
	  Log.e("Sidekick_V4","While entering currCat: "+MainActivity.currCatSelected);
	    adapter = new MAdapter(this);
	    listView.setAdapter(adapter);
	    listView.setDropListener(onDrop);
	    listView.setRemoveListener(onRemove);
	    
	    
	    actionBar = getSupportActionBar();
	    actionBar.setTitle("  Edit Categories");
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    
	    DragSortController controller = new DragSortController(listView);

	    controller.setDragHandleId(R.id.finhandle);
	    controller.setRemoveEnabled(false);
	    controller.setSortEnabled(true);
	    controller.setDragInitMode(1);
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//			    Toast.makeText(FinEditCat.this, "HEY IT CLIKED", Toast.LENGTH_SHORT).show();
			}
		});
	    
	    listView.setFloatViewManager(controller);
	    listView.setOnTouchListener(controller);
	    listView.setDragEnabled(true);
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				MainActivity.datasource.updateOrder(MainActivity.categories);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	
}
