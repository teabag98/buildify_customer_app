package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.ProductDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.adapter.AdapterServices;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import java.util.ArrayList;

public class ViewServices extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ArtistDetailsDTO artistDetailsDTO;
    private RecyclerView rvGallery;
    private AdapterServices adapterServices;
    private ArrayList<ProductDTO> productDTOList;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout llBack;
    public CustomTextViewBold tvNotFound, tvPrice, tvPriceType;
    private JsonArray array;
    private CardView cardBook;
    private DialogInterface dialog_book;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String artist_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_services);
        mContext = ViewServices.this;
        prefrence = SharedPrefrence.getInstance(mContext);

        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.ARTIST_DTO)) {
            artistDetailsDTO = (ArtistDetailsDTO) getIntent().getSerializableExtra(Consts.ARTIST_DTO);
            artist_id = getIntent().getStringExtra(Consts.ARTIST_ID);
        }
        showUiAction();
    }


    public void showUiAction() {
        tvPriceType = (CustomTextViewBold) findViewById(R.id.tvPriceType);
        tvPrice = (CustomTextViewBold) findViewById(R.id.tvPrice);
        tvNotFound = (CustomTextViewBold) findViewById(R.id.tvNotFound);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        rvGallery = (RecyclerView) findViewById(R.id.rvGallery);
        cardBook = (CardView) findViewById(R.id.cardBook);
        llBack.setOnClickListener(this);
        cardBook.setOnClickListener(this);
        showData();

    }

    public void showData() {
        gridLayoutManager = new GridLayoutManager(mContext, 2);
        productDTOList = new ArrayList<>();
        productDTOList = artistDetailsDTO.getProducts();
        if (productDTOList.size() > 0) {
            tvNotFound.setVisibility(View.GONE);
            rvGallery.setVisibility(View.VISIBLE);
            cardBook.setVisibility(View.VISIBLE);
            adapterServices = new AdapterServices(ViewServices.this, productDTOList, artistDetailsDTO);
            rvGallery.setLayoutManager(gridLayoutManager);
            rvGallery.setAdapter(adapterServices);
        } else {
            tvNotFound.setVisibility(View.VISIBLE);
            rvGallery.setVisibility(View.GONE);
            cardBook.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                onBackPressed();
                break;
            case R.id.cardBook:
                updateList();
                if (array.size() > 0) {
                    Intent in = new Intent(mContext, Booking.class);
                    in.putExtra(Consts.ARTIST_DTO, artistDetailsDTO);
                    in.putExtra(Consts.ARTIST_ID, artist_id);
                    in.putExtra(Consts.SERVICE_ARRAY, array.toString());
                    in.putExtra(Consts.SCREEN_TAG, 2);
                    in.putExtra(Consts.PRICE, tvPrice.getText().toString().trim());
                    startActivity(in);
                    //finish();
                } else {
                    ProjectUtils.showLong(mContext, "Please select any service");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    public void updateList() {
        array = new JsonArray();
        for (int i = 0; i < productDTOList.size(); i++) {
            if (productDTOList.get(i).isSelected()) {
                array.add(productDTOList.get(i).getId());
            }
        }

    }

/*
    public void bookDailog() {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.book_artist))
                    .setMessage(getResources().getString(R.string.book_msg) + " " + artistDetailsDTO.getName() + "?")
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            if (array.size() > 0)
                                dialogSelectAddress();
                            else
                                ProjectUtils.showLong(mContext, "Please select any service");
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
*/


}
