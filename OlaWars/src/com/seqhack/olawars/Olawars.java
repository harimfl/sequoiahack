package com.seqhack.olawars;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.seqhack.olawars.ItemAdapter.NewsHolder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;

public class Olawars extends Activity {

	SwipeListView swipelistview;
	ItemAdapter adapter;
	List<ItemRow> itemData;
	public static Olawars _staticInstance = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olawars);
        
        swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list); 
        itemData=new ArrayList<ItemRow>();
        adapter=new ItemAdapter(this,R.layout.custom_row,itemData);
        _staticInstance = this;
     
        
        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                
             
                swipelistview.openAnimate(position); //when you touch front view it will open
              
             
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
                
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            	
            }

        });
        
        //These are the swipe listview settings. you can change these
        //setting as your requirement 
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); //there are four swipe actions 
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipelistview.setOffsetLeft(convertDpToPixel(50f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(50f)); // right side offset
        swipelistview.setAnimationTime(400); // Animation time
        swipelistview.setSwipeCloseAllItemsWhenMoveList(true);
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
	
        swipelistview.setAdapter(adapter);
        onJsonResponse("");
    }
    
    public void onJsonResponse(String json) {
    	JSONParser parser=new JSONParser();
        Object obj = null;
        json = "{	\"top\" : [			{				\"name\" : \"hari\",				\"rank\" : 1,				\"snuid\" : \"1623842314\",				\"chips\"	: 3444			},			{				\"name\" : \"gitesh\",				\"rank\" : 2,				\"snuid\" : \"1283286538\",				\"chips\"	: 5444			},			{				\"name\" : \"srinaths\",				\"rank\" : 3,				\"snuid\" : \"524740442\",				\"chips\"	: 7444			}		],	\"rest\" : [			{				\"name\" : \"srinath1\",				\"rank\" : 6,				\"snuid\" : \"134345987234987\",				\"chips\"	: 3144			},			{				\"name\" : \"srinath2\",				\"rank\" : 8,				\"snuid\" : \"134345987234987\",				\"chips\"	: 3644			},			{				\"name\" : \"srinath3\",				\"rank\" : 9,				\"snuid\" : \"134593487234987\",				\"chips\"	: 3044			}	]}";
		try {
			obj = parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray topPlayers = (JSONArray)jsonObject.get("top");
        JSONArray restPlayer = (JSONArray)jsonObject.get("rest");

        
        for(int i=0; i<topPlayers.size() ; i++){
            JSONObject ob = (JSONObject)topPlayers.get(i);
            
            String name = ob.get("name").toString();
            String rank = ob.get("rank").toString();
            String snuid = ob.get("snuid").toString();
            String chips = ob.get("chips").toString();
            addRowToLeaderBoard(name, rank, snuid, chips);
        }
        for(int i=0; i<restPlayer.size() ; i++){
        	   JSONObject ob = (JSONObject)restPlayer.get(i);
               
               String name = ob.get("name").toString();
               String rank = ob.get("rank").toString();
               String snuid = ob.get("snuid").toString();
               String chips = ob.get("chips").toString();
               addRowToLeaderBoard(name, rank, snuid, chips);
               addRowToLeaderBoard(name, rank, snuid, chips);
               addRowToLeaderBoard(name, rank, snuid, chips);
               addRowToLeaderBoard(name, rank, snuid, chips);
        }
        adapter.notifyDataSetChanged();
    }
    
    public void addRowToLeaderBoard(String name, String rank, String snuid, String chips) {
    	itemData.add(new ItemRow(name, rank, snuid, chips, null));
    }
	
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.olawars, menu);
        return true;
    }
    
}
