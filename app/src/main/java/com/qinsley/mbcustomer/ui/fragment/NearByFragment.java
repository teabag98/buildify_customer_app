package com.qinsley.mbcustomer.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinsley.mbcustomer.DTO.AllAtristListDTO;
import com.qinsley.mbcustomer.DTO.CategoryDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.interfacess.OnSpinerItemClick;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.preferences.SharedPrefrence;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.utils.SpinnerDialog;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.ui.activity.ArtistProfile;
import com.qinsley.mbcustomer.ui.activity.BaseActivity;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


public class NearByFragment extends Fragment {
    private String TAG = NearByFragment.class.getSimpleName();
    View view;
    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> optionsList = new ArrayList<>();
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    HashMap<String, String> parms = new HashMap<>();
    private ArrayList<AllAtristListDTO> allAtristListDTOList;
    private Hashtable<String, AllAtristListDTO> markers;
    private Marker marker;
    private BaseActivity baseActivity;
    private HashMap<String, String> parmsCategory = new HashMap<>();
    private SpinnerDialog spinnerDialogCate;
    private ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();
    private ImageView ivCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_near_by, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        parmsCategory.put(Consts.USER_ID, userDTO.getUser_id());

        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        markers = new Hashtable<String, AllAtristListDTO>();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)));
               googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO.getName()).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_current_pin)).snippet("My Location"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(30).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });


        baseActivity.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryDTOS.size() > 0) {
                    spinnerDialogCate.showSpinerDialog();
                } else {
                    ProjectUtils.showLong(getActivity(), getResources().getString(R.string.no_cate_found));
                }
            }
        });
        return view;

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        getCategory();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            parms.put(Consts.CATEGORY_ID, "");
            getArtist();
        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void getArtist() {
        parms.put(Consts.LATITUDE, prefrence.getValue(Consts.LATITUDE));
        parms.put(Consts.LONGITUDE, prefrence.getValue(Consts.LONGITUDE));
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_ALL_ARTISTS_API, parms, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        allAtristListDTOList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<AllAtristListDTO>>() {
                        }.getType();
                        allAtristListDTOList = (ArrayList<AllAtristListDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);


                        for (int i = 0; i < allAtristListDTOList.size(); i++) {

                            optionsList.add(new MarkerOptions().position(
                                    new LatLng(Double.parseDouble(allAtristListDTOList.get(i).getLatitude()), Double.parseDouble(allAtristListDTOList.get(i).getLongitude()))).title(allAtristListDTOList.get(i).getName()).snippet(allAtristListDTOList.get(i).getUser_id()));

                        }


                        //    mMapView.onResume(); // needed to get the map to display immediately

                        try {
                            MapsInitializer.initialize(getActivity().getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                googleMap = mMap;


                                // For showing a move to my location button
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the fabcustomer grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                googleMap.setMyLocationEnabled(true);
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.getUiSettings().setMapToolbarEnabled(false);

                                // For dropping a marker at a point on the Map

                            /*    for (LatLng point : latlngs) {
                                    options.position(point);
                                    options.title("SAMYOTECH");
                                    options.snippet("SAMYOTECH");
                                    googleMap.addMarker(options);
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(12).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }*/
                                for (MarkerOptions options : optionsList) {
                                    options.position(options.getPosition());
                                    options.title(options.getTitle());
                                    options.snippet(options.getSnippet());
                                    final Marker hamburg = googleMap.addMarker(options);

//                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(options.getPosition()).zoom(12).build();
//                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                    for (int i = 0; i < allAtristListDTOList.size(); i++) {

                                        if (allAtristListDTOList.get(i).getUser_id().equalsIgnoreCase(options.getSnippet()))

                                            markers.put(hamburg.getId(), allAtristListDTOList.get(i));


                                    }
                                    googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

                                }





                                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                    @Override
                                    public void onInfoWindowClick(Marker arg0) {
                                        Intent intent = new Intent(getActivity().getBaseContext(), ArtistProfile.class);
                                        intent.putExtra(Consts.ARTIST_ID, arg0.getSnippet());
                                        // Starting the Place Details Activity
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    googleMap.clear();
                }
            }
        });
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (NearByFragment.this.marker != null
                    && NearByFragment.this.marker.isInfoWindowShown()) {
                NearByFragment.this.marker.hideInfoWindow();
                NearByFragment.this.marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            NearByFragment.this.marker = marker;

            String url = null;
            String name = null;
            String id = null;
            String category = null;

            if (marker.getId() != null && markers != null && markers.size() > 0) {
                if (markers.get(marker.getId()) != null &&
                        markers.get(marker.getId()) != null) {
                    url = markers.get(marker.getId()).getImage();
                    name = markers.get(marker.getId()).getName();
                    id = markers.get(marker.getId()).getUser_id();
                    category = markers.get(marker.getId()).getCategory_name();
                }
            }
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));

            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                Glide.with(getActivity()).
                        load(url)
                        .placeholder(R.drawable.dummyuser_image)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image);

            } else {
                image.setImageResource(R.mipmap.ic_launcher);
            }

            //final String title = marker.getTitle();
            final CustomTextViewBold titleUi = ((CustomTextViewBold) view.findViewById(R.id.title));
            if (name != null) {
                titleUi.setText(name);
            } else {
                titleUi.setText("");
            }

            //   final String snippet = marker.getSnippet();
            final CustomTextView snippetUi = ((CustomTextView) view
                    .findViewById(R.id.snippet));
            if (category != null) {
                snippetUi.setText(category);
            } else {
                snippetUi.setText("");
            }

            return view;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    public void getCategory() {
        new HttpsRequest(Consts.GET_ALL_CATEGORY_API, parmsCategory, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    try {
                        categoryDTOS = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<CategoryDTO>>() {
                        }.getType();
                        categoryDTOS = (ArrayList<CategoryDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);

                        spinnerDialogCate = new SpinnerDialog((Activity) getActivity(), categoryDTOS, getResources().getString(R.string.select_category));// With 	Animation
                        spinnerDialogCate.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(String item, String id, int position) {
                                parms.put(Consts.CATEGORY_ID, id);
                                getArtist();

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }

}
