package rs.pedjaapps.shoppinglist;

public class ListsDatabaseEntry
{

	//private variables
    int _id;
    String _name;
    int _color;
    String _date;
    
   
    
   

    // Empty constructor
    public ListsDatabaseEntry()
	{

    }
    // constructor
    public ListsDatabaseEntry(int id, String name, int color, String date
    				
				 )
	{
        this._id = id;
        this._name = name;
        this._color = color;
        this._date = date;
       
    }

    // constructor
    public ListsDatabaseEntry(String name, 
			int color, String date
				 )
	{

    	this._name = name;
        this._color = color;
        this._date = date;
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

    public int getColor()
	{
        return this._color;
    }

    public void setColor(int color)
	{
        this._color = color;
    }
    
    public String getDate()
	{
        return this._date;
    }

    // setting Name
    public void setDate(String date)
	{
        this._date = date;
    }

}
