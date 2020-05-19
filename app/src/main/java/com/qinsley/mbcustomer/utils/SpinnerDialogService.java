package com.qinsley.mbcustomer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.qinsley.mbcustomer.interfacess.OnSpinerItemClick;
import com.qinsley.mbcustomer.DTO.ProductDTO;
import com.qinsley.mbcustomer.R;

import java.util.ArrayList;

public class SpinnerDialogService {


    ArrayList<ProductDTO> items;
    Activity context;
    String dTitle;
    String closeTitle = "Close";
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int clickpos;
    int style;
    Fragment fragment;

    private int limit = -1;
    private int selected = 0;
    private LimitExceedListener limitListener;
    int selectedIndex = -1;
    ListView listView;
    MyAdapterCheck myAdapterCheck;
    int pos;

    String name = "", image = "";

    public SpinnerDialogService(Activity activity, ArrayList<ProductDTO> items, String dialogTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
    }

    public SpinnerDialogService(Activity activity, ArrayList<ProductDTO> items, String dialogTitle, String closeTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.closeTitle = closeTitle;
    }

    public SpinnerDialogService(Activity activity, ArrayList<ProductDTO> items, String dialogTitle, int style) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
    }

    public SpinnerDialogService(Activity activity, ArrayList<ProductDTO> items, String dialogTitle, int style, String closeTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
        this.closeTitle = closeTitle;
    }

    public SpinnerDialogService(Activity activity, String name, String image, int style) {
        this.context = activity;
        this.name = name;
        this.image = image;
        this.style = style;
    }


    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }


    public void showSpinerDialogMultiple() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this.context);
        View v = this.context.getLayoutInflater().inflate(R.layout.dialog_layout_check, (ViewGroup) null);
        TextView rippleViewClose = (TextView) v.findViewById(R.id.close);
        TextView Ok = (TextView) v.findViewById(R.id.Ok);
        TextView title = (TextView) v.findViewById(R.id.spinerTitle);
        rippleViewClose.setText(this.closeTitle);
        title.setText(this.dTitle);
        listView = (ListView) v.findViewById(R.id.list);
        myAdapterCheck = new MyAdapterCheck(this.context, this.items);
        listView.setAdapter(myAdapterCheck);
        adb.setView(v);
        this.alertDialog = adb.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = this.style;
        this.alertDialog.setCancelable(false);

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpinnerDialogService.this.alertDialog.dismiss();
            }
        });
        Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringBuilder allLabels = new StringBuilder();
                StringBuilder allID = new StringBuilder();
                for (ProductDTO s : myAdapterCheck.arrayList) {
                    if (s.isSelected()) {
                        if (allLabels.length() > 0) {
                            allLabels.append(", "); // some divider between the different texts
                            allID.append(", "); // some divider between the different texts
                        }
                        allLabels.append(s);
                        allID.append(s.getId());

                    }

                }
                SpinnerDialogService.this.onSpinerItemClick.onClick(allLabels.toString(), allID.toString(), clickpos);

                SpinnerDialogService.this.alertDialog.dismiss();
            }
        });
        this.alertDialog.show();
    }


    public class MyAdapterCheck extends BaseAdapter implements Filterable {

        ArrayList<ProductDTO> arrayList;
        ArrayList<ProductDTO> mOriginalValues; // Original Values
        LayoutInflater inflater;

        public MyAdapterCheck(Context context, ArrayList<ProductDTO> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            CustomTextView text1;
            CheckBox checkBox1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.spinner_view_checkbox, parent, false);
                holder.text1 = (CustomTextView) convertView.findViewById(R.id.text1);
                holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.text1.setText(arrayList.get(position).getProduct_name());
            holder.text1.setTypeface(null, Typeface.NORMAL);
            holder.checkBox1.setChecked(arrayList.get(position).isSelected());

            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (arrayList.get(position).isSelected()) { // deselect
                        selected--;
                    } else if (selected == limit) { // select with limit
                        if (limitListener != null)
                            limitListener.onLimitListener(arrayList.get(position));
                        return;
                    } else { // selected
                        selected++;
                    }

                    final ViewHolder temp = (ViewHolder) v.getTag();
                    temp.checkBox1.setChecked(!temp.checkBox1.isChecked());

                    arrayList.get(position).setSelected(!arrayList.get(position).isSelected());
                    Log.i("TAG", "On Click Selected Item : " + arrayList.get(position).getProduct_name() + " : " + arrayList.get(position).isSelected());
                    notifyDataSetChanged();
                }
            });
            holder.checkBox1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (arrayList.get(position).isSelected()) { // deselect
                        selected--;
                    } else if (selected == limit) { // select with limit
                        if (limitListener != null)
                            limitListener.onLimitListener(arrayList.get(position));
                        return;
                    } else { // selected
                        selected++;
                    }

                    final ViewHolder temp = (ViewHolder) v.getTag();
                    temp.checkBox1.setChecked(!temp.checkBox1.isChecked());

                    arrayList.get(position).setSelected(!arrayList.get(position).isSelected());
                    Log.i("TAG", "On Click Selected Item : " + arrayList.get(position).getProduct_name() + " : " + arrayList.get(position).isSelected());
                    notifyDataSetChanged();
                }
            });


            if (arrayList.get(position).isSelected()) {
//                holder.text1.setTypeface(null, Typeface.BOLD);
                // convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
            }
            holder.checkBox1.setTag(holder);

            return convertView;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrayList = (ArrayList<ProductDTO>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<ProductDTO> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(arrayList); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).getProduct_name();
                            if (data.toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(mOriginalValues.get(i));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
        }
    }


    public interface LimitExceedListener {
        void onLimitListener(ProductDTO data);
    }
}



