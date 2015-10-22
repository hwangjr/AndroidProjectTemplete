package co.sihe.apptemplete.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import co.sihe.apptemplete.R;

public class ToastHelper {
    private FrameLayout mFrameLayout;
    private TextView mTextView;

    private long mDelay = 3000;

    public ToastHelper(Activity activity) {
        if (activity != null) {
            mFrameLayout = new FrameLayout(activity);
            activity.addContentView(mFrameLayout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mFrameLayout.setClickable(false);
            mFrameLayout.addView(createView(activity));
            mFrameLayout.setVisibility(View.GONE);
        }
    }

    private View createView(Context context) {
        mTextView = new TextView(context);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setPadding(100, 12, 100, 12);
        mTextView.setBackgroundResource(R.drawable.toast_helper_bg);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layout.bottomMargin = 50;
        mTextView.setLayoutParams(layout);
        return mTextView;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mFrameLayout.setVisibility(View.GONE);
        }
    };

    public void toast(String info) {
        if (mTextView != null) {
            mTextView.setText(info);
            mFrameLayout.setVisibility(View.VISIBLE);
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, mDelay);
        }
    }

    public void toast(int resId) {
        if (mTextView != null) {
            toast(mTextView.getResources().getString(resId));
        }
    }

    public void toast(String info, long delay) {
        if (mTextView != null) {
            mTextView.setText(info);
            mFrameLayout.setVisibility(View.VISIBLE);
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, delay);
        }
    }

    public void cancel() {
        mHandler.removeMessages(0);
        if (mFrameLayout != null) {
            mFrameLayout.setVisibility(View.GONE);
        }
    }
}
