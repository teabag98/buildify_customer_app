package com.qinsley.mbcustomer.ui.fragment;

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
import com.qinsley.mbcustomer.DTO.HistoryDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.adapter.UnPaidAdapter;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnPaidFrag extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    private View view;
    private String TAG = UnPaidFrag.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private UnPaidAdapter unPaidAdapter;
    private ArrayList<HistoryDTO> historyDTOList;
    private ArrayList<HistoryDTO> historyDTOListUnPaid;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private LayoutInflater myInflater;
    private SearchView svSearch;
    private RelativeLayout rlSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unpaid, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        myInflater = LayoutInflater.from(getActivity());
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        rlSearch = v.findViewById(R.id.rlSearch);
        svSearch = v.findViewById(R.id.svSearch);
        tvNo = v.findViewById(R.id.tvNo);
        RVhistorylist = v.findViewById(R.id.RVhistorylist);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVhistorylist.setLayoutManager(mLayoutManager);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    unPaidAdapter.filter(newText.toString());

                } else {


                }
                return false;
            }
        });


    }



    public void getHistroy() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_INVOICE_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    rlSearch.setVisibility(View.VISIBLE);
                    try {
                        historyDTOList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<HistoryDTO>>() {
                        }.getType();
                        historyDTOList = (ArrayList<HistoryDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);
                    rlSearch.setVisibility(View.GONE);
                }
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

                                            getHistroy();

                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        parms.put(Consts.ROLE, "2");
        return parms;
    }

    public void showData() {
        historyDTOListUnPaid = new ArrayList<>();
        for (int i = 0; i < historyDTOList.size(); i++) {
            if (historyDTOList.get(i).getFlag().trim().equals("0")) {
                historyDTOListUnPaid.add(historyDTOList.get(i));
            } else {
            }
        }

        if (historyDTOListUnPaid.size()>0){
            tvNo.setVisibility(View.GONE);
            RVhistorylist.setVisibility(View.VISIBLE);
            rlSearch.setVisibility(View.VISIBLE);

            unPaidAdapter = new UnPaidAdapter(UnPaidFrag.this, historyDTOListUnPaid, userDTO, myInflater);
            RVhistorylist.setAdapter(unPaidAdapter);

        }else {
            tvNo.setVisibility(View.VISIBLE);
            RVhistorylist.setVisibility(View.GONE);
            rlSearch.setVisibility(View.GONE);
        }
    }
    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getHistroy();
    }


}
