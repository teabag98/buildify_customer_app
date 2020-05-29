package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityBookingBinding;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class Booking extends AppCompatActivity {
    private ActivityBookingBinding binding;
    private Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private ArtistDetailsDTO artistDetailsDTO;
    private Date date;
    private SimpleDateFormat sdf1, timeZone;
    private HashMap<String, String> paramsBookingOp = new HashMap<>();
    private String TAG = Booking.class.getSimpleName();
    private JSONArray array;
    private String services;
    private String artist_id = "";
    private int screen_tag = 0;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking);
        mContext = Booking.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        sdf1 = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);
        date = new Date();

        paramsBookingOp.put(Consts.DATE_STRING, sdf1.format(date).toString().toUpperCase());
        paramsBookingOp.put(Consts.TIMEZONE, timeZone.format(date));


        if (getIntent().hasExtra(Consts.ARTIST_DTO)) {
            artistDetailsDTO = (ArtistDetailsDTO) getIntent().getSerializableExtra(Consts.ARTIST_DTO);
            artist_id = getIntent().getStringExtra(Consts.ARTIST_ID);
            screen_tag = getIntent().getIntExtra(Consts.SCREEN_TAG, 0);


            if (screen_tag == 2) {
                services = getIntent().getStringExtra(Consts.SERVICE_ARRAY);
                price = getIntent().getStringExtra(Consts.PRICE);


                try {
                    array = new JSONArray(services);
                    System.out.println(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        setUiAction();
    }

    public void setUiAction() {
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Glide.with(mContext).
                load(artistDetailsDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProfile);
        binding.tvName.setText(artistDetailsDTO.getName());
        binding.tvWork.setText(artistDetailsDTO.getCategory_name());
        binding.tvBookingDate.setText(sdf1.format(date).toString().toUpperCase());

        if (screen_tag == 1) {
            if (artistDetailsDTO.getArtist_commission_type().equalsIgnoreCase("0")) {
                binding.tvPrice.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr_add_on));

            } else {
                binding.tvPrice.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate_add_on));

            }
        } else if (screen_tag == 2) {
            binding.tvPrice.setText(artistDetailsDTO.getCurrency_type() + price);
        }
        if (!userDTO.getOffice_address().equalsIgnoreCase("")) {
            binding.tvAddress.setText(userDTO.getOffice_address());
        }

        if (!userDTO.getOffice_address().equalsIgnoreCase("")) {
            paramsBookingOp.put(Consts.ADDRESS, userDTO.getOffice_address());
            paramsBookingOp.put(Consts.LATITUDE, userDTO.getLive_lat());
            paramsBookingOp.put(Consts.LONGITUDE, userDTO.getLive_long());

        }
        binding.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkManager.isConnectToInternet(mContext)) {
                    findPlace();
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }
            }
        });
        binding.llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickScheduleDateTime();
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvAddress.getText().toString().trim().length() > 0) {
                    if (screen_tag == 1) {
                        paramsBookingOp.put(Consts.PRICE, artistDetailsDTO.getPrice());
                        submit();
                    } else if ((screen_tag == 2)) {
                        bookForService();
                    }
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.val_address));
                }
            }
        });
    }

    public void bookForService() {
        if (array.length() > 0) {
            paramsBookingOp.put(Consts.SERVICE_ID, array.toString());
            submit();
        } else {
            ProjectUtils.showLong(mContext, "Please select any service");
        }
    }


    public void bookArtist() {
        paramsBookingOp.put(Consts.USER_ID, userDTO.getUser_id());
        paramsBookingOp.put(Consts.ARTIST_ID, artistDetailsDTO.getUser_id());

        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOK_ARTIST_API, paramsBookingOp, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    finish();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    private void findPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                //.withLocation(41.4036299, 2.1743558)
                .build(mContext);

        startActivityForResult(locationPickerIntent, 101);
    }


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            Log.e("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);

            binding.tvAddress.setText(obj.getAddressLine(0));

            paramsBookingOp.put(Consts.ADDRESS, obj.getAddressLine(0));
            paramsBookingOp.put(Consts.LATITUDE, String.valueOf(lat));
            paramsBookingOp.put(Consts.LONGITUDE, String.valueOf(lng));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                try {
                    getAddress(data.getDoubleExtra(LATITUDE, 0.0), data.getDoubleExtra(LONGITUDE, 0.0));


                } catch (Exception e) {

                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    public void clickScheduleDateTime() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .defaultDate(new Date())
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        paramsBookingOp.put(Consts.DATE_STRING, String.valueOf(sdf1.format(date).toString().toUpperCase()));
                        paramsBookingOp.put(Consts.TIMEZONE, String.valueOf(timeZone.format(date)));
                        binding.tvBookingDate.setText(sdf1.format(date).toString().toUpperCase());
                    }
                })
                .display();
    }


    public void submit() {
        if (!validation(binding.tvBookingDate, getResources().getString(R.string.val_date))) {
            return;
        } else if (!validation(binding.tvAddress, getResources().getString(R.string.val_address))) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                bookArtist();

            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }
    }

    public boolean validation(TextView textView, String msg) {
        if (!ProjectUtils.isTextFilled(textView)) {
            ProjectUtils.showLong(mContext, msg);
            return false;
        } else {
            return true;
        }
    }


}
