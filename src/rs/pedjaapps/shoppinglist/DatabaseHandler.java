package rs.pedjaapps.shoppinglist;

import java.util.ArrayList;
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
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "ShoppingList.db";

    // table names
    private static final String TABLE_LISTS = "lists_table";
	private static final String TABLE_CURENCIES = "curencies";
    //private static final String TABLE_ITEM = "item_table";
    // Table Columns names
    private static final String KEY_LISTS_ID = "_id";
    private static final String KEY_LISTS_NAME = "name";
    private static final String KEY_LISTS_COLOR = "color";
    private static final String KEY_LISTS_DATE = "date";

	private static final String KEY_ITEM_ROWID="_id";
    private static final String KEY_ITEM_NAME="name";
	private static final String KEY_ITEM_QUANTITY="quantity";
	private static final String KEY_ITEM_VALUE="value";
	private static final String KEY_ITEM_IMAGE="image";
	private static final String KEY_ITEM_UNIT="unit";
	private static final String KEY_ITEM_CURENCY="curency";
	private static final String KEY_ITEM_DONE="done";
	private static final String[] CURENCY_KEYS = {"_id","EUR", "GBP", "HRK", "HUF", "JPY", "KWD", "NOK", "SEK", "USD", "DKK", "CZK", "CHF", "CAD", "BAM", "AUD"};

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
			+ KEY_LISTS_COLOR + " INTEGER," 
			+ KEY_LISTS_DATE + " TEXT"
			+
			")";
        
		String CREATE_CURENCIES_TABLE = "CREATE TABLE " + TABLE_CURENCIES + "("
			+ CURENCY_KEYS[0] + " INTEGER PRIMARY KEY,"
			+ CURENCY_KEYS[1] + " DOUBLE,"
			+ CURENCY_KEYS[2] + " DOUBLE,"
			+ CURENCY_KEYS[3] + " DOUBLE,"
			+ CURENCY_KEYS[4] + " DOUBLE,"
			+ CURENCY_KEYS[5] + " DOUBLE,"
			+ CURENCY_KEYS[6] + " DOUBLE,"
			+ CURENCY_KEYS[7] + " DOUBLE,"
			+ CURENCY_KEYS[8] + " DOUBLE,"
			+ CURENCY_KEYS[9] + " DOUBLE,"
			+ CURENCY_KEYS[10] + " DOUBLE,"
			+ CURENCY_KEYS[11] + " DOUBLE,"
			+ CURENCY_KEYS[12] + " DOUBLE,"
			+ CURENCY_KEYS[13] + " DOUBLE,"
			+ CURENCY_KEYS[14] + " DOUBLE,"
			+ CURENCY_KEYS[15] + " DOUBLE"
			
			+
			")";
		
        db.execSQL(CREATE_LISTS_TABLE);
		db.execSQL(CREATE_CURENCIES_TABLE);
    }

    
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURENCIES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void createItemTable(String name){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String CREATE_ITEM_TABLE = "CREATE TABLE " + name + "("
    			+ KEY_ITEM_ROWID + " INTEGER PRIMARY KEY,"
    			+ KEY_ITEM_NAME + " TEXT,"
    			+ KEY_ITEM_QUANTITY + " DOUBLE,"
    			+ KEY_ITEM_VALUE + " DOUBLE," 
    			+ KEY_ITEM_IMAGE + " TEXT," 
    			+ KEY_ITEM_UNIT + " TEXT,"
    			+ KEY_ITEM_CURENCY + " TEXT," 
    			+ KEY_ITEM_DONE + " BOOLEAN DEFAULT 'FALSE'" 
    			+
    			")";
            
            db.execSQL(CREATE_ITEM_TABLE);
            db.close();
    }
  
    void addList(ListsDatabaseEntry list)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LISTS_NAME, list.getName());
        values.put(KEY_LISTS_COLOR, list.getColor()); 
        values.put(KEY_LISTS_DATE, list.getDate());

        // Inserting Row
        db.insert(TABLE_LISTS, null, values);
        db.close(); // Closing database connection
        createItemTable(list.getName());
    }

    // Getting single list
    ListsDatabaseEntry getList(int id)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LISTS, new String[] { KEY_LISTS_ID,
									 KEY_LISTS_NAME,
									 KEY_LISTS_COLOR,
									 KEY_LISTS_DATE
									}, KEY_LISTS_ID + "=?",
								 new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ListsDatabaseEntry profile = new ListsDatabaseEntry(Integer.parseInt(cursor.getString(0)),
									  cursor.getString(1),
									  Integer.parseInt(cursor.getString(2)),
									  cursor.getString(3)
									  );
        // return list
        db.close();
        cursor.close();
        return profile;
    }

    ListsDatabaseEntry getListByName(String name)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LISTS, new String[] { KEY_LISTS_ID,
									 KEY_LISTS_NAME,
									 KEY_LISTS_COLOR,
									 KEY_LISTS_DATE
									}, KEY_LISTS_NAME + "=?",
								 new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ListsDatabaseEntry profile = new ListsDatabaseEntry(Integer.parseInt(cursor.getString(0)),
									  cursor.getString(1),
									  Integer.parseInt(cursor.getString(2)),
									  cursor.getString(3)
									);
        
        db.close();
        cursor.close();
        return profile;
    }

    // Getting All Lists
    public List<ListsDatabaseEntry> getAllLists()
	{
        List<ListsDatabaseEntry> lists = new ArrayList<ListsDatabaseEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
		{
            do {
            	ListsDatabaseEntry list = new ListsDatabaseEntry();
                list.setID(Integer.parseInt(cursor.getString(0)));
                list.setName(cursor.getString(1));
                list.setColor(Integer.parseInt(cursor.getString(2)));
                list.setDate(cursor.getString(3));
                
                
                // Adding  to list
                lists.add(list);
            } while (cursor.moveToNext());
        }

        // return list
        db.close();
        cursor.close();
        return lists;
    }

    // Updating single 
    public int updateList(ListsDatabaseEntry list, int position, String name)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LISTS_NAME, list.getName());
        values.put(KEY_LISTS_COLOR, list.getColor());
        values.put(KEY_LISTS_DATE, list.getDate());
        
        // updating row
        //db.close();
        if(!name.equals(list.getName())){
        db.execSQL("ALTER TABLE " + name + " RENAME TO " + list.getName());
        }
        return db.update(TABLE_LISTS, values, KEY_LISTS_ID + " = ?",
						 new String[] { String.valueOf(position) });
        }

    // Deleting single profile
    public void deleteList(ListsDatabaseEntry list)
	{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, KEY_LISTS_ID + " = ?",
				  new String[] { String.valueOf(list.getID()) });
        db.execSQL("drop table "+list.getName());
        db.close();
    }

   /* public void deleteListByName(ListsDatabaseEntry profile)
	{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, KEY_LISTS_NAME + " = ?",
				  new String[] { String.valueOf(profile.getName()) });
        db.close();
    }*/

    // Getting profile Count
    public int getListsCount()
	{
        String countQuery = "SELECT  * FROM " + TABLE_LISTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    
    public boolean listExists(String listName) {
		/*List<String> lists = new ArrayList<String>();
		ListsDatabaseEntry listsEntry = new ListsDatabaseEntry();
		int listCount = getAllLists().size();
		for(int i = 0; i < listCount; i++){
			lists.add(listsEntry.getName());
		}
		return lists.indexOf(listName) > 0;*/
    	SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LISTS, new String[] { KEY_LISTS_ID,
									 KEY_LISTS_NAME,
									 KEY_LISTS_COLOR,
									 KEY_LISTS_DATE
									}, KEY_LISTS_NAME + "=?",
								 new String[] { listName }, null, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
	}
    
    void addItem(ItemsDatabaseEntry item, String listName)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, item.getName());
        values.put(KEY_ITEM_QUANTITY, item.getQuantity());  
        values.put(KEY_ITEM_VALUE, item.getValue());
        values.put(KEY_ITEM_IMAGE, item.getImage());
        values.put(KEY_ITEM_UNIT, item.getUnit()); 
        values.put(KEY_ITEM_CURENCY, item.getCurency());
        values.put(KEY_ITEM_DONE, item.getDone()); 

        // Inserting Row
        db.insert(listName, null, values);
        db.close(); // Closing database connection
    }
    
    ItemsDatabaseEntry getItem(String listName, int id)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(listName, new String[] { KEY_ITEM_ROWID,
									 KEY_ITEM_NAME,
									 KEY_ITEM_QUANTITY,
									 KEY_ITEM_VALUE,
									 KEY_ITEM_IMAGE,
									 KEY_ITEM_UNIT,
									 KEY_ITEM_CURENCY,
									 KEY_ITEM_DONE
									 
									 
									}, KEY_LISTS_ID + "=?",
								 new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ItemsDatabaseEntry items = new ItemsDatabaseEntry(Integer.parseInt(cursor.getString(0)),
									  cursor.getString(1),
									  cursor.getDouble(2),
									  cursor.getDouble(3),
									  cursor.getString(4), 
									  cursor.getString(5), 
									  cursor.getString(6), 
									  intToBool(cursor.getInt(7)) 
									  );
        // return list
        db.close();
        cursor.close();
        return items;
    }
	
	public List<ItemsDatabaseEntry> getAllItems(String listName)
	{
        List<ItemsDatabaseEntry> lists = new ArrayList<ItemsDatabaseEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + listName;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
		{
            do {
            	ItemsDatabaseEntry list = new ItemsDatabaseEntry();
                list.setID(Integer.parseInt(cursor.getString(0)));
                list.setName(cursor.getString(1));
                list.setQuantity(cursor.getDouble(2));
                list.setValue(cursor.getDouble(3));
				list.setImage(cursor.getString(4));
				list.setUnit(cursor.getString(5));
				list.setCurency(cursor.getString(6));
				list.setDone(intToBool(cursor.getInt(7)));


                // Adding  to list
                lists.add(list);
            } while (cursor.moveToNext());
        }

        // return list
        db.close();
        cursor.close();
        return lists;
    }
   
    private boolean intToBool(int i){
    	if(i == 0){
    		return false;
    	}
    	else{
    		return true;
    	}
    }
    
    void addCurency(CurenciesEntry entry)
	{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        	values.put(CURENCY_KEYS[1], entry.getEur());
        	values.put(CURENCY_KEYS[2], entry.getGbp());
        	values.put(CURENCY_KEYS[3], entry.getHrk());
        	values.put(CURENCY_KEYS[4], entry.getHuf());
        	values.put(CURENCY_KEYS[5], entry.getJpy());
        	values.put(CURENCY_KEYS[6], entry.getKwd());
        	values.put(CURENCY_KEYS[7], entry.getNok());
        	values.put(CURENCY_KEYS[8], entry.getSek());
        	values.put(CURENCY_KEYS[9], entry.getUsd());
        	values.put(CURENCY_KEYS[10], entry.getDkk());
        	values.put(CURENCY_KEYS[11], entry.getCzk());
        	values.put(CURENCY_KEYS[12], entry.getChf());
        	values.put(CURENCY_KEYS[13], entry.getCad());
        	values.put(CURENCY_KEYS[14], entry.getBam());
        	values.put(CURENCY_KEYS[15], entry.getAud());
        
        

        // Inserting Row
        db.delete(TABLE_CURENCIES, CURENCY_KEYS[0] + " = ?",
  				  new String[] { String.valueOf(1) });
        db.insert(TABLE_CURENCIES, null, values);
        db.close(); // Closing database connection
    }
    
    double getCurency(String columnName)
	{
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CURENCIES, new String[] { CURENCY_KEYS[0],
        		CURENCY_KEYS[1],
        		CURENCY_KEYS[2],
        		CURENCY_KEYS[3],
        		CURENCY_KEYS[4],
        		CURENCY_KEYS[5],
        		CURENCY_KEYS[6],
        		CURENCY_KEYS[7],
        		CURENCY_KEYS[8],
        		CURENCY_KEYS[9],
        		CURENCY_KEYS[10],
        		CURENCY_KEYS[11],
        		CURENCY_KEYS[12],
        		CURENCY_KEYS[13],
        		CURENCY_KEYS[14],
        		CURENCY_KEYS[15]
									 
									}, CURENCY_KEYS[0] + "=?",
								 new String[] { String.valueOf(1) }, null, null, null, null);
        double value = 0;
        if (cursor != null){
            cursor.moveToFirst();
        	value = cursor.getDouble(cursor.getColumnIndex(columnName));
        }
        // return list
        db.close();
        cursor.close();
        return value;
    }
    
}

