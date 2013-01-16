package rs.pedjaapps.shoppinglist;

public final class ItemsEntry
{


	private final String name;
    private final int quantity;
    private final int value;
    private final String image;
    private final String unit;
    private String curency;
    private boolean done;

	public ItemsEntry(final String name,
	final int quantity,
	final int value, final String image,
    final String unit, final String curency,
    final boolean done	)
	{
		this.name = name;
		this.quantity = quantity;
		this.value = value;
		this.image = image;
		this.unit = unit;
		this.curency = curency;
		this.done = done;

	}


	public String getName()
	{
		return name;
	}


	public int getQuantity()
	{
		return quantity;
	}

	public int geValue()
	{
		return value;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public String getUnit()
	{
		return unit;
	}
	
	public String getCurency()
	{
		return curency;
	}
	
	public boolean getDone()
	{
		return done;
	}

}
