package co.sihe.apptemplete.views.slicepanel;

import android.view.MotionEvent;

public interface InterceptTouchEventListener {
    public boolean isIntercept(MotionEvent motionEvent, float moveX, float moveY,
                               boolean superIntercept, int location);
}
