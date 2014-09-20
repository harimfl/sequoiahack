package com.seqhack.olawars;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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
           
            holder.itemName = (TextView)row.findViewById(R.id.example_itemname);
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
        holder.itemName.setText(itemdata.getItemName());
        holder.icon.setImageDrawable(itemdata.getIcon());
      
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



static class NewsHolder{
	
	TextView itemName;
	ImageView icon;
	Button button1;
	Button button2;
	Button button3;
	}
	
	
}




