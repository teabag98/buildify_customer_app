package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.AllAtristListDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.activity.ArtistProfile;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<AllAtristListDTO> allAtristListDTOList;
    private LayoutInflater inflater;
    private SharedPrefrence prefrence;

    public DiscoverAdapter(Context mContext, ArrayList<AllAtristListDTO> allAtristListDTOList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.allAtristListDTOList = allAtristListDTOList;
        this.inflater = inflater;
        prefrence = SharedPrefrence.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater
                .inflate(R.layout.adapterdiscover, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.CTVartistwork.setText(allAtristListDTOList.get(position).getCategory_name());
        holder.CTVartistname.setText(allAtristListDTOList.get(position).getName());
        if (allAtristListDTOList.get(position).getArtist_commission_type().equalsIgnoreCase("0")) {
            if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("0")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            }
        } else {
            if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("0")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " "+mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " "+mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " "+mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else {
                holder.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " "+mContext.getResources().getString(R.string.fixed_rate_add_on));
            }
        }


        holder.CTVlocation.setText(allAtristListDTOList.get(position).getLocation());
        holder.CTVdistance.setText(allAtristListDTOList.get(position).getDistance() + " "+mContext.getString(R.string.km));

        try {
            holder.CTVtime.setText(ProjectUtils.getDisplayableTime(ProjectUtils.correctTimestamp(Long.parseLong(allAtristListDTOList.get(position).getCreated_at()))));

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.CTVjobdone.setText(allAtristListDTOList.get(position).getJobDone());
        holder.tvRating.setText("(" + allAtristListDTOList.get(position).getAva_rating() + "/5)");
        holder.CTVpersuccess.setText(allAtristListDTOList.get(position).getPercentages() + "%");
        Glide.with(mContext).
                load(allAtristListDTOList.get(position).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.IVartist);
        if (allAtristListDTOList.get(position).getFav_status().equalsIgnoreCase("1")) {
            holder.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_full));
        } else {
            holder.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_blank));
        }
        if (allAtristListDTOList.get(position).getFeatured().equalsIgnoreCase("1")) {
            holder.ivfeatured.setVisibility(View.VISIBLE);
        } else {
            holder.ivfeatured.setVisibility(View.GONE);
        }
        holder.ratingbar.setRating(Float.parseFloat(allAtristListDTOList.get(position).getAva_rating()));
        holder.rlClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ArtistProfile.class);
                in.putExtra(Consts.ARTIST_ID, allAtristListDTOList.get(position).getUser_id());
                mContext.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {

        return allAtristListDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold CTVartistname, CTVartistchargeprh;
        public CustomTextView CTVartistwork, CTVjobdone, CTVpersuccess, CTVlocation, CTVdistance, CTVtime, tvRating;
        public CircleImageView IVartist;
        public RatingBar ratingbar;
        public RelativeLayout rlClick;
        public ImageView ivFav, ivfeatured;

        public MyViewHolder(View view) {
            super(view);

            rlClick = view.findViewById(R.id.rlClick);
            CTVartistname = view.findViewById(R.id.CTVartistname);
            CTVartistwork = view.findViewById(R.id.CTVartistwork);
            CTVjobdone = view.findViewById(R.id.CTVjobdone);
            CTVpersuccess = view.findViewById(R.id.CTVpersuccess);
            CTVartistchargeprh = view.findViewById(R.id.CTVartistchargeprh);
            CTVlocation = view.findViewById(R.id.CTVlocation);
            CTVdistance = view.findViewById(R.id.CTVdistance);
            CTVtime = view.findViewById(R.id.CTVtime);
            IVartist = view.findViewById(R.id.IVartist);
            tvRating = view.findViewById(R.id.tvRating);
            ratingbar = view.findViewById(R.id.ratingbar);
            ivFav = view.findViewById(R.id.ivFav);
            ivfeatured = view.findViewById(R.id.ivfeatured);

        }
    }

}