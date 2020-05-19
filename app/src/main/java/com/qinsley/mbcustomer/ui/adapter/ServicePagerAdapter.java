package com.qinsley.mbcustomer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.qinsley.mbcustomer.DTO.ProductDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.TouchImageView;

import java.util.ArrayList;


public class ServicePagerAdapter extends PagerAdapter {
    private Context mContext;
    LayoutInflater mLayoutInflater;
    private ArrayList<ProductDTO> productDTOList;


    public ServicePagerAdapter(Context mContext, ArrayList<ProductDTO> productDTOList) {
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productDTOList = productDTOList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.servicepager_adapter, container, false);
        TouchImageView ivImage = (TouchImageView) itemView.findViewById(R.id.ivImage);


        CustomTextView tvTitle = (CustomTextView) itemView.findViewById(R.id.tvTitle);
        CustomTextView tvPrice = (CustomTextView) itemView.findViewById(R.id.tvPrice);


        tvTitle.setText(productDTOList.get(position).getProduct_name());
        tvPrice.setText(productDTOList.get(position).getCurrency_type() + productDTOList.get(position).getPrice());

        Glide
                .with(mContext)
                .load(productDTOList.get(position).getProduct_image())
                .placeholder(R.drawable.dummyuser_image)
                .into(ivImage);


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return productDTOList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}