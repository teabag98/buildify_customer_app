package com.qinsley.mbcustomer.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class InputFieldView extends InputOpenFieldView {
    public InputFieldView(Context context) {
        super(context);
        setTheme();
    }

    public InputFieldView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTheme();
    }

    public InputFieldView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setTheme();
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (view instanceof EditText) {
            getEditText().setCursorVisible(false);
            getEditText().setFocusable(false);
            getEditText().setInputType(getEditText().getInputType() | 524288);
        }
    }

    private void setTheme() {
     //   getContext().setTheme(R.style.TextInputLayoutApperance.Theme.Default);
    }

    public void setText(String str) {
        if (TextUtils.isEmpty(str)) {
            getEditText().setText(str);
        } else {
            getEditText().setText(fromHtml(str));
        }
    }

    public void requestExplicitFocus() {
        clearFocus();
        getEditText().setCursorVisible(true);
        getEditText().setFocusable(true);
        setFocusable(true);
        requestFocus();
    }
}
