package com.example.reptilexpress.transportapi;

public class Bus {
	
	public final String route;
	public final String operator;
	public final String direction;
	
	public Bus(final String route, final String operator, final String direction) {
		this.route = route;
		this.operator = operator;
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return "Bus("+route+", "+operator+", "+direction+")";
	}

}
