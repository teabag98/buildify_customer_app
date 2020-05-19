package com.qinsley.mbcustomer.utils;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;


public class InputOpenFieldView extends TextInputLayout implements OnClickListener {
    private OnClickListener clickListener;

    public InputOpenFieldView(Context context) {
        super(context);
        setTheme();
    }

    public InputOpenFieldView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTheme();
    }

    public InputOpenFieldView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setTheme();
    }

    private void setTheme() {
       // getContext().setTheme(R.style.TextInputLayoutApperance.Theme.Default);
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (view instanceof EditText) {
            getEditText().setOnClickListener(this);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    public void onClick(View view) {
        if (this.clickListener != null) {
            this.clickListener.onClick(this);
        }
    }

    public void setText(String str) {
        if (TextUtils.isEmpty(str)) {
            getEditText().setText(str);
        } else {
            getEditText().setText(fromHtml(str));
        }
    }

    public void setSpannedText(Spanned spanned) {
        getEditText().setText(spanned);
    }

    public void setScreening(String str, boolean z) {
        if (!z || TextUtils.isEmpty(getEditText().getText().toString())) {
            setHint(str);
        } else {
            setHint(str + " (under screening)");
        }
    }

    public static Spanned fromHtml(String str) {
        if (VERSION.SDK_INT >= 24) {
            return Html.fromHtml(str, 0);
        }
        return Html.fromHtml(str);
    }
}
