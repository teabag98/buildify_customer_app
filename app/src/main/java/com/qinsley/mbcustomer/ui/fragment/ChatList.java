package com.qinsley.mbcustomer.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.ChatListDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.adapter.ChatListAdapter;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatList extends Fragment {
    private String TAG = ChatList.class.getSimpleName();
    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;
    private ArrayList<ChatListDTO> chatList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private View view;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_chat_list, container, false);

        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.chats));
        setUiAction(view);
        return view;
    }


    public void setUiAction(View v) {
        tvNo = v.findViewById(R.id.tvNo);
        rvChatList = v.findViewById(R.id.rvChatList);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvChatList.setLayoutManager(mLayoutManager);

        chatListAdapter = new ChatListAdapter(getActivity(), chatList);
        rvChatList.setAdapter(chatListAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getChat();

        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    public void getChat() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_CHAT_HISTORY_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    rvChatList.setVisibility(View.VISIBLE);
                    try {
                        chatList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<ChatListDTO>>() {
                        }.getType();
                        chatList = (ArrayList<ChatListDTO>) new Gson().fromJson(response.getJSONArray("my_chat").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    rvChatList.setVisibility(View.GONE);
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
        chatListAdapter = new ChatListAdapter(getActivity(), chatList);
        rvChatList.setAdapter(chatListAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
