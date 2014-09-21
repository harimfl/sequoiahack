package com.seqhack.olawars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Olawars extends Activity {

    protected static final String TAG = Olawars.class.getName();
	SwipeListView swipelistview;
	ItemAdapter adapter;
	List<ItemRow> itemData;
	public static Olawars _staticInstance = null;
	
	public static String SENDER_ID = "925617659837"; //TOOD
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;
    Context context;
    String regid = "";
	
    private Button mButtonLogin;
    private TextView mTextStatus;
    
    private Session.StatusCallback callback;
    private UiLifecycleHelper uiHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        };
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_olawars);

        context = getApplicationContext();
        
        if (checkPlayServices()) {
            
            gcm = GoogleCloudMessaging.getInstance(this);
            context = getApplicationContext();
            regid = getRegistrationId(context);

            if (regid == "") {
                registerInBackground();
            }
        } else {
            Log.i("PlayServices", "No valid Google Play Services APK found.");
        }
        
        swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list); 

        mButtonLogin = (Button) findViewById(R.id.button_login);
        mTextStatus = (TextView) findViewById(R.id.text_status);        
        
        mButtonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Session session = Session.getActiveSession();
			    if (!session.isOpened() && !session.isClosed()) {
			        session.openForRead(new Session.OpenRequest(_staticInstance)
			            .setPermissions(/*Arrays.asList("public_profile")*/)
			            .setCallback(callback));
			    } else {
			        Session.openActiveSession(_staticInstance, true, callback);
			    }
			}
		});
        
        if(Session.getActiveSession().isOpened()) {
        	loggedInUIState();
        } else {
        	loggedOutUIState();
        }

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
    }
    
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    public void getDataFromServer(final String snuid, final String snat) {
        Thread t = new Thread(new Runnable() {
            public void run() {
            	String ssa = "http://ec2-54-169-61-49.ap-southeast-1.compute.amazonaws.com:4000/user/getfriendlb";
                HttpGet verifyRequest = new HttpGet(ssa);  
                DefaultHttpClient client = new DefaultHttpClient();
                try {
                	String msnuid = snuid;
                	String msnat = snat;
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("device_token", getRegistrationId(getApplicationContext())));
                    nameValuePairs.add(new BasicNameValuePair("access_token", msnat));
                    nameValuePairs.add(new BasicNameValuePair("snuid", msnuid));
                    
                    HttpResponse response = client.execute(verifyRequest);
                    if(response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        if(entity != null){
                            InputStream inputStream = entity.getContent();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder stringBuilder = new StringBuilder();

                            String ligneLue = bufferedReader.readLine();
                            while(ligneLue != null){
                                stringBuilder.append(ligneLue + " \n");
                                ligneLue = bufferedReader.readLine();
                            }
                            bufferedReader.close();
                            final String params = stringBuilder.toString();
                            _staticInstance.runOnUiThread(new Runnable() {
                                public void run() {
                                    onJsonResponse(params);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
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
    	itemData.add(new ItemRow(name, chips, snuid, rank, null));
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_send_pn1:
            	//notify1();
                return true;
            case R.id.action_send_pn2:
            	//notify1();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //TODO: (MUSTFIX) handle this case.
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("PlayServices", "This device is not supported.");
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId == "") {
            Log.i("PlayServices", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("PlayServices", "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Olawars.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("PlayServices", msg);
                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(msg != null) {
                    Log.i("PlayServices", "GCM post register msg - " + msg);
                }
            }
        }.execute(null, null, null);
    }
    
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("PlayServices", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void loggedInUIState() {
        mButtonLogin.setEnabled(false);
        mButtonLogin.setVisibility(View.GONE);
//        swipelistview.setVisibility(View.VISIBLE);
        mTextStatus.setText("");
        
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					// TODO Auto-generated method stub
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                        	getDataFromServer(user.getId(), session.getAccessToken());
                        }   
                    } 
				}
			}); 
            Request.executeBatchAsync(request);
        } 
    }

    private void loggedOutUIState() {
//        mButtonLogin.setVisibility(View.VISIBLE);
//        mButtonLogin.setEnabled(true);
//        swipelistview.setVisibility(View.GONE);
//        mTextStatus.setText("");
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
        	loggedInUIState();
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
        	loggedOutUIState();
            Log.i(TAG, "Logged out...");
        } else {
        	Log.i(TAG, "Doing something...");
        }
    }
}
