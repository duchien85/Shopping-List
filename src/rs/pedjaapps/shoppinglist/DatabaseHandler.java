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
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "ShoppingList.db";

    // table names
    private static final String TABLE_LISTS = "lists_table";
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

    void createItemTable(String name){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String CREATE_ITEM_TABLE = "CREATE TABLE " + name + "("
    			+ KEY_ITEM_ROWID + " INTEGER PRIMARY KEY,"
    			+ KEY_ITEM_NAME + " TEXT,"
    			+ KEY_ITEM_QUANTITY + " INTEGER,"
    			+ KEY_ITEM_VALUE + " INTEGER," 
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
									  Integer.parseInt(cursor.getString(2)),
									  cursor.getInt(3),
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
   
    private boolean intToBool(int i){
    	if(i == 0){
    		return false;
    	}
    	else{
    		return true;
    	}
    }
    
}

