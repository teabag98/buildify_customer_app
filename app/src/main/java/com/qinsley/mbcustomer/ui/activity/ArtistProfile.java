package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.fragment.ImageGallery;
import com.qinsley.mbcustomer.ui.fragment.PersnoalInfo;
import com.qinsley.mbcustomer.ui.fragment.PreviousWork;
import com.qinsley.mbcustomer.ui.fragment.Reviews;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityArtistProfile2Binding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ArtistProfile extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {
    private String TAG = ArtistProfile.class.getSimpleName();
    private Context mContext;
    private String artist_id = "";
    private ArtistDetailsDTO artistDetailsDTO;
    private HashMap<String, String> parms = new HashMap<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private HashMap<String, String> paramsFav = new HashMap<>();
    SimpleDateFormat sdf1, timeZone;
    public static String name = "", email = "";
    private ArrayList<String> list;

    private PersnoalInfo persnoalInfo = new PersnoalInfo();
    private ImageGallery imageGallery = new ImageGallery();
    private PreviousWork previousWork = new PreviousWork();
    private Reviews reviews = new Reviews();

    private Bundle bundle;

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    private DialogInterface dialog_book;
    private HashMap<String, String> paramsBookingOp = new HashMap<>();
    ;
    private Date date;
    private HashMap<String, String> paramBookAppointment = new HashMap<>();

    private ActivityArtistProfile2Binding binding;
    int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(ArtistProfile.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_artist_profile2);
        mContext = ArtistProfile.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        sdf1 = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);
        date = new Date();
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.ARTIST_ID)) {
            if (getIntent().hasExtra(Consts.FLAG)) {
                flag = getIntent().getIntExtra(Consts.FLAG, 0);
            }
            artist_id = getIntent().getStringExtra(Consts.ARTIST_ID);

        }
        parms.put(Consts.ARTIST_ID, artist_id);
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        paramsFav.put(Consts.ARTIST_ID, artist_id);
        paramsFav.put(Consts.USER_ID, userDTO.getUser_id());
        setUiAction();
    }

    public void setUiAction() {
        binding.llBack.setOnClickListener(this);
        binding.tvChat.setOnClickListener(this);

        binding.tvAppointment.setOnClickListener(this);
        binding.tvBookNow.setOnClickListener(this);
        binding.ivFav.setOnClickListener(this);
        binding.tvViewServices.setOnClickListener(this);


        if (NetworkManager.isConnectToInternet(mContext)) {
            getArtist();

        } else {
            ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
        }

        binding.appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = binding.appbarLayout.getTotalScrollRange();

        if (flag == 1) {
            binding.llBottom.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBookNow:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO != null) {
                        Intent viewService = new Intent(mContext, Booking.class);
                        viewService.putExtra(Consts.ARTIST_DTO, artistDetailsDTO);
                        viewService.putExtra(Consts.ARTIST_ID, artist_id);
                        viewService.putExtra(Consts.SCREEN_TAG, 1);
                        mContext.startActivity(viewService);
                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.no_data_found));
                    }
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }

                break;
            case R.id.tvAppointment:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    paramBookAppointment.put(Consts.USER_ID, userDTO.getUser_id());
                    paramBookAppointment.put(Consts.ARTIST_ID, artistDetailsDTO.getUser_id());
                    clickScheduleDateTime();
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }


                break;
            case R.id.llBack:
                finish();
                break;
            case R.id.ivFav:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO.getFav_status().equalsIgnoreCase("1")) {
                        removeFav();
                    } else {
                        addFav();
                    }

                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }


                break;
            case R.id.tvChat:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    Intent in = new Intent(mContext, OneTwoOneChat.class);
                    in.putExtra(Consts.ARTIST_ID, artistDetailsDTO.getUser_id());
                    in.putExtra(Consts.ARTIST_NAME, artistDetailsDTO.getName());
                    mContext.startActivity(in);
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }

                break;
            case R.id.tvViewServices:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO != null) {
                        Intent viewService = new Intent(mContext, ViewServices.class);
                        viewService.putExtra(Consts.ARTIST_DTO, artistDetailsDTO);
                        viewService.putExtra(Consts.ARTIST_ID, artist_id);
                        mContext.startActivity(viewService);
                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.no_services_found));
                    }
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }

                break;

        }
    }

    public void getArtist() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_ARTIST_BY_ID_API, parms, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        artistDetailsDTO = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistDetailsDTO.class);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    public void showData() {
        bundle = new Bundle();
        bundle.putSerializable(Consts.ARTIST_DTO, artistDetailsDTO);

        binding.tvName.setText(artistDetailsDTO.getName());
        binding.tvWork.setText(artistDetailsDTO.getCategory_name());




/*
        tvTotalFav.setText(artistDetailsDTO.getNo_of_fav());
*/
        binding.tvTotalJobsDone.setText(artistDetailsDTO.getJobDone());
        if (artistDetailsDTO.getFav_status().equalsIgnoreCase("1")) {
            binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_full));
        } else {
            binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_blank));
        }

        Glide.with(mContext).
                load(artistDetailsDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivArtist);

        Glide.with(mContext).
                load(artistDetailsDTO.getBanner_image())
                .placeholder(R.drawable.banner_img)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBanner);

        setupViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

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

    public void addFav() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.ADD_FAVORITES_API, paramsFav, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    getArtist();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    public void removeFav() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.REMOVE_FAVORITES_API, paramsFav, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    getArtist();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        persnoalInfo.setArguments(bundle);
        imageGallery.setArguments(bundle);
        previousWork.setArguments(bundle);
        reviews.setArguments(bundle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(persnoalInfo, "Info");
        adapter.addFragment(previousWork, "Works");
        adapter.addFragment(imageGallery, "Gallery");
        adapter.addFragment(reviews, "Reviews");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            binding.ivArtist.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            binding.ivArtist.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
                        paramBookAppointment.put(Consts.DATE_STRING, String.valueOf(sdf1.format(date).toString().toUpperCase()));
                        paramBookAppointment.put(Consts.TIMEZONE, String.valueOf(timeZone.format(date)));
                        bookAppointment();
                    }
                })
                .display();
    }

    public void bookAppointment() {

        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOK_APPOINTMENT_API, paramBookAppointment, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.pauseProgressDialog();
                    ProjectUtils.showToast(mContext, msg);

                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }
}
