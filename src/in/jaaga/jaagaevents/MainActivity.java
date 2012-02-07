package in.jaaga.jaagaevents;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter;
    
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
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("eventDate", "05/02");
        item.put("eventName", "Android Class");
        item.put("eventInfo", "Info on Android Class event");
        listItems.add(item);
        HashMap<String, String> item2 = new HashMap<String, String>();
        item2.put("eventDate", "05/02");
        item2.put("eventName", "CodeYear Class");
        item2.put("eventInfo", "Info on CodeYear Class event");
        listItems.add(item2);
        adapter.notifyDataSetChanged();    	
    }

    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	String info = listItems.get(position).get("eventInfo");
    	Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    }
}