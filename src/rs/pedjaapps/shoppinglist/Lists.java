package rs.pedjaapps.shoppinglist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lists);
		db = new DatabaseHandler(this);
		db.open();
		db.createList("inputText", Color.RED);
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
		//lists = db.getAllListsNames();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		/*listsListView = (ListView) findViewById(R.id.list);
		listsAdapter = new ListsAdapter(this, R.layout.lists_row);

		listsListView.setAdapter(listsAdapter);

		for (final ListsEntry entry : getListsEntries()) {
			listsAdapter.add(entry);
		}*/

	}

	private List<ListsEntry> getListsEntries() {

		final List<ListsEntry> entries = new ArrayList<ListsEntry>();

		entries.add(new ListsEntry("test", "25.05.2005", 0xff6500ff));

		/*
		 * for (TimesEntry t : times) { entries.add(new
		 * TISEntry(String.valueOf(t.getFreq()/1000)+"Mhz", hrTime(t.getTime()),
		 * String.valueOf(t.getTime()*100/totalTime) + "%",
		 * (int)(t.getTime()*100/totalTime)));
		 * System.out.println(hrTime(t.getTime())); }
		 */

		return entries;
	}

	private String hrTime(long time) {

		String timeString;
		String s = String.valueOf((int) ((time / 100) % 60));
		String m = String.valueOf((int) ((time / (100 * 60)) % 60));
		String h = String.valueOf((int) ((time / (100 * 3600)) % 24));
		String d = String.valueOf((int) (time / (100 * 60 * 60 * 24)));
		StringBuilder builder = new StringBuilder();
		if (!d.equals("0")) {
			builder.append(d + "d:");

		}
		if (!h.equals("0")) {
			builder.append(h + "h:");

		}
		if (!m.equals("0")) {
			builder.append(m + "m:");

		}

		builder.append(s + "s");

		timeString = builder.toString();
		return timeString;

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

		/*if (item.getItemId() == R.id.menu_delete) {
			db.removeList(getSupportActionBar().getSelectedNavigationIndex() + 1);
			adapter.remove(adapter.getItem(getSupportActionBar()
					.getSelectedNavigationIndex()));
			adapter.notifyDataSetChanged();

		}*/

		return super.onOptionsItemSelected(item);

	}
	
	/*private void editListDialog(final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	input.setGravity(Gravity.CENTER_HORIZONTAL);
    	input.setSingleLine(true);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		input.setSelectAllOnFocus(true);
		
			builder.setTitle("Rename List");
			builder.setMessage("Enter new List Name");
			builder.setIcon(R.drawable.ic_menu_edit);
			input.setText(db.getList(position));
		
				
				builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							
                            String inputText = input.getText().toString();
				            
						    if(inputText.length()==0){
								Toast.makeText(ListActivity.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
							}
							else if(db.listExists(inputText)){
								Toast.makeText(ListActivity.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
							}
							else{
								adapter.remove(db.getList(position));
								db.setListName(position, inputText);
							lists = db.getAllListsNames();
								adapter.insert(inputText, position-1);
								adapter.notifyDataSetChanged();
								
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
}*/

private void addListDialog(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.edit_list_layout, null);
		final RelativeLayout color = (RelativeLayout)view.findViewById(R.id.color);	
		color.setBackgroundColor(Color.YELLOW);
		final EditText input = (EditText)view.findViewById(R.id.name);
		
		color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ColorSelectorDialog dialog = new ColorSelectorDialog(Lists.this, new OnColorChangedListener(){
					
					@Override
					public void colorChanged(int newColor) {
						// TODO Auto-generated method stub
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

				/*if(inputText.length()==0){
					Toast.makeText(Lists.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
				}
				else if(db.listExists(inputText)){
					Toast.makeText(Lists.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
				}
				
				else{*/

					if(db.createList(inputText, newColor)!=db.NOTIFY_TABLE_CREATION_PROBLEM){
						//lists = db.getAllListsNames();
						listsAdapter.add(new ListsEntry(inputText, "24.12.2043", newColor));
						listsAdapter.notifyDataSetChanged();
					}
					/*else{
						Toast.makeText(Lists.this, "Something went wrong.\nPlease try again", Toast.LENGTH_LONG).show();
					}*/
				//}
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

}
