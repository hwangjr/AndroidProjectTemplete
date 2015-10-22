package co.sihe.apptemplete.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.sihe.apptemplete.views.SlicingDrawer;
import co.sihe.apptemplete.views.slicepanel.SlicedPanelExitLayout;
import co.sihe.apptemplete.views.slicepanel.SlicedPanelLayout;

public abstract class SlidingExitFragment extends BaseFragment {
    private SlicedPanelExitLayout mSlidingExitLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = getActivity();
        mSlidingExitLayout = new SlicedPanelExitLayout(context);
        mSlidingExitLayout.setDrawer(new SlicingDrawer());
        mSlidingExitLayout.setSliceExitListener(new SlicedPanelExitLayout.onSliceExitListener() {

            @Override
            public void onExit(SlicedPanelLayout slicedNoodlesLayout) {
                SlidingExitFragment.this.onExit();
            }
        });
        View contentView = onChildCreateView(inflater, mSlidingExitLayout,
                savedInstanceState);
        mSlidingExitLayout.addView(contentView);
        return mSlidingExitLayout;
    }

    protected void onExit() {
        finishFragment();
    }

    public void setSliceExitListener(SlicedPanelExitLayout.onSliceExitListener listener) {
        mSlidingExitLayout.setSliceExitListener(listener);
    }

    protected void slidingToExit() {
        mSlidingExitLayout.sliceToRight();
    }

    protected void closeSlidingMode() {
        if (mSlidingExitLayout != null) {
            mSlidingExitLayout.closeSlidingMode();
        }
    }

    protected void openSlidingMode() {
        if (mSlidingExitLayout != null) {
            mSlidingExitLayout.openSlidingMode();
        }
    }

    protected abstract View onChildCreateView(LayoutInflater inflater,
                                              ViewGroup container, Bundle savedInstanceState);
}
