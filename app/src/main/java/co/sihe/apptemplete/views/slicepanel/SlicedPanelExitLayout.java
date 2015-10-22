package co.sihe.apptemplete.views.slicepanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SlicedPanelExitLayout extends SlicedPanelLayout {

    public interface onSliceExitListener {
        void onExit(SlicedPanelLayout slicedPanelLayout);
    }

    private onSliceExitListener mSliceExitListener;
    private boolean mSlidingMode = true;

    public void setSliceExitListener(onSliceExitListener sliceExitListener) {
        this.mSliceExitListener = sliceExitListener;
    }

    public SlicedPanelExitLayout(Context context) {
        super(context);
        initExitCallback();
        initInterceptListener();
    }

    private void initInterceptListener() {
        setInterceptTouchEventAdapter(new InterceptTouchEventListener() {

            @Override
            public boolean isIntercept(MotionEvent motionEvent, float moveX,
                                       float moveY, boolean superIntercept, int location) {
                return !slidingMode() ? false : superIntercept;
            }
        });
    }

    private boolean slidingMode() {
        return mSlidingMode;
    }

    public void closeSlidingMode() {
        mSlidingMode = false;
    }

    public void openSlidingMode() {
        mSlidingMode = true;
    }

    private void initExitCallback() {
        setSliceListener(new SliceListener() {

            @Override
            public void onSliceStart(int from, int to) {
            }

            @Override
            public void onSliceFinish(int from, int to) {
                if (to == SlicedPanelLayout.LOCATION_RIGHT_UP) {
                    if (SlicedPanelExitLayout.this.mSliceExitListener != null) {
                        SlicedPanelExitLayout.this.mSliceExitListener
                                .onExit(SlicedPanelExitLayout.this);
                    }
                }
            }
        });
    }

    public SlicedPanelExitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initExitCallback();
    }
}
