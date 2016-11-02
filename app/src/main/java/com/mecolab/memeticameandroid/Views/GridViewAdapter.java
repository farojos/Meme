package com.mecolab.memeticameandroid.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
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
            holder.image = (ImageView) row.findViewById(R.id.gallery_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Gallery item = (Gallery) data.get(position);
        holder.imageTitle.setText(item.getTitle());
        //holder.imageTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
       // Log.d("Vista",item.getTitle());
        holder.image.setOnClickListener(null);
        if(item.getMime().equals("image")){
            final Uri path = item.getUri();
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(android.content.Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setDataAndType(path,
                            "image/*");
                    getContext().startActivity(i);
                }
            });}
        else if(item.getMime().equals("video")){
            final Uri path = item.getUri();
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(android.content.Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setDataAndType(path,
                            "video/*");
                    getContext().startActivity(i);//startActivity(i);
                }
            });}
        else if(item.getMime().equals("audio")){
            final Uri path = item.getUri();
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(android.content.Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setDataAndType(path,
                            "audio/*");
                    getContext().startActivity(i);//startActivity(i);
                }
            });

        }
        else if(item.getMime().equals("fotoaudio")){

        }
        else if(item.getMime().equals("other")){

        }
        Bitmap bitmap = Bitmap.createScaledBitmap(item.getImage(),dpToPx(100),dpToPx(100),true);

        holder.image.setImageBitmap(bitmap);
        return row;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
