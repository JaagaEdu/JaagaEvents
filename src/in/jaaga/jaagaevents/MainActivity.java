package in.jaaga.jaagaevents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.*;

public class MainActivity extends ListActivity {

//	private static String CALENDAR_URL = "https://www.googleapis.com/calendar/v3/calendars/ueto14d3dskn1jlhehf27ud3no@group.calendar.google.com/events?key=AIzaSyB5xly8Mk5is7uERjUezZ8D4dDozZI9058&singleEvents=true&orderBy=startTime&timeMin=2012-02-08T00:00:00z";
	private static String CALENDAR_URL = "https://www.googleapis.com/calendar/v3/calendars/ueto14d3dskn1jlhehf27ud3no@group.calendar.google.com/events";
    private ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter;
    private String nextPageToken = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        adapter = new SimpleAdapter(
        		this,
        		listItems,
        		R.layout.item,
        		new String[] {"eventDate", "eventName"},
        		new int[] {R.id.event_date, R.id.event_name});
        
        setListAdapter(adapter);
        
        getListView().setOnScrollListener(new OnScrollListener(){

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                	return;
                }
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
                	MainActivity.this.getCalendarData();
                }
			}

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}
        	
        });

        
        getCalendarData();
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(listItems.get(position).get("eventName"));
        builder.setMessage(listItems.get(position).get("eventInfo"));
        builder.show();
    }
    
    private void getCalendarData() {
    	if (!listItems.isEmpty() && nextPageToken == null) {
    		return;
    	}
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams p = new RequestParams();
        p.put("key", "AIzaSyB5xly8Mk5is7uERjUezZ8D4dDozZI9058");
        p.put("singleEvents", "true");
        p.put("orderBy", "startTime");
        String now = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").format(new Date());
        p.put("timeMin", now);
        if (nextPageToken != null) {
        	p.put("pageToken", nextPageToken);
        }
        client.get(CALENDAR_URL, p, new JsonHttpResponseHandler() {
            private ProgressDialog progress;
            @Override
            public void onStart() {
            	progress = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
            }
            @Override
            public void onSuccess(JSONObject response) {
            	try {
            		JSONArray items = response.getJSONArray("items");
            		int l = items.length();
                    for (int i = 0; i < l; i++) {
                    	JSONObject item = items.getJSONObject(i);
                        HashMap<String, String> itemHash = new HashMap<String, String>();
                        itemHash.put("eventName", item.getString("summary"));
                        JSONObject jobj = item.getJSONObject("start");
                        Date start = new Date();
                        if (jobj.has("dateTime")) {
                            try {
                            	start = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").parse(jobj.getString("dateTime"));
                            } catch(ParseException e) {
                            }
                        } else {
                            try {
                            	start = new SimpleDateFormat("yyyy-MM-dd").parse(jobj.getString("date"));
                            } catch(ParseException e) {
                            }
                        }
                        itemHash.put("eventDate", new SimpleDateFormat("MMM dd").format(start));
                        String desc = "Start: " + new SimpleDateFormat("MMM dd, hh.mm a").format(start) + "\n";
                        jobj = item.getJSONObject("end");
                        if (jobj.has("dateTime")) {
                            try {
                            	start = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").parse(jobj.getString("dateTime"));
                            } catch(ParseException e) {
                            }
                        } else {
                            try {
                            	start = new SimpleDateFormat("yyyy-MM-dd").parse(jobj.getString("date"));
                            } catch(ParseException e) {
                            }
                        }
                        desc = desc + "End: " + new SimpleDateFormat("MMM dd, hh.mm a").format(start) + "\n";
                        if (item.has("description")) {
                        	desc = desc + item.getString("description");
                        }
                        itemHash.put("eventInfo", desc);
                        listItems.add(itemHash);
                    }
                    adapter.notifyDataSetChanged();
                    if (response.has("nextPageToken")) {
                    	nextPageToken = response.getString("nextPageToken");
                    } else {
                    	nextPageToken = null;
                    }
            	} catch(JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();            		
            	}
            }
            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(MainActivity.this, "Connection to internet failed.", Toast.LENGTH_LONG).show();            		
            }
            @Override
            public void onFinish() {
                progress.dismiss();
            }
        });    	
    }
}