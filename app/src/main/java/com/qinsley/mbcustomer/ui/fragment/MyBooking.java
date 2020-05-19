package com.qinsley.mbcustomer.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.UserBooking;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;
import com.qinsley.mbcustomer.ui.adapter.AdapterCustomerBooking;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyBooking extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String TAG = NotificationActivity.class.getSimpleName();
    private RecyclerView rvBooking;
    private AdapterCustomerBooking adapterCustomerBooking;
    private ArrayList<UserBooking> userBookingList;
    private ArrayList<UserBooking> userBookingListSection;
    private ArrayList<UserBooking> userBookingListSection1;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private View view;
    private BaseActivity baseActivity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView svSearch;
    private RelativeLayout rlSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_my_booking, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        baseActivity.headerNameTV.setText(getResources().getString(R.string.my_bookings));
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        setUiAction(view);
        return view;
    }


    public void setUiAction(View v) {
        rlSearch = v.findViewById(R.id.rlSearch);
        svSearch = v.findViewById(R.id.svSearch);
        tvNo = v.findViewById(R.id.tvNo);
        rvBooking = v.findViewById(R.id.rvBooking);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvBooking.setLayoutManager(mLayoutManager);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    adapterCustomerBooking.filter(newText.toString());

                } else {


                }
                return false;
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);

                                            getBooking();

                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }


    public void getBooking() {
        // ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.CURRENT_BOOKING_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //   ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    rlSearch.setVisibility(View.VISIBLE);

                    try {
                        userBookingList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<UserBooking>>() {
                        }.getType();
                        userBookingList = (ArrayList<UserBooking>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                       // setSection();
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    rlSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        adapterCustomerBooking = new AdapterCustomerBooking(MyBooking.this, userBookingList, userDTO);
        rvBooking.setAdapter(adapterCustomerBooking);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getBooking();
    }

    public void setSection() {
        HashMap<String, ArrayList<UserBooking>> has = new HashMap<>();
        userBookingListSection = new ArrayList<>();
        for (int i = 0; i < userBookingList.size(); i++) {


            if (has.containsKey(ProjectUtils.changeDateFormate1(userBookingList.get(i).getBooking_date()))) {
                userBookingListSection1 = new ArrayList<>();
                userBookingListSection1 = has.get(ProjectUtils.changeDateFormate1(userBookingList.get(i).getBooking_date()));
                userBookingListSection1.add(userBookingList.get(i));
                has.put(ProjectUtils.changeDateFormate1(userBookingList.get(i).getBooking_date()), userBookingListSection1);


            } else {
                userBookingListSection1 = new ArrayList<>();
                userBookingListSection1.add(userBookingList.get(i));
                has.put(ProjectUtils.changeDateFormate1(userBookingList.get(i).getBooking_date()), userBookingListSection1);
            }
        }

        for (String key : has.keySet()) {
            UserBooking userBooking = new UserBooking();
            userBooking.setSection(true);
            userBooking.setSection_name(key);
            userBookingListSection.add(userBooking);
            userBookingListSection.addAll(has.get(key));

        }


        showData();

    }
}
