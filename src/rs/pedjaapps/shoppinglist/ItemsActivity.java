package rs.pedjaapps.shoppinglist;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.*;

import com.actionbarsherlock.app.*;
import com.google.ads.*;

import java.util.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class ItemsActivity extends SherlockActivity {

	private DatabaseHandler db;
//	FanView fan;
	ActionBar actionBar;
	private ListsAdapter sideAdapter;
	private ListView sideListView;

	private ItemsAdapter itemsAdapter;
	private ListView itemsListView;
	private static final int ADD_ITEM = 0;
	private static final int EDIT_ITEM = 1;
	String listName;

	TextView tv1;
	LinearLayout ll;
	boolean isLight;
	String theme;
	private ViewGroup header;
	int pos;
	
	ActionMode aMode;
	TextView totalText;
	SharedPreferences sharedPrefs;
	RelativeLayout container;
	LinearLayout sideContainer;
	SlidingMenu	menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		theme = sharedPrefs.getString("theme", "light");
		if(theme.equals("light")){
			isLight = true;
			setTheme(R.style.Theme_Sherlock_Light);
			}
			else if(theme.equals("dark")){
				setTheme(R.style.Theme_Sherlock);
				isLight = false;
			}
			else if(theme.equals("light_dark_action_bar")){
				setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
				isLight = true;
			}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Intent intent = getIntent();
		listName = intent.getExtras().getString("name");

		db = new DatabaseHandler(this);
		//fan = (FanView) findViewById(R.id.fan_view);
		//fan.setViews(R.layout.activity_list, R.layout.side_list);
		
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.side_list);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(listName);
		
		ImageView plus = (ImageView)findViewById(R.id.action_plus);
		plus.setImageResource(isLight ? R.drawable.add_light : R.drawable.add_dark);
		
		container = (RelativeLayout)findViewById(R.id.item_container);
		container.setBackgroundResource(isLight ? R.drawable.background_holo_light : R.drawable.background_holo_dark);
		
		sideContainer = (LinearLayout)findViewById(R.id.side_container);
		sideContainer.setOnClickListener(fanListener);
		
		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		tv1 = (TextView) findViewById(R.id.tv1);
		ll = (LinearLayout) findViewById(R.id.ll1);

		sideListView = (ListView) menu.findViewById(R.id.list);
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
				listName = list.getName();
				actionBar.setTitle(listName);
				menu.showContent();
				itemsAdapter.clear();
				for (final ItemsEntry entry : getItemsEntries()) {
					itemsAdapter.add(entry);
					itemsAdapter.notifyDataSetChanged();
				}
				setUI();
				totalText.setText(calculateTotal());
			}

		});

		itemsListView = (ListView) findViewById(R.id.list_items);
		LayoutInflater inflater = getLayoutInflater();
		header = (ViewGroup)inflater.inflate(R.layout.items_header, itemsListView, false);
		
		itemsListView.addHeaderView(header, null, false);
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
				
				ItemsDatabaseEntry item = db.getItem(listName, position);
			
				if(item.getDone()==true){
					db.updateItem(new ItemsDatabaseEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), false),
							position, listName);
					itemsAdapter.remove(itemsAdapter.getItem(position-1));
					itemsAdapter.insert(new ItemsEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), false), position-1);
					itemsAdapter.notifyDataSetChanged();
				}
				else{
					db.updateItem(new ItemsDatabaseEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), true),
							position, listName);
					itemsAdapter.remove(itemsAdapter.getItem(position-1));
					itemsAdapter.insert(new ItemsEntry(item.getName(), 
							item.getQuantity(), 
							item.getValue(), item.getImage(), item.getUnit(), item.getCurency(), true), position-1);
					itemsAdapter.notifyDataSetChanged();
				}
				totalText.setText(calculateTotal());
			}
			
		});
		
		itemsListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if(aMode!=null){
					aMode.finish();
				}
				aMode = startActionMode(new ItemActionMode(position));
				
				return true;
			}

			
			
		});
		
		totalText = (TextView)findViewById(R.id.total);
		totalText.setTextColor(Color.GREEN);
		totalText.setText(calculateTotal());
		
	}
	
	private OnClickListener fanListener =
	        new OnClickListener() {

				@Override
				public void onClick(View v) {
					menu.showContent();
					
				}
	            
	        };
	
	public String calculateTotal(){
		String curency = sharedPrefs.getString("curency" ,"eur");
		CurencyConverter converter = new CurencyConverter(this);
		List<ItemsDatabaseEntry> items = db.getAllItems(listName);
		double total = 0.00;
		double bought = 0.00;
		for(ItemsDatabaseEntry e: items){
			double temp = converter.convert(e.getValue(), e.getCurency());
			total+=temp;
			if(e.getDone()==false){
				bought+=temp;
			}
		}
		
		return String.format("%.2f",total)+"/"+String.format("%.2f",bought)+curency.toUpperCase();
		
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
			entries.add(new ListsEntry(e.getName(), e.getColor(), "", ""));
		}

		return entries;
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
		if(theme.equals("light")){
			isLight = true;
			}
			else if(theme.equals("dark")){
				isLight = false;
			}
			else if(theme.equals("light_dark_action_bar")){
				isLight = false;
			}
		menu.add(1, 1, 1, getResources().getString(R.string.add))
        .setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(2, 2, 2,getResources().getString(R.string.clear))
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
			startActivityForResult(intent, ADD_ITEM);

		}

		if (item.getItemId() == 2) {
			deleteAllDialog();

		}
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			menu.showContent();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode ==ADD_ITEM ) {
				addItem(data);
				System.out.println("add");
			}
			else if (requestCode ==EDIT_ITEM ) {
				editItem(data);
			}
		}

	}

	public void addItem(Intent data){
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
		totalText.setText(calculateTotal());
		if(db.historyItemExists(data.getExtras().getString("name"))==false){
			db.addHistory(data.getExtras().getString("name"));
		}
	}
	
	private void editItem(Intent data){
		
		
			db.updateItem(new ItemsDatabaseEntry(data.getExtras().getString(
					"name"),
					data.getExtras().getDouble("quantity"), data
							.getExtras().getDouble("price"), data
							.getExtras().getString("image"), data
							.getExtras().getString("unit"), data
							.getExtras().getString("curency"), data
							.getExtras().getBoolean("done")),
					pos, listName);
			itemsAdapter.remove(itemsAdapter.getItem(pos-1));
			itemsAdapter.insert(new ItemsEntry(data.getExtras().getString(
					"name"),
					data.getExtras().getDouble("quantity"), data
							.getExtras().getDouble("price"), data
							.getExtras().getString("image"), data
							.getExtras().getString("unit"), data
							.getExtras().getString("curency"), data
							.getExtras().getBoolean("done")), pos-1);
			itemsAdapter.notifyDataSetChanged();
			totalText.setText(calculateTotal());
		if(db.historyItemExists(data.getExtras().getString("name"))==false){
			db.addHistory(data.getExtras().getString("name"));
		}
	};
	
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

		builder.setTitle(getResources().getString(R.string.delete_all_items));
		builder.setMessage(getResources().getString(R.string.are_you_sure));
		builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);

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
						totalText.setText(calculateTotal());
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

	private final class ItemActionMode implements ActionMode.Callback {
		int id;
		public ItemActionMode(int id){
			this.id = id;
		}
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        
	      //getSupportMenuInflater().inflate(R.menu.list_action, menu);
	    	if(theme.equals("light")){
				isLight = true;
				}
				else if(theme.equals("dark")){
					isLight = false;
				}
				else if(theme.equals("light_dark_action_bar")){
					isLight = false;
				}
			menu.add(1, 1, 1, getResources().getString(R.string.edit))
	        .setIcon(isLight ? R.drawable.edit_light : R.drawable.edit_dark)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(2, 2, 2,getResources().getString(R.string.delete))
	    	.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark)
	    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    

	        
	        return true;
	    }

	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }

	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	
	    	if(item.getItemId()==1){
	    		ItemsDatabaseEntry entry = db.getItem(listName, id);
	    		Intent intent = new Intent(ItemsActivity.this, EditItemActivity.class);
				intent.putExtra("name", listName);
				intent.putExtra("listName", listName);
				intent.putExtra("itemName", entry.getName());
				intent.putExtra("quantity", entry.getQuantity());
				intent.putExtra("price", entry.getValue()/entry.getQuantity());
				intent.putExtra("image", entry.getImage());
				intent.putExtra("unit", entry.getUnit());
				intent.putExtra("curency", entry.getCurency());
				intent.putExtra("done", entry.getDone());
				startActivityForResult(intent, EDIT_ITEM);
				pos = id;
	    	}
	    	if(item.getItemId()==2){
	    		deleteItemDialog(id);
	    	}
	    	mode.finish();
	        return true;
	    }

	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    }
	}
	
	private void deleteItemDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(R.string.delete_item));
		builder.setMessage(getResources().getString(R.string.are_you_sure));
		builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);

		builder.setPositiveButton(getResources()
				.getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ItemsDatabaseEntry entry = db
								.getItem(listName, id);
						
							db.deleteItem(entry, listName);
						
						itemsAdapter.remove(itemsAdapter.getItem(id-1));
						itemsAdapter.notifyDataSetChanged();
						setUI();
						totalText.setText(calculateTotal());
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
