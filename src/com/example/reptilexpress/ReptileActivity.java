package com.example.reptilexpress;

import java.util.List;

import com.example.reptilexpress.transportapi.Arrival;
import com.example.reptilexpress.transportapi.Logic;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class ReptileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reptile);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			public void run() {
				changeLoadingText("Finding nearest buses...");
				Logic logic = new Logic();
				List<Arrival> list = null;
				try {
					list = logic.getNearestBuses(ReptileActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				changeLoadingText("done");
				
				Intent intent = new Intent(ReptileActivity.this, BusesAroundActivity.class);
				Bundle b = new Bundle();
				b.putInt("number", list.size());
				
				int i = 0;
				for (final Arrival arrival : list) {
					b.putString("bus_route"+i, arrival.bus.route);
					b.putString("bus_operator"+i, arrival.bus.operator);
					b.putString("bus_direction"+i, arrival.bus.direction);
					b.putString("stop_atcocode"+i, arrival.stop.atcocode);
					b.putString("stop_name"+i, arrival.stop.name);
					b.putString("time"+i, arrival.time.format2445());
					
					i++;
				}
				
				
				intent.putExtras(b);
				startActivity(intent);
				finish();
			};
		}.start();
	}
	
	/**
	 * Change the text above the load bar
	 * @param text
	 */
	private void changeLoadingText(final String text) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				((TextView) findViewById(R.id.loadText)).setText(text);			
			}
		});
	}

}
