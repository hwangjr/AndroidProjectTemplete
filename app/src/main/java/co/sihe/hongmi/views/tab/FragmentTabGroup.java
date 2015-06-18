package co.sihe.hongmi.views.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class FragmentTabGroup extends BaseTabGroup {

    private FragmentManager mFragmentManager;
    protected int mCurrentPosition = -1;
    protected List<TabInfo> mTabs = new ArrayList<TabInfo>();

    public FragmentTabGroup(Context context, int layoutId) {
        super(context, layoutId);
    }

    public FragmentTabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public String getFragmentTag(int position) {
        String tag = null;
        TabInfo tabInfo = mTabs.get(position);
        if (tabInfo != null) {
            tag = tabInfo.tag;
        }
        return tag;
    }

    public void setupInFragment(Fragment fragment, int tabBarLocation) {
        setupInFragment(fragment, tabBarLocation, null);
    }

    public void setupInFragment(Fragment fragment, int tabBarLocation,
                                ViewGroup tabBarLayout) {
        mFragmentManager = fragment.getChildFragmentManager();
        super.setup(tabBarLocation, tabBarLayout);
    }

    @Override
    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public Fragment getCurrentFragment() {
        Fragment fragment = null;
        TabInfo tabInfo = mTabs.get(mCurrentPosition);
        if (tabInfo != null) {
            fragment = getFragmentByTag(tabInfo.tag);
        }
        return fragment;
    }

    public List<Fragment> getAllFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        for (TabInfo tabInfo : mTabs) {
            Fragment fragment = getFragmentByTag(tabInfo.tag);
            if (fragment != null) {
                fragments.add(fragment);
            }
        }
        return fragments;
    }

    public void removeAllFragments() {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        for (TabInfo tabInfo : mTabs) {
            Fragment fragment = getFragmentByTag(tabInfo.tag);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public int getCount() {
        return mTabs.size();
    }

    protected Fragment getFragmentByTag(String tag) {
        return getFragmentManager().findFragmentByTag(tag);
    }

    protected FragmentManager getFragmentManager() {
        if (mFragmentManager == null) {
            mFragmentManager = ((FragmentActivity) getContext())
                    .getSupportFragmentManager();
        }
        return mFragmentManager;
    }

    protected static final class TabInfo {
        String tag;
        final Class<?> fragmentClass;
        final Bundle args;

        TabInfo(Class<?> fragmentClass, Bundle args) {
            this.fragmentClass = fragmentClass;
            this.args = args;
        }
    }
}
