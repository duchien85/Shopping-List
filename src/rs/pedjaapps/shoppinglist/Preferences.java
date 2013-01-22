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
	private ListPreference updatePrefList;

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{        
	

	SharedPreferences sharedPrefs = PreferenceManager
			.getDefaultSharedPreferences(this);
	String them = sharedPrefs.getString("theme", "light");
	
		if(them.equals("light")){
			setTheme(R.style.Theme_Sherlock_Light);
		}
		else if(them.equals("dark")){
			setTheme(R.style.Theme_Sherlock);
			
		}
		else if(them.equals("light_dark_action_bar")){
			setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
			
		}
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
		updatePrefList = (ListPreference) findPreference("update");
        updatePrefList.setDefaultValue(updatePrefList.getEntryValues()[0]);
        String update = updatePrefList.getValue();
        if (update == null) {
        	updatePrefList.setValue((String)updatePrefList.getEntryValues()[0]);
        	update = updatePrefList.getValue();
        }
        updatePrefList.setSummary(updatePrefList.getEntries()[updatePrefList.findIndexOfValue(update)]);


        updatePrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					updatePrefList.setSummary(updatePrefList.getEntries()[updatePrefList.findIndexOfValue(newValue.toString())]);
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
