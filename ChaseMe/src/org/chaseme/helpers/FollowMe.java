package org.chaseme.helpers;

import org.chaseme.drone.Drone;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class FollowMe implements LocationListener {
	private static final long MIN_TIME_MS = 2000;
	private static final float MIN_DISTANCE_M = 0;
	private Context context;
	private boolean followMeEnabled = false;
	private LocationManager locationManager;
	private Drone drone;

	public FollowMe(Context context, Drone drone) {
		this.context = context;
		this.drone = drone;
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void toogleFollowMeState() {
		if (isEnabledInPreferences()) {
			if (isEnabled()) {
				disableFollowMe();
			} else {
				enableFollowMe();
			}
		} else {
			disableFollowMe();
		}
	}
	
	public Location getLocation() {
	 return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
	
	
	private void enableFollowMe() {
		Toast.makeText(context, "FollowMe Enabled", Toast.LENGTH_SHORT).show();

		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME_MS, MIN_DISTANCE_M, this);

		followMeEnabled = true;
	}

	private void disableFollowMe() {
		Toast.makeText(context, "FollowMe Disabled", Toast.LENGTH_SHORT).show();
		locationManager.removeUpdates(this);
		followMeEnabled = false;
	}

	public boolean isEnabled() {
		return followMeEnabled;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		while(followMeEnabled){
			drone.followMe(location.getLatitude(), location.getLongitude());
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private boolean isEnabledInPreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return true;
		//return prefs.getBoolean("pref_follow_me_mode_enabled", false);
	}
}