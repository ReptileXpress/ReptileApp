package com.example.reptilexpress.transportapi;

public class Stop {
	
	public final String atcocode;
	public final String name;
	
	public Stop(final String atcocode, final String name) {
		this.atcocode = atcocode;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Stop("+atcocode+", "+name+")";
	}

}
