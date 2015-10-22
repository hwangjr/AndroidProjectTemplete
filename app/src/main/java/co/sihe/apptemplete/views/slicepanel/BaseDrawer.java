package co.sihe.apptemplete.views.slicepanel;

import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class BaseDrawer {
    public abstract void draw(Canvas canvas, RectF mBounds, int offset);
}
