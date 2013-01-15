package rs.pedjaapps.shoppinglist;

public class ItemsDatabaseEntry
{

	//private variables
    int _id;
    String _name;
    int _quantity;
    int _value;
    String _image;
    String _unit;
    String _curency;
    boolean _done;

    // Empty constructor
    public ItemsDatabaseEntry()
	{

    }
    // constructor
    public ItemsDatabaseEntry(int id, String name, 
    				int quantity, int value,
    				String image, 
    				String unit,
    				String curency,
    				boolean done
				 )
	{
        this._id = id;
        this._name = name;
        this._quantity = quantity;
        this._value = value;
        this._image = image;
        this._unit = unit;
        this._curency = curency;
        this._done = done;
       
    }

    // constructor
    public ItemsDatabaseEntry(String name, 
			int quantity, int value,
			int totalValue,
			String image, 
			String unit,
			String curency,
			boolean done
				 )
	{

        this._name = name;
        this._quantity = quantity;
        this._value = value;
        this._image = image;
        this._unit = unit;
        this._curency = curency;
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

    public String getCurency()
	{
        return this._curency;
    }

    
    public void setCurency(String curency)
	{
        this._curency = curency;
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
