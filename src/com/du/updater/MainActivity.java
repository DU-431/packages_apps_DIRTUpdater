package com.du.updater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {

	private String model;
	private TextView DeviceId, DeviceVers, CurrVers, DateCheck;
	private Button btnCheck;
	int devVers = 0;
	int serverVers = 0;
    private ProgressDialog mProgressDialog;
    private Intent intent;
    private SharedPreferences settings;
    private boolean online;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences("UpdateInfo", 0);
		Boolean fDialogMode     = getIntent().hasExtra( "dialog_mode" );
    	if(!fDialogMode ) {
    	    super.setTheme( android.R.style.Theme_Holo );
    	}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		intent = new Intent(this, ParsingXML.class);
		if (settings.getString("firstRun", "").isEmpty()) { 
			if (IsOnline()){
					
						startActivityForResult(intent,0);
						startService(new Intent(getApplicationContext() ,ManifestService.class));
			}
		}
		else
		{
			Log.v("DIRT", "Not first run");
			GetInfo();
		}
		

		btnCheck = (Button) findViewById(R.id.btnCheckServer);
		btnCheck.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
            	if (IsOnline()) {
            		startActivityForResult(intent,0);
            	}

            }
        });
	}
	
	public void GetInfo()
	{
		
		model = Build.DEVICE;
		
		DeviceId = (TextView) findViewById(R.id.DeviceID);
		DeviceVers = (TextView) findViewById(R.id.DeviceVers);
		CurrVers = (TextView) findViewById(R.id.CurrVers);
		DateCheck = (TextView) findViewById(R.id.DateCheck);
		DeviceVers.setText(GetBuildNum());
		DeviceId.setText(model);
		CurrVers.setText(settings.getString("currVers", ""));
		DateCheck.setText(settings.getString("dateCheck", ""));
		
		
		devVers = Integer.valueOf(GetBuildNum());
		serverVers = Integer.valueOf(settings.getString("currVers", ""));
		UpdateWidget();
		if (serverVers > devVers)
		{
			AskUpdate();
			
		}
	}
	
	public boolean IsOnline()
	{
		online = false;
		
		ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] activeNet = conMgr.getAllNetworkInfo();
		for (NetworkInfo adapter : activeNet)
		{
			if (adapter.isConnected()){
				Log.v("DU", "Device is online, getting info");
				online = true;
				break;
			}
		}
		
		return online;
	}
	
	public void UpdateWidget()
	{
		Context context = this;
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		ComponentName thisWidget = new ComponentName(context, WidgetActivity.class);
		remoteViews.setImageViewResource(R.id.line, R.drawable.linedark);
		String text = "No Update Available";
		if (serverVers > devVers)
		{
			text = "Update Available";
			remoteViews.setImageViewResource(R.id.line, R.drawable.line);
		}
		
		
		remoteViews.setTextViewText(R.id.myImageViewText, text);
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
		
	}
	
	public void AskUpdate() {

		final SharedPreferences settings = getSharedPreferences("UpdateInfo", 0); 
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	// instantiate it within the onCreate method
	       			mProgressDialog = new ProgressDialog(MainActivity.this);
	       			mProgressDialog.setMessage("Downloading Current DU ROM");
	       			mProgressDialog.setIndeterminate(false);
	       			mProgressDialog.setMax(100);
	       			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	       			try {
	       			// execute this when the downloader must be fired
	       			DownloadFile downloadFile = new DownloadFile();
	       			downloadFile.execute(settings.getString("link",""));
	       			}
	       			catch (Exception e) {
	       				Log.v("DIRT", e.getMessage());
	       				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	       			}
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Download the latest version from the server?")
			.setPositiveButton("Yes", dialogClickListener)
			.setTitle("Download?")
			.setIcon(android.R.drawable.ic_dialog_alert)
		    .setNegativeButton("No", dialogClickListener).show();
	}
	
	private class DownloadFile extends AsyncTask<String, Integer, String> {
	    @Override
	    protected String doInBackground(String... sUrl) {
	        try {
	            URL url = new URL(sUrl[0]);
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Download/DU_CurrentVersion_" + model + ".zip");

	            byte data[] = new byte[1024];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	        } catch (Exception e) {
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mProgressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        mProgressDialog.setProgress(progress[0]);
	        if (progress[0] == 100) {
	        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
	            alertDialog.setIcon(android.R.drawable.ic_dialog_info);
	            alertDialog.setTitle("Flash Zip");
	            alertDialog.setMessage("Flash the zip now?");
	            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
	            {
	               @Override
	               public void onClick(DialogInterface dialog, int which) {

		       			try{
		       				File downloadedFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/DU_CurrentVersion_" + model + ".zip");
		       				Intent i = new Intent();
		       				i.setAction(android.content.Intent.ACTION_VIEW);
		       				i.setDataAndType(Uri.fromFile(downloadedFile), "application/zip");
		       				startActivity(i);
		       				
			       			
		       			}
		               catch (Exception e)
		               {
		               		System.out.println(e.getMessage());
		               }
		           }
	            });
	            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
	               @Override
	               public void onClick(DialogInterface dialog, int which) {
	                  finish();
	               }
	           });
	            alertDialog.show();
	        }
	    }
	}
	
	public String GetBuildNum() {
		String line = "";
		try {
		 Process ifc = Runtime.getRuntime().exec("getprop ro.du.buildnum");
		 BufferedReader bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
		 line = bis.readLine();
		 ifc.destroy();
		} catch (java.io.IOException e) {
		}
		
		return line;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GetInfo();
	}
	
	public void OpenSettings(MenuItem item) {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	

}
