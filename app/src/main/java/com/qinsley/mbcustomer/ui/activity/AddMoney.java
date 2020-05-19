package com.qinsley.mbcustomer.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.DecimalDigitsInputFilter;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityAddMoney2Binding;
import com.qinsley.mbcustomer.databinding.DailogPaymentOptionBinding;

import org.json.JSONObject;

import java.util.HashMap;

public class AddMoney extends AppCompatActivity implements View.OnClickListener {
    private String TAG = AddMoney.class.getSimpleName();
    private Context mContext;
    float rs = 0;
    float rs1 = 0;
    float final_rs = 0;
    private HashMap<String, String> parmas = new HashMap<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String amt = "";
    private String currency = "";
    private Dialog dialog;
    private ActivityAddMoney2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_money2);
        mContext = AddMoney.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parmas.put(Consts.USER_ID, userDTO.getUser_id());
        setUiAction();
    }

    public void setUiAction() {

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().hasExtra(Consts.AMOUNT)) {
            amt = getIntent().getStringExtra(Consts.AMOUNT);
            currency = getIntent().getStringExtra(Consts.CURRENCY);

            binding.tvWallet.setText(currency + " " + amt);
        }

        binding.cbAdd.setOnClickListener(this);

        binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());

        binding.tv1000.setOnClickListener(this);

        binding.tv1500.setOnClickListener(this);

        binding.tv2000.setOnClickListener(this);

        binding.tv1000.setText("+ " + currency + " 1000");
        binding.tv1500.setText("+ " + currency + " 1500");
        binding.tv2000.setText("+ " + currency + " 2000");


        binding.etAddMoney.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
        binding.etAddMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (binding.etAddMoney.getText().toString().trim().equalsIgnoreCase("")) {
            rs1 = 0;

        } else {
            rs1 = Float.parseFloat(binding.etAddMoney.getText().toString().trim());

        }

        switch (v.getId()) {
            case R.id.tv1000:
                rs = 1000;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(final_rs + "");
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.tv1500:
                rs = 1500;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(final_rs + "");
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.tv2000:
                rs = 2000;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(final_rs + "");
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.cbAdd:
                if (binding.etAddMoney.getText().toString().length() > 0 && Float.parseFloat(binding.etAddMoney.getText().toString().trim()) > 0) {
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        parmas.put(Consts.AMOUNT, ProjectUtils.getEditTextValue(binding.etAddMoney));
                        dialogPayment();


                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
                    }
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.val_money));
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (prefrence.getValue(Consts.SURL).equalsIgnoreCase(Consts.PAYMENT_SUCCESS_paypal)) {
            prefrence.clearPreferences(Consts.SURL);
            addMoney();
        } else if (prefrence.getValue(Consts.FURL).equalsIgnoreCase(Consts.PAYMENT_FAIL_Paypal)) {
            prefrence.clearPreferences(Consts.FURL);
            finish();
        }
    }


    public void addMoney() {
        new HttpsRequest(Consts.ADD_MONEY_API, parmas, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showLong(mContext, msg);
                    finish();
                } else {
                    ProjectUtils.showLong(mContext, msg);
                }
            }
        });
    }


    public void dialogPayment() {
        dialog = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        final DailogPaymentOptionBinding binding1 = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dailog_payment_option, null, false);
        dialog.setContentView(binding1.getRoot());
        ///dialog.getWindow().setBackgroundDrawableResource(R.color.black);

        dialog.show();
        dialog.setCancelable(false);
        binding1.llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        binding1.llPaypall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Consts.MAKE_PAYMENT_paypal + "amount=" + ProjectUtils.getEditTextValue(binding.etAddMoney) + "&userId=" + userDTO.getUser_id();
                Intent in2 = new Intent(mContext, PaymetWeb.class);
                in2.putExtra(Consts.PAYMENT_URL, url);
                startActivity(in2);
                dialog.dismiss();

            }
        });
//        binding1.llStripe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = Consts.MAKE_PAYMENT + userDTO.getUser_id() + "/" + ProjectUtils.getEditTextValue(binding.etAddMoney);
//                Intent in2 = new Intent(mContext, PaymetWeb.class);
//                in2.putExtra(Consts.PAYMENT_URL, url);
//                startActivity(in2);
//                dialog.dismiss();
//
//            }
//        });

    }

}
