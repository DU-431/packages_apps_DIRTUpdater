package com.du.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.content.SharedPreferences;

public class WidgetActivity extends AppWidgetProvider{
	
    RemoteViews views;
	
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	SharedPreferences prefs = context.getSharedPreferences("UpdateInfo", 0);
    	ComponentName thisWidget = new ComponentName(context, WidgetActivity.class);
    	int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    	for (int widgetId : allWidgetIds) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("dialog_mode","yes");
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			int devVers = Integer.valueOf(GetBuildNum());
			int serverVers = Integer.valueOf(prefs.getString("currVers", ""));
			
			String update = "No update available";
			views = new RemoteViews(context.getPackageName(), R.layout.widget_layout); 
			if (serverVers > devVers)
			{
				update = "Update Available";
				views.setImageViewResource(R.id.line, R.drawable.line);
			}
			
			
			System.out.println("Adding text");
			views.setTextViewText(R.id.myImageViewText, update);
			views.setOnClickPendingIntent(R.id.blankImage, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, views);
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
}
