package com.example.sidekick_offline_try2;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

//
//
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.GridView;
// 
//public class EditChannelActivity extends Activity {
// 
//	GridView gridView;
//	Category userCategory;
//	Category catSelected;
//	
//	EditText ed;
//	Boolean newlyCreated;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
// 
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.channeledit);
//		Intent intent = getIntent();
//		whichOneIsClicked = new ArrayList<Integer>();
//		final String name = intent.getStringExtra("catName");
//		final int pos = intent.getIntExtra("catPos",0);
//		newlyCreated = intent.getBooleanExtra("newlyCreated", true);
//		ed = (EditText) findViewById(R.id.chanEditTopName);
//		ed.setEnabled(false);
//		if(newlyCreated){
//			ed.setText("New Category");
//			ed.selectAll();
//			userCategory = new Category();
//			userCategory.setId(pos);
//			userCategory.setOrder(pos);    
//			userCategory.setVisibility(1);
//		}
//		else{
//			userCategory = MainActivity.categories.get(pos);
//			ed.setText(userCategory.retCategoryName());
//		}
//		
//		//To see if it is one of the default cateogries or a user category. 
//		// Null in catSelected indicates it is a user category.
//		for(Category item : MainActivity.defaultcategories){
//			if(item.retCategoryName().equals(name)){
//				catSelected = item;
//			}
//		}
//		
//		
//		// If it is a user category, we display the contents of all categories for user to choose from.
//		// TODO : Display contents from 1st category i.e. 'All Channels' as it already contains all channels.
//		if(catSelected == null){
//			ed.setEnabled(true);
//			ed.selectAll();
//			Log.i("NEW CATEGORY", "NEW CATEGORY SELECTED");
//			catSelected = new Category();
//			for(Category item : MainActivity.defaultcategories){
//				Log.v("Adding Category Channels of ", item.retCategoryName());
//				catSelected.channel_list.addAll(item.returnAsIntArray());
//				Log.v("Now new category contains : ", catSelected.returnAsIntArray().toString());
//			}
//		}
//		else
//			ed.setFocusable(false);
//		
//		// whichOneIsClicked indicates which channels are selected. Initially we set 1
//		// for the channels that are present in uesrCategory. 
//		for(Integer item : catSelected.returnAsIntArray()){
//			if(userCategory.returnAsIntArray().size()==0){
//				whichOneIsClicked.add(0);
//			}
//			else if(userCategory.returnAsIntArray().contains(item))
//				whichOneIsClicked.add(1);
//			else
//				whichOneIsClicked.add(0);
//		}
//	
//		gridView = (GridView) findViewById(R.id.gridView1);
//		gridView.setAdapter(new GridAdapter(this, catSelected.returnAsIntArray(), pos));
//		// When grid item is clicked, we toggle the checkbox. If it is checked, we set its 
//		// corresponding position in whichOneIsClicked to 1
//		gridView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
//				Log.e("lengthof WOC ",String.valueOf(whichOneIsClicked.size()));
//				
//				CheckBox ch = (CheckBox) v.findViewById(R.id.chanvis);
//				ch.toggle();
//				if(ch.isChecked())
//					whichOneIsClicked.set(position,1);
//				else
//					whichOneIsClicked.set(position,0);
//			}
//		});
// 
//	}
//	
//	/**
//	 * OnClick function for 'BACK' button in the layout (Not the android back button.)
//	 * Here we clear the channels present in the userCategory and fill them with the ones that are checked.
//	 * The ones that are checked, have 1 set in corresponding position in whichOneIsChecked array.
//	 * If the category is newly created, we add it to 'categories' variable of MainActivity as well
//	 * as to the database.
// 	 * @param v
//	 */
//	public void bak(View v){
//		Log.e("wOC: ",whichOneIsClicked.toString());
//		if(!newlyCreated)
//		userCategory.channel_list.clear();
//		for(int k=0;k<catSelected.channel_list.size();k++){
//			if(whichOneIsClicked.get(k)==1)
//				userCategory.channel_list.add(catSelected.channel_list.get(k));
//		}
//		
//		if(newlyCreated){
//			userCategory.setCategoryName(ed.getText().toString());
//			MainActivity.categories.add(userCategory);
//			MainActivity.datasource.addNewCategory(userCategory);
//			Log.i("Added this :", userCategory.retCategoryName()+"|"+userCategory.returnAsIntArray().toString());
//		}
//		finish();
//	}
// 
//}

public class EditChannelActivity extends SherlockActivity{
	
	public static ArrayList<Integer> whichOneIsClicked;
	ActionBar actionBar;
    EditText editedName;
    Integer catPosition;
    String catName;
	public boolean hide;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channeledit);
		Intent intent = getIntent();
		catName = intent.getStringExtra("catName");
		final Integer catPos = intent.getIntExtra("catPos", 1);
		catPosition = catPos;
		final Boolean isNewlyCreated = intent.getBooleanExtra("isNewlyCreated", false);
	    ArrayList<Integer> totalChanIds = new ArrayList<Integer>();
		ArrayList<Integer> selectedChanIds = new ArrayList<Integer>();
		editedName = new EditText(this);
		final Category newCat = new Category();
		
		actionBar = getSupportActionBar();
		actionBar.setTitle("\t"+catName);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		editedName.setText(catName);
		
		
		Category tempDefaultCategory;
		
		if(!isNewlyCreated){
			if((tempDefaultCategory = isItUserCat(catName))==null)
				totalChanIds = MainActivity.defaultcategories.get(0).returnTotalAsIntArray();
			else{
				totalChanIds = tempDefaultCategory.returnTotalAsIntArray();
				hide=true;
				invalidateOptionsMenu();
			}
			
			selectedChanIds = MainActivity.categories.get(catPos).returnTotalAsIntArray();
		}
		else{
			
			totalChanIds = MainActivity.defaultcategories.get(0).returnTotalAsIntArray();
			
			newCat.setCategoryName(catName);
			newCat.setOrder(catPos+1);
			newCat.setVisibility(1);
			newCat.setId(catPos+1);
			for(int i=0;i<totalChanIds.size();i++)
				newCat.channel_list.add(-1);
			selectedChanIds = newCat.returnTotalAsIntArray();
			MainActivity.categories.add(newCat);
			MainActivity.datasource.addNewCategory(newCat);
		}
		
		
		ListView channelList = (ListView) findViewById(R.id.selectChannels);
		final GridAdapter channelListAdapter = new GridAdapter(this,totalChanIds,selectedChanIds);
		channelList.setAdapter(channelListAdapter);
		channelList.setFocusable(true);
		channelList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
					if(GridAdapter.selectedChanIds.contains(GridAdapter.totalChanIds.get(pos))){
						Log.e("Before ChanList",GridAdapter.selectedChanIds.toString());
						GridAdapter.selectedChanIds.set(pos,-1);
						Log.e("After ChanList",GridAdapter.selectedChanIds.toString());
					}
					else{
						Log.e("Before ChanList",GridAdapter.selectedChanIds.toString());
						GridAdapter.selectedChanIds.set(pos,GridAdapter.totalChanIds.get(pos));
						Log.e("After ChanList",GridAdapter.selectedChanIds.toString());
					}
					channelListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void backAction(View v){
		MainActivity.datasource.updateOrder(MainActivity.categories);
		finish();
	}
	
	public void backPressed(){
		if(GridAdapter.selectedChanIds.isEmpty())
			Toast.makeText(this, "0 channels added..", Toast.LENGTH_SHORT).show();
		MainActivity.datasource.updateOrder(MainActivity.categories);
		finish();
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				if(GridAdapter.selectedChanIds.isEmpty())
					Toast.makeText(this, "0 channels added..", Toast.LENGTH_SHORT).show();
				MainActivity.datasource.updateOrder(MainActivity.categories);
				finish();
				return true;
			case R.id.action_editname:
				AlertDialog.Builder alert = new AlertDialog.Builder(EditChannelActivity.this);
				alert.setTitle("Edit Category Name");
				editedName = new EditText(this);
				editedName.setText(catName);
				alert.setView(editedName);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						actionBar.setTitle("\t"+editedName.getText().toString());
						catName = editedName.getText().toString();
						MainActivity.categories.get(catPosition).setCategoryName(editedName.getText().toString());
					  }
					});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	
				    }
				});
					alert.show();
				return true;	
			case R.id.action_deletecat:
				AlertDialog.Builder al = new AlertDialog.Builder(EditChannelActivity.this);
				al.setMessage("Are you sure you want to delete ?");
				al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						Toast.makeText(EditChannelActivity.this, "catPos was : "+catPosition, Toast.LENGTH_SHORT).show();
						if(MainActivity.categories.get(catPosition).retId()==MainActivity.currCatIdSelected){
//							Toast.makeText(EditChannelActivity.this, "Selectd waali deleted: "+catPosition, Toast.LENGTH_SHORT).show();
							for(int i=catPosition-1;i>=0;i--){
								if(MainActivity.categories.get(i).retVisibility()==1){
									MainActivity.currCatSelected = i;
									break;
								}
							}
							MainActivity.currCatIdSelected = MainActivity.categories.get(MainActivity.currCatSelected).retId();
							MainActivity.deleted = true;
							Toast.makeText(EditChannelActivity.this, "Now currCatSelected is : "+MainActivity.currCatSelected, Toast.LENGTH_SHORT).show();
						}
						
						
						
						MainActivity.datasource.deleteCategory(MainActivity.categories.get(catPosition));
						MainActivity.categories.remove(MainActivity.categories.get(catPosition));
						new FinEditCat().assignorders();
						
						finish();		
						
//						catName = editedName.getText().toString();
//						MainActivity.categories.get(catPosition).setCategoryName(editedName.getText().toString());
					  }
				  });

				al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	
				    }
				});
				al.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		if(hide){
			for (int i = 0; i < menu.size(); i++)
	            menu.getItem(i).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	public Category isItUserCat(String catName){
		for(Category item : MainActivity.defaultcategories){
			if(item.retCategoryName().equals(catName))
				return item;
		}
		return null;
	}
	
}
