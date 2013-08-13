package com.dirtrom.updater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingsActivity extends PreferenceActivity {
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        
	        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	    }
	    	
	    	public static class MyPreferenceFragment extends PreferenceFragment
	        {
	    		final Context ctx = getActivity();
	            @Override
	            public void onCreate(final Bundle savedInstanceState)
	            {
	            	super.onCreate(savedInstanceState);
	                addPreferencesFromResource(R.layout.settings);
	                
	                final CheckBoxPreference oneday = (CheckBoxPreference) findPreference("pref_key_update_interval_1_day");
	                final CheckBoxPreference week = (CheckBoxPreference) findPreference("pref_key_update_interval_7_day");
	                final CheckBoxPreference twoday = (CheckBoxPreference) findPreference("pref_key_update_interval_2_day");
	                final CheckBoxPreference threeday = (CheckBoxPreference) findPreference("pref_key_update_interval_3_day");
	                final CheckBoxPreference fiveday = (CheckBoxPreference) findPreference("pref_key_update_interval_5_day");
	                
	                oneday.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                	public boolean onPreferenceClick(Preference preference) {   
	                	   if (oneday.isChecked()) {
	                		   fiveday.setChecked(false);
	                		   twoday.setChecked(false);
	                		   threeday.setChecked(false);
	                		   week.setChecked(false);
	                	   }
						return true;
	                	   
	                	}
	                });
	                
	                
	                twoday.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                	public boolean onPreferenceClick(Preference preference) {   
	                	   if (twoday.isChecked()) {
	                		   oneday.setChecked(false);
	                		   fiveday.setChecked(false);
	                		   threeday.setChecked(false);
	                		   week.setChecked(false);
	                	   }
						return true;
	                	   
	                	}
	                });
	                
	                threeday.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                	public boolean onPreferenceClick(Preference preference) {   
	                	   if (threeday.isChecked()) {
	                		   oneday.setChecked(false);
	                		   twoday.setChecked(false);
	                		   fiveday.setChecked(false);
	                		   week.setChecked(false);
	                	   }
						return true;
	                	   
	                	}
	                });
	                
	                
	                fiveday.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                	public boolean onPreferenceClick(Preference preference) {   
	                	   if (fiveday.isChecked()) {
	                		   oneday.setChecked(false);
	                		   twoday.setChecked(false);
	                		   threeday.setChecked(false);
	                		   week.setChecked(false);
	                	   }
						return true;
	                	   
	                	}
	                });
	                
	                
	                week.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                	public boolean onPreferenceClick(Preference preference) {   
	                	   if (week.isChecked()) {
	                		   oneday.setChecked(false);
	                		   twoday.setChecked(false);
	                		   threeday.setChecked(false);
	                		   fiveday.setChecked(false);
	                	   }
						return true;
	                	   
	                	}
	                });
	            }
	            
	            public void onSharedPreferenceChanged(SharedPreferences sharePreferences, String key) {
	            	ctx.startService(new Intent(ctx.getApplicationContext() ,ManifestService.class));
	            }
	        }

}
