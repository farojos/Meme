package com.mecolab.memeticameandroid.Views;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.R;

import java.util.ArrayList;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private LayoutInflater mInflater;
    private ArrayList<Conversation> mConversations;

    public ConversationAdapter(Context context, int resource, ArrayList<Conversation> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mConversations = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Conversation conversation = mConversations.get(position);
       /*if(conversation.mIsGroup)
           Log.d("Mensaje","Grupo"+conversation.mTitle);
        else
            Log.d("Mensaje","Melospaseo"+conversation.mTitle);*/
        if (view == null) {
            if(conversation.mIsGroup)
                view = mInflater.inflate(R.layout.conversation_group_list_item , parent, false);
            else
                view = mInflater.inflate(R.layout.conversation_list_item , parent, false);
        }
        else
        {
            if(conversation.mIsGroup)
                view = mInflater.inflate(R.layout.conversation_group_list_item , parent, false);
            else
                view = mInflater.inflate(R.layout.conversation_list_item , parent, false);
        }


        if (conversation.mHasNewMessage) {
            view.setBackgroundColor(Color.CYAN);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        TextView conversationTitleView = (TextView) view.findViewById(R.id.ConversationListItem_Title);
        if(!conversation.mIsGroup)
            conversationTitleView = (TextView) view.findViewById(R.id.ConversationListItem_Title);
        else
            conversationTitleView = (TextView) view.findViewById(R.id.ConversationListItemG_Title);
       /* if(!conversation.mIsGroup)
         conversationTitleView = (TextView) view.findViewById(R.id.ConversationListItem_Title);
        else conversationTitleView = (TextView) view.findViewById(R.id.ConversationListItemGroup_Title);*/

        conversationTitleView.setText(conversation.mTitle);
        return view;
    }

}
