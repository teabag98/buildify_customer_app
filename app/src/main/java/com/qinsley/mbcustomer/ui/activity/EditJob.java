package com.qinsley.mbcustomer.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.CategoryDTO;
import com.qinsley.mbcustomer.DTO.PostedJobDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.interfacess.OnSpinerItemClick;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.ImageCompression;
import com.qinsley.mbcustomer.utils.MainFragment;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.utils.SpinnerDialog;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.databinding.ActivityEditJobBinding;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class EditJob extends AppCompatActivity implements View.OnClickListener {
    private String TAG = EditJob.class.getSimpleName();
    private Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private ArrayList<CategoryDTO> categoryDTOS;
    private HashMap<String, String> parmsadd = new HashMap<>();
    private HashMap<String, String> parmsCategory = new HashMap<>();
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    BottomSheet.Builder builder;
    String pathOfImage = "";
    Bitmap bm;
    ImageCompression imageCompression;
    private File image;
    public static final int MEDIA_TYPE_VIDEO = 6;
    HashMap<String, File> parmsFile = new HashMap<>();
    private Place place;
    private SpinnerDialog spinnerDialogCate;
    private PostedJobDTO postedJobDTO;
    private ActivityEditJobBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit_job);
        mContext = EditJob.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parmsadd.put(Consts.USER_ID, userDTO.getUser_id());
        parmsCategory.put(Consts.USER_ID, userDTO.getUser_id());

        if (getIntent().hasExtra(Consts.POST_JOB_DTO)) {
            postedJobDTO = (PostedJobDTO) getIntent().getSerializableExtra(Consts.POST_JOB_DTO);
        }
        setUiAction();
    }


    public void setUiAction() {

        binding.ivBack.setOnClickListener(this);
        binding.tvCategory.setOnClickListener(this);
        binding.etAddress.setOnClickListener(this);
        binding.llPicture.setOnClickListener(this);
        binding.llPost.setOnClickListener(this);
        builder = new BottomSheet.Builder(EditJob.this).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.take_image));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case R.id.camera_cards:
                        if (ProjectUtils.hasPermissionInManifest(EditJob.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(EditJob.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                try {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    File file = getOutputMediaFile(1);
                                    if (!file.exists()) {
                                        try {
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                        picUri = FileProvider.getUriForFile(EditJob.this.getApplicationContext(), EditJob.this.getApplicationContext().getPackageName() + ".fileprovider", file);
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
                        if (ProjectUtils.hasPermissionInManifest(EditJob.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(EditJob.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

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

        if (NetworkManager.isConnectToInternet(mContext)) {
            getCategory();
        } else {
            ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPicture:
                builder.show();
                break;
            case R.id.llPost:
                submitForm();
                break;
            case R.id.etAddress:
                findPlace();
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvCategory:
                if (categoryDTOS.size() > 0 || categoryDTOS != null) {
                    spinnerDialogCate.showSpinerDialog();
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.no_cate_found));
                }
                break;
        }
    }

    public void submitForm() {
        if (!validateTitle()) {
            return;
        } else if (!validateAddress()) {
            return;
        } else if (!validateComment()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                addPost();

            } else {
                ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
            }
        }
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    Consts.APP_NAME + timeStamp + ".png");

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
                    //bitmap = MediaStore.Images.Media.getBitmap(PostJob.this.getContentResolver(), resultUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(EditJob.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            Glide.with(mContext).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(binding.ivImg);

                            pathOfImage = imagePath;
                            image = new File(imagePath);
                            parmsFile.put(Consts.AVTAR, image);
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
                Log.e("image 1", picUri + "");
                try {
                    bm = MediaStore.Images.Media.getBitmap(EditJob.this.getContentResolver(), picUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(EditJob.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {

                            Glide.with(mContext).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(binding.ivImg);
                            image = new File(imagePath);

                            pathOfImage = imagePath;
                            parmsFile.put(Consts.AVTAR, image);

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
                // image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, picUri));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            } else {
                picUri = Uri.parse(prefrence.getValue(Consts.IMAGE_URI_CAMERA));
                // image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, picUri));

                startCropping(picUri, CROP_CAMERA_IMAGE);
            }
        }


        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();

                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    //    image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, tempUri));
                    Log.e("image 2", image + "");
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

        Intent intent = new Intent(EditJob.this, MainFragment.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }


    public boolean validateComment() {
        if (!ProjectUtils.isEditTextFilled(binding.etCommet)) {
            binding.etCommet.setError(getResources().getString(R.string.val_des));
            binding.etCommet.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateAddress() {
        if (!ProjectUtils.isEditTextFilled(binding.etAddress)) {
            binding.etAddress.setError(getResources().getString(R.string.val_address));
            binding.etAddress.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateTitle() {
        if (!ProjectUtils.isEditTextFilled(binding.etTitle)) {
            binding.etTitle.setError(getResources().getString(R.string.val_title));
            binding.etTitle.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public void addPost() {
        parmsadd.put(Consts.JOB_ID, postedJobDTO.getJob_id());
        parmsadd.put(Consts.TITLE, ProjectUtils.getEditTextValue(binding.etTitle));
        parmsadd.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(binding.etCommet));
        parmsadd.put(Consts.ADDRESS, ProjectUtils.getEditTextValue(binding.etAddress));
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));

        new HttpsRequest(Consts.EDIT_POST_JOB_API, parmsadd, parmsFile, mContext).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showLong(mContext, msg);
                    finish();
                } else {
                    ProjectUtils.showLong(mContext, msg);
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void findPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                //.withLocation(41.4036299, 2.1743558)
                .build(mContext);

        startActivityForResult(locationPickerIntent, 101);
    }


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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

            binding.etAddress.setText(obj.getAddressLine(0));

            parmsadd.put(Consts.LATI, String.valueOf(lat));
            parmsadd.put(Consts.LONGI, String.valueOf(lng));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void getCategory() {
        new HttpsRequest(Consts.GET_ALL_CATEGORY_API, parmsCategory, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    try {
                        categoryDTOS = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<CategoryDTO>>() {
                        }.getType();
                        categoryDTOS = (ArrayList<CategoryDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);

                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }


    public void showData() {
        binding.etCommet.setText(postedJobDTO.getDescription());
        binding.etTitle.setText(postedJobDTO.getTitle());
        binding.etAddress.setText(postedJobDTO.getAddress());

        for (int j = 0; j < categoryDTOS.size(); j++) {
            if (categoryDTOS.get(j).getId().equalsIgnoreCase(postedJobDTO.getCategory_id())) {
                categoryDTOS.get(j).setSelected(true);
                binding.tvCategory.setText(categoryDTOS.get(j).getCat_name());
            }
        }
        spinnerDialogCate = new SpinnerDialog((Activity) mContext, categoryDTOS, getResources().getString(R.string.select_category));// With 	Animation
        spinnerDialogCate.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, String id, int position) {
                binding.tvCategory.setText(item);
                parmsadd.put(Consts.CATEGORY_ID, id);
            }
        });

        Glide.with(mContext).load(postedJobDTO.getAvtar())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivImg);

    }
}
