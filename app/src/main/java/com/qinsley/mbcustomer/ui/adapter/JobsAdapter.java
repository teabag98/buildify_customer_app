package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.PostedJobDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.activity.AppliedJob;
import com.qinsley.mbcustomer.ui.activity.EditJob;
import com.qinsley.mbcustomer.ui.fragment.Jobs;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AppliedJobAdapter.class.getSimpleName();
    private HashMap<String, String> params;
    private HashMap<String, String> paramsComplete;
    private DialogInterface dialog_book;
    private Context mContext;
    private Jobs jobs;
    private ArrayList<PostedJobDTO> objects = null;
    private ArrayList<PostedJobDTO> postedJobDTOSList;
    private UserDTO userDTO;
    private SharedPrefrence preferences;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    public JobsAdapter(Jobs jobs, ArrayList<PostedJobDTO> objects, UserDTO userDTO) {
        this.jobs = jobs;
        this.mContext = jobs.getActivity();
        this.objects = objects;
        this.postedJobDTOSList = new ArrayList<PostedJobDTO>();
        this.postedJobDTOSList.addAll(objects);
        this.userDTO = userDTO;
        preferences = SharedPrefrence.getInstance(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_jobs, parent, false);
            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new MyViewHolderSection(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {

        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;

            //0 = pending, 1 = confirm , 2 = complete, 3 =reject

            holder.tvJobId.setText(mContext.getResources().getString(R.string.job_id) + " " + objects.get(position).getJob_id());
            holder.tvTitle.setText(objects.get(position).getTitle());
            holder.tvDescription.setText(objects.get(position).getDescription());
            holder.tvCategory.setText(objects.get(position).getCategory_name());
            holder.tvAddress.setText(objects.get(position).getAddress());
            holder.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getJob_date())+" "+objects.get(position).getTime());
            holder.tvApplied.setText(mContext.getResources().getString(R.string.applied1) + " " + objects.get(position).getApplied_job());
            holder.tvPrice.setText(objects.get(position).getCurrency_symbol() + objects.get(position).getPrice());

            if (objects.get(position).getStatus().equalsIgnoreCase("0")) {
                holder.rlComplete.setVisibility(View.VISIBLE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.open));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("1")) {
                holder.rlComplete.setVisibility(View.VISIBLE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.confirm));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("2")) {
                holder.rlComplete.setVisibility(View.GONE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.completed));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("3")) {
                holder.rlComplete.setVisibility(View.VISIBLE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.rejected));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
            }

            Glide.with(mContext).
                    load(objects.get(position).getAvtar())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivImage);

            holder.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, AppliedJob.class);
                    preferences.setValue(Consts.JOB_ID, objects.get(position).getJob_id());
                    mContext.startActivity(in);
                }
            });

            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objects.get(position).getIs_edit().equalsIgnoreCase("1")) {
                        Intent in = new Intent(mContext, EditJob.class);
                        in.putExtra(Consts.POST_JOB_DTO, objects.get(position));
                        mContext.startActivity(in);
                    } else {
                        ProjectUtils.showLong(mContext, mContext.getResources().getString(R.string.not_edit_job));
                    }

                }
            });
            holder.tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params = new HashMap<>();
                    params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    params.put(Consts.STATUS, "4");

                    rejectDialog(mContext.getResources().getString(R.string.delete) + " " + objects.get(position).getTitle(), mContext.getResources().getString(R.string.delete_job));
                }
            });
            holder.tvComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paramsComplete = new HashMap<>();
                    paramsComplete.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    paramsComplete.put(Consts.USER_ID, objects.get(position).getUser_id());

                    completeDialog(mContext.getResources().getString(R.string.complete), mContext.getResources().getString(R.string.complete_job));
                }
            });
        } else {
            MyViewHolderSection view = (MyViewHolderSection) holderMain;
            view.tvSection.setText(objects.get(position).getSection_name());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return this.objects.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold tvJobId, tvPrice;
        public CustomTextView tvDate,tvApplied, tvStatus, tvTitle, tvDescription, tvCategory, tvAddress;
        public LinearLayout llStatus, rlComplete;
        public RelativeLayout rlClick;
        public CircleImageView ivImage;
        public CustomTextView tvDecline, tvEdit, tvComplete;

        public MyViewHolder(View view) {
            super(view);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvJobId = view.findViewById(R.id.tvJobId);
            tvDate = view.findViewById(R.id.tvDate);
            rlClick = view.findViewById(R.id.rlClick);
            ivImage = view.findViewById(R.id.ivImage);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDecline = view.findViewById(R.id.tvDecline);
            tvEdit = view.findViewById(R.id.tvEdit);
            rlComplete = view.findViewById(R.id.rlComplete);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvComplete = view.findViewById(R.id.tvComplete);
            tvApplied = view.findViewById(R.id.tvApplied);

        }
    }


    public void reject() {

        new HttpsRequest(Consts.DELETE_JOB_API, params, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    jobs.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void complete() {

        new HttpsRequest(Consts.JOB_COMPLETE_API, paramsComplete, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    jobs.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void rejectDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            reject();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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

    public void completeDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            complete();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
            objects.addAll(postedJobDTOSList);
        } else {
            for (PostedJobDTO postedJobDTO : postedJobDTOSList) {
                if (postedJobDTO.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(postedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        public CustomTextView tvSection;

        public MyViewHolderSection(View view) {
            super(view);
            tvSection = view.findViewById(R.id.tvSection);
        }
    }


}