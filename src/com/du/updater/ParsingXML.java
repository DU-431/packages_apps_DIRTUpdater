package com.du.updater;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
 
public class ParsingXML extends Activity {
       
	SharedPreferences.Editor editor; 
    //* Called when the activity is first created. 
    @SuppressLint("SimpleDateFormat")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parse_xml);
        editor = getSharedPreferences("UpdateInfo", 0).edit(); 
        Log.v("DIRT", "Starting a new thread to run");
            
		GetUpdates gu = new GetUpdates();
		gu.doInBackground("Test");
            
    }
    
    private class GetUpdates extends AsyncTask<String, Void, String> {
    	
    	@Override
	    protected void onPostExecute(String result) { 
	    	//possibly add notification here
	    	finish();
	    }

		@Override
		protected String doInBackground(String... params) {
			Thread t = new Thread() {
				@SuppressLint("SimpleDateFormat")
				public void run() {
					try {
		    			Log.v("DIRT", "Connecting to xml file");
		                // Create a URL we want to load some xml-data from. 
		                URL url = new URL("http://dirtrom.com/private/duversions.xml");

		                // Get a SAXParser from the SAXPArserFactory. 
		                SAXParserFactory spf = SAXParserFactory.newInstance();
		                SAXParser sp = spf.newSAXParser();
		                Log.v("DIRT", "Reading XML");
		                // Get the XMLReader of the SAXParser we created. 
		                XMLReader xr = sp.getXMLReader();
		                // Create a new ContentHandler and apply it to the XML-Reader
		                Handlers myExampleHandler = new Handlers();
		                myExampleHandler.setHWDevice(Build.DEVICE);
		                xr.setContentHandler(myExampleHandler);
		                Log.v("DIRT", "Parsing XML");
		                // Parse the xml-data from our URL. 
		                xr.parse(new InputSource(url.openStream()));
		                // Parsing has finished. 

		                // Our ExampleHandler now provides the parsed data to us. 
		                ParsedDataSet ParsedDataSet = myExampleHandler.getParsedData();

		                Log.v("DIRT", "Creating Settings");
		                // Set the result to be displayed in our GUI. 
		                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
		    	        String currentDateandTime = sdf.format(new Date());
		                
		                
		                String buildNumber = ParsedDataSet.getBuildNumber();
		                editor.putString("currVers", buildNumber);
		                editor.putString("firstRun", "1");
		                editor.putString("dateCheck", currentDateandTime);
		                editor.putString("link", ParsedDataSet.getLink());
		                editor.commit();
		                try {
		                	synchronized(this) {
		                		wait(1500);
		                	}
		                }catch(InterruptedException ex){                    
		                }
		                
		                Log.v("DIRT", "Message Should Go Away");
		                finish();
			        } catch (Exception e) {
			                Log.e("DU", "DU Updater", e);
			                finish();
			        }
				}
			};
			t.start();
			

            Log.v("DU", "Away with you");
			return "Success";
			
		}
    }
}