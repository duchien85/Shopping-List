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
import android.widget.EditText;
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
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lists);
		
		tv1 = (TextView)findViewById(R.id.tv1);
		ll = (LinearLayout)findViewById(R.id.ll1);
		db = new DatabaseHandler(this);
		
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

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
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
		entries.add(new ListsEntry(e.getName(), e.getColor(), e.getDate()));
		}
		
		return entries;
	}

	private String getDate(){
		return DateFormat.getDateTimeInstance().format(new Date());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_add) {
			addListDialog();
			
		}

		if (item.getItemId() == R.id.menu_clear) {
			deleteAllDialog();

		}

		return super.onOptionsItemSelected(item);

	}
	
	private void deleteAllDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
		
			builder.setTitle("Delete All Lists");
			builder.setMessage("Are you sure?");
			builder.setIcon(R.drawable.ic_menu_delete);
		
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
	


private void addListDialog(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.edit_list_layout, null);
		final RelativeLayout color = (RelativeLayout)view.findViewById(R.id.color);	
		newColor = Color.YELLOW;
		color.setBackgroundColor(newColor);
		final EditText input = (EditText)view.findViewById(R.id.name);
		
		color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				ColorSelectorDialog dialog = new ColorSelectorDialog(Lists.this, new OnColorChangedListener(){
					
					@Override
					public void colorChanged(int newColor) {
						
						color.setBackgroundColor(newColor);
						Lists.this.newColor = newColor;
					}
					
				}, Color.YELLOW);
				dialog.setTitle("Pick Color");
				dialog.show();
			}

			
			
		});
		
		builder.setTitle("Add List");
		builder.setMessage("Enter List Name");
		builder.setIcon(R.drawable.ic_menu_add);


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
					listsAdapter.add(new ListsEntry(inputText, newColor, date));
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
		final RelativeLayout color = (RelativeLayout)view.findViewById(R.id.color);	
		ListsDatabaseEntry dbList = db.getList(position);
		final String originalName = dbList.getName();
		newColor = dbList.getColor();
		color.setBackgroundColor(newColor);
		final EditText input = (EditText)view.findViewById(R.id.name);
		input.setText(dbList.getName());
		color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				ColorSelectorDialog dialog = new ColorSelectorDialog(Lists.this, new OnColorChangedListener(){
					
					@Override
					public void colorChanged(int newColor) {
					
						color.setBackgroundColor(newColor);
						Lists.this.newColor = newColor;
					}
					
				}, Color.YELLOW);
				dialog.setTitle("Pick Color");
				dialog.show();
			}

			
			
		});
		
		builder.setTitle("Edit List");
		builder.setMessage("Edit List Details");
		builder.setIcon(R.drawable.ic_menu_edit);
		

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
					listsAdapter.insert(new ListsEntry(inputText, newColor, date), position-1);
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
        
        menu.add("Edit")
            .setIcon(R.drawable.ic_menu_edit)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
       //Toast.makeText(Lists.this, "Got click: " + id, Toast.LENGTH_SHORT).show();
        editListDialog(id);
    	mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }
}

}
