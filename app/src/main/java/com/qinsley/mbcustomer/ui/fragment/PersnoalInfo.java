package com.qinsley.mbcustomer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.DTO.QualificationsDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.ui.adapter.QualificationAdapter;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.R;

import java.util.ArrayList;

public class PersnoalInfo extends Fragment {
    private View view;
    private CustomTextView tvAbout, tvArtistRate, tvRating, tvJobComplete, tvProfileComplete,tvLocation;
    private RatingBar ratingbar;
    private ArtistDetailsDTO artistDetailsDTO;
    private Bundle bundle;
    private RecyclerView rvQualification;
    private QualificationAdapter qualificationAdapter;
    private ArrayList<QualificationsDTO> qualificationsDTOList;
    private LinearLayoutManager mLayoutManagerQuali;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_persnoal_info, container, false);
        bundle = this.getArguments();
        artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);
        showUiAction(view);

        return view;
    }


    public void showUiAction(View v) {
        tvArtistRate = v.findViewById(R.id.tvArtistRate);
        tvRating = v.findViewById(R.id.tvRating);
        tvJobComplete = v.findViewById(R.id.tvJobComplete);
        tvProfileComplete = v.findViewById(R.id.tvProfileComplete);
        tvAbout = v.findViewById(R.id.tvAbout);
        ratingbar = v.findViewById(R.id.ratingbar);
        tvLocation = v.findViewById(R.id.tvLocation);
        rvQualification = v.findViewById(R.id.rvQualification);
        mLayoutManagerQuali = new LinearLayoutManager(getActivity().getApplicationContext());
        rvQualification.setLayoutManager(mLayoutManagerQuali);
        showData();
    }

    public void showData(){
        ratingbar.setRating(Float.parseFloat(artistDetailsDTO.getAva_rating()));
        if (artistDetailsDTO.getArtist_commission_type().equalsIgnoreCase("0")) {

            if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("0")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            } else if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("1") && artistDetailsDTO.getFlat_type().equalsIgnoreCase("2")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            } else if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("1") && artistDetailsDTO.getFlat_type().equalsIgnoreCase("1")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr_add_on) + " " + artistDetailsDTO.getCategory_price() + "%");
            } else {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            }

        } else {
            if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("0")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            } else if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("1") && artistDetailsDTO.getFlat_type().equalsIgnoreCase("2")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            } else if (artistDetailsDTO.getCommission_type().equalsIgnoreCase("1") && artistDetailsDTO.getFlat_type().equalsIgnoreCase("1")) {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate_add_on) + " " + artistDetailsDTO.getCategory_price() + "%");
            } else {
                tvArtistRate.setText(artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate_add_on) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getCategory_price());
            }


        }

        tvLocation.setText(artistDetailsDTO.getLocation());

        tvRating.setText("(" + artistDetailsDTO.getAva_rating() + "/5)");
        tvJobComplete.setText(artistDetailsDTO.getJobDone() + " " + getResources().getString(R.string.jobs_completed));
        tvProfileComplete.setText(artistDetailsDTO.getName());
        tvProfileComplete.setText(artistDetailsDTO.getCompletePercentages() + "" + getResources().getString(R.string.completion));

        tvAbout.setText(artistDetailsDTO.getAbout_us());

        qualificationsDTOList = new ArrayList<>();
        qualificationsDTOList = artistDetailsDTO.getQualifications();
        qualificationAdapter = new QualificationAdapter(getActivity(), qualificationsDTOList);
        rvQualification.setAdapter(qualificationAdapter);
    }

}
