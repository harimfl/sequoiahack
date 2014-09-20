package com.seqhack.olawars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService
{ 
	private NotificationManager mManager;
    public static final String TAG = GCMIntentService.class.getName();
    
    public GCMIntentService() {
        super("925617659837");
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	Bundle b=intent.getExtras();
    	int type = Integer.parseInt(b.getString("type"));
    	String param = b.getString("param");

    	notify1(context);
    	/*
    	mManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    	Intent intent1 = new Intent(this.getApplicationContext(), Olawars.class);
    	
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
	    
	    
	    mManager.notify(1, notification);
	    */
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
	
    public void notify1(Context context) {
    	// Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
 
        // Set Notification Title
        String strtitle = getString(R.string.customnotificationtitle);
        // Set Notification Text
        String strtext = getString(R.string.customnotificationtext);
 
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(GCMIntentService.this, NotificationView.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        
        // Open NotificationView.java Activity
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent = PendingIntent.getBroadcast(GCMIntentService.this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
 
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true).setContentIntent(pIntent).setContent(remoteViews);

        remoteViews.setTextViewText(R.id.notiftext1, "Something I want");
        remoteViews.setViewVisibility(R.id.cardback2, View.INVISIBLE);
        
        //String snuid = "param6";
		//remoteViews.setImageViewBitmap(R.id.cardback33, getBitmapFromURL("https://graph.facebook.com/" + snuid + "/picture?type=normal&height=150&width=150", snuid));
 
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(1, builder.build());
    }
    
	public Bitmap getBitmapFromURL(String strURL, String senderId) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			// set up some things on the connection
			connection.setRequestMethod("GET");
			connection.setDoOutput(false);
			connection.connect();

			File imgFile = new File(this.getApplicationContext().getFilesDir().getPath() + "/", senderId + ".jpg");
			FileOutputStream fileOutput = new FileOutputStream(imgFile);

			InputStream input = connection.getInputStream();

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = input.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			fileOutput.close();

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}