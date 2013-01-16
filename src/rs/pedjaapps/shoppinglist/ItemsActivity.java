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

public class ItemsActivity extends SherlockActivity {

	
	private DatabaseHandler db;
	FanView fan;
	ActionBar actionBar;
	private ListsAdapter sideAdapter;
	private ListView sideListView;

	private ListsAdapter itemsAdapter;
	private ListView itemsListView;
	private static final int GET_CODE = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fan);
		Intent intent = getIntent();
		String name = intent.getExtras().getString("name");
		
		db = new DatabaseHandler(this);
		fan = (FanView) findViewById(R.id.fan_view);
        fan.setViews(R.layout.activity_list, R.layout.side_list);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(name);
		
		sideListView = (ListView) findViewById(R.id.list);
		sideAdapter = new ListsAdapter(this, R.layout.side_lists_row);

		sideListView.setAdapter(sideAdapter);

		for (final ListsEntry entry : getSideListEntries()) {
			sideAdapter.add(entry);
			
		}
		sideListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				ListsDatabaseEntry list = db.getList(position+1);
				actionBar.setTitle(list.getName());
				sideMenu();
			}
			
		});
		
	}

	private List<ListsEntry> getSideListEntries() {

		final List<ListsEntry> entries = new ArrayList<ListsEntry>();
		List<ListsDatabaseEntry> dbEntry = db.getAllLists();
		for(ListsDatabaseEntry e: dbEntry){
		entries.add(new ListsEntry(e.getName(), e.getColor(), ""));
		}
		
		return entries;
	}
	
	private void sideMenu(){
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
		getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

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
		if(item.getItemId()==android.R.id.home || item.getItemId()==0){
	        fan.showMenu();
			return true;
		}
		
	return super.onOptionsItemSelected(item);

	}
	
	

	private void editListDialog(final int position){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	final EditText input = new EditText(this);
        	input.setGravity(Gravity.CENTER_HORIZONTAL);
        	input.setSingleLine(true);
    		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    		input.setSelectAllOnFocus(true);
    		
				builder.setTitle("Rename List");
				builder.setMessage("Enter new List Name");
				builder.setIcon(R.drawable.ic_menu_edit);
				//input.setText(db.getList(position));
			
					
					builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								
	                            String inputText = input.getText().toString();
					            
							    if(inputText.length()==0){
									Toast.makeText(ItemsActivity.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
								}
								/*else if(db.listExists(inputText)){
									Toast.makeText(ListActivity.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
								}
								else{
									adapter.remove(db.getList(position));
									db.setListName(position, inputText);
								lists = db.getAllListsNames();
									adapter.insert(inputText, position-1);
									adapter.notifyDataSetChanged();
									
								}*/
								
							}
						});
						
	        	builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					

				}
			});
					builder.setView(input);

					AlertDialog alert = builder.create();

					alert.show();
	}
	
	private void addListDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setGravity(Gravity.CENTER_HORIZONTAL);

		
			builder.setTitle("Add List");
			builder.setMessage("Enter List Name");
			builder.setIcon(R.drawable.ic_menu_add);
	

		builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{

					String inputText = input.getText().toString();

					if(inputText.length()==0){
						Toast.makeText(ItemsActivity.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
					}
					/**else if(db.listExists(inputText)){
						Toast.makeText(ListActivity.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
					}*/
					
					else{

						/*if(db.createList(inputText)!=db.NOTIFY_TABLE_CREATION_PROBLEM){
							lists = db.getAllListsNames();
							adapter.add(inputText);
							adapter.notifyDataSetChanged();
							setNavigationMode();
						}
						else{
							Toast.makeText(ListActivity.this, "Something went wrong.\nPlease try again", Toast.LENGTH_LONG).show();
						}*/
					}
				}
			});

		builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{


				}
			});
		builder.setView(input);

		AlertDialog alert = builder.create();

		alert.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  
	  if (requestCode == GET_CODE){
	   if (resultCode == RESULT_OK) {
		   
	   }
	  }
	
	   }
	
}
