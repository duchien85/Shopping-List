package rs.pedjaapps.shoppinglist;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import com.deaux.fan.*;
import com.google.ads.*;
import java.util.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ItemsActivity extends SherlockActivity {

	private DatabaseHandler db;
	FanView fan;
	ActionBar actionBar;
	private ListsAdapter sideAdapter;
	private ListView sideListView;

	private ItemsAdapter itemsAdapter;
	private ListView itemsListView;
	private static final int GET_CODE = 0;
	String listName;

	TextView tv1;
	LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		SharedPreferences sharedPrefs = PreferenceManager
			.getDefaultSharedPreferences(this);
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		tv1 = (TextView) findViewById(R.id.tv1);
		ll = (LinearLayout) findViewById(R.id.ll1);

		sideListView = (ListView) findViewById(R.id.list);
		sideAdapter = new ListsAdapter(this, R.layout.side_lists_row);

		sideListView.setAdapter(sideAdapter);

		for (final ListsEntry entry : getSideListEntries()) {
			sideAdapter.add(entry);

		}

		sideListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ListsDatabaseEntry list = db.getList(position + 1);
				actionBar.setTitle(list.getName());
				sideMenu();
			}

		});

		itemsListView = (ListView) findViewById(R.id.list_items);
		itemsAdapter = new ItemsAdapter(this, R.layout.item_row);

		itemsListView.setAdapter(itemsAdapter);

		for (final ItemsEntry entry : getItemsEntries()) {
			itemsAdapter.add(entry);

		}
		setUI();
		itemsListView.setOnItemClickListener(new OnItemClickListener(){

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				ItemsDatabaseEntry item = db.getItem(listName, position+1);
				
				if(item.getDone()==true){
					db.updateItem(new ItemsDatabaseEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), false),
							position+1, listName);
					itemsAdapter.remove(itemsAdapter.getItem(position));
					itemsAdapter.insert(new ItemsEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), false), position);
					itemsAdapter.notifyDataSetChanged();
					System.out.println(item.getDone());
				}
				else{
					db.updateItem(new ItemsDatabaseEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), true),
							position+1, listName);
					itemsAdapter.remove(itemsAdapter.getItem(position));
					itemsAdapter.insert(new ItemsEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), true), position);
					itemsAdapter.notifyDataSetChanged();
					System.out.println(item.getDone());
				}
				
			}
			
		});
	}

	private List<ItemsEntry> getItemsEntries() {

		final List<ItemsEntry> entries = new ArrayList<ItemsEntry>();
		List<ItemsDatabaseEntry> dbEntry = db.getAllItems(listName);
		for (ItemsDatabaseEntry e : dbEntry) {
			entries.add(new ItemsEntry(e.getName(), e.getQuantity(), e
					.getValue(), e.getImage(), e.getUnit(), e.getCurency(), e
					.getDone()));
		}

		return entries;
	}

	private List<ListsEntry> getSideListEntries() {

		final List<ListsEntry> entries = new ArrayList<ListsEntry>();
		List<ListsDatabaseEntry> dbEntry = db.getAllLists();
		for (ListsDatabaseEntry e : dbEntry) {
			entries.add(new ListsEntry(e.getName(), e.getColor(), ""));
		}

		return entries;
	}

	private void sideMenu() {
		fan.showMenu();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		boolean isLight = true;
		menu.add(1, 1, 1, "Add")
        .setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(2, 2, 2,"Clear")
    	.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			// addListDialog();
			Intent intent = new Intent(this, EditItemActivity.class);
			intent.putExtra("name", "");
			intent.putExtra("listName", listName);
			startActivityForResult(intent, GET_CODE);

		}

		if (item.getItemId() == 2) {
			deleteAllDialog();

		}
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			fan.showMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_CODE) {
			if (resultCode == RESULT_OK) {
				db.addItem(
						new ItemsDatabaseEntry(data.getExtras().getString(
								"name"),
								data.getExtras().getDouble("quantity"), data
										.getExtras().getDouble("price"), data
										.getExtras().getString("image"), data
										.getExtras().getString("unit"), data
										.getExtras().getString("curency"), data
										.getExtras().getBoolean("done")),
						listName);
				itemsAdapter.add(new ItemsEntry(data.getExtras().getString(
						"name"), data.getExtras().getDouble("quantity"), data
						.getExtras().getDouble("price"), data.getExtras()
						.getString("image"),
						data.getExtras().getString("unit"), data.getExtras()
								.getString("curency"), data.getExtras()
								.getBoolean("done")));
				itemsAdapter.notifyDataSetChanged();
				setUI();
			}
		}

	}

	private void setUI() {
		if (itemsAdapter.isEmpty() == false) {
			tv1.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
		} else {
			tv1.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
		}
	}

	private void deleteAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Delete All Itemss");
		builder.setMessage("Are you sure?");
		builder.setIcon(R.drawable.ic_menu_delete);

		builder.setPositiveButton(getResources()
				.getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<ItemsDatabaseEntry> entry = db
								.getAllItems(listName);
						for (ItemsDatabaseEntry e : entry) {
							db.deleteItem(e, listName);
						}
						itemsAdapter.clear();
						itemsAdapter.notifyDataSetChanged();
						setUI();

					}
				});

		builder.setNegativeButton(
				getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		AlertDialog alert = builder.create();

		alert.show();
	}

}
