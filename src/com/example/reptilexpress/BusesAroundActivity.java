package com.example.reptilexpress;

import java.util.ArrayList;
import java.util.List;

import com.example.reptilexpress.transportapi.Arrival;
import com.example.reptilexpress.transportapi.Bus;
import com.example.reptilexpress.transportapi.Stop;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BusesAroundActivity extends ListActivity {
	
	List<Arrival> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Pick your bus");
		
		Bundle b = getIntent().getExtras();
		final int count = b.getInt("number");
		
		list = new ArrayList<Arrival>();
		for (int i = 0; i < count; i++) {
			Time t = new Time("GMT");
			t.parse(b.getString("time"+i));
			t.normalize(true);
			
			list.add(new Arrival(
					new Bus(b.getString("bus_route"+i), b.getString("bus_operator"+i), b.getString("bus_direction"+i)),
					new Stop(b.getString("stop_atcocode"+i), b.getString("stop_name"+i)),
					t));
		}
		
		BusesAroundAdapter adapter = new BusesAroundAdapter(this, list);
	    setListAdapter(adapter);
	    
	    getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			          int position, long id) {
				Arrival arrival = list.get(position);
				Intent intent = new Intent(BusesAroundActivity.this, BusTimetable.class);
				Bundle b = new Bundle();
				b.putString("bus_route", arrival.bus.route);
				b.putString("bus_operator", arrival.bus.operator);
				b.putString("bus_direction", arrival.bus.direction);
				b.putString("stop_atcocode", arrival.stop.atcocode);
				b.putString("stop_name", arrival.stop.name);
				b.putString("time", arrival.time.format2445());
				
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
	
	private class BusesAroundAdapter extends ArrayAdapter<Arrival> {
		
		private final List<Arrival> values;
		private final Context context;
		
		public BusesAroundAdapter(Context context, List<Arrival> values) {
		    super(context, R.layout.buses_around_row_layout, values);
		    this.context = context;
		    this.values = values;
		  }
		
		@Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.buses_around_row_layout, parent, false);
		    final Arrival arrival = values.get(position);
		    
		    
		    ((TextView) rowView.findViewById(R.id.route)).setText(arrival.bus.route+" ("+arrival.bus.operator+")");
		    ((TextView) rowView.findViewById(R.id.destination)).setText(arrival.bus.direction);
		    ((TextView) rowView.findViewById(R.id.nextstop)).setText(arrival.stop.name == null || arrival.stop.name.trim().equals("") ? arrival.stop.atcocode : arrival.stop.name);
		    ((TextView) rowView.findViewById(R.id.arrivaltime)).setText(arrival.time.format("%H:%M"));

		    return rowView;
		  }
		
	}

}
