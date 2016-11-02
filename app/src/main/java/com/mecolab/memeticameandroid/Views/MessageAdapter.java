package com.mecolab.memeticameandroid.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.R;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageAdapter extends ArrayAdapter<Message> {
    private LayoutInflater mInflater;
    private ArrayList<Message> mMessages;
    static HashMap<String,Boolean> isD;
    static HashMap<Integer, Integer> sLayouts;
    static HashMap<Message.MessageType, Integer> sTypesInt;
    static boolean play;
    static  MediaPlayer mPlayer;
    static boolean created;
    static {
        play=false;
        created=false;
        sLayouts = new HashMap<>();
        sTypesInt = new HashMap<>();
        isD=new HashMap<>();
        sTypesInt.put(Message.MessageType.TEXT, 0);
        sLayouts.put(0, R.layout.message_text_list_item);
        sTypesInt.put(Message.MessageType.IMAGE, 1);
        sLayouts.put(1, R.layout.message_image_list_item);
        sTypesInt.put(Message.MessageType.AUDIO, 2);
        sLayouts.put(2, R.layout.message_audio_list_item);
        sTypesInt.put(Message.MessageType.VIDEO, 3);
        sLayouts.put(3, R.layout.message_video_list_item);
        sTypesInt.put(Message.MessageType.OTHER, 4);
        sLayouts.put(4, R.layout.message_other_list_item);
        sTypesInt.put(Message.MessageType.NOT_SET, 5);
        sLayouts.put(5, R.layout.message_text_list_item);
    }

    public MessageAdapter(Context context, int resource, ArrayList<Message> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMessages = objects;

    }
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File("file:///storage/emulated/0/MemeticaMe/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("ERROR",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Message message = mMessages.get(position);
        view = mInflater.inflate(getLayout(message.mType), parent, false);
        if (view == null) {
            view = mInflater.inflate(getLayout(message.mType), parent, false);
        }
        if (message.mType == Message.MessageType.TEXT) {
            EmojiconTextView contentView =
                    (EmojiconTextView) view.findViewById(R.id.MessageListItem_Content);
            contentView.setText(message.mContent);
        }
        else if (message.mType == Message.MessageType.IMAGE) {

            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_Content);
           // Picasso.with(parent.getContext()).load(message.mContent).into(contentView);
            final int THUMBSIZE = 64;
            String s = Uri.parse(message.mContent).getPath();


            if(!isD.containsKey(message.mContent)) {
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(s),
                        THUMBSIZE, THUMBSIZE);
                contentView.setImageBitmap(ThumbImage);

                Button b = (Button) view.findViewById(R.id.msg_image_btn);
                if(!isD.containsKey(message.mContent))
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewGroup row = (ViewGroup) v.getParent();
                            ImageView contentView = (ImageView) row.findViewById(R.id.MessageListItem_Content);
                            final int THUMBSIZE = 64;
                            Picasso.with(row.getContext()).load(message.mContent).into(contentView);
                            //contentView.setImageBitmap(thumb);
                            row.removeView(v);
                            message.setisDown(true);
                            isD.put(message.mContent,true);
                            contentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent();
                                    Log.d("PATH0",message.mContent);
                                    i.setAction(android.content.Intent.ACTION_VIEW);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.setDataAndType(Uri.parse(message.mContent),
                                            "image/*");
                                    getContext().startActivity(i);//startActivity(i);
                                }
                            });

                        }
                    });
                else
                {
                    ((ViewGroup)b.getParent()).removeView(b);
                }

            }
            else {
                Button b = (Button) view.findViewById(R.id.msg_image_btn);
                ((ViewGroup)b.getParent()).removeView(b);
                Picasso.with(parent.getContext()).load(message.mContent).into(contentView);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.d("PATH0",message.mContent);
                        i.setDataAndType(Uri.parse(message.mContent),
                                "image/*");
                        getContext().startActivity(i);//startActivity(i);
                    }
                });
            }
        }
        /*
        else if(message.mType == Message.MessageType.AUDIO){
            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_ClipImage);
            TextView fileName = (TextView) view.findViewById(R.id.MessageListItem_FileName);

        }*/
        else if (message.mType == Message.MessageType.VIDEO) {

            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_Content);
           // Log.d("Mensaje Video","Soy un video q wa?:"+Uri.parse(message.mContent).getPath());
            //contentView.setImageURI(Uri.parse(message.mContent));
            Bitmap thumb;
            //message.mContent

            if(!isD.containsKey(message.mContent))
                 thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(message.mContent).getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            else {
                thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(message.mContent).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                Message m = mMessages.get(position);
                m.setisDown(true);
            }

            contentView.setImageBitmap(thumb);

            Button b = (Button) view.findViewById(R.id.msg_video_btn);
            if(!isD.containsKey(message.mContent))
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup row = (ViewGroup) v.getParent();
                    ImageView contentView = (ImageView) row.findViewById(R.id.MessageListItem_Content);

                    Bitmap  thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(message.mContent).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    contentView.setImageBitmap(thumb);
                    row.removeView(v);
                    message.setisDown(true);
                    isD.put(message.mContent,true);
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setDataAndType(Uri.parse(message.mContent),
                                    "video/*");
                            getContext().startActivity(i);//startActivity(i);
                        }
                    });

                }
            });
            else
            {
                ((ViewGroup)b.getParent()).removeView(b);
            }

            //Picasso.with(parent.getContext()).load(message.mContent).into(contentView);
        }

        else if (message.mType == Message.MessageType.AUDIO){
            final MediaPlayer mPlayer = new MediaPlayer();
            ImageView contentView = (ImageView) view.findViewById(R.id.msg_audio_play);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(play){
                        MessageAdapter.mPlayer.pause();
                        MessageAdapter.play=false;
                        //MessageAdapter.mPlayer=null;

                    } else {
                        String s = Uri.parse(message.mContent).getPath();
                        try {
                            if(!MessageAdapter.created) {
                                mPlayer.setDataSource(s);
                                mPlayer.prepare();
                            }
                            mPlayer.start();
                            MessageAdapter.play=true;
                            MessageAdapter.created=true;
                            MessageAdapter.mPlayer=mPlayer;
                        }
                        catch (Exception e ){

                        }
                    }
                }
            });

        }
        else if (message.mType == Message.MessageType.OTHER ) {
            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_ClipImage);
            TextView fileName = (TextView) view.findViewById(R.id.MessageListItem_FileName);
            try {
                fileName.setText(String.valueOf(message.mMimeType.split("/")[1].toUpperCase()));
            }
            catch(Exception e){
                fileName.setText(String.valueOf(message.mMimeType.split("/")[0].toUpperCase()));
            }
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(message.mContent), message.mMimeType);
                    try {
                        getContext().startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Can't open this type of file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if (message.mType == Message.MessageType.NOT_SET) {
            TextView contentView = (TextView) view.findViewById(R.id.MessageListItem_Content);
            contentView.setText(message.mContent);
        }

        TextView authorView = (TextView) view.findViewById(R.id.MessageListItem_Author);
        authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName);
        if(message.mType == Message.MessageType.AUDIO || message.mType == Message.MessageType.VIDEO || message.mType == Message.MessageType.IMAGE) {
            String s = Uri.parse(message.mContent).getPath();
            File file = new File(s);
            authorView = (TextView) view.findViewById(R.id.size);
            authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName+"-Size: "+file.length()/1024+"KB");
        }
        if(message.mType == Message.MessageType.OTHER) {
            String s = Uri.parse(message.mContent).getPath();
            File file = new File(s);
            //authorView = (TextView) view.findViewById(R.id.size);
            authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName+"-Size: "+file.length()/1024+"KB");
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return Message.MessageType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Message.MessageType type = mMessages.get(position).mType;
        if (type == null) type = Message.MessageType.TEXT;
        return sTypesInt.get(type);
    }

    private int getLayout(Message.MessageType type) {
        if (type == null) type = Message.MessageType.TEXT;
        return sLayouts.get(sTypesInt.get(type));
    }
}
