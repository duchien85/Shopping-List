package rs.pedjaapps.shoppinglist;

public class ListsDatabaseEntry
{

	//private variables
    int _id;
    String _name;
    String _table_name;
    int _color;
    
   
    
   

    // Empty constructor
    public ListsDatabaseEntry()
	{

    }
    // constructor
    public ListsDatabaseEntry(int id, String name, 
    				
    				String table_name, 
    				int color
    				
				 )
	{
        this._id = id;
        this._name = name;
        
        this._table_name = table_name;
        this._color = color;
       
    }

    // constructor
    public ListsDatabaseEntry(String name, 
    		
			String table_name, 
			int color
				 )
	{

    	this._name = name;
    	
        this._table_name = table_name;
        this._color = color;
      
    }
    // getting ID
    public int getID()
	{
        return this._id;
    }

    // setting id
    public void setID(int id)
	{
        this._id = id;
    }

	// getting Name
    public String getName()
	{
        return this._name;
    }

    // setting Name
    public void setName(String name)
	{
        this._name = name;
    }

    
    public String getTableName()
	{
        return this._table_name;
    }

    public void setTableName(String table_name)
	{
        this._table_name = table_name;
    }

    public int getColor()
	{
        return this._color;
    }

    public void setColor(int color)
	{
        this._color = color;
    }

}
