package com.qinsley.mbcustomer.ui.adapter;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinsley.mbcustomer.DTO.UserBooking;
import com.qinsley.mbcustomer.DTO.UserDTO;
import com.qinsley.mbcustomer.https.HttpsRequest;
import com.qinsley.mbcustomer.interfacess.Consts;
import com.qinsley.mbcustomer.interfacess.Helper;
import com.qinsley.mbcustomer.network.NetworkManager;
import com.qinsley.mbcustomer.ui.activity.MapActivity;
import com.qinsley.mbcustomer.ui.activity.PaymentProActivity;
import com.qinsley.mbcustomer.ui.activity.ViewInvoice;
import com.qinsley.mbcustomer.ui.fragment.MyBooking;
import com.qinsley.mbcustomer.utils.CustomTextView;
import com.qinsley.mbcustomer.utils.CustomTextViewBold;
import com.qinsley.mbcustomer.utils.ProjectUtils;
import com.qinsley.mbcustomer.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterCustomerBooking extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AdapterCustomerBooking.class.getSimpleName();
    MyBooking myBooking;
    private Context mContext;
    private ArrayList<UserBooking> objects = null;
    private ArrayList<UserBooking> userBookingsList;
    private HashMap<String, String> paramsDecline;
    private UserDTO userDTO;
    private HashMap<String, String> paramsBookingOp;
    private DialogInterface dialog_book;
    int min;
    int sec;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    public AdapterCustomerBooking(MyBooking myBooking, ArrayList<UserBooking> objects, UserDTO userDTO) {
        this.myBooking = myBooking;
        mContext = myBooking.getActivity();
        this.objects = objects;
        this.userDTO = userDTO;
        userBookingsList = new ArrayList<>();
        userBookingsList.addAll(objects);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_customer_booking, parent, false);
            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new MyViewHolderSection(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {
        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;
            Glide.with(mContext).
                    load(objects.get(position).getArtistImage())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivArtist);

            if (objects.get(position).getBooking_type().equalsIgnoreCase("0") || objects.get(position).getBooking_type().equalsIgnoreCase("3")) {
                if (objects.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.ivMap.setVisibility(View.GONE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.waiting_artist));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_waiting));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.ivMap.setVisibility(View.GONE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.request_acc));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                    holder.ivMap.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.work_inpro));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                    holder.llTime.setVisibility(View.VISIBLE);
                    holder.llCancel.setVisibility(View.GONE);
                    holder.llFinish.setVisibility(View.GONE);
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_inprogress));

                    SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                    try {
                        Date dt = sdf.parse(objects.get(position).getWorking_min());
                        sdf = new SimpleDateFormat("HH:mm:ss");
                        Log.e("time", sdf.format(dt) + "");
                        min = dt.getHours() * 60 + dt.getMinutes();
                        sec = dt.getSeconds();
                        holder.chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                        holder.chronometer.start();
                        Log.e("min", min + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("4")) {
                    if (objects.get(position).getInvoice().getFlag().equalsIgnoreCase("1")) {
                        holder.ivMap.setVisibility(View.GONE);
                        holder.llTime.setVisibility(View.GONE);
                        holder.llCancel.setVisibility(View.GONE);
                        holder.llFinish.setVisibility(View.GONE);
                        holder.llPay.setVisibility(View.GONE);
                        holder.tvStatus.setText(mContext.getResources().getString(R.string.invoice_paid));
                        holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                        holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                    } else {
                        holder.ivMap.setVisibility(View.GONE);
                        holder.llTime.setVisibility(View.GONE);
                        holder.llCancel.setVisibility(View.GONE);
                        holder.llFinish.setVisibility(View.GONE);
                        holder.llPay.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(mContext.getResources().getString(R.string.invoice_genrate));
                        holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                        holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                    }

                }
            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("2")) {
                if (objects.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.ivMap.setVisibility(View.GONE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.waiting_artist));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_waiting));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.ivMap.setVisibility(View.GONE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.request_acc));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                    holder.ivMap.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText(mContext.getResources().getString(R.string.work_inpro));
                    holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                    holder.llTime.setVisibility(View.VISIBLE);
                    holder.llCancel.setVisibility(View.GONE);
                    holder.llFinish.setVisibility(View.VISIBLE);
                    holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_inprogress));
                    SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                    try {
                        Date dt = sdf.parse(objects.get(position).getWorking_min());
                        sdf = new SimpleDateFormat("HH:mm:ss");
                        System.out.println(sdf.format(dt));
                        min = dt.getHours() * 60 + dt.getMinutes();
                        sec = dt.getSeconds();
                        holder.chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                        holder.chronometer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("4")) {

                    if (objects.get(position).getInvoice().getFlag().equalsIgnoreCase("1")) {
                        holder.ivMap.setVisibility(View.GONE);
                        holder.llTime.setVisibility(View.GONE);
                        holder.llCancel.setVisibility(View.GONE);
                        holder.llFinish.setVisibility(View.GONE);
                        holder.llPay.setVisibility(View.GONE);
                        holder.llViewInvoice.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(mContext.getResources().getString(R.string.invoice_paid));
                        holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                        holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                    } else {
                        holder.ivMap.setVisibility(View.GONE);
                        holder.llTime.setVisibility(View.GONE);
                        holder.llCancel.setVisibility(View.GONE);
                        holder.llFinish.setVisibility(View.GONE);
                        holder.llPay.setVisibility(View.VISIBLE);
                        holder.llViewInvoice.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(mContext.getResources().getString(R.string.invoice_genrate));
                        holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                        holder.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                    }
                }
            }

            if (objects.get(position).getBooking_type().equalsIgnoreCase("0")) {
                if (objects.get(position).getArtist_commission_type().equalsIgnoreCase("0")) {
                    if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                        float price_hr = Float.parseFloat(objects.get(position).getPrice()) / 60;
                        float total_price = price_hr * min;
                        holder.tvPrice.setText(objects.get(position).getCurrency_type() + String.format("%.2f", total_price));
                    } else {
                        holder.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));

                    }
                } else {
                    holder.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());
                }
            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("2")) {
                holder.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());

            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("3")) {
                holder.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());

            }


            holder.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getBooking_date()) + " " + objects.get(position).getBooking_time());
            holder.tvDescription.setText(objects.get(position).getDescription());
            holder.tvWork.setText(objects.get(position).getCategory_name());
            holder.tvLocation.setText(objects.get(position).getArtistLocation());
            holder.tvJobComplete.setText(objects.get(position).getJobDone() + " " + mContext.getResources().getString(R.string.jobs_completed));
            holder.tvProfileComplete.setText(objects.get(position).getCompletePercentages() + mContext.getResources().getString(R.string.completion));

            holder.tvName.setText(mContext.getResources().getString(R.string.booking_with) + " " + objects.get(position).getArtistName());


            holder.llCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    completeDialog(mContext.getResources().getString(R.string.cancel), mContext.getResources().getString(R.string.booking_cancel_msg) + " " + objects.get(position).getArtistName() + "?", position);
                }
            });
            holder.ivMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, MapActivity.class);
                    in.putExtra(Consts.ARTIST_ID, objects.get(position).getArtist_id());
                    mContext.startActivity(in);
                }
            });
            holder.llFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        booking("3", position);
                    } else {
                        ProjectUtils.showToast(mContext, mContext.getResources().getString(R.string.internet_concation));
                    }
                }
            });
            holder.llPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, PaymentProActivity.class);
                    in.putExtra(Consts.HISTORY_DTO, objects.get(position).getInvoice());
                    mContext.startActivity(in);

                }
            });
            holder.llViewInvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, ViewInvoice.class);
                    in.putExtra(Consts.HISTORY_DTO, objects.get(position).getInvoice());
                    mContext.startActivity(in);

                }
            });

        } else {
            MyViewHolderSection view = (MyViewHolderSection) holderMain;
            view.tvSection.setText(objects.get(position).getSection_name());
        }
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.objects.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivArtist;
        public LinearLayout llStatus, llCancel, llFinish, llPay, llViewInvoice;
        public CustomTextView tvStatus, tvWork, tvLocation, tvDate, tvJobComplete, tvProfileComplete, tvDescription;
        public CustomTextViewBold tvName, tvPrice;
        public RelativeLayout rlClick, llTime;
        public Chronometer chronometer;
        public ImageView ivMap, ivStatus;

        public MyViewHolder(View view) {
            super(view);
            ivArtist = view.findViewById(R.id.ivArtist);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvDescription = view.findViewById(R.id.tvDescription);
            llStatus = view.findViewById(R.id.llStatus);
            llCancel = view.findViewById(R.id.llCancel);
            llTime = view.findViewById(R.id.llTime);
            chronometer = view.findViewById(R.id.chronometer);
            tvWork = view.findViewById(R.id.tvWork);
            tvName = view.findViewById(R.id.tvName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvJobComplete = view.findViewById(R.id.tvJobComplete);
            tvProfileComplete = view.findViewById(R.id.tvProfileComplete);
            ivMap = view.findViewById(R.id.ivMap);
            llFinish = view.findViewById(R.id.llFinish);
            ivStatus = view.findViewById(R.id.ivStatus);
            tvPrice = view.findViewById(R.id.tvPrice);
            llPay = view.findViewById(R.id.llPay);
            llViewInvoice = view.findViewById(R.id.llViewInvoice);
            tvDate = view.findViewById(R.id.tvDate);

        }
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        public CustomTextView tvSection;

        public MyViewHolderSection(View view) {
            super(view);
            tvSection = view.findViewById(R.id.tvSection);
        }
    }


    public void decline(int pos) {
        paramsDecline = new HashMap<>();
        paramsDecline.put(Consts.USER_ID, userDTO.getUser_id());
        paramsDecline.put(Consts.BOOKING_ID, objects.get(pos).getId());
        paramsDecline.put(Consts.DECLINE_BY, "2");
        paramsDecline.put(Consts.DECLINE_REASON, "Busy");
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DECLINE_BOOKING_API, paramsDecline, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                dialog_book.dismiss();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    myBooking.getBooking();


                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void booking(String req, int pos) {
        paramsBookingOp = new HashMap<>();
        paramsBookingOp.put(Consts.BOOKING_ID, objects.get(pos).getId());
        paramsBookingOp.put(Consts.REQUEST, req);
        paramsBookingOp.put(Consts.USER_ID, objects.get(pos).getUser_id());
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOKING_OPERATION_API, paramsBookingOp, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    myBooking.getBooking();


                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void completeDialog(String title, String msg, final int pos) {
        try {
            new AlertDialog.Builder(mContext)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            decline(pos);

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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(userBookingsList);
        } else {
            for (UserBooking userBooking : userBookingsList) {
                if (userBooking.getArtistName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(userBooking);
                }
            }
        }
        notifyDataSetChanged();
    }


}