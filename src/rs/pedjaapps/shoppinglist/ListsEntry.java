package rs.pedjaapps.shoppinglist;

public final class ListsEntry
{

	private final String name;
	private final String date_time;
	private final int color;

	public ListsEntry(final String name, final String date_time, final int color)
	{
		this.name = name;
		this.date_time = date_time;
		this.color = color;

	}


	public String getName()
	{
		return name;
	}


	public String getDateTime()
	{
		return date_time;
	}

	public int getColor()
	{
		return color;
	}
	
	

}