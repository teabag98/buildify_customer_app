package com.qinsley.mbcustomer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.activity.AppIntro;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;
import com.qinsley.mbcustomer.utils.ProjectUtils;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    private SharedPrefrence prefference;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1003;
    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean cameraAccepted, storageAccepted, accessNetState, fineLoc, corasLoc;
    private Handler handler = new Handler();
    private static int SPLASH_TIME_OUT = 3000;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(this, new Crashlytics());

        ProjectUtils.Fullscreen(SplashActivity.this);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        prefference = SharedPrefrence.getInstance(SplashActivity.this);



    }


    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            if (prefference.getBooleanValue(Consts.IS_REGISTERED)) {
                Intent in = new Intent(mContext, BaseActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }else {
                startActivity(new Intent(SplashActivity.this, AppIntro.class));
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }


        }

    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!hasPermissions(SplashActivity.this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } else {
            handler.postDelayed(mTask, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                try {

                    cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.CAMERA_ACCEPTED, cameraAccepted);

                    storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.STORAGE_ACCEPTED, storageAccepted);

                    accessNetState = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.MODIFY_AUDIO_ACCEPTED, accessNetState);

                    fineLoc = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.FINE_LOC, fineLoc);

                    corasLoc = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    prefference.setBooleanValue(Consts.CORAS_LOC, corasLoc);
                    handler.postDelayed(mTask, 3000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}


