package com.rohanmaharaj.owntry.projectdapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards>  {
    //This is the adapter which is going to control everything
    //This is called at a positiona dn populates it in correct array position with the correct card

    //context for passing
    Context context;
    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent){
        //now we are going to get the card which is specific to the current view of teh screen
        cards card_item = getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        //now add random image fro time being and after that use glide library to add actual profile images
        image.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }

}
