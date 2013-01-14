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

public class DatabaseHandler {
	
	public static final String TAG="ShoppingList.db";
	
	// used to get id of the list in list table
	private static final int ID_COLUMN_INDEX = 0;
	
	/* Constants for following created lists */
	public static final String KEY_LIST_ROWID = "_id";
	public static final String KEY_LIST_NAME = "name";
	public static final String KEY_LIST_TABLE_NAME = "table_name";
		
	/* Constants needed for items in the list */
	public static final String KEY_ITEM_NAME="name";
	public static final String KEY_ITEM_QUANTITY="quantity";
	public static final String KEY_ITEM_VALUE="value";
	public static final String KEY_ITEM_TOTAL_ITEM_VALUE="totalvalue";
	public static final String KEY_ITEM_ROWID="_id";
	public static final String KEY_ITEM_DONE="done";
	public static final String KEY_ITEM_IMAGE="image";
	public static final String KEY_ITEM_CATEGORY="category";
	public static final String KEY_ITEM_TABLE_NAME = "tablename";
	public static final String DATABASE_NAME="ShoppingList.db";
	public static final int DATABASE_VERSION=2;
	
	/* This constant is used to notify that there was problem creating table in DB */
	public static final long NOTIFY_TABLE_CREATION_PROBLEM = -2;
	
	public static final String LIST_TABLE_NAME = "list_table_name";
	
	private static final String CREATE_LIST_TABLE ="create table " + LIST_TABLE_NAME + " (" + KEY_LIST_ROWID + " integer primary key autoincrement, "
    + KEY_LIST_NAME + " text not null, " + KEY_LIST_TABLE_NAME + " text not null "  + ");";

	public static final int LIST_NAME_COLUMN = 1;
	public static final int TABLE_NAME_COLUMN = 2;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
			
			DatabaseHelper(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}
	
			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(CREATE_LIST_TABLE);
								
			}
	
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
				db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
				onCreate(db);
			}
			
		}
	/**
	 * Constructor
	 * @param context
	 */
	public DatabaseHandler (Context context) {
		this.mContext = context;
		
	}
	/**
	 * This method opens data base
	 * @return
	 * @throws SQLException
	 */
	public DatabaseHandler open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	/**
	 * This method closes database
	 */
	public void close() {
		mDbHelper.close();
	}
	/**
	 * Inserts name of the newly created list in the lists table and 
	 * creates new table in data base using _id from the list table for
	 * name of newly created table
	 * 
	 * @param name name of the list
	 * @return row id of the newly inserted row or -1 if it cannot be created
	 */
	public long createList (String name) {
		long newPosition = 1;
		Cursor c = getAllLists();
		if (c.getCount()>0) {
			c.moveToLast();
			newPosition = c.getLong(ID_COLUMN_INDEX) + 1;
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LIST_NAME, name);
		name = "ListId_" + newPosition; 		
		initialValues.put(KEY_LIST_TABLE_NAME, name);
		
		/* this creates table in the DB for list */
		try {
			mDb.execSQL(createSQLStatementAdd(name));
			return mDb.insert(LIST_TABLE_NAME, null, initialValues);
		} catch (SQLException e) {
			return NOTIFY_TABLE_CREATION_PROBLEM;
		}
		
	}
	
	public void setListName(long id, String name) {
		ContentValues values = new ContentValues();
		values.put(KEY_LIST_NAME, name);
		mDb.update(LIST_TABLE_NAME, values, KEY_LIST_ROWID + "=" + id , null);
	}
	
	public void setListNameByName(String name, String newName) {
		ContentValues values = new ContentValues();
		values.put(KEY_LIST_NAME, newName);
		mDb.update(LIST_TABLE_NAME, values, KEY_LIST_NAME + "=" + name , null);
	}
	
	/**
	 * This method deletes row in the LIST_TABLE_NAME of the selected id
	 * and deletes appropriate table from the DB  
	 * @param id row id of the selected list in the LIST_TABLE_NAMES
	 * @return id of the deleted row
	 */
	public long removeList (long id) {
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(true, LIST_TABLE_NAME, columns, KEY_LIST_ROWID + "=" + id, null, null, null, null, null);
		if (c!=null) {
			c.moveToFirst();
		}
		String tableName = c.getString(TABLE_NAME_COLUMN);
		mDb.execSQL(createSQLStatementRemove(tableName));
		return mDb.delete(LIST_TABLE_NAME, KEY_LIST_ROWID + "=" + id, null);
		
	}
	/**
	 * This method removes all lists from database
	 * It removes entries from list table and drops all other tables
	 * 
	 * @return number of affected tables
	 */
	public int removeAllLists () {
		int counter = 0;
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(LIST_TABLE_NAME, columns, null, null, null, null, null);
		c.moveToFirst();
		for (c.move(-1); c.moveToNext(); c.isAfterLast()) {
			String tableName = c.getString(TABLE_NAME_COLUMN);
			mDb.execSQL(createSQLStatementRemove(tableName));
			counter++;
		}
		if (counter>0) mDb.delete(LIST_TABLE_NAME, null, null);
		return counter;
	}
	/**
	 * Gets list
	 * @param id id of the list
	 * @return list cursor
	 */
/*	public Cursor getList(long id) {
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(true, LIST_TABLE_NAME, columns, KEY_LIST_ROWID + "=" + id, null, null, null, null, null);
		if (c!=null) {
			c.moveToFirst();
		}
		return c;
	}*/
	
	public String getList(long id)
	{
		String list;
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};

        Cursor c = mDb.query(LIST_TABLE_NAME, columns, KEY_LIST_ROWID + "=?",
							 new String[] { String.valueOf(id) }, null, null, null, null);
	//	Cursor c = mDb.query(true, LIST_TABLE_NAME, columns, KEY_LIST_ROWID + "=" + id, null, null, null, null, null);
		
        if (c != null)
            c.moveToFirst();

        list = c.getString(1);


        c.close();
        return list;
    }
	

	/**
	 * Gets all lists from the LIST_TABLE_NAME sorted
	 * by name. It is used to display existing lists sorted alphabetically 
	 * in EZCart activity
	 * @return cursor with all lists
	 */
	public Cursor getAllListsSorted() {
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(LIST_TABLE_NAME, columns, null, null, null, null, KEY_LIST_NAME, null);
		if (c!=null) {
			c.moveToFirst();
		}
		return c;
	}
	
	/**
	 * Gets all lists from the LIST_TABLE_NAME
	 * It is used to determine id of the last list in create
	 * list method.
	 * @return cursor with all lists
	 */
	public Cursor getAllLists() {
		String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(LIST_TABLE_NAME, columns, null, null, null, null, null, null);
		if (c!=null) {
			c.moveToFirst();
		}
		return c;
	}
	
	public List<String> getAllListsNames()
	{
        List<String> nameList = new ArrayList<String>();
        String[] columns = new String[] {KEY_LIST_ROWID, KEY_LIST_NAME, KEY_LIST_TABLE_NAME};
		Cursor c = mDb.query(LIST_TABLE_NAME, columns, null, null, null, null, null, null);
		
        if (c.moveToFirst())
		{
            do {
				
                // Adding  to list
                nameList.add(c.getString(1));
            } while (c.moveToNext());
        }

        
        return nameList;
    }
	
	/**
	 * Creates SQL statement for creating table to be used with
	 * execSQL.
	 * This is used for creating multiple lists, this creates table
	 * in existing database that is used store items in list;
	 * 
	 * @param tableNaDatabaseme name of the table to be created in Database
	 * @return
	 */
	private String createSQLStatementAdd (String tableName) {
		return "create table "+tableName+" (_id integer primary key autoincrement, "
        + DatabaseHandler.KEY_ITEM_NAME + " text not null, "
		+ DatabaseHandler.KEY_ITEM_VALUE + " double not null, " 
        + DatabaseHandler.KEY_ITEM_QUANTITY + " double not null, " 
		+ DatabaseHandler.KEY_ITEM_TOTAL_ITEM_VALUE + " double not null, " 
		+ DatabaseHandler.KEY_ITEM_IMAGE + " text not null, " 
		+ DatabaseHandler.KEY_ITEM_CATEGORY + " text not null, " 
        + DatabaseHandler.KEY_ITEM_DONE +" boolean not null );";
	}
	/**	
	 * Creates SQL statement for creating table to be used with execSQL.
	 * This is used for creating multiple lists, this deletes table
	 * in existing database that is used store items in list;
	 * @param tableName
	 * @return
	 */
	private String createSQLStatementRemove (String tableName) {
		return "drop table "+tableName;
	}
	
	/**
	 * This method inserts item in the list 
	 * @param table name of the list
	 * @param name nDatabaseame of the item
	 * @param value price of the item
	 * @param quantity number of items
	 * @param totalItemValue total value of the item (value*quantity)
	 * @param done true if item is bought, false if item is to be bought
	 * @return row id of the newly inserted row
	 */
	public long addItem (String table, String name, double value, double quantity, double totalItemValue, boolean done, String image, String category) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ITEM_NAME, name);
		initialValues.put(KEY_ITEM_VALUE, value);
		initialValues.put(KEY_ITEM_QUANTITY, quantity);
		initialValues.put(KEY_ITEM_TOTAL_ITEM_VALUE, totalItemValue);
		initialValues.put(KEY_ITEM_IMAGE, image);
		initialValues.put(KEY_ITEM_CATEGORY, category);
		initialValues.put(KEY_ITEM_DONE, done);
		
		
		return mDb.insert(table, null, initialValues);
	}
	/**
	 * This gets item from the list
	 * @param listName name of the list
	 * @param id row id of the item
	 * @return cursor with all values of single item (name, price, quantity, total price, done)
	 */
	public Cursor getItem(String listName, long id) {
		String[] columns = new String[] {KEY_ITEM_ROWID, KEY_ITEM_NAME, KEY_ITEM_VALUE, KEY_ITEM_QUANTITY, KEY_ITEM_TOTAL_ITEM_VALUE, KEY_ITEM_IMAGE, KEY_ITEM_CATEGORY, KEY_ITEM_DONE};
		Cursor c = mDb.query(true, listName, columns, KEY_ITEM_ROWID + "=" + id, null, null, null, null, null);
		if (c!=null) {
			c.moveToFirst();
		}
		return c;
	}
	
	/**
	 * This gets all items from the list
	 * @param listName name of the list
	 * @return cursor holding values of all items in the list (name, price, quantity, total price, done)
	 */
	public Cursor getAllItems(String listName) {
		String[] columns = new String[] {KEY_ITEM_ROWID, KEY_ITEM_NAME, KEY_ITEM_VALUE, KEY_ITEM_QUANTITY, KEY_ITEM_TOTAL_ITEM_VALUE, KEY_ITEM_IMAGE, KEY_ITEM_CATEGORY, KEY_ITEM_DONE};
		return mDb.query(listName, columns, null, null, null, null, KEY_ITEM_NAME );
	}
	
	/**
	 * This updates item in a list
	 * @param listName name of the list
	 * @param id id of the item that is edited
	 * @param name name of the item
	 * @param value price of the item
	 * @param quantity number of items
	 * @param totalItemValue total price of added item
	 * @param done boolean true if bought, false if it is to be bought
	 * @return boolean true succeeded, false if not 
	 */
	public boolean updateItem(String listName, long id, String name, double value, double quantity, double totalItemValue, boolean done, String category, String image) {
		ContentValues args= new ContentValues();
		args.put(KEY_ITEM_NAME, name);
		args.put(KEY_ITEM_VALUE, value);
		args.put(KEY_ITEM_QUANTITY, quantity);
		args.put(KEY_ITEM_TOTAL_ITEM_VALUE, totalItemValue);
		args.put(KEY_ITEM_DONE, done);
		args.put(KEY_ITEM_IMAGE, image);
		args.put(KEY_ITEM_CATEGORY, category);
		
		return mDb.update(listName, args, KEY_ITEM_ROWID+"="+id, null)>0;
	}
	
	public boolean updateDone(String listName, long id, boolean done) {
		ContentValues args= new ContentValues();
		args.put(KEY_ITEM_DONE, done);
		return mDb.update(listName, args, KEY_ITEM_ROWID+"="+id, null)>0;
	}
	
	/**
	 * This removes item from the list 
	 * @param listName name of the list
	 * @param id id of the item in the list
	 * @return boolean true succeeded, false if not
	 */
	public boolean removeItem(String listName, long id) {
		return mDb.delete(listName, KEY_ITEM_ROWID + "=" + id, null) > 0;
	}
	/**
	 * Removes all items from the list
	 * @param listName name of the list 
	 * @return boolean true succeeded, false if not
	 */
	public boolean clearList(String listName) {
		return mDb.delete(listName, null, null) > 0;
	}
	
	
	
	/**
	 * This method checks if list with a given name already exists
	 * in the database.
	 * First it creates array list and then it checks for the index 
	 * of the given name.
	 *  
	 * @param listName Name of the list we are checking if it already exists
	 * @return boolean true if exists, false otherwise
	 */
	public boolean listExists(String listName) {
		Cursor c = getAllListsSorted();
		c.moveToFirst();
		ArrayList<String> listOfNames = new ArrayList<String>();
		for (c.move(-1); c.moveToNext(); c.isAfterLast()) {
			String name = c.getString(LIST_NAME_COLUMN);
			listOfNames.add(name);
		}
		return listOfNames.indexOf(listName) > 0;
	}
	
	
	private final Context mContext;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
}

/*import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KTDatabase.db";

    // table names
    private static final String TABLE_PROFILES = "profiles";
    // Table Columns names
    private static final String KEY_PROFILE_ID = "id";
    private static final String KEY_PROFILE_NAME = "Name";
    private static final String KEY_PROFILE_CPU0MIN = "cpu0min";
    private static final String KEY_PROFILE_CPU0MAX = "cpu0max";
    private static final String KEY_PROFILE_CPU1MAX = "cpu1max";
    private static final String KEY_PROFILE_CPU1MIN = "cpu1min";

    private static final String KEY_PROFILE_CPU2MIN = "cpu2min";
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
  
	
    

    public DatabaseHandler(Context context)
	{
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
	{
        String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
			+ KEY_PROFILE_ID + " INTEGER PRIMARY KEY,"
			+ KEY_PROFILE_NAME + " TEXT,"
			+ KEY_PROFILE_CPU0MIN + " TEXT,"
			+ KEY_PROFILE_CPU0MAX + " TEXT," 
			+ KEY_PROFILE_CPU1MIN + " TEXT,"
			+ KEY_PROFILE_CPU1MAX + " TEXT,"
			+ KEY_PROFILE_CPU2MIN + " TEXT,"
			+ KEY_PROFILE_CPU2MAX + " TEXT," 
			+ KEY_PROFILE_CPU3MAX + " TEXT,"
			+ KEY_PROFILE_CPU3MIN + " TEXT,"
			+ KEY_PROFILE_CPU0GOV + " TEXT,"
			+ KEY_PROFILE_CPU1GOV + " TEXT,"
			+ KEY_PROFILE_CPU2GOV + " TEXT,"
			+ KEY_PROFILE_CPU3GOV + " TEXT,"
			+ KEY_PROFILE_VOLTAGE + " TEXT,"
			+ KEY_PROFILE_MTD + " TEXT,"
			+ KEY_PROFILE_MTU + " TEXT,"
			+ KEY_PROFILE_GPU2D + " TEXT,"
			+ KEY_PROFILE_GPU3D + " TEXT,"
			+ KEY_PROFILE_BUTTONS_BACKLIGHT + " TEXT,"
			+ KEY_PROFILE_VSYNC + " INTEGER,"
			+ KEY_PROFILE_F_CHARGE + " INTEGER,"
			+ KEY_PROFILE_CDEPTH + " TEXT,"
			+ KEY_PROFILE_IOSCHEDULER + " TEXT,"
			+ KEY_PROFILE_SDCACHE + " INTEGER,"
			+ KEY_PROFILE_SWEEP2WAKE + " INTEGER"
			
			+
			")";
        
        db.execSQL(CREATE_PROFILES_TABLE);
    }

    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     

  
    void addProfile(Profile profile)
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
       

        // Inserting Row
        db.insert(TABLE_PROFILES, null, values);
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
*/
