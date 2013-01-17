package rs.pedjaapps.shoppinglist;

import java.util.ArrayList;
import java.util.List;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.*;
import android.text.InputType;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.deaux.fan.FanView;

public class ItemsActivity extends SherlockActivity
{


	private DatabaseHandler db;
	FanView fan;
	ActionBar actionBar;
	private ListsAdapter sideAdapter;
	private ListView sideListView;

	private ItemsAdapter itemsAdapter;
	private ListView itemsListView;
	private static final int GET_CODE = 0;
	String listName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fan);
		Intent intent = getIntent();
	    listName = intent.getExtras().getString("name");

		db = new DatabaseHandler(this);
		fan = (FanView) findViewById(R.id.fan_view);
        fan.setViews(R.layout.activity_list, R.layout.side_list);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(listName);

		sideListView = (ListView) findViewById(R.id.list);
		sideAdapter = new ListsAdapter(this, R.layout.side_lists_row);

		sideListView.setAdapter(sideAdapter);

		for (final ListsEntry entry : getSideListEntries())
		{
			sideAdapter.add(entry);

		}
		sideListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
										long arg3)
				{
					ListsDatabaseEntry list = db.getList(position + 1);
					actionBar.setTitle(list.getName());
					sideMenu();
				}

			});
		
		itemsListView = (ListView) findViewById(R.id.list_items);
		itemsAdapter = new ItemsAdapter(this, R.layout.item_row);

		itemsListView.setAdapter(itemsAdapter);

		for (final ItemsEntry entry : getItemsEntries())
		{
			itemsAdapter.add(entry);

		}

	}

	private List<ItemsEntry> getItemsEntries()
	{

		final List<ItemsEntry> entries = new ArrayList<ItemsEntry>();
		//List<ItemsDatabaseEntry> dbEntry = db.getAllLists();
		//for (ListsDatabaseEntry e: dbEntry)
		//{
			entries.add(new ItemsEntry("test", 4.6, 3.0, listName, "kg", "eur", true));
		//}

		return entries;
	}
	
	private List<ListsEntry> getSideListEntries()
	{

		final List<ListsEntry> entries = new ArrayList<ListsEntry>();
		List<ListsDatabaseEntry> dbEntry = db.getAllLists();
		for (ListsDatabaseEntry e: dbEntry)
		{
			entries.add(new ListsEntry(e.getName(), e.getColor(), ""));
		}

		return entries;
	}

	private void sideMenu()
	{
		fan.showMenu();
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		// Restore the previously serialized current dropdown position.

	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// Serialize the current dropdown position.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		if (item.getItemId() == R.id.menu_add)
		{
			//addListDialog();
			Intent intent = new Intent(this, EditItemActivity.class);
			intent.putExtra("name", "");
			startActivityForResult(intent, GET_CODE);

		}	


		/*	if (item.getItemId() == R.id.menu_delete)
		 {
		 //db.removeList(getSupportActionBar().getSelectedNavigationIndex()+1);
		 adapter.remove(adapter.getItem(getSupportActionBar().getSelectedNavigationIndex()));
		 adapter.notifyDataSetChanged();

		 }	*/
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
		{
	        fan.showMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				db.addItem(new ItemsDatabaseEntry(data.getExtras().getString("name"), 
												  data.getExtras().getDouble("quantity"),
												  data.getExtras().getDouble("price"),
												  data.getExtras().getString("image"),
												  data.getExtras().getString("unit"),
												  data.getExtras().getString("curency"),
												  data.getExtras().getBoolean("done")
												  ), listName);
			}
		}

	}

}
