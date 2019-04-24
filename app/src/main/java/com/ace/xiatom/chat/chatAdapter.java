package com.ace.xiatom.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiatom on 2019/4/24.
 */

public class chatAdapter extends ArrayAdapter<chat_content> {
    private  int resourceId;
    public chatAdapter(Context context, int textViewResourceId, List<chat_content> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        chat_content ch_content = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView from = view.findViewById(R.id.chat_person_from);
        TextView content = view.findViewById(R.id.chat_content);
        ImageView self = view.findViewById(R.id.chat_person_self);
        ImageView bubbleOther =view.findViewById(R.id.bubbleOther);
        ImageView bubbleSelf =view.findViewById(R.id.bubbleSelf);
        //来自其他人
        if(ch_content.getFromOther()){
            bubbleOther.setVisibility(View.VISIBLE);
            bubbleSelf.setVisibility(View.INVISIBLE);
            from.setVisibility(View.VISIBLE);
            self.setVisibility(View.INVISIBLE);
        }else{
            bubbleOther.setVisibility(View.INVISIBLE);
            bubbleSelf.setVisibility(View.VISIBLE);
            from.setVisibility(View.INVISIBLE);
            self.setVisibility(View.VISIBLE);
        }
        content.setText(ch_content.getContent());
        return  view;
    }
}