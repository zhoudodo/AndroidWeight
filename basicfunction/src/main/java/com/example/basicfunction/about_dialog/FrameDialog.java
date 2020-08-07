package com.example.basicfunction.about_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.basicfunction.R;


/**
 * 常用自定义dialog 继承FrameDialog实现布局即可
 */

public abstract class FrameDialog extends Dialog implements View.OnClickListener {

    protected Activity mActivity;

    public FrameDialog(Activity context) {
        super(context, R.style.custom_dialog);
        mActivity = context;
        setContentView(getViewIds());
        initLocation();
        initView();
    }

    public FrameDialog(Activity context, int themeStyle) {
        super(context, themeStyle);
        setContentView(getViewIds());
        initLocation();
        mActivity = context;
        initView();
    }

// 生命周期靠后
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initView();
//    }

    protected void initLocation() {
        WindowManager.LayoutParams params = getWindow()
                .getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setGravity(Gravity.BOTTOM);
    }

    //获取资源ids
    protected abstract int getViewIds();


    protected void initView() {
    }

    @Override
    public void onClick(View v) {

    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViews(int id) {
        return findViewsId(id, false);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewsId(int id, boolean clickAble) {
        T views = findViews(getWindow().getDecorView(), id);
        if (clickAble)
            views.setOnClickListener(this);
        return views;
    }

    @Override
    public void show() {
        Activity activity = mActivity;
        if(activity!= null && !activity.isFinishing()) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
        Activity activity = mActivity;
        if(activity!= null && !activity.isFinishing()) {
            super.dismiss();
        }

    }

    public static <T extends View> T findViews(View rootView, int id) {
        T views = rootView.findViewById(id);
        return views;
    }

}
