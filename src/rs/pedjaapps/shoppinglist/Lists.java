package rs.pedjaapps.shoppinglist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.deaux.fan.FanView;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import de.devmil.common.ui.color.*;
import de.devmil.common.ui.color.ColorSelectorDialog.OnColorChangedListener;

public class Lists extends SherlockActivity {

	private ListsAdapter listsAdapter;
	private ListView listsListView;
	private ActionBar actionBar;
	private DatabaseHandler db;
	List<String> lists;
	int newColor;
	TextView tv1;
	LinearLayout ll;
	ActionMode aMode;
	boolean isLight;
	String theme;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPrefs = PreferenceManager
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
		setContentView(R.layout.lists);
		
		new SyncCurencies(this).execute();
		//System.out.println(new CurencyConverter().convert(this, 20, "usd"));
		tv1 = (TextView)findViewById(R.id.tv1);
		ll = (LinearLayout)findViewById(R.id.ll1);
		db = new DatabaseHandler(this);
		
		ImageView plus = (ImageView)findViewById(R.id.action_plus);
		
		
		
		plus.setImageResource(isLight ? R.drawable.add_light : R.drawable.add_dark);
		
		/*
		 * This method backups database to sdcard for debuging purposes
		 * Remove it in final release
		 * */
	try {
	        File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();

	        if (sd.canWrite()) {
	            String currentDBPath = "//data//rs.pedjaapps.shoppinglist//databases//ShoppingList.db";
	            String backupDBPath = "ShoppingList.db";
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);

	            if (currentDB.exists()) {
	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                System.out.println("sdfsdfsdfdsfdsfdsfs");
	            }
	        }
	    } catch (Exception e) {
	    }
		
		actionBar = getSupportActionBar();

		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		listsListView = (ListView) findViewById(R.id.list);
		listsAdapter = new ListsAdapter(this, R.layout.lists_row);

		listsListView.setAdapter(listsAdapter);

		for (final ListsEntry entry : getListsEntries()) {
			listsAdapter.add(entry);
			setUI();
		}

		listsListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				ListsDatabaseEntry list = db.getList(position+1);
				Intent intent = new Intent(Lists.this, ItemsActivity.class);
				intent.putExtra("name", list.getName());
				startActivity(intent);
			}
			
		});
		listsListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if(aMode!=null){
					aMode.finish();
				}
				aMode = startActionMode(new ListActionMode(position+1));
				return true;
			}

			
			
		});
		
	}

	public void onResume(){
		listsAdapter.clear();
		for (final ListsEntry entry : getListsEntries()) {
			listsAdapter.add(entry);
			setUI();
		}
		super.onResume();
	}
	
	private  void setUI(){
		if(listsAdapter.isEmpty()==false){
			tv1.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
		}
		else{
			tv1.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
		}
	}
	
	private List<ListsEntry> getListsEntries() {

		final List<ListsEntry> entries = new ArrayList<ListsEntry>();
		List<ListsDatabaseEntry> dbEntry = db.getAllLists();
		for(ListsDatabaseEntry e: dbEntry){
		entries.add(new ListsEntry(e.getName(), e.getColor(), e.getDate(), calculateDone(e.getName())));
		}
		
		return entries;
	}

	private String calculateDone(String listName){
		List<ItemsDatabaseEntry> items = db.getAllItems(listName);
		int done = 0;
		int all = 0;
		for(ItemsDatabaseEntry e: items){
			if(e.getDone()){
				done++;
			}
			all++;
		}
		return done+"/"+all +" items bought";
	}
	
	private String getDate(){
		return DateFormat.getDateTimeInstance().format(new Date());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(theme.equals("light")){
			isLight = true;
			}
			else if(theme.equals("dark")){
				isLight = false;
			}
			else if(theme.equals("light_dark_action_bar")){
				isLight = false;
			}

        menu.add(1, 1, 1, "Add")
            .setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(2, 2, 2,"Clear")
        	.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(3, 3, 3,"Settings")
    	.setIcon(isLight ? R.drawable.settings_light : R.drawable.settings_dark)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case 1:
			addListDialog();
			return true;
		
		case 2:
			deleteAllDialog();
			return true;
		
		case 3:
			Intent intent = new Intent(Lists.this, Preferences.class);
			startActivity(intent);
			return true;
		}
		

		

		return super.onOptionsItemSelected(item);

	}
	
	private void deleteAllDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
		
			builder.setTitle("Delete All Lists");
			builder.setMessage("Are you sure?");
			builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);
		
				builder.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							List<ListsDatabaseEntry> entry = db.getAllLists();
							for(ListsDatabaseEntry e : entry){
								db.deleteList(e);
							}
							listsAdapter.clear();
							listsAdapter.notifyDataSetChanged();
                            setUI();
							
						}
					});
					
        	builder.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				

			}
		});
				

				AlertDialog alert = builder.create();

				alert.show();
}
	
	private void deleteListDialog(final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
		final ListsDatabaseEntry dbList = db.getList(position);
			builder.setTitle("Delete "+dbList.getName());
			builder.setMessage("Are you sure?");
			builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);
		
				builder.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							db.deleteList(dbList);
							
							listsAdapter.remove(listsAdapter.getItem(position-1));
							listsAdapter.notifyDataSetChanged();
                            setUI();
							
						}
					});
					
        	builder.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				

			}
		});
				

				AlertDialog alert = builder.create();

				alert.show();
}

private void addListDialog(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.edit_list_layout, null);
		final Button color = (Button)view.findViewById(R.id.color);	
		newColor = Color.YELLOW;
		color.setTextColor(newColor);
		final EditText input = (EditText)view.findViewById(R.id.name);
		
		color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				ColorSelectorDialog dialog = new ColorSelectorDialog(Lists.this, new OnColorChangedListener(){
					
					@Override
					public void colorChanged(int newColor) {
						
						color.setTextColor(newColor);
						Lists.this.newColor = newColor;
					}
					
				}, Color.YELLOW);
				dialog.setTitle("Pick Color");
				dialog.show();
			}

			
			
		});
		
		builder.setTitle("Add List");
		builder.setMessage("Enter List Name");
		builder.setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark);


	builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				String inputText = input.getText().toString();

				if(inputText.length()==0){
					Toast.makeText(Lists.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
				}
				else if(db.listExists(inputText)){
					Toast.makeText(Lists.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
				}
				
				else{
					String date = getDate();
					db.addList(new ListsDatabaseEntry(inputText, newColor, date));
					listsAdapter.add(new ListsEntry(inputText, newColor, date, calculateDone(inputText)));
					listsAdapter.notifyDataSetChanged();
					
				}
				setUI();
			}
		});

	builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{


			}
		});
	builder.setView(view);

	AlertDialog alert = builder.create();

	alert.show();
}

private void editListDialog(final int position){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.edit_list_layout, null);
		final Button color = (Button)view.findViewById(R.id.color);	
		ListsDatabaseEntry dbList = db.getList(position);
		final String originalName = dbList.getName();
		newColor = dbList.getColor();
		color.setTextColor(newColor);
		final EditText input = (EditText)view.findViewById(R.id.name);
		input.setText(dbList.getName());
		color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				ColorSelectorDialog dialog = new ColorSelectorDialog(Lists.this, new OnColorChangedListener(){
					
					@Override
					public void colorChanged(int newColor) {
					
						color.setTextColor(newColor);
						Lists.this.newColor = newColor;
					}
					
				}, Color.YELLOW);
				dialog.setTitle("Pick Color");
				dialog.show();
			}

			
			
		});
		
		builder.setTitle("Edit List");
		builder.setMessage("Edit List Details");
		builder.setIcon(isLight ? R.drawable.edit_light : R.drawable.edit_dark);
		

	builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				String inputText = input.getText().toString();

				if(inputText.length()==0){
					Toast.makeText(Lists.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
				}
				/*else if(db.listExists(inputText)){
					Toast.makeText(Lists.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
				}*/
				
				else{
					String date = getDate();
					System.out.println(db.updateList(new ListsDatabaseEntry(inputText, newColor, date), position, originalName));
					listsAdapter.remove(listsAdapter.getItem(position-1));
					listsAdapter.insert(new ListsEntry(inputText, newColor, date, calculateDone(inputText)), position-1);
					listsAdapter.notifyDataSetChanged();
					
				}
				setUI();
			}
		});

	builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{


			}
		});
	builder.setView(view);

	AlertDialog alert = builder.create();

	alert.show();
}

private final class ListActionMode implements ActionMode.Callback {
	int id;
	public ListActionMode(int id){
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
		menu.add(1, 1, 1, "Edit")
        .setIcon(isLight ? R.drawable.edit_light : R.drawable.edit_dark)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(2, 2, 2,"Delete")
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
    		editListDialog(id);
    	}
    	if(item.getItemId()==2){
    		deleteListDialog(id);
    	}
    	mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }
}

}
