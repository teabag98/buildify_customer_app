package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.AppliedJobDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityAppliedJob2Binding;
import com.qinsley.mbcustomer.ui.adapter.AppliedJobAdapter;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppliedJob extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private String TAG = AppliedJob.class.getSimpleName();
    private Context mContext;
    private AppliedJobAdapter appliedJobAdapter;
    private ArrayList<AppliedJobDTO> appliedJobDTOSList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private ActivityAppliedJob2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_applied_job2);
        mContext = AppliedJob.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);


        setUiAction();
    }

    public void setUiAction() {
        mLayoutManager = new LinearLayoutManager(mContext.getApplicationContext());
        binding.RVhistorylist.setLayoutManager(mLayoutManager);
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    appliedJobAdapter.filter(newText.toString());


                } else {


                }
                return false;
            }
        });
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(mContext)) {
                                            binding.swipeRefreshLayout.setRefreshing(true);
                                            getjobs();

                                        } else {
                                            ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }


    public void getjobs() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_APPLIED_JOB_BY_ID_API, getparm(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    binding.tvNo.setVisibility(View.GONE);
                    binding.RVhistorylist.setVisibility(View.VISIBLE);
                    binding.rlSearch.setVisibility(View.VISIBLE);
                    try {
                        appliedJobDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<AppliedJobDTO>>() {
                        }.getType();
                        appliedJobDTOSList = (ArrayList<AppliedJobDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(mContext, msg);
                    binding.tvNo.setVisibility(View.VISIBLE);
                    binding.RVhistorylist.setVisibility(View.GONE);
                    binding.rlSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.JOB_ID, prefrence.getValue(Consts.JOB_ID));
        return parms;
    }

    public void showData() {

        appliedJobAdapter = new AppliedJobAdapter(AppliedJob.this, appliedJobDTOSList, userDTO);
        binding.RVhistorylist.setAdapter(appliedJobAdapter);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getjobs();
    }

}
