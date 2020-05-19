package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.ProductDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.ui.activity.ServiceSlider;
import com.qinsley.mbcustomer.ui.activity.ViewServices;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;

import java.util.ArrayList;


public class AdapterServices extends RecyclerView.Adapter<AdapterServices.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private ViewServices context;
    private ArrayList<ProductDTO> productDTOList;
    boolean isHide = false;
    boolean select = true;
    private ArtistDetailsDTO artistDetailsDTO;
    public AdapterServices(ViewServices context, ArrayList<ProductDTO> productDTOList,ArtistDetailsDTO artistDetailsDTO) {
        this.context = context;
        this.productDTOList = productDTOList;
        this.artistDetailsDTO = artistDetailsDTO;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_services, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Glide
                .with(context)
                .load(productDTOList.get(position).getProduct_image())
                .placeholder(R.drawable.dummyuser_image)
                .into(holder.iv_bottom_foster);
        holder.tvPrice.setText(productDTOList.get(position).getCurrency_type() + productDTOList.get(position).getPrice());
        holder.tvProductName.setText(productDTOList.get(position).getProduct_name());


        holder.iv_bottom_foster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, ServiceSlider.class);
                in.putExtra(Consts.DTO, productDTOList);
                in.putExtra(Consts.POSTION, position);
                in.putExtra(Consts.ARTIST_DTO, artistDetailsDTO);
                in.putExtra(Consts.ARTIST_ID, artistDetailsDTO.getUser_id());
                context.startActivity(in);
            }
        });

        if (productDTOList.get(position).isSelected()) {
            holder.cbSelect.setChecked(true);

        } else {
            holder.cbSelect.setChecked(false);
        }


        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (productDTOList.get(position).isSelected()) {
                    productDTOList.get(position).setSelected(false);

                    int quantity = Integer.parseInt(context.tvPrice.getText().toString());
                    quantity = quantity - Integer.parseInt(productDTOList.get(position).getPrice());
                    context.tvPrice.setText("" + quantity);

                } else {
                    productDTOList.get(position).setSelected(true);
                    if (select) {
                        context.tvPrice.setText(productDTOList.get(position).getPrice());
                        context.tvPriceType.setText(productDTOList.get(position).getCurrency_type());
                        select = false;
                    } else {
                        int quantity = Integer.parseInt(context.tvPrice.getText().toString());
                        quantity = quantity + Integer.parseInt(productDTOList.get(position).getPrice());
                        ;
                        context.tvPrice.setText("" + quantity);
                    }
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_bottom_foster;
        public RelativeLayout rl_bottomView;
        public CustomTextViewBold tvProductName, tvPrice;
        public CheckBox cbSelect;

        public MyViewHolder(View view) {
            super(view);

            iv_bottom_foster = (ImageView) view.findViewById(R.id.iv_bottom_foster);
            tvProductName = (CustomTextViewBold) view.findViewById(R.id.tvProductName);
            tvPrice = (CustomTextViewBold) view.findViewById(R.id.tvPrice);
            rl_bottomView = (RelativeLayout) view.findViewById(R.id.rl_bottomView);
            cbSelect = (CheckBox) view.findViewById(R.id.cbSelect);


        }
    }

    private void fadeInAndShowView(final RelativeLayout img) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeIn);
    }

    private void fadeOutAndHideView(final RelativeLayout img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeOut);
    }

}
