package co.sihe.apptemplete.views.slicepanel;

import android.graphics.Canvas;
import android.graphics.RectF;

public class AlphaDrawer extends BaseDrawer {
    private int mLastAlpha = 255;

    @Override
    public void draw(Canvas canvas, RectF mBounds, int offset) {
        float width = mBounds.width();
        float percent = ((width - Math.abs(offset) ) ) / width;
        mLastAlpha = (int) (255 * percent );
        if (mLastAlpha % 5 == 1) {
            canvas.saveLayerAlpha(mBounds, mLastAlpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
        } else {
            canvas.saveLayerAlpha(mBounds, mLastAlpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
        }
    }
}
