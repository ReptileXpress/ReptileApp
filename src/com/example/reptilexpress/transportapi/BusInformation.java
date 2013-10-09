package com.example.reptilexpress.transportapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.Uri;
import android.text.format.Time;

public class BusInformation {

		public static List<Stop> getClosestStops(final Location location) {
			try {
				return sharedInstance.closestStops(location);
			} catch (Exception e) {
				throw new RuntimeException("Failed getting closest stops", e);
			}
		}
		
		public static List<Arrival> getStopTimetable(final Stop stop) {
			try {
				return sharedInstance.stopTimetable(stop);
			} catch (Exception e) {
				throw new RuntimeException("Failed getting stop timetable", e);
			}
		}
		
		public static List<Arrival> getBusTimetable(final Arrival arrival) {
			return null;
		}
	
		/* Unsafe singleton,
		 *   but it doesn't matter if we accidentally (or deliberately)
		 *   create more than one instance. */
		private static BusInformation sharedInstance =
				new BusInformation("api_key", "app_id");
		
		private final String apiKey;
		private final String appId;
		private final HttpClient http;
		
		public BusInformation(final String apiKey, final String appId) {
			this.apiKey = apiKey; this.appId = appId;
			this.http = new DefaultHttpClient();
		}
		
		private static final int numberOfClosestStops = 5;
		
		public List<Stop> closestStops(final Location location) throws Exception {
			final Uri url = Uri.parse("http://transportapi.com").buildUpon()
					.path("v3/uk/bus/stops/near.json")
					.appendQueryParameter("api_key", apiKey)
					.appendQueryParameter("app_id", appId)
					.appendQueryParameter("lat", String.valueOf(location.getLatitude()))
					.appendQueryParameter("lon", String.valueOf(location.getLongitude()))
					.appendQueryParameter("rpp", String.valueOf(numberOfClosestStops))
					.appendQueryParameter("page", String.valueOf(1))
					.build();
			
			final HttpResponse response =
					http.execute(new HttpGet(url.toString()));
			final StatusLine status = response.getStatusLine();
			
			if (status.getStatusCode() != HttpStatus.SC_OK) {
				response.getEntity().getContent().close();
				throw new IOException(status.getReasonPhrase());
			}
			
			final JSONArray stops;
			{
				final CharBuffer buffer = CharBuffer.allocate(
						Integer.MAX_VALUE & ((int) response.getEntity().getContentLength()));
				final InputStream data = response.getEntity().getContent();
				final InputStreamReader text = new InputStreamReader(data, "UTF-8");
				while (text.read(buffer) > 0);
				try {
					stops = new JSONObject(buffer.toString()).getJSONArray("stops");
				} catch (JSONException e) {
					throw new Exception("Request returned invalid JSON", e);
				}
				if (stops == null) {
					throw new Exception("Request returned invalid JSON (data missing)");
				}
			}
			
			final ArrayList<Stop> result = new ArrayList<Stop>(stops.length());
			for (int i = 0; i < stops.length(); ++i) {
				final JSONObject stop = stops.getJSONObject(i);
				result.add(new Stop(
						stop.getString("atcocode"), stop.getString("name")));
			}
			
			return result;
		}
		
		public List<Arrival> stopTimetable(final Stop stop) throws Exception {
			final Uri url = Uri.parse("http://transportapi.com").buildUpon()
					.path("v3/uk/bus/stop/atcocode/live.json")
					.appendQueryParameter("api_key", apiKey)
					.appendQueryParameter("app_id", appId)
					.appendQueryParameter("group", "no")
					.build();
			
			final HttpResponse response =
					http.execute(new HttpGet(url.toString()));
			final StatusLine status = response.getStatusLine();
			
			if (status.getStatusCode() != HttpStatus.SC_OK) {
				response.getEntity().getContent().close();
				throw new IOException(status.getReasonPhrase());
			}
			
			final JSONArray arrivals;
			{
				final CharBuffer buffer = CharBuffer.allocate(
						Integer.MAX_VALUE & ((int) response.getEntity().getContentLength()));
				final InputStream data = response.getEntity().getContent();
				final InputStreamReader text = new InputStreamReader(data, "UTF-8");
				while (text.read(buffer) > 0);
				try {
					JSONObject root = new JSONObject(buffer.toString());
					root = root.getJSONObject("departures");
					if (root == null)
						throw new Exception("Request returned invalid JSON (data missing)");
					
					arrivals = root.getJSONArray("all");
				} catch (JSONException e) {
					throw new Exception("Request returned invalid JSON", e);
				}
				if (arrivals == null) {
					throw new Exception("Request returned invalid JSON (data missing)");
				}
			}
			
			final ArrayList<Arrival> result =
					new ArrayList<Arrival>(arrivals.length());
			for (int i = 0; i < arrivals.length(); ++i) {
				final JSONObject arrival = arrivals.getJSONObject(i);
				result.add(new Arrival(
						new Bus(arrival.getString("line"), arrival.getString("operator"), arrival.getString("direction")),
						stop, parseSimpleTime(arrival.getString("best_departure_estimate"))));		
			}
			
			return result;
		}
		
		public List<Arrival> busTimetable(final Arrival arrival) {
			return null;
		}
		
		private static final SimpleDateFormat timeFormat =
				new SimpleDateFormat("HH:mm", Locale.UK);
		
		private static Time parseSimpleTime(final String time) throws ParseException {
			Date raw = timeFormat.parse(time);
			Calendar now = Calendar.getInstance(Locale.UK);
			Calendar calendar = Calendar.getInstance(Locale.UK);
			calendar.setTime(raw);
			
			while (calendar.before(now))
				calendar.add(Calendar.DATE, 1);
			
			Time result = new Time();
			result.set(calendar.getTimeInMillis());
			return result;
		}
}
