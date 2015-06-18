package co.sihe.hongmi.views.tab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class TabBar extends FrameLayout implements View.OnClickListener {

    protected int mCurrentPosition = -1;
    protected ViewGroup mBarLayout;
    protected ImageView mAnimImage;
    protected Drawable mAnimDrawable;
    protected Animation mImageAnimation;
    protected Interpolator mAnimInterpolator = new OvershootInterpolator(1);
    protected long mAnimationDuration = 400;
    protected OnViewSwitchedListener mViewSwitchedListener;
    private FrameLayout mAnimLayout;
    private boolean mPaddingWithBar;

    public interface OnViewSwitchedListener {
        void onWidgetSwitched(int position, View selectedView);

        void onTabClick(int position);
    }

    public TabBar(Context context, ViewGroup widgetsLayout) {
        super(context);
        mBarLayout = widgetsLayout;
        setupViews();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurrentView(position, true);
    }

    protected void setCurrentView(int position, boolean isStartAnimation) {
        position = Math.min(position, mBarLayout.getChildCount() - 1);
        position = Math.max(0, position);

        if (mCurrentPosition != position) {
            View selectedView = mBarLayout.getChildAt(position);
            View lastView = mBarLayout.getChildAt(mCurrentPosition);

            triggerSelector(selectedView, lastView);

            mCurrentPosition = position;

            if (mViewSwitchedListener != null) {
                mViewSwitchedListener.onWidgetSwitched(position, selectedView);
            }

            if (isStartAnimation && lastView != null) {
                startImageAnimation(lastView.getLeft(), selectedView.getLeft(),
                        lastView.getTop(), selectedView.getTop());
            }
        } else {
            if (mViewSwitchedListener != null) {
                mViewSwitchedListener.onTabClick(position);
            }
        }
    }

    protected void onViewSelected(int position) {
        position = Math.min(position, mBarLayout.getChildCount() - 1);
        position = Math.max(0, position);
        if (mCurrentPosition != position) {
            View selectedView = mBarLayout.getChildAt(position);
            View lastView = mBarLayout.getChildAt(mCurrentPosition);
            triggerSelector(selectedView, lastView);
            mCurrentPosition = position;
        }
    }

    private void triggerSelector(View selectedView, View lastView) {
        if (selectedView instanceof CompoundButton) {
            if (lastView != null) {
                ((CompoundButton) lastView).setChecked(false);
            }
            ((CompoundButton) selectedView).setChecked(true);
        } else {
            if (lastView != null) {
                lastView.setSelected(false);
            }
            selectedView.setSelected(true);
        }
    }

    private void setupViews() {
        this.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        int count = mBarLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mBarLayout.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(this);
        }

        mAnimLayout = createAnimLayout();
        mAnimImage = createAnimImageView();
        mAnimLayout.addView(mAnimImage);
        mAnimLayout.setBackgroundDrawable(mBarLayout.getBackground());
        mBarLayout.setBackgroundResource(android.R.color.transparent);
        this.addView(mAnimLayout);
        this.addView(mBarLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int layoutHeight = mBarLayout.getMeasuredHeight() >= mAnimLayout
                .getMeasuredHeight() ? mBarLayout.getMeasuredHeight()
                : mAnimLayout.getMeasuredHeight();
        this.setMeasuredDimension(mBarLayout.getMeasuredWidth(), layoutHeight);
        int specHeight = MeasureSpec.makeMeasureSpec(layoutHeight,
                MeasureSpec.EXACTLY);
        mBarLayout.measure(widthMeasureSpec, specHeight);
        View selectedView = getSelectedView();
        if (selectedView != null) {
            int widthMeasure = MeasureSpec.makeMeasureSpec(
                    selectedView.getMeasuredWidth(), MeasureSpec.EXACTLY);
            int heightMeasure = MeasureSpec.makeMeasureSpec(
                    selectedView.getMeasuredHeight(), MeasureSpec.EXACTLY);
            mAnimImage.measure(widthMeasure, mPaddingWithBar ? specHeight
                    : heightMeasure);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getSelectedView() != null) {
            mAnimImage.offsetLeftAndRight(getSelectedView().getLeft()
                    - this.getLeft());
        }
    }

    private FrameLayout createAnimLayout() {
        FrameLayout animLayout = new FrameLayout(getContext());
        animLayout.setBackgroundResource(android.R.color.transparent);
        animLayout.setVisibility(VISIBLE);
        return animLayout;
    }

    private ImageView createAnimImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ScaleType.FIT_XY);
        return imageView;
    }

    private void startImageAnimation(int startX, int endX, int startY, int endY) {
        if (mAnimDrawable != null) {
            mAnimImage.requestLayout();
            mImageAnimation = getAnimation(startX - endX, 0, 0, 0);
            mAnimImage.clearAnimation();
            mAnimImage.startAnimation(mImageAnimation);
        }
    }

    private Animation getAnimation(int startX, int endX, int startY, int endY) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnim = new TranslateAnimation(startX, endX,
                startY, endY);
        animationSet.addAnimation(translateAnim);
        if (mAnimInterpolator != null) {
            animationSet.setInterpolator(mAnimInterpolator);
        }
        animationSet.setDuration(mAnimationDuration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    public void setOnViewSwitchedListener(OnViewSwitchedListener listener) {
        mViewSwitchedListener = listener;
    }

    public int getViewCount() {
        return mBarLayout.getChildCount();
    }

    public View getSelectedView() {
        mCurrentPosition = Math.min(mCurrentPosition,
                mBarLayout.getChildCount() - 1);
        mCurrentPosition = Math.max(0, mCurrentPosition);
        return mBarLayout.getChildAt(mCurrentPosition);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public Drawable getAnimDrawable() {
        return mAnimDrawable;
    }

    public void setAnimDrawable(Drawable animDrawable, boolean paddingWithBar) {
        if (animDrawable != null) {
            mPaddingWithBar = paddingWithBar;
            mAnimDrawable = animDrawable;
            mAnimImage.setImageDrawable(animDrawable);
            mAnimLayout.setVisibility(VISIBLE);
        }
    }

    public Interpolator getAnimInterpolator() {
        return mAnimInterpolator;
    }

    public void setAnimInterpolator(Interpolator animInterpolator) {
        this.mAnimInterpolator = animInterpolator;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

}
