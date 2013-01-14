package rs.pedjaapps.shoppinglist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper
{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ShoppingList.db";

    // table names
    private static final String TABLE_LISTS = "lists_table";
    // Table Columns names
    private static final String KEY_LISTS_ID = "id";
    private static final String KEY_LISTS_NAME = "name";
    private static final String KEY_LISTS_TABLE_NAME = "table_name";
    private static final String KEY_LISTS_COLOR = "color";
    
    /*static final String KEY_PROFILE_CPU2MIN = "cpu2min";
    private static final String KEY_PROFILE_CPU2MAX = "cpu2max";
    private static final String KEY_PROFILE_CPU3MAX = "cpu3max";
    private static final String KEY_PROFILE_CPU3MIN = "cpu3min";
    private static final String KEY_PROFILE_CPU0GOV = "cpu0gov";
    private static final String KEY_PROFILE_CPU1GOV = "cpu1gov";
    private static final String KEY_PROFILE_CPU2GOV = "cpu2gov";
    private static final String KEY_PROFILE_CPU3GOV = "cpu3gov";
	private static final String KEY_PROFILE_VOLTAGE = "voltageProfile";
    private static final String KEY_PROFILE_MTD = "mtd";
    private static final String KEY_PROFILE_MTU = "mtu";
    private static final String KEY_PROFILE_GPU2D = "gpu2d";
    private static final String KEY_PROFILE_GPU3D = "gpu3d";
	private static final String KEY_PROFILE_BUTTONS_BACKLIGHT = "buttonsLight";
    private static final String KEY_PROFILE_VSYNC = "vsync";
	private static final String KEY_PROFILE_F_CHARGE = "fastcharge";
	private static final String KEY_PROFILE_CDEPTH = "cdepth";
	private static final String KEY_PROFILE_IOSCHEDULER = "IOScheduler";
	private static final String KEY_PROFILE_SDCACHE = "sdCache";
	private static final String KEY_PROFILE_SWEEP2WAKE = "sweep2wake";
  */
	
    

    public DatabaseHandler(Context context)
	{
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
	{
        String CREATE_LISTS_TABLE = "CREATE TABLE " + TABLE_LISTS + "("
			+ KEY_LISTS_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LISTS_NAME + " TEXT,"
			+ KEY_LISTS_TABLE_NAME + " TEXT,"
			+ KEY_LISTS_COLOR + " INTEGER," 
			
			+
			")";
        
        db.execSQL(CREATE_LISTS_TABLE);
    }

    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

  
    void addList(ListsDatabaseEntry list)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LISTS_NAME, list.getName());
        values.put(KEY_LISTS_TABLE_NAME, list.getTableName()); 
        values.put(KEY_LISTS_COLOR, list.getColor()); 
       

        // Inserting Row
        db.insert(TABLE_LISTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single profile
    Profile getProfile(int id)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROFILES, new String[] { KEY_PROFILE_ID,
									 KEY_PROFILE_NAME,
									 KEY_PROFILE_CPU0MIN,
									 KEY_PROFILE_CPU0MAX,
									 KEY_PROFILE_CPU1MIN,
									 KEY_PROFILE_CPU1MAX,
									 KEY_PROFILE_CPU2MIN,
									 KEY_PROFILE_CPU2MAX,
									 KEY_PROFILE_CPU3MIN,
									 KEY_PROFILE_CPU3MAX,
									 KEY_PROFILE_CPU0GOV,
									 KEY_PROFILE_CPU1GOV,
									 KEY_PROFILE_CPU2GOV,
									 KEY_PROFILE_CPU3GOV,
									 KEY_PROFILE_VOLTAGE,
									 KEY_PROFILE_MTD,
									 KEY_PROFILE_MTU,
									 KEY_PROFILE_GPU2D,
									 KEY_PROFILE_GPU3D,
									 KEY_PROFILE_BUTTONS_BACKLIGHT,
									 KEY_PROFILE_VSYNC,
									 KEY_PROFILE_F_CHARGE,
									 KEY_PROFILE_CDEPTH,
									 KEY_PROFILE_IOSCHEDULER,
									 KEY_PROFILE_SDCACHE,
									 KEY_PROFILE_SWEEP2WAKE
									}, KEY_PROFILE_ID + "=?",
								 new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Profile profile = new Profile(Integer.parseInt(cursor.getString(0)),
									  cursor.getString(1),
									  cursor.getString(2),
									  cursor.getString(3), 
									  cursor.getString(4), 
									  cursor.getString(5),
									  cursor.getString(6),
									  cursor.getString(7),
									  cursor.getString(8),
									  cursor.getString(9),
									  cursor.getString(10),
									  cursor.getString(11),
									  cursor.getString(12),
									  cursor.getString(13),
									  cursor.getString(14),
									  cursor.getString(15),
									  cursor.getString(16), 
									  cursor.getString(17), 
									  cursor.getString(18),
									  cursor.getString(19),
									  cursor.getInt(20),
									  cursor.getInt(21),
									  cursor.getString(22),
									  cursor.getString(23),
									  cursor.getInt(24),
									  
									  cursor.getInt(25)
									
									  
									  );
        // return contact
        db.close();
        cursor.close();
        return profile;
    }

    Profile getProfileByName(String name)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROFILES, new String[] { KEY_PROFILE_ID,
									 KEY_PROFILE_NAME,
									 KEY_PROFILE_CPU0MIN,
									 KEY_PROFILE_CPU0MAX,
									 KEY_PROFILE_CPU1MIN,
									 KEY_PROFILE_CPU1MAX,
									 KEY_PROFILE_CPU2MIN,
									 KEY_PROFILE_CPU2MAX,
									 KEY_PROFILE_CPU3MIN,
									 KEY_PROFILE_CPU3MAX,
									 KEY_PROFILE_CPU0GOV,
									 KEY_PROFILE_CPU1GOV,
									 KEY_PROFILE_CPU2GOV,
									 KEY_PROFILE_CPU3GOV,
									 KEY_PROFILE_VOLTAGE,
									 KEY_PROFILE_MTD,
									 KEY_PROFILE_MTU,
									 KEY_PROFILE_GPU2D,
									 KEY_PROFILE_GPU3D,
									 KEY_PROFILE_BUTTONS_BACKLIGHT,
									 KEY_PROFILE_VSYNC,
									 KEY_PROFILE_F_CHARGE,
									 KEY_PROFILE_CDEPTH,
									 KEY_PROFILE_IOSCHEDULER,
									 KEY_PROFILE_SDCACHE,
									 KEY_PROFILE_SWEEP2WAKE
									}, KEY_PROFILE_NAME + "=?",
								 new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Profile profile = new Profile(Integer.parseInt(cursor.getString(0)),
									  cursor.getString(1),
									  cursor.getString(2),
									  cursor.getString(3), 
									  cursor.getString(4), 
									  cursor.getString(5),
									  cursor.getString(6),
									  cursor.getString(7),
									  cursor.getString(8),
									  cursor.getString(9),
									  cursor.getString(10),
									  cursor.getString(11),
									  cursor.getString(12),
									  cursor.getString(13),
									  cursor.getString(14),
									  cursor.getString(15),
									  cursor.getString(16), 
									  cursor.getString(17), 
									  cursor.getString(18),
									  cursor.getString(19),
									  cursor.getInt(20),
									  cursor.getInt(21),
									  cursor.getString(22),
									  cursor.getString(23),
									  cursor.getInt(24),
									  
									  cursor.getInt(25)
									);
        
        db.close();
        cursor.close();
        return profile;
    }

    // Getting All Contacts
    public List<Profile> getAllProfiles()
	{
        List<Profile> profileList = new ArrayList<Profile>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
		{
            do {
				Profile profile = new Profile();
                profile.setID(Integer.parseInt(cursor.getString(0)));
                profile.setName(cursor.getString(1));
                profile.setCpu0min(cursor.getString(2));
                profile.setCpu0max(cursor.getString(3));
                profile.setCpu1min(cursor.getString(4));
                profile.setCpu1max(cursor.getString(5));
                profile.setCpu2min(cursor.getString(6));
                profile.setCpu2max(cursor.getString(7));
                profile.setCpu3min(cursor.getString(8));
                profile.setCpu3max(cursor.getString(9));
                profile.setCpu0gov(cursor.getString(10));
                profile.setCpu1gov(cursor.getString(11));
                profile.setCpu2gov(cursor.getString(12));
                profile.setCpu3gov(cursor.getString(13));
				profile.setVoltage(cursor.getString(14));
                profile.setMtd(cursor.getString(15));
                profile.setMtu(cursor.getString(16));
                profile.setGpu2d(cursor.getString(17));
                profile.setGpu3d(cursor.getString(18));
				profile.setButtonsLight(cursor.getString(19));
                profile.setVsync(cursor.getInt(20));
                profile.setFcharge(cursor.getInt(21));
                profile.setCdepth(cursor.getString(22));
                profile.setIoScheduler(cursor.getString(23));
                profile.setSdcache(cursor.getInt(24));
                
                profile.setSweep2wake(cursor.getInt(25));
              
                
                // Adding  to list
                profileList.add(profile);
            } while (cursor.moveToNext());
        }

        // return list
        db.close();
        cursor.close();
        return profileList;
    }

    // Updating single 
    public int updateProfile(Profile profile)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROFILE_NAME, profile.getName());
        values.put(KEY_PROFILE_CPU0MIN, profile.getCpu0min()); 
        values.put(KEY_PROFILE_CPU0MAX, profile.getCpu0max()); 
        values.put(KEY_PROFILE_CPU1MIN, profile.getCpu1min());
        values.put(KEY_PROFILE_CPU1MAX, profile.getCpu1max());
        values.put(KEY_PROFILE_CPU2MIN, profile.getCpu2min()); 
        values.put(KEY_PROFILE_CPU2MAX, profile.getCpu2max()); 
        values.put(KEY_PROFILE_CPU3MAX, profile.getCpu3max());
        values.put(KEY_PROFILE_CPU3MIN, profile.getCpu3min());
        values.put(KEY_PROFILE_CPU0GOV, profile.getCpu0gov());
        values.put(KEY_PROFILE_CPU1GOV, profile.getCpu1gov());
        values.put(KEY_PROFILE_CPU2GOV, profile.getCpu2gov());
        values.put(KEY_PROFILE_CPU3GOV, profile.getCpu3gov());
		values.put(KEY_PROFILE_VOLTAGE,profile.getVoltage());
        values.put(KEY_PROFILE_MTD, profile.getMtd());
        values.put(KEY_PROFILE_MTU, profile.getMtu());
        values.put(KEY_PROFILE_GPU2D, profile.getGpu2d());
        values.put(KEY_PROFILE_GPU3D, profile.getGpu3d());
		values.put(KEY_PROFILE_BUTTONS_BACKLIGHT, profile.getButtonsLight());
        values.put(KEY_PROFILE_VSYNC, profile.getVsync());
        values.put(KEY_PROFILE_F_CHARGE, profile.getFcharge());
        values.put(KEY_PROFILE_CDEPTH, profile.getCdepth());
        values.put(KEY_PROFILE_IOSCHEDULER, profile.getIoScheduler());
        values.put(KEY_PROFILE_SDCACHE, profile.getSdcache());
        values.put(KEY_PROFILE_SWEEP2WAKE, profile.getSweep2wake());
        
        
        // updating row
        db.close();
        return db.update(TABLE_PROFILES, values, KEY_PROFILE_ID + " = ?",
						 new String[] { String.valueOf(profile.getID()) });
        
	}

    // Deleting single profile
    public void deleteProfile(Profile profile)
	{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILES, KEY_PROFILE_ID + " = ?",
				  new String[] { String.valueOf(profile.getID()) });
        db.close();
    }

    public void deleteProfileByName(Profile profile)
	{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILES, KEY_PROFILE_NAME + " = ?",
				  new String[] { String.valueOf(profile.getName()) });
        db.close();
    }

    // Getting profile Count
    public int getProfileCount()
	{
        String countQuery = "SELECT  * FROM " + TABLE_PROFILES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    
    
}

