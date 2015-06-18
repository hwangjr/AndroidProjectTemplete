package co.sihe.hongmi.views.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;

public class FragmentHostTabGroup extends FragmentTabGroup {

    public FragmentHostTabGroup(Context activity, int layoutId) {
        super(activity, layoutId);
    }

    public FragmentHostTabGroup(Context activity, AttributeSet attrs) {
        super(activity, attrs);
    }

    @Override
    public void addTab(Class<?> fragmentClass, Bundle args) {
        TabInfo tabInfo = new TabInfo(fragmentClass, args);
        tabInfo.tag = getContainerId() + ":" + mTabs.size() + ":"
                + fragmentClass.hashCode();
        // check if already have the fragment for this tab in manager
        Fragment fragment = getFragmentManager().findFragmentByTag(tabInfo.tag);
        if (fragment != null && !fragment.isDetached()) {
            getFragmentManager().beginTransaction().detach(fragment).commit();
        }
        mTabs.add(tabInfo);
    }

    @Override
    protected void onTabSelected(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position out of bounds");
        }
        if (position >= mTabs.size()) {
            return;
        }
        if (position != mCurrentPosition) {
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            // unselectedLast
            TabInfo lastTab = (mCurrentPosition == -1) ? null : mTabs
                    .get(mCurrentPosition);
            Fragment lastFragment = (mCurrentPosition == -1) ? null
                    : getFragmentByTag(lastTab.tag);
            if (lastTab != null && lastFragment != null) {
                lastFragment.setMenuVisibility(false);
                lastFragment.setUserVisibleHint(false);
                transaction.detach(lastFragment);
            }
            // selectedNew
            TabInfo newTab = mTabs.get(position);
            Fragment newFragment = getFragmentByTag(newTab.tag);
            if (newTab != null) {
                if (newFragment == null) {
                    newFragment = Fragment.instantiate(getContext(),
                            newTab.fragmentClass.getName(), newTab.args);
                    transaction.add(getContainerId(), newFragment, newTab.tag);
                } else {
                    newFragment.setMenuVisibility(true);
                    newFragment.setUserVisibleHint(true);
                    transaction.attach(newFragment);
                }
            }
            mCurrentPosition = position;
            transaction.commitAllowingStateLoss();
            // getFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onTabClick(int position) {
        if (mTabChangedListener != null) {
            mTabChangedListener.onTabClick(position);
        }
    }

}
