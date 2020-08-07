package com.example.basicfunction.about_viewgroup;


import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
//FlowLayout实现类

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private int mHorizontalSpacing = dp2px(16); //每个item横向间距
    private int mVerticalSpacing = dp2px(8); //每个item横向间距

    private List<List<View>> allLines = new ArrayList<>(); // 记录所有的行，一行一行的存储，用于layout
    List<Integer> lineHeights = new ArrayList<>(); // 记录每一行的行高，用于layout


    public FlowLayout(Context context) {
        super(context);
//        initMeasureParams();
    }

    //反射
    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initMeasureParams();
    }

    //主题style
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initMeasureParams();
    }
    //四个参数 自定义属性

    private void clearMeasureParams() {
        allLines.clear();
        lineHeights.clear();
    }

    //度量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        clearMeasureParams();//内存 抖动
        //先度量孩子
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);  //ViewGroup解析的父亲给我的宽度
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec); // ViewGroup解析的父亲给我的高度

        List<View> lineViews = new ArrayList<>(); //保存一行中的所有的view
        int lineWidthUsed = 0; //记录这行已经使用了多宽的size
        int lineHeight = 0; // 一行的行高

        int parentNeededWidth = 0;  // measure过程中，子View要求的父ViewGroup的宽
        int parentNeededHeight = 0; // measure过程中，子View要求的父ViewGroup的高

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            LayoutParams childLP = childView.getLayoutParams();
            if (childView.getVisibility() != View.GONE) {
                //将layoutParams转变成为 measureSpec
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                        childLP.width);
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                        childLP.height);
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

                //获取子view的度量宽高 view的宽高需要加上margin值
                int childMesauredWidth = childView.getMeasuredWidth() + mlp.leftMargin+mlp.rightMargin;
                int childMeasuredHeight = childView.getMeasuredHeight() + mlp.topMargin+ mlp.bottomMargin;

                //如果需要换行
                if (childMesauredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {

                    //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时要记录下来
                    allLines.add(lineViews);
                    lineHeights.add(lineHeight);

                    parentNeededHeight = parentNeededHeight + lineHeight + mVerticalSpacing;
                    parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);

                    lineViews = new ArrayList<>();
                    lineWidthUsed = 0;
                    lineHeight = 0;
                }
                // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
                lineViews.add(childView);
                //每行都会有自己的宽和高
                lineWidthUsed = lineWidthUsed + childMesauredWidth + mHorizontalSpacing;
                lineHeight = Math.max(lineHeight, childMeasuredHeight);

                //处理最后一行数据
                if (i == childCount - 1) {
                    allLines.add(lineViews);
                    lineHeights.add(lineHeight);
                    parentNeededHeight = parentNeededHeight + lineHeight + mVerticalSpacing;
                    parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);
                }

            }
        }



        //再度量自己,保存
        //根据子View的度量结果，来重新度量自己ViewGroup
        // 作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父亲给它提供的宽高来度量
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth: parentNeededWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY) ?selfHeight: parentNeededHeight;
        setMeasuredDimension(realWidth, realHeight);
    }

    //布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = allLines.size();

        int curL = getPaddingLeft();
        int curT = getPaddingTop();

        for (int i = 0; i < lineCount; i++){
            List<View> lineViews = allLines.get(i);

            int lineHeight = lineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++){
                View view = lineViews.get(j);
                MarginLayoutParams mlp = (MarginLayoutParams) view.getLayoutParams();
                int left = curL +mlp.leftMargin;
                int top =  curT + mlp.bottomMargin;

//                int right = left + view.getWidth();
//                int bottom = top + view.getHeight();

                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();
                view.layout(left,top,right,bottom);
                curL = right + mHorizontalSpacing;
            }
            curT = curT + lineHeight + mVerticalSpacing;
            curL = getPaddingLeft();
        }

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(super.generateDefaultLayoutParams());
    }


    /**
     * XML 使用效果
     */

//    <?xml version="1.0" encoding="utf-8"?>
//<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
//    xmlns:app="http://schemas.android.com/apk/res-auto"
//    xmlns:tools="http://schemas.android.com/tools"
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    tools:context=".MainActivity">
//
//    <LinearLayout
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    android:orientation="vertical">
//        <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:layout_marginLeft="8dp"
//    android:text="搜索历史"
//    android:textColor="@android:color/black"
//    android:textSize="18sp"/>
//        <com.example.flowlayout.FlowLayout
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:paddingLeft="10dp"
//    android:layout_margin="8dp">
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="水果味孕妇奶粉" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="儿童洗衣机" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="洗衣机全自动" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="小度" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="儿童汽车可坐人" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="抽真空收纳袋" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="儿童滑板车" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="稳压器 电容" />
//
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="羊奶粉" />
//
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="奶粉1段" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="图书勋章日" />
//        </com.example.flowlayout.FlowLayout>
//
//        <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:layout_marginLeft="8dp"
//    android:text="搜索发现"
//    android:textColor="@android:color/black"
//    android:textSize="18sp" />
//        <com.example.flowlayout.FlowLayout
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:layout_margin="8dp">
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="惠氏3段" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="奶粉2段" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="图书勋章日" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="伯爵茶" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="阿迪5折秒杀" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="蓝胖子" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="婴儿洗衣机" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="小度在家" />
//
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="遥控车可坐" />
//
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="搬家袋" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="剪刀车" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="滑板车儿童" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="空调风扇" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="空鼓锤" />
//
//            <TextView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:background="@drawable/shape_button_circular"
//    android:text="笔记本电脑" />
//        </com.example.flowlayout.FlowLayout>
//    </LinearLayout>
//
//</ScrollView>



}
