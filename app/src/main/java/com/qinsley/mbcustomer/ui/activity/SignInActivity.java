package com.qinsley.mbcustomer.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivitySignInBinding;

import org.json.JSONObject;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private String TAG = SignInActivity.class.getSimpleName();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPreferences firebase;
    private boolean isHide = false;
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(SignInActivity.this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in);
        mContext = SignInActivity.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        setUiAction();
    }

    public void setUiAction() {
        binding.CBsignIn.setOnClickListener(this);
        binding.CTVBforgot.setOnClickListener(this);
        binding.CTVsignup.setOnClickListener(this);
        binding.ivEnterShow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTVBforgot:
                startActivity(new Intent(mContext, ForgotPass.class));
                break;
            case R.id.CBsignIn:
                clickForSubmit();
                break;
            case R.id.CTVsignup:
                startActivity(new Intent(mContext, SignUpActivity.class));
                break;
            case R.id.ivEnterShow:
                if (isHide) {
                    binding.ivEnterShow.setImageResource(R.drawable.ic_pass_visible);
                    binding.CETenterpassword.setTransformationMethod(null);
                    binding.CETenterpassword.setSelection(binding.CETenterpassword.getText().length());
                    isHide = false;
                } else {
                    binding.ivEnterShow.setImageResource(R.drawable.ic_pass_invisible);
                    binding.CETenterpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.CETenterpassword.setSelection(binding.CETenterpassword.getText().length());
                    isHide = true;
                }
                break;
        }
    }

    public void login() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.LOGIN_API, getparm(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(mContext, msg);

                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);

                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);
                        ProjectUtils.showToast(mContext, msg);
                        Intent in = new Intent(mContext, BaseActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void clickForSubmit() {
        if (!ProjectUtils.isEmailValid(binding.CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        } else if (!ProjectUtils.isPasswordValid(binding.CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                login();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }


    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
        parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(binding.CETenterpassword));
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        parms.put(Consts.ROLE, "2");
        Log.e(TAG + " Login", parms.toString());
        return parms;
    }

    public void showSickbar(String msg) {
        Snackbar snackbar = Snackbar.make(binding.RRsncbar, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.closeMsg))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
