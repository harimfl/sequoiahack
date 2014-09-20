package com.seqhack.olawars;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemAdapter extends ArrayAdapter<ItemRow> {

	List<ItemRow>   data; 
	Context context;
	int layoutResID;

public ItemAdapter(Context context, int layoutResourceId,List<ItemRow> data) {
	super(context, layoutResourceId, data);
	
	this.data=data;
	this.context=context;
	this.layoutResID=layoutResourceId;

	// TODO Auto-generated constructor stub
}
 
@Override
public View getView(int position, View convertView, ViewGroup parent) {
	
	NewsHolder holder = null;
	   View row = convertView;
	    holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);
            
            holder = new NewsHolder();
           
            holder.itemName = (TextView)row.findViewById(R.id.user_name);
            holder.olaPoints = (TextView)row.findViewById(R.id.olapoints);
            holder.playerRank = (TextView)row.findViewById(R.id.playerRank);
            holder.icon=(ImageView)row.findViewById(R.id.example_img);
            holder.button1=(Button)row.findViewById(R.id.swipe_button1);
            holder.button2=(Button)row.findViewById(R.id.swipe_button2);
            holder.button3=(Button)row.findViewById(R.id.swipe_button3);
            row.setTag(holder);
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }
        
        ItemRow itemdata = data.get(position);
        if (holder.itemName.getText() != itemdata.getUserName()) {
        	new DownloadImageTask(holder.icon, itemdata.getSnuid())
            .execute("https://graph.facebook.com/" + itemdata.getSnuid()
      				+ "/picture?type=normal&height=150&width=150");
		}
        holder.itemName.setText(itemdata.getUserName());
        holder.olaPoints.setText(itemdata.getOlaPoints());
        holder.playerRank.setText(itemdata.getPlayerRank());
        holder.button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Challenge Clicked",Toast.LENGTH_SHORT).show();
			}
		});
        
		 holder.button2.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "Add Team Clicked",Toast.LENGTH_SHORT).show();
					}
				});
		 
		 holder.button3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "View Clicked",Toast.LENGTH_SHORT).show();
				}
			});
        
        
        return row;
}

private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    String file;

    public DownloadImageTask(ImageView bmImage, String file) {
        this.bmImage = bmImage;
        this.file = file;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
        	File imgFile = new File(Olawars._staticInstance.getApplicationContext()
					.getFilesDir().getPath() + "/"
					+ this.file + ".jpg");
			// add check if file exists else show default icon
			if (!imgFile.exists()) {
				InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
				} else {
				mIcon11 = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
    	Log.d("", this.file);
        bmImage.setImageBitmap(result);
    }
}


static class NewsHolder{
	
	TextView itemName;
	TextView olaPoints;
	TextView playerRank;
	ImageView icon;
	Button button1;
	Button button2;
	Button button3;
	}	
}
