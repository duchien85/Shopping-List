package rs.pedjaapps.shoppinglist;

import android.content.*;
import android.preference.*;

public class CurencyConvertor
{

public double convert(Context context, double value, String fromCurency){

double to = 0.0;
double from = 0.0;

SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
String toCurency = prefs.getString("curency" ,"eur");
DatabaseHandler db = new DatabaseHandler(context);
to = db.getCurency(toCurency.toUpperCase());
from = db.getCurency(fromCurency.toUpperCase());

return value*(from/to);
}

}
