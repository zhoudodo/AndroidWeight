package com.example.basicfunction.about_phone;

import android.os.CountDownTimer;
import android.widget.TextView;


/**
 * 发送验证码倒计时功能  包含按钮不可再次点击
 */
public class SendCodeTimeCount extends CountDownTimer {
    private TextView textView;

    public SendCodeTimeCount(long millisInFuture, long countDownInterval, TextView textView) {
        super(millisInFuture, countDownInterval);
        this.textView = textView;
    }

    @Override
    public void onTick(long l) {
        textView.setClickable(false);
        textView.setText(l / 1000 + "秒后可重新发送");
    }

    @Override
    public void onFinish() {
        textView.setClickable(true);
        textView.setText("重新获取");
    }
}
