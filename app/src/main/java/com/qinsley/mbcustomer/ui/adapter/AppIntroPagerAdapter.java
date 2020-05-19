package com.qinsley.mbcustomer.ui.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinsley.mbcustomer.ui.activity.AppIntro;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.AppintropagerAdapterBinding;


public class AppIntroPagerAdapter extends PagerAdapter {
    private Context mContext;
    LayoutInflater mLayoutInflater;
    private int[] mResources;
    private AppIntro activity;


    public AppIntroPagerAdapter(AppIntro appIntroActivity, Context mContext, int[] mResources) {
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
        this.activity = appIntroActivity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {


        AppintropagerAdapterBinding binding =
                DataBindingUtil.inflate(mLayoutInflater, R.layout.appintropager_adapter, container, false);

        binding.ivImage.setImageResource(mResources[position]);
        setDescText(position, binding.ctvText, binding.ctvTextdecrib);


        container.addView(binding.getRoot());
        binding.ctvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int pos = position + 1;
                activity.scrollPage(pos);


            }
        });
        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setDescText(int pos, TextView ctvText, TextView ctvTextdecrib) {
        switch (pos) {
            case 0:

                ctvTextdecrib.setText(mContext.getString(R.string.intro_1));
                break;
            case 1:

                ctvTextdecrib.setText(mContext.getString(R.string.intro_2));
                break;
            case 2:

                ctvTextdecrib.setText(mContext.getString(R.string.intro_3));
                break;
        }
    }
}