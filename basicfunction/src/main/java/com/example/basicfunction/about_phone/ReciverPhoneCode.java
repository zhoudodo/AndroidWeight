package com.example.basicfunction.about_phone;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 接受验证码 自动填写功能
 *
 * 步骤1、权限给予 uses-permission android:name="android.permission.READ_SMS"
 *
 * 步骤2、注册类 实现接口回调即可
 *
 * 步骤3、如果不需要接收验证码  解除注册即可
 *
 * 暂未测试
 *
 */
public class ReciverPhoneCode extends ContentObserver{

    private Context mContext; // 上下文
    private String code; // 验证码
    SmsListener mListener;
    private String mPhone;
    Cursor mCursor;


    //手机短信验证码长度
    private final static int PHONE_CODE_LENGTH = 6;
    //公司颁发验证码抬头String文本内存
    private final static String HEADER_UNIQUE_STRING = "天下为公";

    public ReciverPhoneCode(Context context, Handler handler, String phone, SmsListener listener) {
        super(handler);
        this.mContext = context;
        this.mPhone = phone;
        this.mListener = listener;
    }

    public ReciverPhoneCode(Context context, Handler handler, SmsListener listener) {
        super(handler);
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        // 第一次回调 不是我们想要的 直接返回
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        // 第二次回调 查询收件箱里的内容
        Uri inboxUri = Uri.parse("content://sms/inbox");
        // 按时间顺序排序短信数据库
        mCursor = mContext.getContentResolver().query(inboxUri, null, null,
                null, "date desc");
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                // 获取手机号
                String address = mCursor.getString(mCursor.getColumnIndex("address"));
                Log.e("ADDRESS", address);
                // 获取短信内容
                String body = mCursor.getString(mCursor.getColumnIndex("body"));
                Log.e("ADDRESS", body);
                // 判断手机号是否为目标号码，服务号号码不固定请用正则表达式判断前几位。
                //加上这个判断必须知道发送方的电话号码，局限性比较高
//                if (!address.equals(mPhone)) {
//                    return;
//                }
                if (!body.startsWith(HEADER_UNIQUE_STRING)) {
                    return;
                }
                // 正则表达式截取短信中的6位验证码
                String regEx = "(?<![0-9])([0-9]{" + PHONE_CODE_LENGTH + "})(?![0-9])";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(body);
                // 如果找到通过Handler发送给主线程
                while (matcher.find()) {
                    code = matcher.group();
                    if (mListener != null) {
                        mListener.onResult(code);
                    }
                }

            }
        }
        mCursor.close();
    }

    /**
     * 短信回调接口
     */
    public interface SmsListener {
        /**
         * @param result 回调内容（验证码）
         */
        void onResult(String result);
    }




    ReciverPhoneCode mPhoneCode;

    /**
     * 注册
     * @param context
     * @param phone  发送验证码手机号 别搞错了
     * @param listener 监听验证码Code
     */
    public void startReciverPhoneCode(Context context,String phone,ReciverPhoneCode.SmsListener listener) {
        mPhoneCode = new ReciverPhoneCode(context, new Handler(), phone, listener);
        // 注册短信变化监听
        context.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mPhoneCode);
    }

    /**
     * 注册
     * @param context
     * @param listener 监听验证码Code
     */
    public void startReciverPhoneCode(Context context,ReciverPhoneCode.SmsListener listener) {
        mPhoneCode = new ReciverPhoneCode(context, new Handler(),listener);
        // 注册短信变化监听
        context.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mPhoneCode);
    }

    //不使用之后要解开注册
    public void stopReciverPhoneCode(Context context){
        if(mPhoneCode != null){
            context.getContentResolver().unregisterContentObserver(mPhoneCode);
        }
    }

}
