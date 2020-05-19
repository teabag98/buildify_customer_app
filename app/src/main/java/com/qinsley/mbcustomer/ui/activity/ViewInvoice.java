package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.HistoryDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityViewInvoiceBinding;

public class ViewInvoice extends AppCompatActivity {
    private ActivityViewInvoiceBinding binding;
    private HistoryDTO historyDTO;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_invoice);
        mContext = ViewInvoice.this;
        if (getIntent().hasExtra(Consts.HISTORY_DTO)) {
            historyDTO = (HistoryDTO) getIntent().getSerializableExtra(Consts.HISTORY_DTO);
        }
        setUiAction();
    }

    public void setUiAction() {

        binding.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (historyDTO.getFlag().equalsIgnoreCase("0")) {
            binding.btnPay.setVisibility(View.VISIBLE);
        } else if (historyDTO.getFlag().equalsIgnoreCase("1")) {
            binding.btnPay.setVisibility(View.GONE);
        }

        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkManager.isConnectToInternet(mContext)) {
                    Intent in = new Intent(mContext, PaymentProActivity.class);
                    in.putExtra(Consts.HISTORY_DTO, historyDTO);
                    mContext.startActivity(in);
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }

            }
        });

        Glide.with(mContext).
                load(historyDTO.getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProfile);

        binding.tvInvoiceId.setText(mContext.getResources().getString(R.string.service) + " " + historyDTO.getInvoice_id());
        binding.tvName.setText(ProjectUtils.getFirstLetterCapital(historyDTO.getArtistName()));
        binding.tvServiceType.setText(historyDTO.getCategoryName());
        binding.tvWork.setText(historyDTO.getCategoryName());
        binding.tvPrice.setText(historyDTO.getCurrency_type() + historyDTO.getFinal_amount());
        binding.tvSubtotal.setText(historyDTO.getCurrency_type() + historyDTO.getTotal_amount());
        binding.tvTotal.setText(historyDTO.getCurrency_type() + historyDTO.getFinal_amount());
        binding.tvDiscount.setText(historyDTO.getCurrency_type() + historyDTO.getDiscount_amount());
        try {
            binding.tvInvoiceDate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(historyDTO.getCreated_at()))));
            binding.tvInvoiceDate1.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(historyDTO.getCreated_at()))));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
