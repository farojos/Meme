package com.mecolab.memeticameandroid.Views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Models.Gallery;
import com.mecolab.memeticameandroid.R;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<Gallery> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Gallery>  data ;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        //this.data=data;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder ;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.gallery_text);
         //   holder.image = (ImageView) row.findViewById(R.id.gallery_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Gallery item = (Gallery) data.get(position);
        holder.imageTitle.setText(item.getTitle());
       // Log.d("Vista",item.getTitle());
      //  holder.image.setImageBitmap(item.getImage());
        return row;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("FOO", "item count: " + getCount());
    }

    @Override
    public int getCount(){
        return super.getCount();
    }
    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}
