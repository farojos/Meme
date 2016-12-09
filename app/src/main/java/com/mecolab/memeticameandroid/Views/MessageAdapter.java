package com.mecolab.memeticameandroid.Views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mecolab.memeticameandroid.Activities.ConversationActivity;
import com.mecolab.memeticameandroid.Fragments.GalleryFragment;
import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import us.feras.mdv.MarkdownView;

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
        final Context context = getContext();
        final Message message = mMessages.get(position);
        if(message.mContent.contains("mma"))
            view = mInflater.inflate( R.layout.message_image_audio_list_item, parent, false);
        else
            view = mInflater.inflate(getLayout(message.mType), parent, false);
        if (view == null) {
            if(message.mContent.contains("mma"))
                view = mInflater.inflate( R.layout.message_image_audio_list_item, parent, false);
            else

                view = mInflater.inflate(getLayout(message.mType), parent, false);
        }

        /*
        if(message.mSender.equals(User.getLoggedUser(context).mPhoneNumber)){
            //view.set
            //android:layout_alignParentRight="true"
            //android:layout_gravity="right"
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.


            view.setLayoutParams(params);
            Log.d("SENDER",message.mSender);
            Log.d("SENDER","Number: WIII:  "+User.getLoggedUser(context).mPhoneNumber);
        }
        else
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.LEFT;
            Log.d("SENDER",message.mSender);
            Log.d("SENDER","Number: D: :C :  "+User.getLoggedUser(context).mPhoneNumber);

            view.setLayoutParams(params);
        }*/
        //User.getLoggedUser(context).mPhoneNumber;
        if(message.mContent.contains("mma")){
            //view = mInflater.inflate( R.layout.message_image_audio_list_item, parent, false);
            String path = GalleryFragment.BASE_PATH;
            ImageView contentView = (ImageView) view.findViewById(R.id.image_audio_item);
            String fotopath = message.mContent;
            fotopath=fotopath.replace(".mma","");
            fotopath = fotopath + "/";
            fotopath = fotopath.replace("file://","");
            File dir= new File(fotopath);
            Uri audioUri=null;
            Uri imageUri=null;
            if (!dir.exists()) {
                if (!dir.mkdirs()){
                    Log.e("DIRECTORY","Problems with directory creation");
                    String[] unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    //String[] unpacked =  ConversationActivity.getOnlyStrin(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    if(!new File(unpacked[0]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    if(!new File(unpacked[1]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    File audio =  new File(unpacked[1]);
                    File image = new File(unpacked[0]);
                    audioUri = Uri.fromFile(audio);
                    imageUri = Uri.fromFile(image);
                }
                else
                {
                    String[] unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    //String[] unpacked =  ConversationActivity.getOnlyStrin(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    if(!new File(unpacked[0]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    if(!new File(unpacked[1]).exists())
                        unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                    File audio =  new File(unpacked[1]);
                    File image = new File(unpacked[0]);
                    audioUri = Uri.fromFile(audio);
                    imageUri = Uri.fromFile(image);
                   // Gallery g = new Gallery(null,files[i].getName(),mime,Uri.parse(unpacked[0]));
                  //  g.setSongUri(Uri.parse(unpacked[1]));
                    //gallerys.add(g);
                  //  continue;

                }

            }
            else{
                String[] unpacked =  ConversationActivity.getOnlyStrin(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                if(!new File(unpacked[0]).exists())
                    unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                if(!new File(unpacked[1]).exists())
                    unpacked =  ConversationActivity.unpackZip(path,"/"+message.mContent.split("/")[message.mContent.split("/").length-1]);
                File audio =  new File(unpacked[1]);
                File image = new File(unpacked[0]);
                audioUri = Uri.fromFile(audio);
                imageUri = Uri.fromFile(image);
                //Gallery g = new Gallery(null,files[i].getName(),mime,Uri.fromFile(image));
                //g.setSongUri(Uri.fromFile(audio));
               // gallerys.add(g);
               //continue;

            }


            Glide.with(context).load(imageUri.getPath()).into(contentView);
            final Uri auU=audioUri;
            final MediaPlayer mPlayer = new MediaPlayer();
            ImageView contentView2 = (ImageView) view.findViewById(R.id.msg_audio_play);

            contentView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = auU.getPath();
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
            });
            contentView2 = (ImageView) view.findViewById(R.id.msg_audio_pause);
            contentView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayer.pause();
                }
            });
            contentView2 = (ImageView) view.findViewById(R.id.msg_audio_stop);
            contentView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayer.stop();
                    MessageAdapter.play=false;
                    MessageAdapter.created=false;
                    mPlayer.pause();
                }
            });

        }else
        if (message.mType == Message.MessageType.TEXT) {
            MarkdownView contentView =
                    (MarkdownView) view.findViewById(R.id.markdownView);
            contentView.loadMarkdown(message.mContent);

            ViewGroup.LayoutParams l = view.getLayoutParams();

            ViewGroup.LayoutParams currentParams = view.getLayoutParams();
            FrameLayout.LayoutParams newHeaderParams;
            RelativeLayout r = (RelativeLayout) view;
            //RelativeLayout.LayoutParams laa = (RelativeLayout.LayoutParams)r.getLayoutParams();
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            LinearLayout.LayoutParams lll = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            lll.gravity=Gravity.RIGHT;
            r.setLayoutParams(lll);
            view.setLayoutParams(lll);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int gravity = Gravity.RIGHT;/*
            if (currentParams != null) {
                newHeaderParams = new FrameLayout.LayoutParams(view.getLayoutParams()); //to copy all the margins
                newHeaderParams.width = width;
                newHeaderParams.height = height;
                newHeaderParams.gravity = gravity;
            } else {
                newHeaderParams = new FrameLayout.LayoutParams(width, height, gravity);
            }
            view.setLayoutParams(newHeaderParams);*/
            //LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) view.getLayoutParams();
            //l.gravity= Gravity.RIGHT;




            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                                                   @Override
                                                   public boolean onLongClick(View v) {
                                                       ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                       ClipData clip = ClipData.newPlainText("label", "text|"+message.mContent);
                                                       clipboard.setPrimaryClip(clip);
                                                       Toast.makeText(context, "Copy Success", Toast.LENGTH_SHORT).show();
                                                       return false;
                                                   }
                                               });

        }
        else if (message.mType == Message.MessageType.IMAGE) {

            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_Content);
           // Picasso.with(parent.getContext()).load(message.mContent).into(contentView);
           // final int THUMBSIZE = 64;
            final String s = Uri.parse(message.mContent).getPath();
            String ss = Uri.parse(message.mContent).getPath();
            File file = new File(ss);
            if(file.length()/1024<700){
                isD.put(message.mContent,true);
            }

            if(!isD.containsKey(message.mContent)) {
               // Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(s),
               //         THUMBSIZE, THUMBSIZE);
                //contentView.setImageBitmap(ThumbImage);
                Glide.with(parent.getContext()).load(message.mContent).into(contentView);
                Button b = (Button) view.findViewById(R.id.msg_image_btn);
                if(!isD.containsKey(message.mContent)){
                    //final Context context = getContext();
                    contentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copy image success", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    });
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewGroup row = (ViewGroup) v.getParent();
                            ImageView contentView = (ImageView) row.findViewById(R.id.MessageListItem_Content);
                            final int THUMBSIZE = 300;
                            //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(s),
                           //         THUMBSIZE, THUMBSIZE);
                            //contentView.setImageBitmap(ThumbImage);
                            Glide.with(context).load(message.mContent).into(contentView);
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
                    });}
                else
                {
                    ((ViewGroup)b.getParent()).removeView(b);
                }

            }
            else {
                Button b = (Button) view.findViewById(R.id.msg_image_btn);
                ((ViewGroup)b.getParent()).removeView(b);

               // Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(s),
               //         300, 300);
                Glide.with(parent.getContext()).load(message.mContent).into(contentView);
                //contentView.setImageBitmap(ThumbImage);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setDataAndType(Uri.parse(message.mContent),
                                "image/*");
                        getContext().startActivity(i);//startActivity(i);
                    }
                });
                contentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copy image success", Toast.LENGTH_SHORT).show();
                        return true;
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
            String ss = Uri.parse(message.mContent).getPath();
            File file = new File(ss);
            if(file.length()/1024<700){
                isD.put(message.mContent,true);
            }

            if(!isD.containsKey(message.mContent))
                 thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(message.mContent).getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            else {
                thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(message.mContent).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                Message m = mMessages.get(position);
                m.setisDown(true);
            }

            contentView.setImageBitmap(thumb);

            Button b = (Button) view.findViewById(R.id.msg_video_btn);
            if(!isD.containsKey(message.mContent)){
                contentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copy video success", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
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
            });}
            else
            {
                ((ViewGroup)b.getParent()).removeView(b);
            }

            //Picasso.with(parent.getContext()).load(message.mContent).into(contentView);
        }

        else if (message.mType == Message.MessageType.AUDIO){
            final MediaPlayer mPlayer = new MediaPlayer();
            ImageView contentView = (ImageView) view.findViewById(R.id.msg_audio_play);
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copy audio success", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*
                    if(play){
                        MessageAdapter.mPlayer.pause();
                        MessageAdapter.play=false;
                        //MessageAdapter.mPlayer=null;

                    } else {*/
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
                    //}
                }
            });
            contentView = (ImageView) view.findViewById(R.id.msg_audio_pause);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        mPlayer.pause();
                }
            });
            contentView = (ImageView) view.findViewById(R.id.msg_audio_stop);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                   mPlayer.stop();
                    MessageAdapter.play=false;
                    MessageAdapter.created=false;
                    mPlayer.pause();
                    /*String s = Uri.parse(message.mContent).getPath();
                    try {
                        if (!MessageAdapter.created) {
                            mPlayer.setDataSource(s);
                            mPlayer.prepare();
                        }
                        mPlayer.pause();
                    }catch (Exception e){}*/
                   // MessageAdapter.created=false;

                    //}
                }
            });

        }
        else if (message.mType == Message.MessageType.OTHER ) {
            ImageView contentView = (ImageView) view.findViewById(R.id.MessageListItem_ClipImage);
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copy file success", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
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
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", "file|"+message.mContent);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copy file success", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        TextView authorView = (TextView) view.findViewById(R.id.MessageListItem_Author);
        if(!message.mContent.contains("mma"))
            authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName);

        if(message.mType == Message.MessageType.AUDIO || message.mType == Message.MessageType.VIDEO || message.mType == Message.MessageType.IMAGE) {
            String s = Uri.parse(message.mContent).getPath();
            File file = new File(s);
            authorView = (TextView) view.findViewById(R.id.size);
            authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName+"-Size: "+file.length()/1024+"KB");
        }
        if(message.mType == Message.MessageType.OTHER && !message.mContent.contains("mma")) {
            String s = Uri.parse(message.mContent).getPath();
            File file = new File(s);
            //authorView = (TextView) view.findViewById(R.id.size);

                authorView.setText(User.getUserOrCreate(getContext(), message.mSender).mName);

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
