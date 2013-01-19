package rs.pedjaapps.shoppinglist;




import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.app.ActionBar;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;



public class Preferences extends SherlockPreferenceActivity
{

	private ListPreference themePrefList;
	private ListPreference curencyPrefList;

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{        
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences); 
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		themePrefList = (ListPreference) findPreference("theme");
        themePrefList.setDefaultValue(themePrefList.getEntryValues()[0]);
        String theme = themePrefList.getValue();
        if (theme == null) {
            themePrefList.setValue((String)themePrefList.getEntryValues()[0]);
            theme = themePrefList.getValue();
        }
        themePrefList.setSummary(themePrefList.getEntries()[themePrefList.findIndexOfValue(theme)]);


        themePrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                themePrefList.setSummary(themePrefList.getEntries()[themePrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        }); 
        
        curencyPrefList = (ListPreference) findPreference("curency");
        curencyPrefList.setDefaultValue(curencyPrefList.getEntryValues()[0]);
        String curency = curencyPrefList.getValue();
        if (curency == null) {
        	curencyPrefList.setValue((String)curencyPrefList.getEntryValues()[0]);
        	curency = curencyPrefList.getValue();
        }
        curencyPrefList.setSummary(curencyPrefList.getEntries()[curencyPrefList.findIndexOfValue(curency)]);


        curencyPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	curencyPrefList.setSummary(curencyPrefList.getEntries()[curencyPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        }); 
        
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, Lists.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        
	            
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
