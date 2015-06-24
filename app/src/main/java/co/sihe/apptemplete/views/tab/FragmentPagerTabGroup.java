package co.sihe.apptemplete.views.tab;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class FragmentPagerTabGroup extends FragmentTabGroup implements
        ViewPager.OnPageChangeListener {

    private boolean mPageScrollAnimTrigger = true;
    private ViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;
    private SparseArray<Map<View, Rect>> mUnflingViews = new SparseArray<Map<View, Rect>>();

    public FragmentPagerTabGroup(Context activity, int layoutId) {
        super(activity, layoutId);
    }

    public FragmentPagerTabGroup(Context activity, AttributeSet attrs) {
        super(activity, attrs);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
    }

    public ViewPager getPager() {
        return mViewPager;
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabBar != null) {
            mTabBar.onViewSelected(position);
        }
        mCurrentPosition = position;
        if (mTabChangedListener != null) {
            mTabChangedListener.onTabChanged(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mPageScrollAnimTrigger = true;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (mPageScrollAnimTrigger && mTabBar != null
                && mTabBar.getAnimDrawable() != null) {
            int newX = (int) (mTabBar.mAnimImage.getWidth() * (position + positionOffset));
            int lastX = mTabBar.mAnimImage.getLeft();
            mTabBar.mAnimImage.setTag(newX);
            mTabBar.mAnimImage.offsetLeftAndRight(newX - lastX);
            mTabBar.mAnimImage.postInvalidate();
        }
    }

    @Override
    public void setup(int widgetBarLocation, ViewGroup widgetBarLayout) {
        super.setup(widgetBarLocation, widgetBarLayout);
    }

    @Override
    protected ViewGroup createContainerLayout() {
        mPagerAdapter = new TabPagerAdapter(getFragmentManager());
        mViewPager = createViewPager(getContext());
        mViewPager.setId(this.getId());
        setId(View.NO_ID);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(1);
        return mViewPager;
    }

    protected ViewPager createViewPager(Context context) {
        return new TabPager(context);
    }

    public void addUnflingView(final int pageIndex, final View view) {
        if (mViewPager instanceof TabPager) {
            Map<View, Rect> map = mUnflingViews.get(pageIndex);
            if (map == null) {
                map = new HashMap<View, Rect>();
            }
            if (!map.containsKey(view)) {
                map.put(view, new Rect());
            }
            mUnflingViews.put(pageIndex, map);
        }
    }

    public void setPagerOffscreenPageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public void addTab(Class<?> fragmentClass, Bundle args) {
        TabInfo tabInfo = new TabInfo(fragmentClass, args);
        tabInfo.tag = makeFragmentName(getContainerId(), mTabs.size());
        mTabs.add(tabInfo);
        // check if already have the fragment for this tab in manager
        Fragment fragment = getFragmentManager().findFragmentByTag(
                makeFragmentName(getContainerId(), mTabs.size()));
        if (fragment != null && !fragment.isDetached()) {
            getFragmentManager().beginTransaction().detach(fragment).commit();
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    protected void onTabSelected(int position) {
        position = Math.min(position, mTabs.size() - 1);
        position = Math.max(0, position);
        if (mCurrentPosition != -1) {
            mPageScrollAnimTrigger = false;
        }
        mCurrentPosition = position;
        mViewPager.setCurrentItem(position);
    }

    private boolean isInUnflingArea(int x, int y) {
        if (mUnflingViews.get(mCurrentPosition) != null) {
            for (Map.Entry<View, Rect> entry : mUnflingViews.get(
                    mCurrentPosition).entrySet()) {
                Rect rect = entry.getValue();
                entry.getKey().getHitRect(rect);
                // System.out.println("rect=" + rect + "; x=" + x + "; y=" + y +
                // "; contains=" + rect
                // .contains(x, y));
                if (rect.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    class TabPager extends ViewPager {

        public TabPager(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            // 排除滑屏的区域
            if (isInUnflingArea((int) event.getX(), (int) event.getY())) {
                return false;
            }
            return super.onInterceptTouchEvent(event);
        }
    }

    class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            if (mTabs != null && !mTabs.isEmpty()) {
                TabInfo info = mTabs.get(position);
                if (info.tag == null) {
                    info.tag = ((Fragment) object).getTag();
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(getContext(),
                    info.fragmentClass.getName(), info.args);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }
    }

    @Override
    public void onTabClick(int position) {
        // TODO Auto-generated method stub

    }

    public static interface OnAddUnflingViewListener {
        void addUnflingView(View view);
    }
}
