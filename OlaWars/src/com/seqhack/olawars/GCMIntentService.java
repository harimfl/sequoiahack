package com.seqhack.olawars;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService
{ 
	private NotificationManager mManager;
    public static final String TAG = GCMIntentService.class.getName();
    
    public GCMIntentService() {
        super(bingo.SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	Bundle b=intent.getExtras();
    	int type = Integer.parseInt(b.getString("type"));
		String title = b.getString("title");
		String text = b.getString("text");
		String param1 = b.getString("param1");
		String param2 = b.getString("param2");
		String param3 = b.getString("param3");
		
		// Getting Notification Service
        String action = "Tap to play now!"; 

    	mManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    	Intent intent1 = new Intent(this.getApplicationContext(), bingo.class);
    	
    	JSONObject paramList = new JSONObject();
    	try {
            paramList.put("notifType", "push");
            paramList.put("title", title);
    		paramList.put("text", text);
    		paramList.put("param1", param1);
    		paramList.put("param2", param2);
    		paramList.put("param3", param3);
    		paramList.put("type", type);
    	} catch (JSONException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	}
    	intent1.setData((Uri.parse(paramList.toString())));

    	Notification notification = new Notification(R.drawable.icon, action, System.currentTimeMillis());
    	intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

	    PendingIntent pendingNotificationIntent = PendingIntent.getActivity(
	    		this.getApplicationContext(), 5, intent1,
	    PendingIntent.FLAG_UPDATE_CURRENT);
	
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;

	    notification.setLatestEventInfo(this.getApplicationContext(), title, text, pendingNotificationIntent);
	    
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    
	    if((bingo._staticInstance != null) && (!bingo.is_app_on_foreground)) {
	    	mManager.notify(1, notification);
	    }
    }

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}