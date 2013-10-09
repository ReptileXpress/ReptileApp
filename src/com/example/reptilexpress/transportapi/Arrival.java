package com.example.reptilexpress.transportapi;

import java.util.Comparator;

import android.text.format.Time;

public class Arrival {

	public final Bus bus;
	public final Stop stop;
	public final Time time;
	
	public static final Comparator<Arrival> compare_by_time = new Comparator<Arrival>() {
		
		@Override
		public int compare(Arrival lhs, Arrival rhs) {
			return Time.compare(lhs.time, rhs.time);
		}
	};
	
	public Arrival(final Bus bus, final Stop stop, final Time time) {
		this.bus = bus;
		this.stop = stop;
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "Arrival("+bus+"; "+stop+"; "+time+")";
	}
	

	
}
