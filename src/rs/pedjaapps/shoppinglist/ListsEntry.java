package rs.pedjaapps.shoppinglist;

public final class ListsEntry
{

	private final String name;
	private final int color;
	private final String date;
	private final String done;

	public ListsEntry(final String name, final int color, final String date, final String done)
	{
		this.name = name;
		this.color = color;
		this.date = date;
		this.done = done;

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
	
	public String getDone()
	{
		return done;
	}

}
