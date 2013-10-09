package com.example.reptilexpress;

import java.util.List;

import com.example.reptilexpress.transportapi.Arrival;
import com.example.reptilexpress.transportapi.Bus;
import com.example.reptilexpress.transportapi.BusInformation;
import com.example.reptilexpress.transportapi.Stop;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.ListActivity;
import android.content.Context;

public class BusTimetable extends ListActivity {
	
	Arrival input;
	List<Arrival> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_timetable);
		
		Bundle b = getIntent().getExtras();
		Time t = new Time("GMT");
		t.parse(b.getString("time"));
		t.normalize(true);
		
		input = new Arrival(
				new Bus(b.getString("bus_route"), b.getString("bus_operator"), b.getString("bus_direction")),
				new Stop(b.getString("stop_atcocode"), b.getString("stop_name")),
				t);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		list = BusInformation.getBusTimetable(input);
		
		BusesTimetableAdapter adapter = new BusesTimetableAdapter(this, list);
	    setListAdapter(adapter);
	}
	
private class BusesTimetableAdapter extends ArrayAdapter<Arrival> {
		
		private final List<Arrival> values;
		private final Context context;
		
		public BusesTimetableAdapter(Context context, List<Arrival> values) {
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
		    
		    
		    ((TextView) rowView.findViewById(R.id.route)).setText(arrival.stop.name == null || arrival.stop.name.trim().equals("") ? arrival.stop.atcocode : arrival.stop.name);
		    ((TextView) rowView.findViewById(R.id.destination)).setText(arrival.time.format("%H:%M"));

		    return rowView;
		  }
		
	}

}
