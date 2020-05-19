package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.qinsley.mbcustomer.DTO.AppointmentDTO;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.R;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.ui.fragment.AppointmentFrag;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterAppointmnet extends RecyclerView.Adapter<AdapterAppointmnet.MyViewHolder> {
    private String TAG = AdapterAppointmnet.class.getSimpleName();
    private AppointmentFrag appointmentFrag;
    private Context mContext;
    private ArrayList<AppointmentDTO> appointmentDTOSList;
    private UserDTO userDTO;
    SimpleDateFormat sdf1, timeZone;
    private HashMap<String, String> paramBookAppointment = new HashMap<>();
    private HashMap<String, String> paramDeclineAppointment = new HashMap<>();
    private DialogInterface dialog_book;

    public AdapterAppointmnet(AppointmentFrag appointmentFrag, ArrayList<AppointmentDTO> appointmentDTOSList, UserDTO userDTO) {
        this.appointmentFrag = appointmentFrag;
        mContext = appointmentFrag.getActivity();
        this.appointmentDTOSList = appointmentDTOSList;
        this.userDTO = userDTO;

        sdf1 = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_appointment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//0. Pending 1. Accepted 2. Rejected 3. Completed 4. Decline
        Glide.with(mContext).
                load(appointmentDTOSList.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivArtist);

        holder.tvWork.setText(appointmentDTOSList.get(position).getCategory_name());
        holder.tvLocation.setText(appointmentDTOSList.get(position).getArtistAddress());
        holder.tvName.setText(appointmentDTOSList.get(position).getArtistName());
        holder.tvDate.setText(appointmentDTOSList.get(position).getDate_string());
        holder.tvEmail.setText(appointmentDTOSList.get(position).getArtistEmail());
        holder.tvMobile.setText(appointmentDTOSList.get(position).getArtistMobile());

        if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.accepted));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.rejected));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("3")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.completed));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("4")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.decline));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_blue));
        }


        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramBookAppointment.put(Consts.USER_ID, userDTO.getUser_id());
                paramBookAppointment.put(Consts.ARTIST_ID, appointmentDTOSList.get(position).getArtist_id());
                paramBookAppointment.put(Consts.APPOINTMENT_ID, appointmentDTOSList.get(position).getId());
                clickScheduleDateTime();
            }
        });
        holder.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramDeclineAppointment.put(Consts.USER_ID, userDTO.getUser_id());
                paramDeclineAppointment.put(Consts.APPOINTMENT_ID, appointmentDTOSList.get(position).getId());
                paramDeclineAppointment.put(Consts.REQUEST, "4");
                bookDailog();
            }
        });
    }


    @Override
    public int getItemCount() {

        return appointmentDTOSList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivArtist;
        public CustomTextView tvWork, tvLocation, tvDate, tvStatus, tvMobile, tvEmail;
        public LinearLayout llStatus;
        public RelativeLayout rlComplete;
        public CustomTextViewBold tvName;
        public CustomTextView tvDecline, tvEdit;

        public MyViewHolder(View view) {
            super(view);
            ivArtist = view.findViewById(R.id.ivArtist);
            tvWork = view.findViewById(R.id.tvWork);
            tvName = view.findViewById(R.id.tvName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvDate = view.findViewById(R.id.tvDate);
            tvDecline = view.findViewById(R.id.tvDecline);
            tvEdit = view.findViewById(R.id.tvEdit);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            rlComplete = view.findViewById(R.id.rlComplete);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvMobile = view.findViewById(R.id.tvMobile);
        }
    }

    public void clickScheduleDateTime() {
        new SingleDateAndTimePickerDialog.Builder(mContext)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        paramBookAppointment.put(Consts.DATE_STRING, String.valueOf(sdf1.format(date).toString().toUpperCase()));
                        paramBookAppointment.put(Consts.TIMEZONE, String.valueOf(timeZone.format(date)));
                        bookAppointment();
                    }
                })
                .display();
    }

    public void bookAppointment() {
        new HttpsRequest(Consts.EDIT_APPOINTMENT_API, paramBookAppointment, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    appointmentFrag.getHistroy();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void declineAppointment() {

        new HttpsRequest(Consts.APPOINTMENT_OPERATION_API, paramDeclineAppointment, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    appointmentFrag.getHistroy();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void bookDailog() {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(mContext.getResources().getString(R.string.decline))
                    .setMessage(mContext.getResources().getString(R.string.decline_msg))
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            declineAppointment();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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

}