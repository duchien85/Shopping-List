package rs.pedjaapps.shoppinglist;

public final class ListsEntry
{

	private final String name;
	private final int color;
	private final String date;

	public ListsEntry(final String name, final int color, final String date)
	{
		this.name = name;
		this.color = color;
		this.date = date;

	}


	public String getName()
	{
		return name;
	}


	public String getDate()
	{
		return date;
	}

	public int getColor()
	{
		return color;
	}
	
	

}
