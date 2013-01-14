package rs.pedjaapps.shoppinglist;

public class ItemsDatabaseEntry
{

	//private variables
    int _id;
    String _name;
    int _quantity;
    int _value;
    int _totalValue;
    String _image;
    String _unit;
    String _date;
    boolean _done;

    // Empty constructor
    public ItemsDatabaseEntry()
	{

    }
    // constructor
    public ItemsDatabaseEntry(int id, String name, 
    				int quantity, int value,
    				int totalValue,
    				String image, 
    				String unit,
    				String date,
    				boolean done
				 )
	{
        this._id = id;
        this._name = name;
        this._quantity = quantity;
        this._value = value;
        this._totalValue = totalValue;
        this._image = image;
        this._unit = unit;
        this._date = date;
        this._done = done;
       
    }

    // constructor
    public ItemsDatabaseEntry(String name, 
			int quantity, int value,
			int totalValue,
			String image, 
			String unit,
			String date,
			boolean done
				 )
	{

        this._name = name;
        this._quantity = quantity;
        this._value = value;
        this._totalValue = totalValue;
        this._image = image;
        this._unit = unit;
        this._date = date;
        this._done = done;
      
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

    
    public int getQuantity()
	{
        return this._quantity;
    }

    public void setQuantity(int quantity)
	{
        this._quantity = quantity;
    }

    public int getValue()
	{
        return this._value;
    }

    public void setValue(int value)
	{
        this._value = value;
    }
    
    public int getTotalValue()
	{
        return this._totalValue;
    }

    public void setTotalValue(int totalValue)
	{
        this._totalValue = totalValue;
    }
    
    public String getImage()
	{
        return this._image;
    }

    
    public void setImage(String image)
	{
        this._image = image;
    }
    
    public String getUnit()
	{
        return this._unit;
    }

    
    public void setUnit(String unit)
	{
        this._unit = unit;
    }

    public String getDate()
	{
        return this._date;
    }

    
    public void setDate(String date)
	{
        this._date = date;
    }
    
    public boolean getDone()
	{
        return this._done;
    }

    
    public void setDone(boolean done)
	{
        this._done = done;
    }
    
}
