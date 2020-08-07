package com.example.basicfunction.about_popupwindow;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.basicfunction.R;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;


public abstract class BasePopu extends PopupWindow implements OnClickListener {
    protected FragmentActivity mActivity;

    protected OnPupClickListener listener;

    protected int heightNavigationBar = 0;


    public BasePopu(FragmentActivity act, int idRes, int width, int height) {
        this(act,LayoutInflater.from(act).inflate(idRes, null), width, height);
    }

    public BasePopu(FragmentActivity act, View contentView, int width, int height) {
        super(contentView, width, height, true);
        mActivity = act;
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                if (lp.alpha != 1f) {
                    lp.alpha = 1f;
                    mActivity.getWindow().setAttributes(lp);
                }
                afterDismiss();
            }
        });
        init();
    }

    public void showAtView(View view){
        if (mActivity.isFinishing())
            return;
        setAnimationStyle(R.style.Popup_Animation_scale);
        beforeshow();

        int[] location = new int[2];
        view.getLocationOnScreen(location);


        showAtLocation(view,
                Gravity.NO_GRAVITY,
                location[0],
                location[1]);


        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f; //屏幕变暗 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
        update();
    }

    //自定义位置
    public void showViewBottom(View mView) {
        if (mActivity.isFinishing())
            return;
        setAnimationStyle(R.style.Popup_Animation_UpDown);
        beforeshow();

        int[] location = new int[2];
        mView.getLocationOnScreen(location);


        showAtLocation(mView,
                Gravity.NO_GRAVITY,
                location[0],
                location[1]);


        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f; //屏幕变暗 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
        update();
    }



    //底部暂时时 .需要处理 虚拟导航键的高度
    public void setHeightNavigationBar(int heightNavigationBar) {
        this.heightNavigationBar = heightNavigationBar;
    }




    public void showBottom() {
        if (mActivity.isFinishing())
            return;
        setAnimationStyle(R.style.Popup_Animation_UpDown);
        beforeshow();

//        if (android.os.Build.VERSION.SDK_INT >=24) {
//            int[] a = new int[2];
//            v.getLocationInWindow(a);
//            showAtLocation(mActivity.getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, heightNavigationBar);
//            mPopupWindow.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0 , a[1]+v.getHeight());
//        } else{
//            showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, heightNavigationBar);
//        }
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, heightNavigationBar);

        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f; //屏幕变暗 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
            update();
    }

    //自定义位置
    public void showLocation(int gravity) {
        if (mActivity.isFinishing())
            return;
        setAnimationStyle(R.style.Popup_Animation_UpDown);
        beforeshow();
        showAtLocation(mActivity.getWindow().getDecorView(), gravity, 0, heightNavigationBar);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f; //屏幕变暗 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
        update();
    }

    //自定义位置
    public void showCustomLoacion(View mView) {
        if (mActivity.isFinishing())
            return;
        setAnimationStyle(R.style.Popup_Animation_UpDown);
        beforeshow();

        int[] location = new int[2];
        mView.getLocationOnScreen(location);

        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showAtLocation(mView,
                    Gravity.NO_GRAVITY,
                    location[0] + (dp2px(73) - mView.getMeasuredWidth()) / 2,
                    location[1] - dp2px(252) - 20);
        } else
            showAtLocation(mView,
                    Gravity.NO_GRAVITY,
                    location[0] + (dp2px(73) - mView.getMeasuredWidth()) / 2,
                    location[1] - dp2px(252) - 20);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f; //屏幕变暗 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
        update();
    }

    private static float scale = 0;
    public static int dp2px(final float dpValue) {
        if(scale == 0) {
            scale = Resources.getSystem().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    protected void beforeshow() {
    }

    protected void afterDismiss() {
    }

    protected abstract void init();

    //点击回调
    public interface OnPupClickListener {
        void onPupClick(int position);
    }

    public OnPupClickListener getListener() {
        return listener;
    }

    public void setOnPupClicListener(OnPupClickListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewsId(int id, boolean clickAble) {
        T views = findViews(getContentView(), id);
        if (clickAble)
            views.setOnClickListener(this);
        return views;
    }
    public static <T extends View> T findViews(View rootView, int id) {
        T views = rootView.findViewById(id);
        return views;
    }


    //提取方法
    public void setText(@IdRes int viewId, CharSequence text) {
        TextView view = this.findViewsId(viewId, false);
        view.setText(text);
    }

    @Override
    public void update() {
        if (android.os.Build.VERSION.SDK_INT == 24) {
            //update 方法有问题 gravity 被重置
        }else {
            super.update();
        }
        }
}