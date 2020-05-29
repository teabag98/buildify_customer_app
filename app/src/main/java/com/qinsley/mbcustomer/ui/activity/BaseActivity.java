package com.qinsley.mbcustomer.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.ui.fragment.AppointmentFrag;
import com.qinsley.mbcustomer.ui.fragment.ChatList;
import com.qinsley.mbcustomer.ui.fragment.GetDiscountActivity;
import com.qinsley.mbcustomer.ui.fragment.HistoryFragment;
import com.qinsley.mbcustomer.ui.fragment.Home;
import com.qinsley.mbcustomer.ui.fragment.Jobs;
import com.qinsley.mbcustomer.ui.fragment.MyBooking;
import com.qinsley.mbcustomer.ui.fragment.NotificationActivity;
import com.qinsley.mbcustomer.ui.fragment.ProfileSettingActivity;
import com.qinsley.mbcustomer.ui.fragment.Tickets;
import com.qinsley.mbcustomer.ui.fragment.Wallet;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.CustomTypeFaceSpan;
import com.qinsley.mbcustomer.utils.FontCache;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private String TAG = BaseActivity.class.getSimpleName();
    HashMap<String, String> params = new HashMap<>();

    private FrameLayout frame;
    private View contentView;
    public String latitude;
    public String longitude;
    public NavigationView navigationView;
    public RelativeLayout header;
    public DrawerLayout drawer;
    public View navHeader;
    public ImageView menuLeftIV, ivFilter;
    Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    public static final String TAG_MAIN = "main";
    public static final String TAG_CHAT = "chat";
    public static final String TAG_BOOKING = "booking";
    public static final String TAG_NOTIFICATION = "notification";
    public static final String TAG_DISCOUNT = "discount";
    public static final String TAG_HISTORY = "history";
    public static final String TAG_PROFILE_SETINGS = "profile_settings";
    public static final String TAG_TICKETS = "tickets";
    public static final String TAG_APPOINTMENT = "appointment";
    public static final String TAG_JOBS = "jobs";
    public static final String TAG_WALLET = "wallet";
    public static String CURRENT_TAG = TAG_MAIN;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private static final float END_SCALE = 0.8f;
    InputMethodManager inputManager;
    Home home = null;
    private boolean shouldLoadHomeFragOnBackPress = true;
    public CustomTextViewBold headerNameTV;
    private Location mylocation = new Location("test");
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private CircleImageView img_profile;
    private CustomTextViewBold tvName;
    private CustomTextView tvEmail, tvOther, tvEnglish;
    private LinearLayout llProfileClick;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = BaseActivity.this;
        mHandler = new Handler();
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);


        if (getIntent().hasExtra(Consts.SCREEN_TAG)) {
            type = getIntent().getStringExtra(Consts.SCREEN_TAG);
        }


        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); // two minute interval
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        setUpGClient();

        frame = (FrameLayout) findViewById(R.id.frame);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        contentView = findViewById(R.id.content);
        headerNameTV = findViewById(R.id.headerNameTV);
        menuLeftIV = (ImageView) findViewById(R.id.menuLeftIV);
        ivFilter = (ImageView) findViewById(R.id.ivFilter);


        navHeader = navigationView.getHeaderView(0);
        img_profile = navHeader.findViewById(R.id.img_profile);
        tvName = navHeader.findViewById(R.id.tvName);
        tvEmail = navHeader.findViewById(R.id.tvEmail);

        tvEnglish = navHeader.findViewById(R.id.tvEnglish);
        tvOther = navHeader.findViewById(R.id.tvOther);
        tvOther = navHeader.findViewById(R.id.tvOther);
        llProfileClick = navHeader.findViewById(R.id.llProfileClick);


        llProfileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFilter.setVisibility(View.GONE);
                navItemIndex = 9;
                CURRENT_TAG = TAG_PROFILE_SETINGS;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, new ProfileSettingActivity());
                fragmentTransaction.commitAllowingStateLoss();
                drawer.closeDrawers();
            }
        });

        tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language("en");

            }
        });
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                language("ar");

            }
        });
        Glide.with(mContext).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_profile);
        tvEmail.setText(userDTO.getEmail_id());
        tvName.setText(userDTO.getName());

        if (savedInstanceState == null) {
            if (type != null) {
                if (type.equalsIgnoreCase(Consts.CHAT_NOTIFICATION)) {
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_CHAT;
                    loadHomeFragment(new ChatList(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.TICKET_COMMENT_NOTIFICATION)) {
                    navItemIndex = 10;
                    CURRENT_TAG = TAG_TICKETS;
                    loadHomeFragment(new Tickets(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.TICKET_STATUS_NOTIFICATION)) {
                    navItemIndex = 10;
                    CURRENT_TAG = TAG_TICKETS;
                    loadHomeFragment(new Tickets(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.WALLET_NOTIFICATION)) {
                    navItemIndex = 8;
                    CURRENT_TAG = TAG_WALLET;
                    loadHomeFragment(new Wallet(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.DECLINE_BOOKING_ARTIST_NOTIFICATION)) {
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.START_BOOKING_ARTIST_NOTIFICATION)) {
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.END_BOOKING_ARTIST_NOTIFICATION)) {
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.ACCEPT_BOOKING_ARTIST_NOTIFICATION)) {
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.JOB_APPLY_NOTIFICATION)) {
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_JOBS;
                    loadHomeFragment(new Jobs(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.BRODCAST_NOTIFICATION)) {
                    navItemIndex = 5;
                    CURRENT_TAG = TAG_NOTIFICATION;
                    loadHomeFragment(new NotificationActivity(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Consts.ADMIN_NOTIFICATION)) {
                    navItemIndex = 5;
                    CURRENT_TAG = TAG_NOTIFICATION;
                    loadHomeFragment(new NotificationActivity(), CURRENT_TAG);
                } else {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_MAIN;
                    loadHomeFragment(new Home(), CURRENT_TAG);
                }
            } else {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAIN;
                loadHomeFragment(new Home(), CURRENT_TAG);
            }


        }
        menuLeftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerOpen();
            }
        });

        setUpNavigationView();
        Menu menu = navigationView.getMenu();

        changeColorItem(menu, R.id.nav_home_features);
        changeColorItem(menu, R.id.nav_bookings_and_job);
        changeColorItem(menu, R.id.nav_personal);
        changeColorItem(menu, R.id.other);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyCustomFont(subMenuItem);
                }
            }
            applyCustomFont(mi);
        }


        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {

                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                     }
                                 }
        );


    }

    public void changeColorItem(Menu menu, int id) {
        MenuItem tools = menu.findItem(id);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);

    }

    public void applyCustomFont(MenuItem mi) {
        Typeface customFont = FontCache.getTypeface("Poppins-Regular.otf", BaseActivity.this);
        SpannableString spannableString = new SpannableString(mi.getTitle());
        spannableString.setSpan(new CustomTypeFaceSpan("", customFont), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(spannableString);
    }

    public void showImage() {
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        Glide.with(mContext).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_profile);
        tvName.setText(userDTO.getName());
    }

    private void loadHomeFragment(final Fragment fragment, final String TAG) {

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, TAG);
                fragmentTransaction.commitAllowingStateLoss();
                ivFilter.setVisibility(View.VISIBLE);

            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        drawer.closeDrawers();

        invalidateOptionsMenu();
    }


    public void drawerOpen() {

        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

/*
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
*/

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        ivFilter.setVisibility(View.VISIBLE);
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAIN;
                        fragmentTransaction.replace(R.id.frame, new Home());
                        break;
                    case R.id.nav_chat:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_CHAT;
                        fragmentTransaction.replace(R.id.frame, new ChatList());
                        break;
                    case R.id.nav_booking:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_BOOKING;
                        fragmentTransaction.replace(R.id.frame, new MyBooking());
                        break;
                    case R.id.nav_jobs:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_JOBS;
                        fragmentTransaction.replace(R.id.frame, new Jobs());
                        break;
                    case R.id.nav_appointment:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_APPOINTMENT;
                        fragmentTransaction.replace(R.id.frame, new AppointmentFrag());
                        break;
                    case R.id.nav_notification:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_NOTIFICATION;
                        fragmentTransaction.replace(R.id.frame, new NotificationActivity());
                        break;
                    case R.id.nav_history:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_DISCOUNT;
                        fragmentTransaction.replace(R.id.frame, new HistoryFragment());
                        break;
                    case R.id.nav_discount:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_HISTORY;
                        fragmentTransaction.replace(R.id.frame, new GetDiscountActivity());
                        break;
                    case R.id.nav_wallet:
                        ivFilter.setVisibility(View.GONE);
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_WALLET;
                        fragmentTransaction.replace(R.id.frame, new Wallet());
                        break;
                    case R.id.nav_profilesetting:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_PROFILE_SETINGS;
                        ivFilter.setVisibility(View.GONE);
                        fragmentTransaction.replace(R.id.frame, new ProfileSettingActivity());
                        break;
                    case R.id.nav_tickets:
                        navItemIndex = 10;
                        CURRENT_TAG = TAG_TICKETS;
                        ivFilter.setVisibility(View.GONE);
                        fragmentTransaction.replace(R.id.frame, new Tickets());
                        break;
                    default:
                        ivFilter.setVisibility(View.VISIBLE);
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAIN;
                        fragmentTransaction.replace(R.id.frame, new Home());
                        break;

                }
                fragmentTransaction.commitAllowingStateLoss();
                drawer.closeDrawers();

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //   loadHomeFragment();

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAIN;
                loadHomeFragment(new Home(), CURRENT_TAG);
                return;
            }
        }

        //super.onBackPressed();
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getResources().getString(R.string.app_name))
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


    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mylocation = location;
                            if (mylocation != null) {

                                latitude = Double.toString(mylocation.getLatitude());
                                longitude = Double.toString(mylocation.getLongitude());
//                                mylocation.setLatitude(location.getLatitude());
//                                mylocation.setLongitude(location.getLongitude());
                                prefrence.setValue(Consts.LATITUDE, latitude + "");
                                prefrence.setValue(Consts.LONGITUDE, longitude + "");

                                params.put(Consts.USER_ID, userDTO.getUser_id());
                                params.put(Consts.ROLE, "2");
                                params.put(Consts.LATITUDE, latitude + "");
                                params.put(Consts.LONGITUDE, longitude + "");


                                Log.d("messagejjjkkj", "customer location" + latitude + longitude);

                            } else {
                                Toast.makeText(BaseActivity.this, "Turn on your GPS to enable your location update", Toast.LENGTH_LONG).show();
                                Log.d("message", "turn on your GPS mtfk");
                            }
                            updateLocation();

//                            final Status status = result.getStatus();
//                            switch (status.getStatusCode()) {
//                                case LocationSettingsStatusCodes.SUCCESS:
//                                    // All location settings are satisfied.
//                                    // You can initialize location requests here.
//                                    int permissionLocation = ContextCompat
//                                            .checkSelfPermission(BaseActivity.this,
//                                                    Manifest.permission.ACCESS_FINE_LOCATION);
//                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                                        mylocation = LocationServices.FusedLocationApi
//                                                .getLastLocation(googleApiClient);
//
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                    // Location settings are not satisfied.
//                                    // But could be fixed by showing the fabcustomer a dialog.
//                                    try {
//                                        // Show the dialog by calling startResolutionForResult(),
//                                        // and check the result in onActivityResult().
//                                        // Ask to turn on GPS automatically
//                                        status.startResolutionForResult(BaseActivity.this,
//                                                REQUEST_CHECK_SETTINGS_GPS);
//                                    } catch (IntentSender.SendIntentException e) {
//                                        // Ignore the error.
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                    // Location settings are not satisfied. However, we have no way to fix the
//                                    // settings so we won't show the dialog.
//                                    //finish();
//                                    break;
                        }
                    });

                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = Double.toString(mylocation.getLatitude());
            longitude = Double.toString(mylocation.getLongitude());
            prefrence.setValue(Consts.LATITUDE, latitude + "");
            prefrence.setValue(Consts.LONGITUDE, longitude + "");

            params.put(Consts.USER_ID, userDTO.getUser_id());
            params.put(Consts.ROLE, "2");
            params.put(Consts.LATITUDE, latitude + "");
            params.put(Consts.LONGITUDE, longitude + "");

        }
        updateLocation();
    }


    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(BaseActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(BaseActivity.this)
                .enableAutoManage(BaseActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    public void updateLocation() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_LOCATION_API, params, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {

                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    public void language(String language) {
        String languageToLoad = language; // your language

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        BaseActivity.this.getResources().updateConfiguration(config,
                BaseActivity.this.getResources().getDisplayMetrics());

        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);


    }

}
