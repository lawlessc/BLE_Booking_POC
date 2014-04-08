package com.estimote.examples.demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;


import com.estimote.sdk.Beacon;

public class BookingActivity extends ListActivity {
	
	private static final String TAG = BookingActivity.class.getSimpleName();
	private int major;
	private int minor;
	String room = null;
	JSONObject jBookings = null;
	JSONObject json = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Beacon beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);

		major = beacon.getMajor();
		minor = beacon.getMinor();

		ArrayAdapter<JSONObject> adapter;
		ArrayList<JSONObject> listItems = new ArrayList<JSONObject>();

		try {
			room = new HttpAsyncTask().execute("http://localhost:8080/" + major + "/" + minor).get();
			json = new JSONObject(room);
			jBookings = json.getJSONObject("bookingsDetails");
			JSONArray bookingArray = jBookings.getJSONArray("bookings");
			Log.e("JSON", bookingArray.toString());

			for(int i = 0 ; i < bookingArray.length() ; i++ ){		
				listItems.add(bookingArray.getJSONObject(i));
			}

			adapter=new ArrayAdapter<JSONObject>(this, android.R.layout.simple_list_item_1,listItems);
			setListAdapter(adapter);
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
	public void makeBooking(View view) throws JSONException{		
		Intent i = new Intent(getApplicationContext(), PostBookingActivity.class);
		Log.e("Context",getApplicationContext().toString());
//		String rName = jBookings.getString("roomname").toString();
		String rName = "solas";
		Log.e("JSON", rName);
		i.putExtra("roomname", rName );
		startActivity(i);

	}

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {

			return GET(urls[0], major, minor);
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			if(result != null){
				Toast.makeText(getBaseContext(), "Connected!", Toast.LENGTH_LONG).show();
			}
		
			if (result == null) {
				Log.d("beacons", "No the result was null");
			}
			else{
//				Log.d("beacons", result);
			}
			
			//etResponse.setText(result);
		}
	}

	public static String GET(String url, int major, int minor){
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		Log.d("beacons","connected");
		Log.d("beacons",result);
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

}
