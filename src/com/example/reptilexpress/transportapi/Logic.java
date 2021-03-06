package com.example.reptilexpress.transportapi;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public List<Arrival> getNearestBuses(final Context ctx) throws Exception {
		 List<Stop> stopList = BusInformation.getClosestStops(this.getDeviceLocation(ctx));
		 List<Arrival> arrivalList = new ArrayList<Arrival>();  
		 for (int i = 0; i < stopList.size() && i < 5; i++) {
			 arrivalList.addAll(BusInformation.getStopTimetable(stopList.get(i)));
		 }
		 Collections.sort(arrivalList, Arrival.compare_by_time);
		 return arrivalList;
	}
	
	public static List<Arrival> getBusTimetable(final Arrival arrival) {
		return BusInformation.getBusTimetable(arrival);
	}
	
	private Location getDeviceLocation(final Context ctx) throws Exception {
		new Thread() {
			public void run() {
				Looper.prepare();
				
				final LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

				final LocationListener locationListener = new LocationListener() {
					@Override
				    public void onLocationChanged(Location location) {
				        Logic.this.location = location;
				        locationManager.removeUpdates(this);

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

		Log.d("Reptile", "Awaiting device location .....");
		synchronized (locker) {
			locker.wait();
		}
		
		Log.d("Reptile", "Got location: Long: " + location.getLongitude() + " Lat: " + location.getLatitude());
		
		return this.location;
	}
}
