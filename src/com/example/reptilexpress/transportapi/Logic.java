package com.example.reptilexpress.transportapi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class Logic {
	
	BusInformation busInfo;
	Location location;
	Object locker = new Object();
	
	public Logic() {
		this.busInfo = new BusInformation(); 
	}
	
	public List<Arrival> getNearestBuses(final Context ctx) throws Exception {
		 List<Stop> stopList = BusInformation.getClosestStops(this.getDeviceLocation(ctx));
		 if (stopList.size() < 1) {
			 return new ArrayList<Arrival>();
		 }
		 return BusInformation.getStopTimetable(stopList.get(0));
	}
	
	private Location getDeviceLocation(final Context ctx) throws Exception {
		new Thread() {
			public void run() {
				Looper.prepare();
				
				final LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

				final LocationListener locationListener = new LocationListener() {
					@Override
				    public void onLocationChanged(Location location) {
						Log.d("Reptile", "LOCATION CHANGED!");
				        Logic.this.location = location;
				        synchronized (locker) {
				        	locker.notify();
				        }
				    }

					@Override
				    public void onStatusChanged(String provider, int status, Bundle extras) {}

					@Override
				    public void onProviderEnabled(String provider) {}

					@Override
				    public void onProviderDisabled(String provider) {}
				};

				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			
				Looper.loop();
			}
		}.start();

		synchronized (locker) {
			locker.wait();
		}
		
		Log.d("Reptile", "Loc"+location.getLongitude()+" "+location.getLatitude());
		
		return this.location;
	}
}
