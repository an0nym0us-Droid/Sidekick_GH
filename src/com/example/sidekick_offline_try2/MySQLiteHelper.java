package com.example.sidekick_offline_try2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	
	private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
	//destination path (location) of our database on device
	private static String DB_PATH = ""; 
	private static String DB_NAME ="tv.db";// Database name
	private SQLiteDatabase mDataBase; 
	private final Context mContext;
    public MySQLiteHelper(Context context){
		super(context, DB_NAME, null, 1);// 1? its Database Version
	    DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	    this.mContext = context;
	}

	public void createDataBase() throws IOException
	{
	    //If database not exists copy it from the assets

	    boolean mDataBaseExist = checkDataBase();
	    if(!mDataBaseExist)
	    {
	        this.getReadableDatabase();
	        this.close();
	        try 
	        {
	            //Copy the database from assests
	            copyDataBase();
	            Log.e(TAG, "createDatabase database created");
	        } 
	        catch (IOException mIOException) 
	        {
	            throw new Error("ErrorCopyingDataBase");
	        }
	    }
	}
	    //Check that the database exists here: /data/data/your package/databases/Da Name
	    private boolean checkDataBase()
	    {
	        File dbFile = new File(DB_PATH + DB_NAME);
	        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
	        return dbFile.exists();
	    }

	    //Copy the database from assets
	    private void copyDataBase() throws IOException
	    {
	        InputStream mInput = mContext.getAssets().open(DB_NAME);
	        String outFileName = DB_PATH + DB_NAME;
	        OutputStream mOutput = new FileOutputStream(outFileName);
	        byte[] mBuffer = new byte[1024];
	        int mLength;
	        while ((mLength = mInput.read(mBuffer))>0)
	        {
	            mOutput.write(mBuffer, 0, mLength);
	        }
	        mOutput.flush();
	        mOutput.close();
	        mInput.close();
	    }

	    //Open the database, so we can query it
	    public boolean openDataBase() throws SQLException
	    {
	        String mPath = DB_PATH + DB_NAME;
	        //Log.v("mPath", mPath);
	        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
	        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	        return mDataBase != null;
	    }

	    @Override
	    public synchronized void close() 
	    {
	        if(mDataBase != null)
	            mDataBase.close();
	        super.close();
	    }

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
  /*public static final String TABLE_CHANNELS_LISTINGS = "channels_listings";
  public static final String TABLE_CATEGORIES = "defaultCategories";
  public static final String TABLE_USER_CATEGORIES = "userCategories";
  public static final String TABLE_CHANNELS="channels";
  
  private String[] names ={"English Movies","Hindi Movies","Sports","English News","Hindi News","Religious",};
  private String[] channel_list = {"[456, 457, 458]","[459, 460, 470]","[480, 490, 500]","[1, 2, 3]","[234, 236, 237]","[555, 666, 777]"};
  
  private Integer[] c_list = {456,457,458,459,460,470,480,490,500,1,2,3,234,236,237,555,666,777};
  Integer tt = 11;
  private String[] c_name = {"HBO","StarMovies","WB","Star Gold","Set Max","UTV","Start Sports","ESPN","Ten Sports","BBC","NDTVeng","TimesNow","ABP News","IndiaTV","AajTak","Sanskar","Aastha","BhaktiTV"};
  private String[] times = {"2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13",
		  "2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13",
		  "2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13","2013-05-15 "+(tt++)+":41:13"}; 
  private static final String DATABASE_NAME = "tv.db";
  private static final int DATABASE_VERSION = 1;
  
  // Database creation sql statement
  private static final String TABLE_CHANNELS_LISTINGS_CREATE = "create table "
      + TABLE_CHANNELS_LISTINGS +"(channel_id integer,channel_name string,st_time text,end_time text,show_name string,description string);";
  
  private static final String TABLE_USER_CATEGORIES_CREATE = "create table "
	      + TABLE_USER_CATEGORIES +"(category_id integer primary key,category_name string,category_sequence integer,channels string,isvisible integer,last_updated string);";

  private static final String TABLE_CATEGORIES_CREATE = "create table "
	      + TABLE_CATEGORIES +"(category_id integer primary key,category_name string,category_sequence integer,channels string,last_updated string);";
  
  private static final String TABLE_CHANNELS_CREATE = "create table "
		  + TABLE_CHANNELS + "(channel_id integer,channel_name string);";
  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);   
  }
*/
 /* @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(TABLE_CHANNELS_LISTINGS_CREATE);
    database.execSQL(TABLE_CATEGORIES_CREATE);
    database.execSQL(TABLE_USER_CATEGORIES_CREATE);
    database.execSQL(TABLE_CHANNELS_CREATE);
    
	  for(int i=0;i<names.length;i++){
		  database.execSQL("INSERT INTO "+TABLE_CATEGORIES+" VALUES(null,'"+names[i]+"',"+(i+1)+",'"+channel_list[i]+"','nodata');");
	  }
	  
	  for(int i=0;i<names.length;i++){
		  database.execSQL("INSERT INTO "+TABLE_USER_CATEGORIES+" VALUES(null,'"+names[i]+"',"+(i+1)+",'"+channel_list[i]+"',1,'nodata');");
	  }
	  
	  for(int c=0;c<c_list.length;c++){
		  ContentValues val2 = new ContentValues();
		  val2.put("channel_id",c_list[c]);
		  val2.put("channel_name", c_name[c]);
		  database.insert(TABLE_CHANNELS, null, val2);
		  for(int i=0;i<12;i++){
			  ContentValues val = new ContentValues();
	  
			  val.put("channel_id",c_list[c]);
			  val.put("channel_name", c_name[c]);
			  val.put("st_time", times[i]);
			  val.put("end_time", times[(i+1)%12]);
			  val.put("show_name", c_name[c]+"_Show"+(i+1));
			  val.put("description",c_name[c]+"_Show"+(i+1)+"description should be long enough to you know so that it can be accomodated in larger view");
			  database.insert(TABLE_CHANNELS_LISTINGS, null, val);
		  }
	  }
	  
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS_LISTINGS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CATEGORIES);
    onCreate(db);
  }*/

} 