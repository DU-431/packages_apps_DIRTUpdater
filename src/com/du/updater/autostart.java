package com.du.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class autostart extends BroadcastReceiver 
{
    public void onReceive(Context arg0, Intent arg1) 
    {
        Intent intent = new Intent(arg0,ManifestService.class);
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}
