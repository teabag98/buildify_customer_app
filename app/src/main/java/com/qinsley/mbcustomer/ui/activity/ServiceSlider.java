package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.ProductDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.ui.adapter.ServicePagerAdapter;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import java.util.ArrayList;

public class ServiceSlider extends AppCompatActivity {
    ViewPager mViewPager;
    private Context mContext;
    private ArrayList<ProductDTO> productDTOList = new ArrayList<>();
    private ServicePagerAdapter mAdapter;
    int pos = 0;
    private ImageView ivBack;
    private ArtistDetailsDTO artistDetailsDTO;
    private String artist_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(ServiceSlider.this);
        setContentView(R.layout.activity_service_slider);
        mContext = ServiceSlider.this;
        if (getIntent().hasExtra(Consts.DTO)) {
            productDTOList = (ArrayList<ProductDTO>) getIntent().getSerializableExtra(Consts.DTO);
            pos = getIntent().getIntExtra(Consts.POSTION, 0);
            artistDetailsDTO = (ArtistDetailsDTO) getIntent().getSerializableExtra(Consts.ARTIST_DTO);
            artist_id = getIntent().getStringExtra(Consts.ARTIST_ID);
        }
        setUiAction();
    }

    public void setUiAction() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        mAdapter = new ServicePagerAdapter(mContext, productDTOList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new StackTransformer());
        mViewPager.setCurrentItem(pos);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}
