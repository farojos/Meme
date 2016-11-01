package com.mecolab.memeticameandroid.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.mecolab.memeticameandroid.Activities.MainActivity;
import com.mecolab.memeticameandroid.FileUtils.FileManager;
import com.mecolab.memeticameandroid.MemeticameApplication;
import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.Networking.NetworkingManager;
import com.mecolab.memeticameandroid.R;

import java.util.ArrayList;


/**
 * Created by Andres on 04-11-2015.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "GcmListenerService";
    public static boolean isMyGroup=false;

    @Override
    public void onMessageReceived(String from, final Bundle data) {

        Log.d(TAG, "From: " + from);

        if (data.getString("collapse_key").equals("new_message")) {
            int conversationId = Integer.valueOf(data.getString("conversation_id"));
            String content = data.getString("message");
            String sender = data.getString("sender");
            int id = Integer.valueOf(data.getString("id"));
            Conversation conversation = Conversation.getConversation(this, conversationId);
            String conversationTitle = "New conversation";
            if (conversation != null) {
                Message.Builder builder = new Message.Builder();
                conversationTitle = conversation.mTitle;
                builder .setSender(sender)
                        .setConversationId(conversationId)
                        .setServerId(id);
                if (data.getString("mime_type").equals("plain/text")) {
                    builder.setContent(content).setMimeType("plain/text");

                }
                else {
                    try {
                        builder.setMimeType(data.getString("mime_type"))
                                .setContent(NetworkingManager.downloadFile(data.getString("link"), FileManager.BASE_PATH +
                                        "/" + FileManager.generateFileName(data.getString("mime_type"))).toString());
                    }
                    catch(Exception e){
                        final Context context = getBaseContext();
                        //  Toast.makeText(getBaseContext(), "Hello", Toast.LENGTH_LONG).show();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(
                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(context, R.string.Download_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    //new CustomTask().execute((Void[])null);
                    //



                    content = "File received";
                }
                final Message message = builder.build();
                if (message.save(this)) {
                    Intent intent = new Intent();
                    intent.putExtra("SERVER_ID", message.mServerId);
                    intent.setAction(MemeticameApplication.MESSAGE_RECEIVED_ACTION);
                    sendBroadcast(intent);
                    sendNotification(conversationTitle, content, sender);
                }
            }
        }
        else if (data.getString("collapse_key").equals("new_group_conversation")) {
            int conversationId = Integer.valueOf(data.getString("conversation_id"));
            String title = data.getString("title");
            String admin = data.getString("admin");
            String createdAt = data.getString("created_at");
            String participantsString = data.getString("participants");
            String[] participantsNumber = participantsString.split(",");
            ArrayList<User> participants = new ArrayList<>();
            for (String number : participantsNumber) {
                participants.add(User.getUserOrCreate(this, number.replaceAll("\\D+","")));
            }
            if (Conversation.getConversation(this, conversationId) == null) {
                Conversation conversation = new Conversation(0, conversationId, title
                        , admin, createdAt,
                        true, participants);
                conversation.save(this);
            }
            //String sender =from;
            if (!isMyGroup) {
                sendNotification("INVITATION", " want you to join to this new group", admin );
                isMyGroup=false;
            }
        }
        else if (data.getString("collapse_key").equals("new_two_conversation")) {
            int conversationId = Integer.valueOf(data.getString("conversation_id"));
            String createdAt = data.getString("created_at");
            String participantsString = data.getString("participants");
            String[] participantsNumber = participantsString.split(",");
            ArrayList<User> participants = new ArrayList<>();
            for (String number : participantsNumber) {
                participants.add(User.getUserOrCreate(this, number.replaceAll("\\D+","")));
            }
            User admin = participants.get(1);
            User other = participants.get(0);
            if (participants.get(0).mPhoneNumber.equals(User.getLoggedUser(this).mPhoneNumber)) {
                admin = participants.get(0);
                other = participants.get(1);
            }

            if (Conversation.getConversation(this, conversationId) == null) {
                Conversation conversation = new Conversation(0, conversationId, other.mPhoneNumber,
                        admin.mPhoneNumber, createdAt, false, participants);
                conversation.save(this);
            }
        }
    }

    private void sendNotification(String title, String content, String sender) {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.whatsicon)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentText(User.getUserOrCreate(this, sender).mName +": " + content);
        // Sets an ID for the notification
        int mNotificationId = 001;


        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }






}

