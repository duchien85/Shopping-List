package rs.pedjaapps.shoppinglist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Date;


public class SyncCurencies extends AsyncTask<String, Void, Double>
{

	Context context;
	ProgressDialog pd;
	
	
	public SyncCurencies(Context context)
	{
		this.context = context;
		
	}
	

	

	@Override
	protected Double doInBackground(String... args)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpPost httppost = new HttpPost("http://api.kursna-lista.info/63a58ddf17e973290b7a61c73922081d/kursna_lista/json");
		// Depends on your web service
		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		final String[] CURENCY_KEYS = {"eur", "gbp", "hrk", 
				"huf", "jpy", "kwd", 
				"nok", "sek", 
				"usd", "dkk", "czk", 
				"chf", "cad", "bam", 
				"aud",};
		List<Double> values = new ArrayList<Double>();
		
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			// json is UTF-8 by default i beleive
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
			    sb.append(line + "\n");
			}
			result = sb.toString();
			JSONObject jObject = new JSONObject(result).getJSONObject("result");
			
			for(int i = 0; i<CURENCY_KEYS.length; i++){
				values.add(jObject.getJSONObject(CURENCY_KEYS[i]).getDouble("sre"));
			}
			db.addCurency(new CurenciesEntry(values.get(0),
					values.get(1),
					values.get(2),
					values.get(3),
					values.get(4),
					values.get(5),
					values.get(6),
					values.get(7),
					values.get(8),
					values.get(9),
					values.get(10),
					values.get(11),
					values.get(12),
					values.get(13),
					values.get(14)
					));
			System.out.println(values);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putLong("time",new Date().getTime()).commit();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           
		
		
		return 0.0;
	}

	@Override
	protected void onPreExecute(){
	/*	pd = new ProgressDialog(context);
		pd.setMessage("Downloading Data");
		pd.setIndeterminate(true);
		pd.show();*/
		Toast.makeText(context, "Updating currencies", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPostExecute(Double result)
	{
	//	pd.dismiss();
	}
}	

