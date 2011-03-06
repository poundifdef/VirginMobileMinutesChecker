package com.jaygoel.virginminuteschecker;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;


public class ViewMinutes extends Activity implements Runnable {

	String PREFS_NAME = "loginInfo"; 
	ProgressDialog pd;
	Map<String, String> rc = null;
	//private TextView tv;
	Activity me = this;

	String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_minutes);
		setTitle(getString(R.string.viewTitle));

		//tv = (TextView) this.findViewById(R.id.minutes);
		setLoginInfo();
		
		if (username.equals("u") || password.equals("p")) {
			Intent i = new Intent(this, MinutesChecker.class);
			startActivityForResult(i, 1);
			// startActivity(i);
		} else {
		    gatherAndDisplay();
		}

	}

    private void setLoginInfo() {
	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	username = settings.getString("username", "u");
	password = settings.getString("password", "p");
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
	Log.d("DEBUG", "in onActivityResult");
	if (reqCode == 1 && resultCode != RESULT_CANCELED) {
	    Log.d("DEBUG", "login activity succeeded, continuing");

	    setLoginInfo();
	    gatherAndDisplay();
	} else {
	    Log.d("DEBUG", "login activity failed, repeating");
	    Log.d("DEBUG", Integer.toString(reqCode));
	    Log.d("DEBUG", Integer.toString(resultCode));

	    showErrorMessageAndRequery();
	}
    }


    private void gatherAndDisplay() {
	pd = new ProgressDialog(ViewMinutes.this);
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	pd.setMessage(getString(R.string.loadingMessage));
	pd.setIndeterminate(true);
	pd.setCancelable(false);
	
	doInfo();
    }

	private void doInfo() {
		pd.show();

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.logout:
		TableLayout tl = (TableLayout) findViewById(R.id.minutes);
		tl.removeAllViews();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.clear();

		// Commit the edits!
		editor.commit();

		SharedPreferences cache = getSharedPreferences("cache", 0);
		SharedPreferences.Editor ceditor = cache.edit();
		ceditor.clear();
		ceditor.commit();
			
			
		Intent i = new Intent(this, MinutesChecker.class);
		startActivityForResult(i, 1);
		return true;
	    case R.id.refresh:
		doInfo();
		return true;
	    case R.id.settings:
		Intent i2 = new Intent(this, Preferences.class);
		startActivity(i2);
		return true;
	    default:
		return super.onOptionsItemSelected(item);
	    }
	}

	public void run() {
		rc = WebsiteScraper.getInfo(username, password);
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();
			if (rc.get("isValid").equals("TRUE")) {


			    // cache minutes used
			    SharedPreferences cache = getSharedPreferences("cache", 0);
			    SharedPreferences.Editor ceditor = cache.edit();
			    ceditor.putString("minutes", rc.get("Minutes Used"));
			    ceditor.commit();
			    
			    
				
		        TableLayout tl = (TableLayout) findViewById(R.id.minutes);
			tl.removeAllViews();
		        
		        int current = 0;
			    for (Map.Entry<String, String> entry : rc.entrySet()) {
			    	
			    	if (entry.getKey().equals("isValid"))
			    		continue;
			    	
			        current++;
			    	
			           TableRow tr = new TableRow(me);
			            tr.setId(100+current);
			            tr.setLayoutParams(new LayoutParams(
			                    LayoutParams.FILL_PARENT,
			                    LayoutParams.WRAP_CONTENT));   

			            // Create a TextView to show the name of the property
			            TextView labelTV = new TextView(me);
			            labelTV.setId(200+current);
			            labelTV.setText(entry.getKey());
			            labelTV.setTextColor(Color.LTGRAY);
			            labelTV.setTextSize(TypedValue.COMPLEX_UNIT_PT ,7);	
			            labelTV.setLayoutParams(new LayoutParams(
			                    LayoutParams.FILL_PARENT,
			                    LayoutParams.WRAP_CONTENT));
			            tr.addView(labelTV);

			            // Create a TextView to show that property's value
			            TextView valueTV = new TextView(me);
			            valueTV.setId(current);
			            valueTV.setText(entry.getValue());
			            valueTV.setTextColor(Color.WHITE);
			            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_PT ,9);
			            valueTV.setLayoutParams(new LayoutParams(
			                    LayoutParams.FILL_PARENT,
			                    LayoutParams.WRAP_CONTENT));
			            tr.addView(valueTV);

			            // Add the TableRow to the TableLayout
			            tl.addView(tr, new TableLayout.LayoutParams(
			                    LayoutParams.FILL_PARENT,
			                    LayoutParams.WRAP_CONTENT));
			        
			    }
			    
				//tv.setText(rc.get("info"));
			} else {
			    showErrorMessageAndRequery();
			}
		}
	};

    private void showErrorMessageAndRequery() {
	AlertDialog.Builder builder = new AlertDialog.Builder(me);
	builder.setMessage(getString(R.string.loginFail))
	    .setCancelable(false)
	    .setNeutralButton("Ok.",
			      new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog,
						      int id) {
				      Intent i = new Intent(me,
							    MinutesChecker.class);
				      startActivityForResult(i, 1);
				      //startActivity(i);

				  }
			      });

	AlertDialog alert = builder.create();

	alert.show();

    }
}
