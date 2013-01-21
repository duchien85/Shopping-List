package rs.pedjaapps.shoppinglist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.*;
import android.text.InputType;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.deaux.fan.FanView;

public class EditItemActivity extends SherlockActivity {

	
	private DatabaseHandler db;
	FanView fan;
	ActionBar actionBar;

	private ListsAdapter itemsAdapter;
	private ListView itemsListView;
	private EditText title;
	private EditText quantity;
	private ImageView plus;
	private ImageView minus;
	private Spinner unit;
	private ImageButton image;
	private EditText price;
	private Spinner curency;
	private Handler repeatUpdateHandler = new Handler();
	private boolean mAutoIncrement = false;
	private boolean mAutoDecrement = false;
	private int REP_DELAY = 50;
	private static final int SELECT_PHOTO = 100;
	private String imageUri;
	private String unitValue;
	private String curencyValue;
	String itemName;
	String listName;
	boolean isEdit;
	ImageView img;
	boolean done;
	
	class RptUpdater implements Runnable {
	    public void run() {
	        if( mAutoIncrement ){
	            increment();
	            repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        } else if( mAutoDecrement ){
	            decrement();
	            repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        }
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String theme = sharedPrefs.getString("theme", "light");
		if(theme.equals("light")){
			setTheme(R.style.Theme_Sherlock_Light);
			}
			else if(theme.equals("dark")){
				setTheme(R.style.Theme_Sherlock);
			}
			else if(theme.equals("light_dark_action_bar")){
				setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
				
			}
		setContentView(R.layout.item_edit);
		db = new DatabaseHandler(this);
		title = (EditText)findViewById(R.id.item_title);
		quantity = (EditText)findViewById(R.id.item_quantity);
		plus = (ImageView)findViewById(R.id.plus);
		minus = (ImageView)findViewById(R.id.minus);
		unit = (Spinner)findViewById(R.id.item_unit);
		image = (ImageButton)findViewById(R.id.image);
		price = (EditText)findViewById(R.id.item_price);
		curency = (Spinner)findViewById(R.id.curency);
		img = (ImageView)findViewById(R.id.imageView1);
		actionBar = getSupportActionBar();
		Intent intent = getIntent();
		itemName = intent.getExtras().getString("name");
		listName = intent.getExtras().getString("listName");
		System.out.println(listName+itemName);
		if(itemName.length()!=0){
			actionBar.setTitle("Edit - "+itemName);
			isEdit = true;
			title.setText(intent.getExtras().getString("itemName"));
			quantity.setText(String.valueOf(intent.getExtras().getDouble("quantity")));
			imageUri = intent.getExtras().getString("image");
			if(imageUri!=null && imageUri.length()>0){
			img.setImageURI(Uri.parse(imageUri));
			}
			price.setText(String.format("%.2f",intent.getExtras().getDouble("price")));
			done = intent.getExtras().getBoolean("done");
			
		}
		else{
			actionBar.setTitle("Add New List");
			isEdit = false;
		}
		plus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				increment();
			}
			
		});
		minus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				decrement();
			}
			
		});
		
		plus.setOnLongClickListener( 
	            new View.OnLongClickListener(){
	                public boolean onLongClick(View arg0) {
	                    mAutoIncrement = true;
	                    repeatUpdateHandler.post( new RptUpdater() );
	                    return false;
	                }
	            }
	    );   
		
		minus.setOnLongClickListener( 
	            new View.OnLongClickListener(){
	                public boolean onLongClick(View arg0) {
	                    mAutoDecrement = true;
	                    repeatUpdateHandler.post( new RptUpdater() );
	                    return false;
	                }
	            }
	    );  
		
		plus.setOnTouchListener( new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
	                    && mAutoIncrement ){
	                mAutoIncrement = false;
	            }
	            return false;
	        }
	    });  
		
		minus.setOnTouchListener( new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
	                    && mAutoDecrement ){
	                mAutoDecrement = false;
	            }
	            return false;
	        }
	    });  
		
		image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				imagePickDialog();
			}
			
		});
		
		String[] units = {"pieces", "kg", "lit", "gr", "cm", "package", "inch", "foot", "bottle", "box", "unit", "pair"};
		String[] curencies = {"DIN","EUR", "GBP", "HRK", "HUF", "JPY", "KWD", "NOK", "SEK", "USD", "DKK", "CZK", "CHF", "CAD", "BAM", "AUD"};
		
		ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, units);
		unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		unit.setAdapter(unitAdapter);

		if(isEdit){
			int unitSpinnerPosition = unitAdapter.getPosition(intent.getExtras().getString("unit"));
			
			//System.out.println(unitSpinnerPosition+" "+intent.getExtras().getString("unit"));
			unit.setSelection(unitSpinnerPosition);
		}

		unit.setOnItemSelectedListener(new OnItemSelectedListener() {
        		@Override
        	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
        	    	unitValue = parent.getItemAtPosition(pos).toString();

        	    }

        		@Override
        	    public void onNothingSelected(AdapterView<?> parent)
				{
        	        //do nothing
        	    }
        	});
		
		ArrayAdapter<String> curencyAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, curencies);
		curencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		curency.setAdapter(curencyAdapter);

		if(isEdit){
			int curencySpinnerPosition = curencyAdapter.getPosition(intent.getExtras().getString("curency"));
			curency.setSelection(curencySpinnerPosition);
		}

		curency.setOnItemSelectedListener(new OnItemSelectedListener() {
        		@Override
        	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
        	    	curencyValue = parent.getItemAtPosition(pos).toString();

        	    }

        		@Override
        	    public void onNothingSelected(AdapterView<?> parent)
				{
        	        //do nothing
        	    }
        	});
		
	}

	public void decrement(){
		if(Double.parseDouble(quantity.getText().toString())>0.0){
	    
	    quantity.setText( String.format("%.1f", (Double.parseDouble(quantity.getText().toString())-0.5)));
		}
	}
	
	public void increment(){
	    
	    quantity.setText( String.format("%.1f",(Double.parseDouble(quantity.getText().toString())+0.5)));
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
		getSupportMenuInflater().inflate(R.menu.item_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_save)
		{
			if(title.getText().toString().length()==0){
				Toast.makeText(this, "Item name cannot be empty!", Toast.LENGTH_LONG).show();
			}
			/*else if(db.itemExists(listName, title.getText().toString())){
				Toast.makeText(this, "Item already exists.\nTry differente name!", Toast.LENGTH_LONG).show();
			}*/
			
			else if(quantity.getText().toString().length()==0){
				Toast.makeText(this, "Quantity cannot be empty!", Toast.LENGTH_LONG).show();
			}
			else if(Double.parseDouble(quantity.getText().toString())==0){
				Toast.makeText(this, "Quantity cannot be 0", Toast.LENGTH_LONG).show();
			}
			
			else if(price.getText().toString().length()==0){
				Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_LONG).show();
			}
			else if(Double.parseDouble(price.getText().toString())==0){
				Toast.makeText(this, "Price cannot be 0", Toast.LENGTH_LONG).show();
			}
			else{
				
			String mPrice = String.format("%.2f", ( Double.parseDouble(price.getText().toString())*Double.parseDouble(quantity.getText().toString())));
			Intent intent = new Intent();
			intent.putExtra("name", title.getText().toString());
			intent.putExtra("quantity", Double.parseDouble(quantity.getText().toString()));
			intent.putExtra("unit", unitValue);
			intent.putExtra("image", imageUri);
			intent.putExtra("price",Double.parseDouble(mPrice));
		    intent.putExtra("curency", curencyValue);
		    intent.putExtra("done", done);
			setResult(RESULT_OK, intent);
			finish();
				
			}
		}	
		if (item.getItemId() == R.id.menu_cancel)
		{
			
			finish();

		}	
		
		
		
		
	return super.onOptionsItemSelected(item);

	}
	
	private void imagePickDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image Source");
		builder.setIcon(android.R.drawable.ic_menu_camera);
		builder.setNeutralButton("Select From Galery", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO); 	
			}
			
		});
		builder.setPositiveButton("Take image with Camera", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 	
				
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
			
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	        	Uri uri = imageReturnedIntent.getData();
	        	imageUri = uri.toString();
	        	
	        	img.setImageURI(uri);
	        }
	    }
	}
	
	
	
}
