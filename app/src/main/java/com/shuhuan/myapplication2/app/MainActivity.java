package com.shuhuan.myapplication2.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.ScrollView;


public class MainActivity extends ActionBarActivity {

    private ScrollView scrollView;
    private ImageView img;
    private DisplayMetrics dm;

    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;
    private int scrollY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scrollView = (ScrollView) findViewById(R.id.scollview);
        img = (ImageView) findViewById(R.id.img);
        dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);

        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
        lp.width = dm.widthPixels;
        lp.height = dm.widthPixels * 9 / 16;
        img.setLayoutParams(lp);


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "NewApi"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //手指离开后恢复图片 开始的时候 代表首次点击位置  离开时候代表离开时候的位置
                        Log.d("===自动复原==", event.getY() + "");
                        mScaling = false;
                        replyImage();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!mScaling) {
                            if (scrollView.getScrollY() == 0) {
                                mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                                Log.d("===起始位置 屏幕首次点击的位置==", mFirstPosition + "");
                            } else {
                                scrollY = scrollView.getScrollY();
                                Log.d("====可以上滑 在此处==", scrollY + "");
                                break;
                            }
                        }
                        int distance = (int) ((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                        Log.d("=滑动距离==", distance + "");
                        if (distance < 0) { // 当前位置比记录位置要小，正常返回
                            scrollY = scrollView.getScrollY();
                            Log.d("=====上滑getScrollY()", scrollY + "");
                            break;
                        }

                        // 处理放大
                        mScaling = true;
                        lp.width = dm.widthPixels + distance;
                        Log.d("==下拉宽度==", lp.width + "");
                        lp.height = (dm.widthPixels + distance) * 9 / 16;
                        Log.d("==下拉高==", lp.height + "");
                        Log.d("==%%%==", scrollView.getScrollY() + "");
                        img.setLayoutParams(lp);
                        return true; // 返回true表示已经完成触摸事件，不再处理
                }
                Log.d("*****图片高度", img.getHeight() + "");
                Log.d("$$$$", scrollY + "");
                // TODO  计算 渐变度
                return false;
            }
        });

    }


    @SuppressLint("NewApi")
    public void replyImage() {
        final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
        final float w = img.getLayoutParams().width;// 图片当前宽度
        final float h = img.getLayoutParams().height;// 图片当前高度
        final float newW = dm.widthPixels;// 图片原宽度
        final float newH = dm.widthPixels * 9 / 16;// 图片原高度

        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(200);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                lp.width = (int) (w - (w - newW) * cVal);
                lp.height = (int) (h - (h - newH) * cVal);
                img.setLayoutParams(lp);
            }
        });
        anim.start();

    }


}
