package com.qinsley.mbcustomer.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.TicketDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.adapter.TicketAdapter;
import com.qinsley.mbcustomer.utils.CustomEditText;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tickets extends Fragment {
    private String TAG = Tickets.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private TicketAdapter ticketAdapter;
    private ArrayList<TicketDTO> ticketDTOSList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private View view;
    private BaseActivity baseActivity;
    private ImageView ivPost;
    private Dialog dialog;
    private CustomEditText etReason,etDescription;
    private CustomTextView tvCancel, tvAdd;
    private HashMap<String, String> parmsadd = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ticket, container, false);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.support));
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        tvNo = v.findViewById(R.id.tvNo);
        RVhistorylist = v.findViewById(R.id.RVhistorylist);
        ivPost = v.findViewById(R.id.ivPost);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVhistorylist.setLayoutManager(mLayoutManager);

        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    dialogshow();
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getTicket();

        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    public void getTicket() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_MY_TICKET_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    try {
                        ticketDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<TicketDTO>>() {
                        }.getType();
                        ticketDTOSList = (ArrayList<TicketDTO>) new Gson().fromJson(response.getJSONArray("my_ticket").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);
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
        ticketAdapter = new TicketAdapter(Tickets.this, ticketDTOSList, userDTO);
        RVhistorylist.setAdapter(ticketAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }


    public void dialogshow() {
        dialog = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_add_ticket);

        etReason = (CustomEditText) dialog.findViewById(R.id.etReason);
        tvCancel = (CustomTextView) dialog.findViewById(R.id.tvCancel);
        tvAdd = (CustomTextView) dialog.findViewById(R.id.tvAdd);
        etDescription = (CustomEditText) dialog.findViewById(R.id.etDescription);

        dialog.show();
        dialog.setCancelable(false);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

    }


    public void submitForm() {
        if (!validateReason()) {
            return;
        } else if (!validateDescription()) {
            return;
        } else {
            addTicket();

        }
    }

    public boolean validateReason() {
        if (etReason.getText().toString().trim().equalsIgnoreCase("")) {
            etReason.setError(getResources().getString(R.string.val_title));
            etReason.requestFocus();
            return false;
        } else {
            etReason.setError(null);
            etReason.clearFocus();
            return true;
        }
    }

    public boolean validateDescription() {
        if (etDescription.getText().toString().trim().equalsIgnoreCase("")) {
            etDescription.setError(getResources().getString(R.string.val_description));
            etDescription.requestFocus();
            return false;
        } else {
            etDescription.setError(null);
            etDescription.clearFocus();
            return true;
        }
    }

    public void addTicket() {
        parmsadd.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(etDescription));
        parmsadd.put(Consts.REASON, ProjectUtils.getEditTextValue(etReason));
        parmsadd.put(Consts.USER_ID, userDTO.getUser_id());
        ProjectUtils.showProgressDialog(getActivity(), false, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GENERATE_TICKET_API, parmsadd, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    dialog.dismiss();
                    ProjectUtils.showToast(getActivity(), msg);
                    getTicket();
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }
}
