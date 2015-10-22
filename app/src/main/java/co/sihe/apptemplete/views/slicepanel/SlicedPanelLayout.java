package co.sihe.apptemplete.views.slicepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class SlicedPanelLayout extends RelativeLayout {
    public static final int LOCATION_CENTER = 0;

    public static final int LOCATION_LEFT_DOWN = 1;

    public static final int LOCATION_RIGHT_UP = 2;

    private static final int BACKVIEW_ID = 11;

    private float mTriggerSliceDistance;// 触发throw的距离

    private Scroller mScroller;

    private float mTouchX;// 记录第一次点击的坐标X

    private float mTouchY;// 记录第一次点击的坐标Y

    private View mBackView;// 用于拦截事件和做背景的空白View

    private View mForeView;

    private int mMaxLeftOffset;// 左边要让开多少距离，单位px

    private int mMaxRightOffset;// 右边要让开多少距离，单位px

    private int mLocation = LOCATION_CENTER;// 当前滑块的位置，初始在中间

    private int mLastLocation = LOCATION_CENTER;// 上一次滑块的位置，初始在中间

    private boolean mScrollHorizontal;// 是否左右滑动，默认是

    private int mScaledTouchSlop;// 触发MOVE事件的距离

    private VelocityTracker mVelocityTracker;

    private int mScaledMinimumFlingVelocity; // 触发加速度滑动的距离

    private float mScaledMaxmumFlingVelocity;// 最大加速度

    private RectF mBounds;

    private SliceListener mSliceListener;

    private InterceptTouchEventListener mInterceptTouchEventAdapter;

    private boolean mScrollRightDraw;

    private boolean mScrollLeftDraw;

    private BaseDrawer mBaseDrawer;

    private long mMaxDuration;

    private boolean mOnStartCalled = false;

    private static final boolean USE_CACHE = false;

    private boolean mScrolling;

    private boolean mScrollingCacheEnabled;

    private boolean mScrollingClickable = true;

    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private final int mShadowWidth = 18;

    public SlicedPanelLayout(Context context) {
        super(context);
        init(context);
    }

    public SlicedPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initDefaultValue(context);
        initViews(context);
        setupShadowDrawable();
    }

    private void setupShadowDrawable() {
        int start = Color.parseColor("#00000000");
        int center = Color.parseColor("#11000000");
        int end = Color.parseColor("#33000000");
        mDrawableLeft = new GradientDrawable(Orientation.LEFT_RIGHT, new int[] { start, center, end });
        mDrawableRight = new GradientDrawable(Orientation.LEFT_RIGHT, new int[] { end, center, start });
    }

    private void drawShadow(Canvas canvas) {
        int right = this.getRight();
        mDrawableRight.setBounds(right, 0, right + mShadowWidth, getHeight());
        mDrawableRight.draw(canvas);
        int left = this.getLeft() - mShadowWidth;
        mDrawableLeft.setBounds(left, 0, left + mShadowWidth, getHeight());
        mDrawableLeft.draw(canvas);
    }

    private final Interpolator sMenuInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            // return (float) Math.pow(t, 5) + 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private void initViews(Context context) {
        mScroller = new Scroller(context, sMenuInterpolator);
        // 开启4.0以上版本的效果
        try {
            Field declaredField = mScroller.getClass().getDeclaredField("mFlywheel");
            if (declaredField != null && declaredField.getType() == boolean.class) {
                declaredField.setAccessible(true);
                declaredField.setBoolean(mScroller, true);
            }
        }
        catch (Exception e) {}
        if (mBackView == null) {
            mBackView = new View(context);
        }
        mBackView
                .setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mBackView.setClickable(true);
        mBackView.setLongClickable(true);
        mBackView.setId(BACKVIEW_ID);
        initSlicedViews(getContext());
        addView(mBackView, 0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 打开抽屉打开时回复功能
        mForeView = new View(getContext());
        mForeView
                .setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mForeView.setClickable(true);
        mForeView.setLongClickable(true);
        mForeView.setVisibility(GONE);
        mForeView.setOnClickListener(createForeViewListener());
        this.addView(mForeView);
    }

    private OnClickListener createForeViewListener() {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                sliceToCenter();
            }
        };
    }

    private void initSlicedViews(Context context) {
        int unit2 = convertDipToPx(context, 2);

        View left = new View(context);
        LayoutParams leftParams = new LayoutParams(unit2, LayoutParams.MATCH_PARENT);
        leftParams.addRule(RelativeLayout.LEFT_OF, BACKVIEW_ID);
        this.addView(left, leftParams);

        View right = new View(context);
        LayoutParams rightParams = new LayoutParams(unit2, LayoutParams.MATCH_PARENT);
        rightParams.addRule(RelativeLayout.RIGHT_OF, BACKVIEW_ID);
        this.addView(right, rightParams);

        View top = new View(context);
        LayoutParams topParams = new LayoutParams(LayoutParams.MATCH_PARENT, unit2);
        topParams.addRule(RelativeLayout.ABOVE, BACKVIEW_ID);
        this.addView(top, topParams);

        View bottom = new View(context);
        LayoutParams bottomParams = new LayoutParams(LayoutParams.MATCH_PARENT, unit2);
        bottomParams.addRule(RelativeLayout.BELOW, BACKVIEW_ID);
        this.addView(bottom, bottomParams);
    }

    private void initDefaultValue(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        mScaledMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScaledMaxmumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        try {
            // 适配adt插件
            width = windowManager.getDefaultDisplay().getWidth();
        }
        catch (Exception e) {}
        mMaxLeftOffset = (int) (width * 1.0 );
        mMaxRightOffset = 0;
        mTriggerSliceDistance = convertDipToPx(context, 150);
        mBaseDrawer = new AlphaDrawer();
        mScrollLeftDraw = false;
        mScrollRightDraw = true;
        mScrollHorizontal = true;
        mMaxDuration = 600;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mBounds = new RectF(l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if ((mScrollRightDraw && getScrollX() < 0 ) || (mScrollLeftDraw && getScrollX() > 0 )) {
            mBaseDrawer.draw(canvas, mBounds, Math.abs(getScrollX()));
        }
        drawShadow(canvas);
        super.dispatchDraw(canvas);
    }

    public void sliceToLeft() {
        onSliceStart(LOCATION_LEFT_DOWN);
        onTouchScrollToLeft();
    }

    public void sliceToRight() {
        onSliceStart(LOCATION_RIGHT_UP);
        onTouchScrollToRight();
    }

    public void sliceToCenter() {
        if (mLocation == LOCATION_LEFT_DOWN) {
            onSliceStart(LOCATION_CENTER);
            onTouchScrollToRight();
        } else if (mLocation == LOCATION_RIGHT_UP) {
            onSliceStart(LOCATION_CENTER);
            onTouchScrollToLeft();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            // Keep on drawing until the animation has finished. Just re-draw
            // the necessary part
            invalidate(getLeft() + oldX, getTop(), getRight(), getBottom());
            if (mSliceListener != null) {
                // mSliceListener.onSlicing(getScrollX() /
                // mBackView.getWidth());
            }
        } else {
            completeScroll();
        }
    }

    private void completeScroll() {
        if (mScrollingClickable) {
            this.setClickable(false);
        }
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            if (mSliceListener != null) {
                int distance = mScrollHorizontal ? mScroller.getCurrX() : mScroller.getCurrY();
                int target = 0;
                if (distance < 0) {
                    target = LOCATION_RIGHT_UP;
                } else if (distance > 0) {
                    target = LOCATION_LEFT_DOWN;
                } else if (distance == 0) {
                    target = LOCATION_CENTER;
                }
                if (target != mLocation) {
                    mOnStartCalled = false;
                    mLastLocation = mLocation;
                    mLocation = target;
                    onSliceFinished();
                }
            }

        }
        mScrolling = false;
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }

    private void onSliceStart(int nextLocation) {
        if (mOnStartCalled == false) {
            mSliceListener.onSliceStart(mLocation, nextLocation);
            mOnStartCalled = true;
        }
    }

    private void onSliceFinished() {
        if (mForeView != null) {
            if (mLocation == LOCATION_CENTER) {
                mForeView.setVisibility(View.GONE);
            } else {
                mForeView.setVisibility(View.VISIBLE);
            }
        }
        mSliceListener.onSliceFinish(mLastLocation, mLocation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean superIntercept = false;
        float moveX = 0;
        float moveY = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = event.getX();
                mTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX() - mTouchX;
                moveY = event.getY() - mTouchY;
                if (mScrollHorizontal) {
                    if (Math.abs(moveX) > mScaledTouchSlop) {
                        mTouchX = event.getX();
                        if (Math.abs(moveX) > Math.abs(moveY)) {
                            superIntercept = true;
                        }
                    }
                } else {
                    if (Math.abs(moveY) > mScaledTouchSlop) {
                        mTouchY = event.getY();
                        if (Math.abs(moveY) > Math.abs(moveX)) {
                            superIntercept = true;
                        }
                    }
                }

                break;
            default:
                break;
        }
        boolean in = mInterceptTouchEventAdapter == null ? superIntercept : superIntercept && mInterceptTouchEventAdapter
                .isIntercept(event, moveX, moveY, superIntercept, mLocation);
        return in;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            default:
                int distance = calculateOffset();
                float velocity = calculateVelocity();
                onTouchUp(distance, velocity);
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    private VelocityTracker acquireVelocityTracker(MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        return mVelocityTracker;
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private float calculateVelocity() {
        mVelocityTracker.computeCurrentVelocity(100, mScaledMaxmumFlingVelocity);
        return mScrollHorizontal ? mVelocityTracker.getXVelocity() : mVelocityTracker
                .getYVelocity();
    }

    private void onTouchMove(MotionEvent event) {
        int mMoveDistance;
        // 上下滑动的处理
        if (mScrollHorizontal) {
            mMoveDistance = (int) (event.getX() - mTouchX );
            scrollBy(-mMoveDistance, 0);
            mTouchX = event.getX();
        } else {
            // 左右滑动的处理
            mMoveDistance = (int) (event.getY() - mTouchY );
            scrollBy(0, -mMoveDistance);
            mTouchY = event.getY();
        }
        // System.out.println("onTouchMove=" + mMoveDistance + ";;;" +
        // mLocation);
        triggleOnSliceStart(mMoveDistance);
    }

    private void onTouchUp(int distance, double velocity) {
        if (mScrollingClickable) {
            this.setClickable(true);
        }
        if (Math.abs(velocity) > mScaledMinimumFlingVelocity) {
            if (velocity > 0) {
                onTouchScrollToRight();
            } else {
                onTouchScrollToLeft();
            }
        } else {
            if (distance <= -mTriggerSliceDistance) {
                onTouchScrollToRight();
            } else if (distance >= mTriggerSliceDistance) {
                onTouchScrollToLeft();
            } else {
                beginScroll(-distance);
            }
        }
    }

    private void triggleOnSliceStart(float moveX) {
        if (mSliceListener != null) {
            int currentX = mScrollHorizontal ? getScrollX() : getScrollY();
            int target = 0;
            if (currentX < 0) {
                target = LOCATION_RIGHT_UP;
            } else if (currentX > 0) {
                target = LOCATION_LEFT_DOWN;
            } else if (currentX == 0) {
                target = LOCATION_CENTER;
            }
            if (target != mLastLocation) {
                mLastLocation = target;
                mSliceListener.onSliceStart(LOCATION_CENTER, target);
            }
        }
    }

    private int calculateOffset() {
        int offset = 0;
        switch (mLocation) {
            case LOCATION_CENTER:
                offset = mScrollHorizontal ? getScrollX() : getScrollY();
                break;
            case LOCATION_RIGHT_UP:
                offset = mScrollHorizontal ? getScrollX() + mMaxLeftOffset : mMaxLeftOffset + getScrollY();
                break;
            case LOCATION_LEFT_DOWN:
                offset = mScrollHorizontal ? getScrollX() - mMaxRightOffset : getScrollY() - mMaxRightOffset;
                break;
            default:
                break;
        }
        return offset;
    }

    private void onTouchScrollToLeft() {
        // 超过最大的可以出发滑动距离的时候释放，激活向左滚动
        int target = 0;
        if (mLocation == LOCATION_CENTER) {
            target = mMaxRightOffset - getScrollX();
        } else if (mLocation == LOCATION_LEFT_DOWN) {
            // 在左边的时候,又继续向左滑动，超出最大滑动距离，往右回弹吧;
            target = mMaxRightOffset - getScrollX();
        } else if (mLocation == LOCATION_RIGHT_UP) {
            // 回到中间
            target = -getScrollX();
        }
        beginScroll(target);
    }

    private void onTouchScrollToRight() {
        // 超过最大的可以出发滑动距离的时候释放，激活向右滚动
        int target = 0;
        if (mLocation == LOCATION_CENTER) {
            target = -getScrollX() - mMaxLeftOffset;
        } else if (mLocation == LOCATION_LEFT_DOWN) {
            // 在左边的时候,回到中间吧
            target = -getScrollX();
        } else if (mLocation == LOCATION_RIGHT_UP) {
            // 在右边，又继续向右滑动，超出最大滑动距离，往左回弹吧;
            target = -getScrollX() - mMaxLeftOffset;
        }
        beginScroll(target);
    }

    private void beginScroll(int target) {
        mScrolling = true;
        int duration = (int) Math.min((Math.abs(target) * 1.5 ), mMaxDuration);
        if (mScrollHorizontal) {
            mScroller.startScroll(getScrollX(), 0, target, 0, duration);
        } else {
            mScroller.startScroll(0, getScrollY(), 0, target, duration);
        }
        invalidate();
    }

    private int convertDipToPx(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context
                .getResources().getDisplayMetrics());
    }

    public int getMaxLeftOffset() {
        return mMaxLeftOffset;
    }

    public void setMaxLeftOffsetDip(Context context, int maxLeftOffset) {
        this.mMaxLeftOffset = convertDipToPx(context, maxLeftOffset);
    }

    public int getMaxRightOffset() {
        return mMaxRightOffset;
    }

    public void setMaxRightOffset(int maxRightOffset) {
        this.mMaxRightOffset = maxRightOffset;
    }

    public void setMaxRightOffsetDip(Context context, int maxRightOffset) {
        this.mMaxRightOffset = convertDipToPx(context, maxRightOffset);
    }

    public int getCurrentLocation() {
        return mLocation;
    }

    public void setSliceListener(SliceListener sliceListener) {
        this.mSliceListener = sliceListener;
    }

    public void setScrollingClickable(boolean clickable) {
        this.mScrollingClickable = clickable;
    }

    @Override
    public void setBackgroundColor(int color) {
        if (mBackView == null) {
            mBackView = new View(getContext());
        }
        mBackView.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundDrawable(Drawable d) {
        if (mBackView == null) {
            mBackView = new View(getContext());
        }
        mBackView.setBackgroundDrawable(d);
    }

    @Override
    public void setBackgroundResource(int resId) {
        if (mBackView == null) {
            mBackView = new View(getContext());
        }
        mBackView.setBackgroundResource(resId);
    }

    public void setHorizontal(boolean isHorizontal) {
        mScrollHorizontal = isHorizontal;
    }

    public void setScrollRightDraw(boolean scrollRightDraw) {
        this.mScrollRightDraw = scrollRightDraw;
    }

    public void setScrollLeftDraw(boolean scrollLeftDraw) {
        this.mScrollLeftDraw = scrollLeftDraw;
    }

    public void setInterceptTouchEventAdapter(InterceptTouchEventListener interceptTouchEventAdapter) {
        this.mInterceptTouchEventAdapter = interceptTouchEventAdapter;
    }

    public void setDrawer(BaseDrawer drawer) {
        this.mBaseDrawer = drawer;
    }

    public void setMaxDuration(long duration) {
        mMaxDuration = duration;
    }

    public void setMaxLeftOffset(int maxLeftOffset) {
        this.mMaxLeftOffset = maxLeftOffset;
    }

}
