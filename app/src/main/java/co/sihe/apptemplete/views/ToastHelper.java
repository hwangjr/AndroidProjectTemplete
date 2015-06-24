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

public class ToastHelper {
    private FrameLayout mFrameLayout;
    private TextView view;

    private long mDelay = 3000;

    public ToastHelper(Activity activity) {
        super();
        mFrameLayout = new FrameLayout(activity);
        activity.addContentView(mFrameLayout, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mFrameLayout.setClickable(false);
        mFrameLayout.addView(createView(activity));
        mFrameLayout.setVisibility(View.GONE);
    }

    private View createView(Context context) {
        view = new TextView(context);
        view.setTextColor(Color.WHITE);
        view.setPadding(100, 12, 100, 12);
        view.setBackgroundColor(Color.parseColor("#333333"));
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.BOTTOM;
        view.setLayoutParams(layout);
        return view;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mFrameLayout.setVisibility(View.GONE);
        }
    };

    public void toast(String info) {
        view.setText(info);
        mFrameLayout.setVisibility(View.VISIBLE);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, mDelay);
    }

    public void cancel() {
        mHandler.removeMessages(0);
        mFrameLayout.setVisibility(View.GONE);
    }
}
