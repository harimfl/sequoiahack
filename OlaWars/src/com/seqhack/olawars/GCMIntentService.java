package com.seqhack.olawars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public static final String TAG = GCMIntentService.class.getName();
    
    public GCMIntentService() {
        super(Olawars.SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	Bundle b=intent.getExtras();
    	notify(b, context);
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
	
    public void notify(Bundle b, Context context) {    	
    	int type = Integer.parseInt(b.getString("type"));

    	// Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getBroadcast(GCMIntentService.this, 1, new Intent(GCMIntentService.this, NotificationView.class), PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.icon).setAutoCancel(true).setContentIntent(pIntent).setContent(remoteViews);
        
        String other_name, traveller_snuid, other_snuid, traveller_name, num_people;
        
        switch(type) {
        case 1:
        	other_name = b.getString("other_name");
        	traveller_snuid = b.getString("traveller_snuid");
        	other_snuid = b.getString("other_snuid");
        	remoteViews.setViewVisibility(R.id.cardback1, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback2, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback3, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback33, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback4, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback5, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback55, View.VISIBLE);
        	remoteViews.setTextViewText(R.id.notiftext1, "You Ran Over " + other_name);
        	remoteViews.setImageViewBitmap(R.id.cardback33, getBitmapFromURL("https://graph.facebook.com/" + traveller_snuid + "/picture?type=normal&height=150&width=150", traveller_snuid));
        	remoteViews.setImageViewBitmap(R.id.cardback55, getBitmapFromURL("https://graph.facebook.com/" + other_snuid + "/picture?type=normal&height=150&width=150", other_snuid));
        	break;
        case 2:
        	traveller_name = b.getString("traveller_name");
        	traveller_snuid = b.getString("traveller_snuid");
        	other_snuid = b.getString("other_snuid");
        	remoteViews.setViewVisibility(R.id.cardback1, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback2, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback3, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback33, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback4, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback5, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback55, View.VISIBLE);
        	remoteViews.setTextViewText(R.id.notiftext1, traveller_name + " Ran you over");
        	remoteViews.setImageViewBitmap(R.id.cardback33, getBitmapFromURL("https://graph.facebook.com/" + traveller_snuid + "/picture?type=normal&height=150&width=150", traveller_snuid));
        	remoteViews.setImageViewBitmap(R.id.cardback55, getBitmapFromURL("https://graph.facebook.com/" + other_snuid + "/picture?type=normal&height=150&width=150", other_snuid));
        	break;
        case 3:
        	num_people = b.getString("num_people");
        	remoteViews.setViewVisibility(R.id.cardback1, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback2, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback3, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback33, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback4, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback5, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback55, View.INVISIBLE);
        	remoteViews.setTextViewText(R.id.notiftext1, "You Ran Over " + num_people + " People");
        	break;
        case 4:
        	num_people = b.getString("num_people");
        	remoteViews.setViewVisibility(R.id.cardback1, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback2, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback3, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback33, View.INVISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback4, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback5, View.VISIBLE);
        	remoteViews.setViewVisibility(R.id.cardback55, View.VISIBLE);
        	remoteViews.setTextViewText(R.id.notiftext1, num_people + " People Ran you over");
        	break;
        	
        }
 
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        Notification notification = builder.build();
        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.car_crash);
        notificationmanager.notify(1, notification);
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