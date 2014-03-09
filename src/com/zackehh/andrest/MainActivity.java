package com.zackehh.andrest;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * Test activity to ensure that the methods work as expected. You'll have 
 * to set the url if you want to use this.
 * 
 * @author 	Isaac Whitfield
 * @version 09/03/2014
 */
public class MainActivity extends Activity {

	private AndrestClient rest = new AndrestClient();
	private String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		TextView get = (TextView)findViewById(R.id.get);
		TextView post = (TextView)findViewById(R.id.post);
		TextView put = (TextView)findViewById(R.id.put);
		TextView delete = (TextView)findViewById(R.id.delete);
		
		get.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				new doRequest(MainActivity.this, null, "GET", url).execute();
			}
		});
		
		post.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Map<String, Object> toPost = new HashMap<String, Object>();
				toPost.put("string", "This is a test string");
				toPost.put("boolean", true);
				toPost.put("integer", 10);
				new doRequest(MainActivity.this, toPost, "POST", url).execute();
			}
		});
		
		put.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Map<String, Object> toPut = new HashMap<String, Object>();
				toPut.put("string", "This is a test string");
				toPut.put("boolean", true);
				toPut.put("integer", 10);
				new doRequest(MainActivity.this, toPut, "PUT", url).execute();
			}
		});
		
		delete.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				new doRequest(MainActivity.this, null, "DELETE", url).execute();
			}
		});
				
		return true;
	}
	
	/**
	 * Handles a button press and calls the AndrestClient.rest() method with the 
	 * given parameters. Runs in the background (Async) and pops up a dialog on
	 * completion.
	 * 
	 * @author 	Isaac Whitfield
	 * @ersion	09/03/2014
	 */
	private class doRequest extends AsyncTask<Void, JSONObject, JSONObject>{
		
		// Store context for dialogs
		private Context context = null;
		// Store error message
		private Exception e = null;
		// Passed in data object
		private Map<String, Object> data = null;
		// Passed in method
		private String method = "";
		// Passed in url
		private String url = "";
		
		public doRequest(Context context, Map<String, Object> data, String method, String url){
			this.context = context;
			this.data = data;
			this.method = method;
			this.url = url;
		}
		
		@Override
		protected JSONObject doInBackground(Void... arg0) {
			try {
				return rest.request(url, method, data); // Do request
			} catch (Exception e) {
				this.e = e;	// Store error
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject data){
			super.onPostExecute(data);
			// Display based on error existence
			if(e != null){
				new ResponseDialog(context, "We found an error!", e.getMessage()).showDialog();
			} else {
				new ResponseDialog(context, "Success!", data.toString()).showDialog();
			}
		}
	}
	
	/**
	 * Extremely simple dialog builder, just so I don't have to mess about when creating
	 * dialogs for the user.
	 * 
	 * @author 	Isaac Whitfield
	 * @version 09/03/2014
	 */
	private class ResponseDialog extends Builder {

		// Store the passed context
		private Context context;
		
		// Can be used as a regular builder
		public ResponseDialog(Context context) {
			super(context);
		}
		
		// Or as a custom builder, which we want
		public ResponseDialog(Context context, String title, String message) {
			super(context);
			// Store context
			this.context = context;
			// Set up everything
			setMessage(message);
			setTitle(title);
			setCancelable(false);
			setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss(); // It's just for info so we don't really care what this does
				}
			});
		}

		public void showDialog(){
			// Create and show
			AlertDialog alert = create();
			alert.show();
			// Center align everything
			((TextView)alert.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
			((TextView)alert.findViewById((context.getResources().getIdentifier("alertTitle", "id", "android")))).setGravity(Gravity.CENTER);
		}
	}

}
