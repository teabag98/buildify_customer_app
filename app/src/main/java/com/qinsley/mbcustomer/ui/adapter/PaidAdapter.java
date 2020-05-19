package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.HistoryDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.activity.PaymentProActivity;
import com.qinsley.mbcustomer.ui.activity.ViewInvoice;
import com.qinsley.mbcustomer.ui.fragment.PaidFrag;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.MyViewHolder> {

    private Context mContext;
    private PaidFrag paidFrag;
    private ArrayList<HistoryDTO> objects = null;
    ArrayList<HistoryDTO> historyDTOList;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    private LayoutInflater inflater;

    public PaidAdapter(PaidFrag paidFrag, ArrayList<HistoryDTO> objects, UserDTO userDTO, LayoutInflater inflater) {
        this.paidFrag = paidFrag;
        this.mContext = paidFrag.getActivity();
        this.objects = objects;
        this.userDTO = userDTO;
        this.historyDTOList = new ArrayList<HistoryDTO>();
        this.historyDTOList.addAll(objects);
        this.inflater = inflater;
        prefrence = SharedPrefrence.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater
                .inflate(R.layout.adapter_paid, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        holder.CTVBservice.setText(mContext.getResources().getString(R.string.service) + " " + objects.get(position).getInvoice_id());

        try {
            holder.CTVdate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(objects.get(position).getCreated_at()))));

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.CTVprice.setText(objects.get(position).getCurrency_type() + objects.get(position).getFinal_amount());
        holder.CTVServicetype.setText(objects.get(position).getCategoryName());
        holder.CTVwork.setText(objects.get(position).getCategoryName());
        holder.CTVname.setText(ProjectUtils.getFirstLetterCapital(objects.get(position).getArtistName()));

        Glide.with(mContext).
                load(objects.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.IVprofile);

        if (objects.get(position).getFlag().equalsIgnoreCase("0")) {
            holder.llPay.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.unpaid));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
        } else if (objects.get(position).getFlag().equalsIgnoreCase("1")) {
            holder.llPay.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.paid));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        }
        holder.llPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, PaymentProActivity.class);
                in.putExtra(Consts.HISTORY_DTO, objects.get(position));
                mContext.startActivity(in);

            }
        });
        holder.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ViewInvoice.class);
                in.putExtra(Consts.HISTORY_DTO, objects.get(position));
                mContext.startActivity(in);

            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

        try {
            Date dt = sdf.parse(objects.get(position).getWorking_min());
            sdf = new SimpleDateFormat("HH:mm:ss");

            holder.CTVTime.setText(mContext.getResources().getString(R.string.duration) + " " + sdf.format(dt));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold CTVBservice;
        public CustomTextView CTVprice, CTVdate, CTVServicetype, CTVwork, CTVname, tvStatus, CTVTime, tvView;
        public CircleImageView IVprofile;
        public LinearLayout llStatus, llPay;

        public MyViewHolder(View view) {
            super(view);
            llStatus = view.findViewById(R.id.llStatus);
            llPay = view.findViewById(R.id.llPay);
            tvStatus = view.findViewById(R.id.tvStatus);
            CTVBservice = view.findViewById(R.id.CTVBservice);
            CTVdate = view.findViewById(R.id.CTVdate);
            CTVprice = view.findViewById(R.id.CTVprice);
            CTVTime = view.findViewById(R.id.CTVTime);
            CTVServicetype = view.findViewById(R.id.CTVServicetype);
            CTVwork = view.findViewById(R.id.CTVwork);
            CTVname = view.findViewById(R.id.CTVname);
            tvView = view.findViewById(R.id.tvView);
            IVprofile = view.findViewById(R.id.IVprofile);


        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(historyDTOList);
        } else {
            for (HistoryDTO historyDTO : historyDTOList) {
                if (historyDTO.getInvoice_id().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(historyDTO);
                }
            }
        }
        notifyDataSetChanged();
    }


}