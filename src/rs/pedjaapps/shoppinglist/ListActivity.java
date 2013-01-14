package rs.pedjaapps.shoppinglist;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import java.util.*;
import rs.pedjaapps.shoppinglist.*;

import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ListActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private DatabaseHandler db = new DatabaseHandler(this);
	List<String> lists;
	ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
	
		db.open();
		
		lists = db.getAllListsNames();
		setNavigationMode();
		//String[] lists = {"sfds"};
		adapter = new ArrayAdapter<String>(getActionBarThemedContextCompat(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,  lists);
		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				adapter, this);
	}

	public void setNavigationMode(){
		if(lists.isEmpty()){
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		else{
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
	}
	
	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
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
		addListDialog();
			
		}	
		if (item.getItemId() == R.id.menu_edit)
		{
			
			editListDialog(getSupportActionBar().getSelectedNavigationIndex());
			

		}	
		
			if (item.getItemId() == R.id.menu_delete)
		{
			db.removeList(getSupportActionBar().getSelectedNavigationIndex());
			adapter.remove(adapter.getItem(getSupportActionBar().getSelectedNavigationIndex()));
			adapter.notifyDataSetChanged();
			
		}	
		
	return super.onOptionsItemSelected(item);

	}
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
			
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}

	private void editListDialog(final int position){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	final EditText input = new EditText(this);
        	input.setGravity(Gravity.CENTER_HORIZONTAL);
		    
			
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
									adapter.add(inputText);
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
						Toast.makeText(ListActivity.this, "List name cannot be empty!", Toast.LENGTH_LONG).show();
					}
					else if(db.listExists(inputText)){
						Toast.makeText(ListActivity.this, "List already exists.\nSelect diferent name!", Toast.LENGTH_LONG).show();
					}
					
					else{

						if(db.createList(inputText)!=db.NOTIFY_TABLE_CREATION_PROBLEM){
							lists = db.getAllListsNames();
							adapter.add(inputText);
							adapter.notifyDataSetChanged();
							setNavigationMode();
						}
						else{
							Toast.makeText(ListActivity.this, "Something went wrong.\nPlease try again", Toast.LENGTH_LONG).show();
						}
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
	
}
