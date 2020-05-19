package com.qinsley.mbcustomer.ui.adapter;



import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.AppliedJobDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.activity.AppliedJob;
import com.qinsley.mbcustomer.ui.activity.ArtistProfile;
import com.qinsley.mbcustomer.ui.activity.OneTwoOneChat;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AppliedJobAdapter extends RecyclerView.Adapter<AppliedJobAdapter.MyViewHolder> {
    private String TAG = AppliedJobAdapter.class.getSimpleName();
    private HashMap<String, String> params;
    private DialogInterface dialog_book;
    private AppliedJob appliedJob;
    private ArrayList<AppliedJobDTO> objects = null;
    private ArrayList<AppliedJobDTO> appliedJobDTOSList;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;

    public AppliedJobAdapter(AppliedJob appliedJob, ArrayList<AppliedJobDTO> objects, UserDTO userDTO) {
        this.appliedJob = appliedJob;
        this.objects = objects;
        this.appliedJobDTOSList = new ArrayList<AppliedJobDTO>();
        this.appliedJobDTOSList.addAll(objects);
        this.userDTO = userDTO;
        prefrence = SharedPrefrence.getInstance(appliedJob);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_applied_job, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvDate.setText(appliedJob.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getJob_date()) + " " + objects.get(position).getTime());
        holder.tvJobId.setText(objects.get(position).getJob_id());
        holder.tvName.setText(objects.get(position).getArtist_name());
        holder.tvDescription.setText(objects.get(position).getDescription());
        holder.tvCategory.setText(objects.get(position).getCategory_name());
        holder.tvAddress.setText(objects.get(position).getArtist_address());
        holder.tvEmail.setText(objects.get(position).getArtist_email());
        holder.tvMobile.setText(objects.get(position).getArtist_mobile());
        holder.tvPrice.setText(objects.get(position).getCurrency_symbol() + objects.get(position).getPrice());
        holder.tvRating.setText("(" + objects.get(position).getAva_rating() + "/5)");
        holder.ratingbar.setRating(Float.parseFloat(objects.get(position).getAva_rating()));

        Glide.with(appliedJob).
                load(objects.get(position).getArtist_image())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProfile);

        if (objects.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.llACDE.setVisibility(View.VISIBLE);
            holder.llComplete.setVisibility(View.GONE);
            holder.llWating.setVisibility(View.GONE);
            holder.tvStatus.setText(appliedJob.getResources().getString(R.string.applied));
            holder.llStatus.setBackground(appliedJob.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.llACDE.setVisibility(View.GONE);
            holder.llComplete.setVisibility(View.GONE);
            holder.llWating.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(appliedJob.getResources().getString(R.string.confirm));
            holder.llStatus.setBackground(appliedJob.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.llACDE.setVisibility(View.GONE);
            holder.llComplete.setVisibility(View.GONE);
            holder.llWating.setVisibility(View.GONE);
            holder.tvStatus.setText(appliedJob.getResources().getString(R.string.complete));
            holder.llStatus.setBackground(appliedJob.getResources().getDrawable(R.drawable.rectangle_green));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("3")) {
            holder.llACDE.setVisibility(View.GONE);
            holder.llComplete.setVisibility(View.GONE);
            holder.llWating.setVisibility(View.GONE);
            holder.tvStatus.setText(appliedJob.getResources().getString(R.string.rejected));
            holder.llStatus.setBackground(appliedJob.getResources().getDrawable(R.drawable.rectangle_dark_red));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("5")) {
            holder.llACDE.setVisibility(View.GONE);
            holder.llComplete.setVisibility(View.GONE);
            holder.llWating.setVisibility(View.GONE);
            holder.tvStatus.setText(appliedJob.getResources().getString(R.string.inprogress));
            holder.llStatus.setBackground(appliedJob.getResources().getDrawable(R.drawable.rectangle_green));
        }


        holder.llDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new HashMap<>();
                params.put(Consts.AJ_ID, objects.get(position).getAj_id());
                params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                params.put(Consts.STATUS, "3");
                rejectDialog(appliedJob.getResources().getString(R.string.reject), appliedJob.getResources().getString(R.string.reject_msg));
            }
        });
        holder.llAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new HashMap<>();
                params.put(Consts.AJ_ID, objects.get(position).getAj_id());
                params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                params.put(Consts.STATUS, "1");
                rejectDialog(appliedJob.getResources().getString(R.string.confirm), appliedJob.getResources().getString(R.string.confirm_msg));
            }
        });
        holder.llComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new HashMap<>();
                params.put(Consts.AJ_ID, objects.get(position).getAj_id());
                params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                params.put(Consts.STATUS, "2");
                rejectDialog(appliedJob.getResources().getString(R.string.complete), appliedJob.getResources().getString(R.string.complete_msg));
            }
        });
        holder.rlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(appliedJob, ArtistProfile.class);
                in.putExtra(Consts.ARTIST_ID, objects.get(position).getArtist_id());
                in.putExtra(Consts.FLAG, 1);
                appliedJob.startActivity(in);
            }
        });

        holder.icChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(appliedJob, OneTwoOneChat.class);
                in.putExtra(Consts.ARTIST_ID, objects.get(position).getArtist_id());
                in.putExtra(Consts.ARTIST_NAME, objects.get(position).getArtist_name());
                appliedJob.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView tvJobId, tvDate, tvStatus, tvName, tvDescription, tvCategory, tvMobile, tvEmail, tvAddress, tvRating;
        public CircleImageView ivProfile;
        public LinearLayout llStatus, llAccept, llDecline, llACDE, llComplete, llWating;
        public RatingBar ratingbar;
        public CustomTextViewBold tvPrice;
        public ImageView icChat;
        RelativeLayout rlPhoto;

        public MyViewHolder(View view) {
            super(view);
            icChat = view.findViewById(R.id.icChat);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvDate = view.findViewById(R.id.tvDate);
            tvJobId = view.findViewById(R.id.tvJobId);
            tvName = view.findViewById(R.id.tvName);
            tvDescription = view.findViewById(R.id.tvDescription);
            ivProfile = view.findViewById(R.id.ivProfile);
            llAccept = view.findViewById(R.id.llAccept);
            llDecline = view.findViewById(R.id.llDecline);
            llACDE = view.findViewById(R.id.llACDE);
            llComplete = view.findViewById(R.id.llComplete);
            llWating = view.findViewById(R.id.llWating);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvMobile = view.findViewById(R.id.tvMobile);
            tvRating = view.findViewById(R.id.tvRating);
            ratingbar = view.findViewById(R.id.ratingbar);
            rlPhoto = view.findViewById(R.id.rlPhoto);
        }
    }

    public void reject() {

        new HttpsRequest(Consts.JOB_STATUS_USER_API, params, appliedJob).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(appliedJob, msg);
                    dialog_book.dismiss();
                    appliedJob.getjobs();
                } else {
                    ProjectUtils.showToast(appliedJob, msg);
                }


            }
        });
    }

    public void rejectDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(appliedJob)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(appliedJob.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            reject();

                        }
                    })
                    .setNegativeButton(appliedJob.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(appliedJobDTOSList);
        } else {
            for (AppliedJobDTO appliedJobDTO : appliedJobDTOSList) {
                if (appliedJobDTO.getArtist_name().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(appliedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

}