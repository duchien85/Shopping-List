package rs.pedjaapps.shoppinglist;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BackupRestore extends SherlockActivity {

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		setContentView(R.layout.backup_restore);
	}
	
}
