package com.qinsley.mbcustomer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qinsley.mbcustomer.DTO.ArtistBookingDTO;
import com.qinsley.mbcustomer.DTO.ArtistDetailsDTO;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.ui.adapter.PreviousworkPagerAdapter;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.R;

import java.util.ArrayList;

public class PreviousWork extends Fragment {
    private View view;
    private ArtistDetailsDTO artistDetailsDTO;
    private PreviousworkPagerAdapter previousworkPagerAdapter;
    private ArrayList<ArtistBookingDTO> artistBookingDTOList;
    private Bundle bundle;
    private LinearLayoutManager mLayoutManagerReview;
    private RecyclerView rvPreviousWork;
    private CustomTextViewBold tvNotFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_previous_work, container, false);
        bundle = this.getArguments();
        artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);
        showUiAction(view);
        return view;
    }

    public void showUiAction(View v) {
        tvNotFound = (CustomTextViewBold) v.findViewById(R.id.tvNotFound);
        rvPreviousWork = v.findViewById(R.id.rvPreviousWork);
        mLayoutManagerReview = new LinearLayoutManager(getActivity().getApplicationContext());
        rvPreviousWork.setLayoutManager(mLayoutManagerReview);
        showData();
    }

    public void showData() {
        artistBookingDTOList = new ArrayList<>();
        artistBookingDTOList = artistDetailsDTO.getArtist_booking();
        if (artistBookingDTOList.size() > 0) {
            tvNotFound.setVisibility(View.GONE);
            rvPreviousWork.setVisibility(View.VISIBLE);
            previousworkPagerAdapter = new PreviousworkPagerAdapter(getActivity(), artistBookingDTOList);
            rvPreviousWork.setAdapter(previousworkPagerAdapter);
        } else {
            tvNotFound.setVisibility(View.VISIBLE);
            rvPreviousWork.setVisibility(View.GONE);
        }

    }


}
