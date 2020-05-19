package com.qinsley.mbcustomer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.ChatListDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.ui.activity.OneTwoOneChat;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<ChatListDTO> chatList;


    public ChatListAdapter(Context mContext, ArrayList<ChatListDTO> chatList) {
        this.mContext = mContext;
        this.chatList = chatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvTitle.setText(chatList.get(position).getArtistName());
        holder.tvMsg.setText(chatList.get(position).getMessage());
        try{
            holder.tvDay.setText(ProjectUtils.getDisplayableDay(ProjectUtils.correctTimestamp(Long.parseLong(chatList.get(position).getDate()))));
            holder.tvDate.setText(ProjectUtils.convertTimestampToTime(ProjectUtils.correctTimestamp(Long.parseLong(chatList.get(position).getSend_at()))));

        }catch (Exception e){
            e.printStackTrace();
        }


        Glide.with(mContext).
                load(chatList.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.IVprofile);
        holder.cardClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, OneTwoOneChat.class);
                in.putExtra(Consts.ARTIST_ID, chatList.get(position).getArtist_id());
                in.putExtra(Consts.ARTIST_NAME, chatList.get(position).getArtistName());
                mContext.startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() {

        return chatList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold tvTitle;
        public CustomTextView tvDay, tvDate, tvMsg;
        public CircleImageView IVprofile;
        public CardView cardClick;

        public MyViewHolder(View view) {
            super(view);

            cardClick = view.findViewById(R.id.cardClick);
            IVprofile = view.findViewById(R.id.IVprofile);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDay = view.findViewById(R.id.tvDay);
            tvDate = view.findViewById(R.id.tvDate);
            tvMsg = view.findViewById(R.id.tvMsg);

        }
    }

}