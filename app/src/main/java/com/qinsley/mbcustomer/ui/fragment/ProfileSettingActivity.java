package com.qinsley.mbcustomer.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.CustomButton;
import com.qinsley.mbcustomer.utils.CustomEditText;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ImageCompression;
import com.qinsley.mbcustomer.utils.MainFragment;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;
import com.qinsley.mbcustomer.ui.activity.SignInActivity;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class ProfileSettingActivity extends Fragment implements View.OnClickListener {
    private ImageView ivPersonalInfoChange, ivAddressChange;
    private CircleImageView ivProfile;
    private CustomButton btnDelete, btnChange;
    private CustomEditText etName, etEmail, etMobile, etGender, etAddress, etAddressSelect, etCity, etCountry;
    public RadioGroup rg_gender_options;
    public RadioButton rb_gender_female, rb_gender_male;
    private Dialog dialog_profile, dialog_pass, dialog_address;
    private CustomTextViewBold tvYes, tvNo, tvYesPass, tvNoPass, tvYesAddress, tvNoAddress;
    private CustomEditText etNameD, etEmailD, etMobileD, etOldPassD, etNewPassD, etConfrimPassD, etAddressD, etCityD, etCountryD, etAddressDs;
    private HashMap<String, String> params;
    private RelativeLayout RRsncbar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String TAG = ProfileSettingActivity.class.getSimpleName();

    private HashMap<String, File> paramsFile = new HashMap<>();
    BottomSheet.Builder builder;
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    String imageName;
    String pathOfImage;
    Bitmap bm;
    ImageCompression imageCompression;
    byte[] resultByteArray;
    File file;
    Bitmap bitmap = null;
    private View view;
    private BaseActivity baseActivity;
    private HashMap<String, String> paramsDeleteImg = new HashMap<>();
    private double lats = 0.0;
    private double longs = 0.0;
    private LinearLayout llChangePass, llLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_profile_setting, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        baseActivity.headerNameTV.setText(getResources().getString(R.string.profile_settings));
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        llLogout = v.findViewById(R.id.llLogout);
        RRsncbar = v.findViewById(R.id.RRsncbar);
        ivPersonalInfoChange = v.findViewById(R.id.ivPersonalInfoChange);
        llChangePass = v.findViewById(R.id.llChangePass);
        ivAddressChange = v.findViewById(R.id.ivAddressChange);
        ivProfile = v.findViewById(R.id.ivProfile);
        btnDelete = v.findViewById(R.id.btnDelete);
        btnChange = v.findViewById(R.id.btnChange);
        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etMobile = v.findViewById(R.id.etMobile);
        etGender = v.findViewById(R.id.etGender);
        etAddress = v.findViewById(R.id.etAddress);
        etCity = v.findViewById(R.id.etCity);
        etCountry = v.findViewById(R.id.etCountry);
        etAddressSelect = v.findViewById(R.id.etAddressSelect);


        ivPersonalInfoChange.setOnClickListener(this);
        llChangePass.setOnClickListener(this);
        ivAddressChange.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        llLogout.setOnClickListener(this);

        showData();

        builder = new BottomSheet.Builder(getActivity()).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.take_image));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.camera_cards:
                        if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                try {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File file = getOutputMediaFile(1);
                                    if (!file.exists()) {
                                        try {
                                            ProjectUtils.pauseProgressDialog();
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                        picUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
                                    } else {
                                        picUri = Uri.fromFile(file); // create
                                    }

                                    prefrence.setValue(Consts.IMAGE_URI_CAMERA, picUri.toString());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                                    startActivityForResult(intent, PICK_FROM_CAMERA);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        break;
                    case R.id.gallery_cards:
                        if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                File file = getOutputMediaFile(1);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                picUri = Uri.fromFile(file);

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), PICK_FROM_GALLERY);

                            }
                        }
                        break;
                    case R.id.cancel_cards:
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        break;
                }
            }
        });
    }

    private File getOutputMediaFile(int type) {
        String root = Environment.getExternalStorageDirectory().toString();
        File mediaStorageDir = new File(root, Consts.APP_NAME);
        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    Consts.APP_NAME + timeStamp + ".png");

            imageName = Consts.APP_NAME + timeStamp + ".png";
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_CAMERA_IMAGE) {
            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(getActivity());
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            Glide.with(getActivity()).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivProfile);
                            try {
                                // bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                                file = new File(imagePath);
                                paramsFile.put(Consts.IMAGE, file);
                                Log.e("image", imagePath);
                                params = new HashMap<>();
                                params.put(Consts.USER_ID, userDTO.getUser_id());
                                if (NetworkManager.isConnectToInternet(getActivity())) {
                                    updateProfile();
                                } else {
                                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CROP_GALLERY_IMAGE) {
            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                try {
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(getActivity());
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            Glide.with(getActivity()).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivProfile);
                            Log.e("image", imagePath);
                            try {
                                file = new File(imagePath);
                                paramsFile.put(Consts.IMAGE, file);

                                params = new HashMap<>();
                                params.put(Consts.USER_ID, userDTO.getUser_id());
                                if (NetworkManager.isConnectToInternet(getActivity())) {
                                    updateProfile();
                                } else {
                                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                }
                                Log.e("image", imagePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            if (picUri != null) {
                picUri = Uri.parse(prefrence.getValue(Consts.IMAGE_URI_CAMERA));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            } else {
                picUri = Uri.parse(prefrence
                        .getValue(Consts.IMAGE_URI_CAMERA));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            }
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();
                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    startCropping(tempUri, CROP_GALLERY_IMAGE);
                } else {

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                try {
                    getAddress(data.getDoubleExtra(LATITUDE, 0.0), data.getDoubleExtra(LONGITUDE, 0.0));


                } catch (Exception e) {

                }
            }
        }

    }

    public void startCropping(Uri uri, int requestCode) {
        Intent intent = new Intent(getActivity(), MainFragment.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    public void showData() {
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        Glide.with(getActivity()).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfile);

        etName.setText(userDTO.getName());
        etEmail.setText(userDTO.getEmail_id());
        etMobile.setText(userDTO.getMobile());
        if (userDTO.getGender().equalsIgnoreCase("0")) {
            etGender.setText(getResources().getString(R.string.female));
        } else if (userDTO.getGender().equalsIgnoreCase("1")) {
            etGender.setText(getResources().getString(R.string.male));
        } else {
            etGender.setText("");
        }

        etAddress.setText(userDTO.getAddress());
        etCity.setText(userDTO.getCity());
        etCountry.setText(userDTO.getCountry());
        etAddressSelect.setText(userDTO.getOffice_address());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    if (!userDTO.getImage().equalsIgnoreCase("")) {
                        deleteImage();
                    } else {
                        ProjectUtils.showToast(getActivity(), getResources().getString(R.string.upload_image_first));
                    }
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.btnChange:
                builder.show();
                break;
            case R.id.ivPersonalInfoChange:
                dialogPersonalProfile();
                break;
            case R.id.llChangePass:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    dialogPassword();
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.ivAddressChange:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    dialogAddress();
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.llLogout:
                confirmLogout();
                break;

        }
    }

    public void dialogPersonalProfile() {
        dialog_profile = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialog_profile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_profile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_profile.setContentView(R.layout.dailog_personal_info);

        rg_gender_options = dialog_profile.findViewById(R.id.rg_gender_options);
        rb_gender_male = dialog_profile.findViewById(R.id.rb_gender_male);
        rb_gender_female = dialog_profile.findViewById(R.id.rb_gender_female);

        etNameD = (CustomEditText) dialog_profile.findViewById(R.id.etNameD);
        etEmailD = (CustomEditText) dialog_profile.findViewById(R.id.etEmailD);
        etMobileD = (CustomEditText) dialog_profile.findViewById(R.id.etMobileD);

        etNameD.setText(userDTO.getName());
        etEmailD.setText(userDTO.getEmail_id());
        etMobileD.setText(userDTO.getMobile());

        if (userDTO.getGender().equalsIgnoreCase("0")) {
            rb_gender_female.setChecked(true);
            rb_gender_male.setChecked(false);
        } else if (userDTO.getGender().equalsIgnoreCase("1")) {
            rb_gender_female.setChecked(false);
            rb_gender_male.setChecked(true);
        } else {
            rb_gender_female.setChecked(false);
            rb_gender_male.setChecked(false);
        }

        tvYes = (CustomTextViewBold) dialog_profile.findViewById(R.id.tvYes);
        tvNo = (CustomTextViewBold) dialog_profile.findViewById(R.id.tvNo);
        dialog_profile.show();
        dialog_profile.setCancelable(false);

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_profile.dismiss();

            }
        });
        tvYes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        params.put(Consts.NAME, ProjectUtils.getEditTextValue(etNameD));
                        params.put(Consts.MOBILE, ProjectUtils.getEditTextValue(etMobileD));
                        if (rb_gender_female.isChecked()) {
                            params.put(Consts.GENDER, "0");
                        } else {
                            params.put(Consts.GENDER, "1");
                        }

                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            updateProfile();
                            dialog_profile.dismiss();
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    public void dialogPassword() {
        dialog_pass = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialog_pass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_pass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pass.setContentView(R.layout.dailog_password);


        etOldPassD = (CustomEditText) dialog_pass.findViewById(R.id.etOldPassD);
        etNewPassD = (CustomEditText) dialog_pass.findViewById(R.id.etNewPassD);
        etConfrimPassD = (CustomEditText) dialog_pass.findViewById(R.id.etConfrimPassD);


        etOldPassD.setTransformationMethod(new PasswordTransformationMethod());
        etNewPassD.setTransformationMethod(new PasswordTransformationMethod());
        etConfrimPassD.setTransformationMethod(new PasswordTransformationMethod());


        tvYesPass = (CustomTextViewBold) dialog_pass.findViewById(R.id.tvYesPass);
        tvNoPass = (CustomTextViewBold) dialog_pass.findViewById(R.id.tvNoPass);
        dialog_pass.show();
        dialog_pass.setCancelable(false);

        tvNoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_pass.dismiss();

            }
        });
        tvYesPass.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        params.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(etOldPassD));
                        params.put(Consts.NEW_PASSWORD, ProjectUtils.getEditTextValue(etNewPassD));

                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            Submit();

                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    public void dialogAddress() {
        dialog_address = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialog_address.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_address.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog_address.setContentView(R.layout.dailog_address);


        etAddressD = (CustomEditText) dialog_address.findViewById(R.id.etAddressD);
        etCityD = (CustomEditText) dialog_address.findViewById(R.id.etCityD);
        etCountryD = (CustomEditText) dialog_address.findViewById(R.id.etCountryD);
        etAddressDs = (CustomEditText) dialog_address.findViewById(R.id.etAddressDs);


        etAddressD.setText(userDTO.getAddress());
        etCityD.setText(userDTO.getCity());
        etCountryD.setText(userDTO.getCountry());
        etAddressDs.setText(userDTO.getOffice_address());

        tvYesAddress = (CustomTextViewBold) dialog_address.findViewById(R.id.tvYesAddress);
        tvNoAddress = (CustomTextViewBold) dialog_address.findViewById(R.id.tvNoAddress);
        dialog_address.show();
        dialog_address.setCancelable(false);

        etAddressDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace();

            }
        });
        tvNoAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_address.dismiss();

            }
        });
        tvYesAddress.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        params.put(Consts.ADDRESS, ProjectUtils.getEditTextValue(etAddressD));
                        params.put(Consts.OFFICE_ADDRESS, ProjectUtils.getEditTextValue(etAddressDs));
                        params.put(Consts.CITY, ProjectUtils.getEditTextValue(etCityD));
                        params.put(Consts.COUNTRY, ProjectUtils.getEditTextValue(etCountryD));
                        params.put(Consts.LATITUDE, String.valueOf(lats));
                        params.put(Consts.LONGITUDE, String.valueOf(longs));
                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            updateProfile();
                            dialog_address.dismiss();
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    public void updateProfile() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_PROFILE_API, params, paramsFile, getActivity()).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(getActivity(), msg);

                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        baseActivity.showImage();
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

    public void confirmLogout() {
        try {
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.logout_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            prefrence.clearAllPreferences();
                            Intent intent = new Intent(getActivity(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            baseActivity.finish();
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    public void deleteImage() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        paramsDeleteImg.put(Consts.USER_ID, userDTO.getUser_id());
        new HttpsRequest(Consts.DELETE_PROFILE_IMAGE_API, paramsDeleteImg, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    userDTO.setImage("");
                    prefrence.setParentUser(userDTO, Consts.USER_DTO);
                    showData();
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }


    private void findPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                //.withLocation(41.4036299, 2.1743558)
                .build(getActivity());

        startActivityForResult(locationPickerIntent, 101);
    }


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            Log.e("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);

            etAddressDs.setText(obj.getAddressLine(0));

            lats = lat;
            longs = lng;


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void Submit() {
        if (!passwordValidation()) {
            return;
        } else if (!checkpass()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(getActivity())) {
                updateProfile();
                dialog_pass.dismiss();
            } else {
                ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
            }

        }
    }

    public boolean passwordValidation() {
        if (!ProjectUtils.isPasswordValid(etOldPassD.getText().toString().trim())) {
            etOldPassD.setError(getResources().getString(R.string.val_pass_c));
            etOldPassD.requestFocus();
            return false;
        } else if (!ProjectUtils.isPasswordValid(etNewPassD.getText().toString().trim())) {
            etNewPassD.setError(getResources().getString(R.string.val_pass_c));
            etNewPassD.requestFocus();
            return false;
        } else
            return true;

    }

    private boolean checkpass() {
        if (etNewPassD.getText().toString().trim().equals("")) {
            etNewPassD.setError(getResources().getString(R.string.val_new_pas));
            return false;
        } else if (etConfrimPassD.getText().toString().trim().equals("")) {
            etConfrimPassD.setError(getResources().getString(R.string.val_c_pas));
            return false;
        } else if (!etNewPassD.getText().toString().trim().equals(etConfrimPassD.getText().toString().trim())) {
            etConfrimPassD.setError(getResources().getString(R.string.val_n_c_pas));
            return false;
        }
        return true;
    }
}
